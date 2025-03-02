package com.deye.web.util.mapper;

import com.deye.web.controller.view.AttributeView;
import com.deye.web.controller.view.ProductView;
import com.deye.web.entity.ProductEntity;
import com.deye.web.service.impl.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ConfigService configService;
    private final AttributeMapper attributeMapper;

    public ProductView toProductView(ProductEntity product) {
        List<AttributeView> attributes = product.getAttributesValuesForProduct().stream()
                .map(attributeMapper::toAttributeView)
                .toList();
        String bucketUrl = configService.getMinioBucketName() + "/";
        return ProductView.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .images(product.getImagesNames().stream().map(image -> bucketUrl + image).collect(Collectors.toSet()))
                .attributes(attributes)
                .build();
    }
}
