package com.deye.web.unit.entity.attribute.definition;

import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.deye.web.entity.attribute.definition.NumberAttributeDefinition;
import com.deye.web.entity.attribute.definition.NumberSelectAttributeDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumberSelectAttributeDefinitionTest {

    static Stream<Arguments> provideNumbers() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3),
                Arguments.of(4)
        );
    }

    @Test
    void validateAttributeValue_shouldReturnTrueWhenProvidedValueIsOneOfNumberSelectAttributeDefinition() {
        NumberSelectAttributeDefinition attributeDefinition = new NumberSelectAttributeDefinition();
        attributeDefinition.setValues(List.of(1, 2, 3, 4));

        assertTrue(attributeDefinition.validateAttributeValue(3, Boolean.TRUE));
    }

    @Test
    void validateAttributeValue_shouldReturnFalseWhenProvidedValueIsNotOfFromNumberSelectAttributeDefinition() {
        NumberSelectAttributeDefinition attributeDefinition = new NumberSelectAttributeDefinition();
        attributeDefinition.setValues(List.of(1, 2, 3, 4));

        assertFalse(attributeDefinition.validateAttributeValue(5, Boolean.TRUE));
    }

    @Test
    void validateAttributeValue_shouldReturnFalseWhenProvidedValueIsNotNumberSelectAttributeDefinition() {
        NumberSelectAttributeDefinition attributeDefinition = new NumberSelectAttributeDefinition();
        attributeDefinition.setValues(List.of(1, 2, 3, 4));

        assertFalse(attributeDefinition.validateAttributeValue("VALUE", Boolean.TRUE));
    }

    @Test
    void validateAttributeValue_shouldReturnFalseWhenProvidedValueIsNullAndRequired_NumberSelectAttributeDefinition() {
        NumberSelectAttributeDefinition attributeDefinition = new NumberSelectAttributeDefinition();
        attributeDefinition.setValues(List.of(1, 2, 3, 4));

        assertFalse(attributeDefinition.validateAttributeValue(null, Boolean.TRUE));
    }

    @Test
    void validateAttributeValue_shouldReturnTrueWhenProvidedValueIsNullAndIsNotRequired_NumberSelectAttributeDefinition() {
        NumberSelectAttributeDefinition attributeDefinition = new NumberSelectAttributeDefinition();
        attributeDefinition.setValues(List.of(1, 2, 3, 4));

        assertTrue(attributeDefinition.validateAttributeValue(null, Boolean.FALSE));
    }
}
