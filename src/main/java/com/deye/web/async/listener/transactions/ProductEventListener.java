package com.deye.web.async.listener.transactions;

import com.deye.web.async.listener.transactions.events.DeletedProductEvent;
import com.deye.web.async.listener.transactions.events.ReservationResultEvent;
import com.deye.web.async.listener.transactions.events.SavedProductEvent;
import com.deye.web.async.publisher.PublisherService;
import com.deye.web.async.util.RabbitMqEvent;
import com.deye.web.controller.dto.CreateImageDto;
import com.deye.web.controller.dto.DeleteImageDto;
import com.deye.web.entity.ProductEntity;
import com.deye.web.exception.TransactionConsistencyException;
import com.deye.web.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {
    private final FileService fileService;
    private final PublisherService publisherService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onProductSaved(SavedProductEvent event) {
        try {
            ProductEntity product = event.getProduct();
            UUID productId = product.getId();
            List<CreateImageDto> createImages = event.getCreateImages();
            log.info("Product with id: {} successfully saved to db", productId);
            if (createImages != null) {
                log.info("Adding images to db, productId: {}", productId);
                for (CreateImageDto createImage : createImages) {
                    uploadNewProductImage(createImage.getImage(), createImage.getFileName(), createImage.getDirectoryName(), productId);
                }
            }
            List<DeleteImageDto> deleteImages = event.getDeleteImages();
            if (deleteImages != null) {
                for (DeleteImageDto deleteImage : deleteImages) {
                    log.info("Removing image from the storage as it was marked as to delete: {}", deleteImage.getName());
                    deleteProductImage(deleteImage.getName(), deleteImage.getDirectory());
                }
            }
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }

    private void uploadNewProductImage(MultipartFile image, String fileName, String directoryName, UUID productId) {
        log.info("Saving product image to the file storage, productId: {}, fileName: {}", productId, image.getOriginalFilename());
        try {
            fileService.upload(image, directoryName, fileName);
        } catch (Exception e) {
            log.error("Product image uploading failed. id: {}", productId);
            throw e;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onProductDeleted(DeletedProductEvent event) {
        try {
            UUID productId = event.getProductId();
            List<DeleteImageDto> deleteImages = event.getDeleteImages();
            log.info("Product with ids: {} successfully deleted from DB. Trying to delete its images from storage", productId);
            for (DeleteImageDto deleteImage : deleteImages) {
                deleteProductImage(deleteImage.getName(), deleteImage.getDirectory());
            }
            publisherService.onProductsDeleted(Set.of(productId));
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }

    private void deleteProductImage(String fileName, String directoryName) {
        try {
            fileService.delete(directoryName, fileName);
            log.info("Product image with name: {} successfully deleted from file storage", fileName);
        } catch (Exception e) {
            log.error("Product image with name: {} deletion failed.", fileName);
            throw e;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onSuccessfulReservationResult(ReservationResultEvent event) {
        processReservationResult(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onFailedReservationResult(ReservationResultEvent event) {
        processReservationResult(event);
    }

    private void processReservationResult(ReservationResultEvent event) {
        try {
            UUID orderId = event.getOrderId();
            RabbitMqEvent rabbitMqEvent = event.getRabbitMqEvent();
            log.info("Reservation result: {}, for order id: {}. Publish message to message broker", rabbitMqEvent, orderId);
            publisherService.onReservationResult(orderId, rabbitMqEvent);
            log.info("Reservation result published successfully for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }
}
