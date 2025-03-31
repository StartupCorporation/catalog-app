package com.deye.web.unit.entity.attribute.definition;

import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.deye.web.entity.attribute.definition.CheckboxAttributeDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckboxAttributeDefinitionTest {

    @Test
    public void validateAttributeValue_shouldReturnTrueWhenProvidedValueHasCheckboxAttributeDefinition() {
        AttributeDefinition attributeDefinition = new CheckboxAttributeDefinition();
        assertTrue(attributeDefinition.validateAttributeValue(Boolean.TRUE, Boolean.TRUE));
    }

    @Test
    public void validateAttributeValue_shouldReturnTrueWhenProvidedValueIsNullAndIsNotRequiredForCheckboxAttributeDefinition() {
        AttributeDefinition attributeDefinition = new CheckboxAttributeDefinition();
        assertTrue(attributeDefinition.validateAttributeValue(null, Boolean.FALSE));
    }

    @Test
    public void validateAttributeValue_shouldReturnFalseWhenProvidedValueIsNullAndIsRequiredForCheckboxAttributeDefinition() {
        AttributeDefinition attributeDefinition = new CheckboxAttributeDefinition();
        assertFalse(attributeDefinition.validateAttributeValue(null, Boolean.TRUE));
    }

    @Test
    public void validateAttributeValue_shouldReturnFalseWhenProvidedValueHasDifferentTypeComparingCheckboxAttributeDefinition() {
        AttributeDefinition attributeDefinition = new CheckboxAttributeDefinition();
        assertFalse(attributeDefinition.validateAttributeValue("VALUE", Boolean.TRUE));
    }
}
