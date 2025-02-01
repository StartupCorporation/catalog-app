package com.deye.web.listeners.events;

import com.deye.web.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class SavedProductEvent {
    private ProductEntity product;
    private MultipartFile[] images;
}
