package com.deye.web.unit.service.factories;

import com.deye.web.controller.dto.CategoryAttributeDto;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.CategoryAttributeEntity;
import com.deye.web.entity.CategoryEntity;

import java.util.UUID;

public class CategoryAttributeTestFactory {

    public static CategoryAttributeEntity createCategoryAttributeEntity(CategoryEntity categoryEntity, AttributeEntity attributeEntity) {
        CategoryAttributeEntity categoryAttributeEntity = new CategoryAttributeEntity();
        categoryAttributeEntity.setId(UUID.randomUUID());
        categoryAttributeEntity.setAttribute(attributeEntity);
        categoryAttributeEntity.setCategory(categoryEntity);
        categoryAttributeEntity.setFilterable(Boolean.TRUE);
        categoryAttributeEntity.setRequired(Boolean.TRUE);
        return categoryAttributeEntity;
    }

    public static CategoryAttributeDto createCategoryAttributeDto(AttributeEntity attributeEntity) {
        CategoryAttributeDto categoryAttributeDto = new CategoryAttributeDto();
        categoryAttributeDto.setId(attributeEntity.getId());
        categoryAttributeDto.setIsRequired(Boolean.TRUE);
        categoryAttributeDto.setIsFilterable(Boolean.TRUE);
        return categoryAttributeDto;
    }
}
