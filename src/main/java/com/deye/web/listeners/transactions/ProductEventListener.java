package com.deye.web.listeners.transactions;

import com.deye.web.entity.ProductEntity;
import com.deye.web.listeners.events.DeletedProductEvent;
import com.deye.web.listeners.events.SavedProductEvent;
import com.deye.web.repository.ProductRepository;
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
    private final ProductRepository productRepository;
    private final PublisherService publisherService;
    private final FileService fileService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductSaved(SavedProductEvent event) {
        ProductEntity product = event.getProduct();
        UUID productId = product.getId();
        MultipartFile[] images = event.getImages();
        log.info("Product with id: {} successfully saved to db. Trying to upload image to the storage", productId);
        for (MultipartFile image : images) {
            uploadNewProductImage(image, productId);
        }
        log.info("All product {} images successfully saved to file storage. Trying to seng message to message broker", productId);
        publisherService.onProductSaved(product);
    }

    private void uploadNewProductImage(MultipartFile image, UUID productId) {
        log.info("Saving product image to the file storage, productId: {}, fileName: {}", productId, image.getOriginalFilename());
        try {
            fileService.upload(image);
        } catch (Exception e) {
            log.error("Product image uploading failed. Deleting product with id: {}", productId);
            productRepository.deleteById(productId);
            throw e;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductDeleted(DeletedProductEvent event) {
        UUID productId = event.getProductId();
        log.info("Product with id: {} successfully deleted from DB. Trying to delete its images from storage", productId);
        for (String imageName : event.getImageNames()) {
            deleteProductImage(imageName);
        }
        log.info("Product {} images deleted from file storage. Trying to seng message to message broker", productId);
        publisherService.onProductDeleted(productId);
    }

    private void deleteProductImage(String fileName) {
        try {
            fileService.delete(fileName);
            log.info("Category image with name: {} successfully deleted from file storage", fileName);
        } catch (Exception e) {
            log.error("Category image with name: {} deletion failed. Please try again manually.", fileName);
        }
    }
}
