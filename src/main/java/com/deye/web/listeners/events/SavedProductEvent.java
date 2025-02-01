package com.deye.web.listeners.events;

import com.deye.web.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class SavedProductEvent {
    private ProductEntity product;
    private MultipartFile[] imagesToAdd;
    private List<String> imagesToRemove;

    public SavedProductEvent(ProductEntity product, MultipartFile[] imagesToAdd) {
        this.product = product;
        this.imagesToAdd = imagesToAdd;
    }
}
