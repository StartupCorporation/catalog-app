package com.deye.web.unit.entity.attribute.definition;

import com.deye.web.entity.attribute.definition.StringSelectAttributeDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringSelectAttributeDefinitionTest {

    @Test
    public void validateAttributeValue_shouldReturnTrueWhenValueExistsInAllowedValues() {
        StringSelectAttributeDefinition attributeDefinition = new StringSelectAttributeDefinition();
        attributeDefinition.setValues(List.of("Option1", "Option2", "Option3"));

        assertTrue(attributeDefinition.validateAttributeValue("Option1", true));
    }

    @Test
    public void validateAttributeValue_shouldReturnFalseWhenValueIsNotInAllowedValues() {
        StringSelectAttributeDefinition attributeDefinition = new StringSelectAttributeDefinition();
        attributeDefinition.setValues(List.of("Option1", "Option2", "Option3"));

        assertFalse(attributeDefinition.validateAttributeValue("InvalidOption", true));
    }

    @Test
    public void validateAttributeValue_shouldReturnFalseWhenValueIsNullAndRequired() {
        StringSelectAttributeDefinition attributeDefinition = new StringSelectAttributeDefinition();
        attributeDefinition.setValues(List.of("Option1", "Option2", "Option3"));

        assertFalse(attributeDefinition.validateAttributeValue(null, true));
    }

    @Test
    public void validateAttributeValue_shouldReturnTrueWhenValueIsNullAndNotRequired() {
        StringSelectAttributeDefinition attributeDefinition = new StringSelectAttributeDefinition();
        attributeDefinition.setValues(List.of("Option1", "Option2", "Option3"));

        assertTrue(attributeDefinition.validateAttributeValue(null, false));
    }

    @Test
    public void validateAttributeValue_shouldReturnFalseWhenValueIsBlankAndRequired() {
        StringSelectAttributeDefinition attributeDefinition = new StringSelectAttributeDefinition();
        attributeDefinition.setValues(List.of("Option1", "Option2", "Option3"));

        assertFalse(attributeDefinition.validateAttributeValue("   ", true));
    }

    @Test
    public void validateAttributeValue_shouldReturnFalseWhenValueIsNotAString() {
        StringSelectAttributeDefinition attributeDefinition = new StringSelectAttributeDefinition();
        attributeDefinition.setValues(List.of("Option1", "Option2", "Option3"));

        assertFalse(attributeDefinition.validateAttributeValue(123, true));
    }

    @ParameterizedTest
    @MethodSource("provideValidValues")
    void validateAttributeValue_shouldReturnTrueForValidValues(String value) {
        StringSelectAttributeDefinition attributeDefinition = new StringSelectAttributeDefinition();
        attributeDefinition.setValues(List.of("Option1", "Option2", "Option3"));

        assertTrue(attributeDefinition.validateAttributeValue(value, true));
    }

    static Stream<Arguments> provideValidValues() {
        return Stream.of(
                Arguments.of("Option1"),
                Arguments.of("Option2"),
                Arguments.of("Option3")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidValuesForStringSelect")
    void validateAttributeValue_shouldReturnFalseForInvalidValues(Object value) {
        StringSelectAttributeDefinition attributeDefinition = new StringSelectAttributeDefinition();
        attributeDefinition.setValues(List.of("Option1", "Option2", "Option3"));

        assertFalse(attributeDefinition.validateAttributeValue(value, true));
    }

    static Stream<Arguments> provideInvalidValuesForStringSelect() {
        return Stream.of(
                Arguments.of("InvalidOption"),
                Arguments.of(123),
                Arguments.of("   ")
        );
    }
}
