package com.deye.web.unit.service;

import com.deye.web.entity.*;
import com.deye.web.exception.ActionNotAllowedException;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.service.ProductAttributeService;
import com.deye.web.util.error.ErrorMessageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class ProductAttributeServiceTest {

    private final ProductAttributeService productAttributeService = new ProductAttributeService();

    @Test
    void testAddAttributeValue_NewAttributeValue() {
        // Test the scenario where the attribute value is new and should be added to the product

        // Arrange
        ProductEntity product = new ProductEntity();
        ReflectionTestUtils.setField(product, "id", UUID.randomUUID());
        Set<AttributeProductValuesEntity> attributesValues = new HashSet<>();
        ReflectionTestUtils.setField(product, "attributesValuesForProduct", attributesValues);

        AttributeEntity attribute = new AttributeEntity();
        ReflectionTestUtils.setField(attribute, "id", UUID.randomUUID());

        Object value = "New Value";

        // Act
        productAttributeService.addAttributeValue(product, attribute, value);

        // Assert
        assertEquals(1, product.getAttributesValuesForProduct().size(), "The attribute value should be added.");
        assertTrue(product.getAttributesValuesForProduct().stream().anyMatch(attr -> attr.getValue().equals(value)),
                "The added attribute value should match the expected value.");
    }

    @Test
    void testAddAttributeValue_ExistingAttributeValueWithDifferentValue() {
        // Test the scenario where an existing attribute value is updated with a new value

        // Arrange
        ProductEntity product = new ProductEntity();
        ReflectionTestUtils.setField(product, "id", UUID.randomUUID());

        AttributeEntity attribute = new AttributeEntity();
        ReflectionTestUtils.setField(attribute, "id", UUID.randomUUID());

        AttributeProductValuesEntity existingAttributeValue = new AttributeProductValuesEntity();
        existingAttributeValue.setProduct(product);
        existingAttributeValue.setAttribute(attribute);
        existingAttributeValue.setValue("Old Value");

        Set<AttributeProductValuesEntity> attributesValues = new HashSet<>();
        attributesValues.add(existingAttributeValue);
        ReflectionTestUtils.setField(product, "attributesValuesForProduct", attributesValues);

        Object newValue = "New Value";

        // Act
        productAttributeService.addAttributeValue(product, attribute, newValue);

        // Assert
        assertEquals(1, product.getAttributesValuesForProduct().size(), "The attribute value should be updated, not added.");
        assertTrue(product.getAttributesValuesForProduct().stream().anyMatch(attr -> attr.getValue().equals(newValue)),
                "The attribute value should be updated to the new value.");
    }

    @Test
    void testAddAttributeValue_ExistingAttributeValueWithSameValue() {
        // Test the scenario where an existing attribute value is the same and should not be updated

        // Arrange
        ProductEntity product = new ProductEntity();
        ReflectionTestUtils.setField(product, "id", UUID.randomUUID());

        AttributeEntity attribute = new AttributeEntity();
        ReflectionTestUtils.setField(attribute, "id", UUID.randomUUID());

        Object value = "Same Value";

        AttributeProductValuesEntity existingAttributeValue = new AttributeProductValuesEntity();
        existingAttributeValue.setProduct(product);
        existingAttributeValue.setAttribute(attribute);
        existingAttributeValue.setValue(value);

        Set<AttributeProductValuesEntity> attributesValues = new HashSet<>();
        attributesValues.add(existingAttributeValue);
        ReflectionTestUtils.setField(product, "attributesValuesForProduct", attributesValues);

        // Act
        productAttributeService.addAttributeValue(product, attribute, value);

        // Assert
        assertEquals(1, product.getAttributesValuesForProduct().size(), "No new attribute value should be added.");
        assertTrue(product.getAttributesValuesForProduct().stream().anyMatch(attr -> attr.getValue().equals(value)),
                "The existing attribute value should remain unchanged.");
    }

    // Test case for when the attribute is not found in the category
    @Test
    void testRemoveAttributeValue_AttributeNotFound() {
        // Arrange
        ProductEntity product = new ProductEntity();
        CategoryEntity category = new CategoryEntity();
        product.setCategory(category);
        category.setCategoryAttributes(new HashSet<>()); // No attributes in the category

        AttributeEntity attribute = new AttributeEntity();

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                productAttributeService.removeAttributeValue(product, attribute));

        // Verify the exception message
        assertEquals(ErrorMessageUtils.ATTRIBUTE_TO_DELETE_WAS_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    // Test case for when the attribute is required and cannot be removed
    @Test
    void testRemoveAttributeValue_AttributeIsRequired() {
        // Arrange
        ProductEntity product = new ProductEntity();
        CategoryEntity category = new CategoryEntity();
        product.setCategory(category);

        AttributeEntity attribute = new AttributeEntity();
        CategoryAttributeEntity categoryAttribute = new CategoryAttributeEntity();
        categoryAttribute.setAttribute(attribute);
        categoryAttribute.setRequired(true); // Attribute is required

        Set<CategoryAttributeEntity> categoryAttributes = new HashSet<>();
        categoryAttributes.add(categoryAttribute);
        category.setCategoryAttributes(categoryAttributes);

        // Act & Assert
        ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class, () ->
                productAttributeService.removeAttributeValue(product, attribute));

        // Verify the exception message
        assertEquals(ErrorMessageUtils.ATTRIBUTE_DELETE_ACTION_NOT_ALLOWED_ERROR_MESSAGE, exception.getMessage());
    }

    // Test case for successful removal of the attribute value
    @Test
    void testRemoveAttributeValue_SuccessfulRemoval() {
        // Arrange
        ProductEntity product = new ProductEntity();
        CategoryEntity category = new CategoryEntity();
        product.setCategory(category);

        AttributeEntity attribute = new AttributeEntity();
        CategoryAttributeEntity categoryAttribute = new CategoryAttributeEntity();
        categoryAttribute.setAttribute(attribute);
        categoryAttribute.setRequired(false); // Attribute is not required

        Set<CategoryAttributeEntity> categoryAttributes = new HashSet<>();
        categoryAttributes.add(categoryAttribute);
        category.setCategoryAttributes(categoryAttributes);

        AttributeProductValuesEntity attributeProductValue = new AttributeProductValuesEntity();
        attributeProductValue.setAttribute(attribute);
        Set<AttributeProductValuesEntity> attributesValuesForProduct = new HashSet<>();
        attributesValuesForProduct.add(attributeProductValue);
        ReflectionTestUtils.setField(product, "attributesValuesForProduct", attributesValuesForProduct);

        // Act
        productAttributeService.removeAttributeValue(product, attribute);

        // Assert
        assertFalse(product.getAttributesValuesForProduct().contains(attributeProductValue), "Attribute value should be removed");
    }

    // Test case for when the attribute value is not present in the product
    @Test
    void testRemoveAttributeValue_AttributeValueNotPresent() {
        // Arrange
        ProductEntity product = new ProductEntity();
        CategoryEntity category = new CategoryEntity();
        product.setCategory(category);

        AttributeEntity attribute = new AttributeEntity();
        CategoryAttributeEntity categoryAttribute = new CategoryAttributeEntity();
        categoryAttribute.setAttribute(attribute);
        categoryAttribute.setRequired(false); // Attribute is not required

        Set<CategoryAttributeEntity> categoryAttributes = new HashSet<>();
        categoryAttributes.add(categoryAttribute);
        category.setCategoryAttributes(categoryAttributes);

        // No attribute values in the product
        Set<AttributeProductValuesEntity> attributesValuesForProduct = new HashSet<>();
        ReflectionTestUtils.setField(product, "attributesValuesForProduct", attributesValuesForProduct);

        // Act
        productAttributeService.removeAttributeValue(product, attribute);

        // Assert
        assertTrue(product.getAttributesValuesForProduct().isEmpty(), "No attribute value should be present");
    }
}