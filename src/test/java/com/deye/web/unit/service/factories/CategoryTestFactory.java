package com.deye.web.unit.service.factories;

import com.deye.web.entity.CategoryEntity;

import java.util.UUID;

public class CategoryTestFactory {

    public static CategoryEntity createCategoryEntity() {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(UUID.randomUUID());
        categoryEntity.setName("name");
        categoryEntity.setDescription("description");
        return categoryEntity;
    }
}
