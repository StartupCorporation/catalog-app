package com.deye.web.service;

import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.AttributeProductValuesEntity;
import com.deye.web.entity.CategoryAttributeEntity;
import com.deye.web.entity.ProductEntity;
import com.deye.web.exception.ActionNotAllowedException;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductAttributeService {

    public void addAttributeValue(ProductEntity product, AttributeEntity attribute, Object value) {
        AttributeProductValuesEntity attributeProductValue = new AttributeProductValuesEntity();
        attributeProductValue.setProduct(product);
        attributeProductValue.setAttribute(attribute);
        attributeProductValue.setValue(value);
        Set<AttributeProductValuesEntity> attributesValuesForProduct = product.getAttributesValuesForProduct();
        AttributeProductValuesEntity existedAttributeValue = attributesValuesForProduct.stream()
                .filter(attributeValueForProduct -> attributeValueForProduct.equals(attributeProductValue))
                .findAny()
                .orElse(null);
        if (existedAttributeValue != null && !existedAttributeValue.getValue().equals(value)) {
            existedAttributeValue.setValue(attributeProductValue.getValue());
        } else {
            attributesValuesForProduct.add(attributeProductValue);
        }
    }

    public void removeAttributeValue(ProductEntity product, AttributeEntity attribute) {
        Optional<CategoryAttributeEntity> categoryAttributeOpt = product.getCategory().getCategoryAttributes().stream()
                .filter(categoryAttr -> categoryAttr.getAttribute().equals(attribute))
                .findAny();
        if (categoryAttributeOpt.isEmpty()) {
            throw new EntityNotFoundException(ErrorCodeUtils.ATTRIBUTE_NOT_FOUND_ERROR_CODE, ErrorMessageUtils.ATTRIBUTE_TO_DELETE_WAS_NOT_FOUND_ERROR_MESSAGE);
        }
        CategoryAttributeEntity categoryAttribute = categoryAttributeOpt.get();
        if (categoryAttribute.isRequired()) {
            throw new ActionNotAllowedException(ErrorCodeUtils.ACTION_NOT_ALLOWED_ERROR_CODE, ErrorMessageUtils.ATTRIBUTE_DELETE_ACTION_NOT_ALLOWED_ERROR_MESSAGE);
        }
        Set<AttributeProductValuesEntity> attributesValuesForProduct = product.getAttributesValuesForProduct();
        AttributeProductValuesEntity attributeProductValue = attributesValuesForProduct.stream()
                .filter(attributeProduct -> attributeProduct.getAttribute().equals(attribute))
                .findAny()
                .orElse(null);
        if (attributeProductValue != null) {
            attributesValuesForProduct.remove(attributeProductValue);
        }
    }
}
