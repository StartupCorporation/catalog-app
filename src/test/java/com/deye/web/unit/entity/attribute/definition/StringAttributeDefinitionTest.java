package com.deye.web.unit.entity.attribute.definition;

import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.deye.web.entity.attribute.definition.StringAttributeDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringAttributeDefinitionTest {

    @Test
    public void validateAttributeValue_shouldReturnTrueWhenProvidedValidString() {
        AttributeDefinition attributeDefinition = new StringAttributeDefinition();
        assertTrue(attributeDefinition.validateAttributeValue("Valid String", true));
    }

    @Test
    public void validateAttributeValue_shouldReturnFalseWhenProvidedNullAndIsRequired() {
        AttributeDefinition attributeDefinition = new StringAttributeDefinition();
        assertFalse(attributeDefinition.validateAttributeValue(null, true));
    }

    @Test
    public void validateAttributeValue_shouldReturnTrueWhenProvidedNullAndIsNotRequired() {
        AttributeDefinition attributeDefinition = new StringAttributeDefinition();
        assertTrue(attributeDefinition.validateAttributeValue(null, false));
    }

    @Test
    public void validateAttributeValue_shouldReturnTrueWhenProvidedIsBlankAndIsNotRequired() {
        AttributeDefinition attributeDefinition = new StringAttributeDefinition();
        assertTrue(attributeDefinition.validateAttributeValue("", false));
    }

    @Test
    public void validateAttributeValue_shouldReturnFalseWhenProvidedEmptyStringAndIsRequired() {
        AttributeDefinition attributeDefinition = new StringAttributeDefinition();
        assertFalse(attributeDefinition.validateAttributeValue("", true));
    }

    @Test
    public void validateAttributeValue_shouldReturnFalseWhenProvidedBlankStringAndIsRequired() {
        AttributeDefinition attributeDefinition = new StringAttributeDefinition();
        assertFalse(attributeDefinition.validateAttributeValue("   ", true));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidValues")
    public void validateAttributeValue_shouldReturnFalseWhenProvidedNonStringValue(Object value) {
        AttributeDefinition attributeDefinition = new StringAttributeDefinition();
        assertFalse(attributeDefinition.validateAttributeValue(value, true));
    }

    static Stream<Arguments> provideInvalidValues() {
        return Stream.of(
                Arguments.of(123),
                Arguments.of(45.6),
                Arguments.of(true),
                Arguments.of(new Object())
        );
    }
}
