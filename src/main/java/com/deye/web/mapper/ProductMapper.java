package com.deye.web.mapper;

import com.deye.web.controller.view.ProductView;
import com.deye.web.entity.ProductEntity;
import com.deye.web.service.impl.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ConfigService configService;

    public ProductView toProductView(ProductEntity product) {
        return ProductView.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory().getId())
                .images(product.getImagesNames())
                .build();
    }
}
