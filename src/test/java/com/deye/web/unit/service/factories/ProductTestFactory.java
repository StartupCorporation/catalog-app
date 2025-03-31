package com.deye.web.unit.service.factories;

import com.deye.web.entity.CategoryEntity;
import com.deye.web.entity.ProductEntity;

import java.util.UUID;

public class ProductTestFactory {

    public static ProductEntity createProduct(CategoryEntity categoryEntity) {
        ProductEntity product = new ProductEntity();
        product.setId(UUID.randomUUID());
        product.setName("name");
        product.setDescription("description");
        product.setPrice(25f);
        product.setReservedQuantity(0);
        product.setStockQuantity(10);
        product.setCategory(categoryEntity);
        return product;
    }
}
