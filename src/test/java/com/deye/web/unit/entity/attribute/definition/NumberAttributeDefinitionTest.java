package com.deye.web.unit.entity.attribute.definition;

import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.deye.web.entity.attribute.definition.NumberAttributeDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumberAttributeDefinitionTest {

    static Stream<Arguments> provideNumbers() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3),
                Arguments.of(4)
        );
    }

    @ParameterizedTest
    @MethodSource("provideNumbers")
    void validateAttributeValue_shouldReturnTrueWhenProvidedValueHasNumberAttributeDefinition(Number value) {
        AttributeDefinition attributeDefinition = new NumberAttributeDefinition();
        assertTrue(attributeDefinition.validateAttributeValue(value, Boolean.TRUE));
    }


    @Test
    public void validateAttributeValue_shouldReturnFalseWhenProvidedValueHasDifferentTypeComparingNumberAttributeDefinition() {
        AttributeDefinition attributeDefinition = new NumberAttributeDefinition();
        assertFalse(attributeDefinition.validateAttributeValue("VALUE", Boolean.TRUE));
    }
}
