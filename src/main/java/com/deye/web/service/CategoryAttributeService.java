package com.deye.web.service;

import com.deye.web.controller.dto.CategoryAttributeDto;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.CategoryAttributeEntity;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.entity.ProductEntity;
import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.exception.WrongRequestBodyException;
import com.deye.web.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.deye.web.util.error.ErrorCodeUtils.*;
import static com.deye.web.util.error.ErrorMessageUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryAttributeService {
    private final AttributeRepository attributeRepository;

    public CategoryAttributeEntity getCategoryAttribute(CategoryEntity category, UUID attributeId) {
        Optional<CategoryAttributeEntity> categoryAttributeOpt = category.getCategoryAttributes().stream()
                .filter(categoryAttr -> categoryAttr.getAttribute().getId().equals(attributeId))
                .findFirst();
        if (categoryAttributeOpt.isEmpty()) {
            throw new EntityNotFoundException(CATEGORY_ATTRIBUTE_NOT_FOUND_ERROR_CODE, CATEGORY_ATTRIBUTE_NOT_FOUND_ERROR_MESSAGE);
        }
        return categoryAttributeOpt.get();
    }

    public void addAttributesToCategory(CategoryEntity category, List<CategoryAttributeDto> attributesDto) {
        log.info("Adding attributes to category: {}", category.getName());
        if (attributesDto != null && !attributesDto.isEmpty()) {
            List<UUID> attributesIds = attributesDto.stream()
                    .map(CategoryAttributeDto::getId)
                    .toList();

            List<AttributeEntity> attributes = attributeRepository.findAllById(attributesIds);

            if (attributes.size() != attributesIds.size()) {
                throw new EntityNotFoundException(ATTRIBUTE_NOT_FOUND_ERROR_CODE, ATTRIBUTE_NOT_FOUND_ERROR_MESSAGE);
            }

            for (AttributeEntity attribute : attributes) {
                CategoryAttributeDto attributeDto = attributesDto.stream()
                        .filter(attrDto -> attrDto.getId().equals(attribute.getId()))
                        .findAny()
                        .get();
                CategoryAttributeEntity categoryAttribute = new CategoryAttributeEntity();
                categoryAttribute.setCategory(category);
                categoryAttribute.setAttribute(attribute);
                categoryAttribute.setFilterable(attributeDto.getIsFilterable());
                categoryAttribute.setRequired(attributeDto.getIsRequired());
                category.getCategoryAttributes().add(categoryAttribute);
            }
            log.info("Added attributes to category: {}", category.getName());
        }
    }

    public void validateCategoryAttributesValues(ProductEntity product, Map<UUID, Object> attributesValues) {
        CategoryEntity category = product.getCategory();
        validateThatAllRequiredAttributesValuesAreProvided(category, attributesValues, product);
        if (attributesValues != null) {
            for (UUID attributeId : attributesValues.keySet()) {
                CategoryAttributeEntity categoryAttribute = getCategoryAttribute(category, attributeId);
                validateThatProvidedValueIsValid(categoryAttribute, attributesValues.get(attributeId));
            }
        }
    }

    private void validateThatProvidedValueIsValid(CategoryAttributeEntity categoryAttribute, Object attributesValue) {
        AttributeDefinition attributeDefinition = categoryAttribute.getAttribute().getDefinition();
        boolean isValidAttributeValue = attributeDefinition.validateAttributeValue(attributesValue, categoryAttribute.isRequired());
        if (!isValidAttributeValue) {
            throw new WrongRequestBodyException(ATTRIBUTES_VALUES_ERROR_CODE, WRONG_ATTRIBUTE_VALUES_ERROR_MESSAGE);
        }
    }

    private void validateThatAllRequiredAttributesValuesAreProvided(CategoryEntity category, Map<UUID, Object> attributesValues, ProductEntity product) {
        Set<UUID> requiredAttributesIds = category.getCategoryAttributes().stream()
                .filter(CategoryAttributeEntity::isRequired)
                .map(categoryAttribute -> categoryAttribute.getAttribute().getId())
                .collect(Collectors.toSet());
        Set<UUID> attributesIdsTharAreAlreadySet = product.getAttributesValuesForProduct().stream()
                .map(attributeProductValue -> attributeProductValue.getAttribute().getId())
                .collect(Collectors.toSet());
        requiredAttributesIds.removeAll(attributesIdsTharAreAlreadySet);
        if (!requiredAttributesIds.isEmpty()) {
            if (attributesValues == null) {
                throw new WrongRequestBodyException(ATTRIBUTES_VALUES_ERROR_CODE, REQUIRED_ATTRIBUTE_VALUE_NOT_PROVIDED_ERROR_MESSAGE);
            }
            boolean allRequiredAttributesAreSet = attributesValues.keySet().containsAll(requiredAttributesIds);
            if (!allRequiredAttributesAreSet) {
                throw new WrongRequestBodyException(ATTRIBUTES_VALUES_ERROR_CODE, REQUIRED_ATTRIBUTE_VALUE_NOT_PROVIDED_ERROR_MESSAGE);
            }
        }
    }
}
