package com.deye.web.listeners.transactions;

import com.deye.web.entity.ProductEntity;
import com.deye.web.exception.TransactionConsistencyException;
import com.deye.web.listeners.events.DeletedProductEvent;
import com.deye.web.listeners.events.SavedProductEvent;
import com.deye.web.service.FileService;
import com.deye.web.service.PublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {
    private final PublisherService publisherService;
    private final FileService fileService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onProductSaved(SavedProductEvent event) {
        try {
            ProductEntity product = event.getProduct();
            UUID productId = product.getId();
            MultipartFile[] imagesToAdd = event.getImagesToAdd();
            log.info("Product with id: {} successfully saved to db", productId);
            if (imagesToAdd != null) {
                log.info("Adding images to db, productId: {}", productId);
                for (MultipartFile image : imagesToAdd) {
                    uploadNewProductImage(image, productId);
                }
            }
            if (event.getImagesToRemove() != null) {
                for (String imageToRemove : event.getImagesToRemove()) {
                    log.info("Removing image from the storage as it was marked as to delete: {}", imageToRemove);
                    deleteProductImage(imageToRemove);
                }
            }
            log.info("All product {} images successfully saved to file storage. Trying to seng message to message broker", productId);
            publisherService.onProductSaved(product);
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }

    private void uploadNewProductImage(MultipartFile image, UUID productId) {
        log.info("Saving product image to the file storage, productId: {}, fileName: {}", productId, image.getOriginalFilename());
        try {
            fileService.upload(image);
        } catch (Exception e) {
            log.error("Product image uploading failed. id: {}", productId);
            throw e;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onProductDeleted(DeletedProductEvent event) {
        try {
            UUID productId = event.getProductId();
            log.info("Product with id: {} successfully deleted from DB. Trying to delete its images from storage", productId);
            for (String imageName : event.getImageNames()) {
                deleteProductImage(imageName);
            }
            log.info("Product {} images deleted from file storage. Trying to seng message to message broker", productId);
            publisherService.onProductDeleted(productId);
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }

    private void deleteProductImage(String fileName) {
        try {
            fileService.delete(fileName);
            log.info("Product image with name: {} successfully deleted from file storage", fileName);
        } catch (Exception e) {
            log.error("Product image with name: {} deletion failed. Please try again manually.", fileName);
            throw e;
        }
    }
}
