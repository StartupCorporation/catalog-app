package com.deye.web.unit.service;

import com.deye.web.controller.dto.CategoryAttributeDto;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.CategoryAttributeEntity;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.entity.ProductEntity;
import com.deye.web.exception.ActionNotAllowedException;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.exception.WrongRequestBodyException;
import com.deye.web.repository.AttributeRepository;
import com.deye.web.service.CategoryAttributeService;
import com.deye.web.unit.service.factories.AttributeTestFactory;
import com.deye.web.unit.service.factories.CategoryAttributeTestFactory;
import com.deye.web.unit.service.factories.CategoryTestFactory;
import com.deye.web.unit.service.factories.ProductTestFactory;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CategoryAttributeServiceTest {

    @InjectMocks
    private CategoryAttributeService categoryAttributeService;

    @Mock
    private AttributeRepository attributeRepository;

    private AttributeEntity attributeEntity;
    private CategoryEntity categoryEntity;
    private CategoryAttributeEntity categoryAttributeEntity;
    private ProductEntity productEntity;

    @BeforeEach
    public void init() {
        categoryEntity = CategoryTestFactory.createCategoryEntity();
        attributeEntity = AttributeTestFactory.createAttributeEntity();
        categoryAttributeEntity = CategoryAttributeTestFactory.createCategoryAttributeEntity(categoryEntity, attributeEntity);
        categoryEntity.setCategoryAttributes(Set.of(categoryAttributeEntity));
        productEntity = ProductTestFactory.createProduct(categoryEntity);
    }


    @Test
    public void getCategoryAttribute_shouldGetCategoryAttributeWhenItExist() {
        CategoryAttributeEntity result = categoryAttributeService.getCategoryAttribute(categoryEntity, attributeEntity.getId());

        assertEquals(categoryAttributeEntity, result);
    }

    @Test
    public void getCategoryAttribute_shouldThrowEntityNotFoundExceptionWhenCategoryAttributeNotExist() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryAttributeService.getCategoryAttribute(categoryEntity, UUID.randomUUID()));

        assertEquals(ErrorCodeUtils.CATEGORY_ATTRIBUTE_NOT_FOUND_ERROR_CODE, exception.getCode());
        assertEquals(ErrorMessageUtils.CATEGORY_ATTRIBUTE_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void getCategoryAttribute_shouldThrowActionNotAllowedExceptionWhenAttributeIdIsNull() {
        assertThrows(ActionNotAllowedException.class, () -> categoryAttributeService.getCategoryAttribute(categoryEntity, null));
    }

    @Test
    public void addAttributesToCategory_shouldThrowActionNotAllowedExceptionWhenCategoryIsNull() {
        assertThrows(ActionNotAllowedException.class, () -> categoryAttributeService.addAttributesToCategory(null, List.of()));
    }

    @Test
    public void addAttributesToCategory_shouldAddAttributesToCategory() {
        when(attributeRepository.findAllById(any())).thenReturn(List.of(attributeEntity));

        categoryEntity.setCategoryAttributes(new HashSet<>());

        CategoryAttributeDto categoryAttributeDto = CategoryAttributeTestFactory.createCategoryAttributeDto(attributeEntity);

        categoryAttributeService.addAttributesToCategory(categoryEntity, List.of(categoryAttributeDto));

        assertEquals(1, categoryEntity.getCategoryAttributes().size());
        CategoryAttributeEntity result = categoryEntity.getCategoryAttributes().iterator().next();
        assertEquals(categoryAttributeDto.getId(), result.getAttribute().getId());
        assertEquals(categoryAttributeDto.getIsFilterable(), result.isFilterable());
        assertEquals(categoryAttributeDto.getIsRequired(), result.isRequired());
        assertEquals(categoryEntity, result.getCategory());
    }

    @Test
    public void addAttributesToCategory_shouldThrowEntityNotFoundExceptionWhenFoundAttributeSizeIsLessThenInDto() {
        when(attributeRepository.findAllById(any())).thenReturn(List.of());

        CategoryAttributeDto categoryAttributeDto = CategoryAttributeTestFactory.createCategoryAttributeDto(attributeEntity);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryAttributeService.addAttributesToCategory(categoryEntity, List.of(categoryAttributeDto)));

        assertEquals(ErrorCodeUtils.ATTRIBUTE_NOT_FOUND_ERROR_CODE, exception.getCode());
        assertEquals(ErrorMessageUtils.ATTRIBUTE_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void validateCategoryAttribute_shouldBeValidatedSuccessfullyWhenPassedAttributeValueIsValid() {
        categoryAttributeService.validateCategoryAttributesValues(productEntity, Map.of(attributeEntity.getId(), "VALUE"));
    }

    @Test
    public void validateCategoryAttribute_shouldThrowWrongRequestBodyExceptionWhenPassedAttributeValueIsInvalid() {
        WrongRequestBodyException exception = assertThrows(WrongRequestBodyException.class, () -> categoryAttributeService.validateCategoryAttributesValues(productEntity, Map.of(attributeEntity.getId(), Boolean.FALSE)));

        assertEquals(ErrorCodeUtils.ATTRIBUTES_VALUES_ERROR_CODE, exception.getCode());
        assertEquals(ErrorMessageUtils.WRONG_ATTRIBUTE_VALUES_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void validateCategoryAttribute_shouldThrowWrongRequestBodyExceptionWhenPassedAttributeValueIsNullButThereIsRequiredAttributeToSet() {
        WrongRequestBodyException exception = assertThrows(WrongRequestBodyException.class, () -> categoryAttributeService.validateCategoryAttributesValues(productEntity, null));

        assertEquals(ErrorCodeUtils.ATTRIBUTES_VALUES_ERROR_CODE, exception.getCode());
        assertEquals(ErrorMessageUtils.REQUIRED_ATTRIBUTE_VALUE_NOT_PROVIDED_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void validateCategoryAttribute_shouldThrowWrongRequestBodyExceptionWhenPassedRequiredAttributeValusIsNotProvided() {
        WrongRequestBodyException exception = assertThrows(WrongRequestBodyException.class, () -> categoryAttributeService.validateCategoryAttributesValues(productEntity, Map.of(UUID.randomUUID(), "VALUE")));

        assertEquals(ErrorCodeUtils.ATTRIBUTES_VALUES_ERROR_CODE, exception.getCode());
        assertEquals(ErrorMessageUtils.REQUIRED_ATTRIBUTE_VALUE_NOT_PROVIDED_ERROR_MESSAGE, exception.getMessage());
    }
}
