package com.deye.web.unit.entity.attribute.definition;

import com.deye.web.entity.attribute.definition.RangeAttributeDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RangeAttributeDefinitionTest {

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
    void validateAttributeValue_shouldReturnTrueWhenProvidedValueInRangeOfAttributeDefinition(Integer value) {
        RangeAttributeDefinition attributeDefinition = new RangeAttributeDefinition();
        attributeDefinition.setMax(5);
        attributeDefinition.setMin(0);

        assertTrue(attributeDefinition.validateAttributeValue(value, Boolean.TRUE));
    }

    @Test
    void validateAttributeValue_shouldReturnTrueWhenProvidedRangeValueInRangeOfAttributeDefinition() {
        RangeAttributeDefinition attributeDefinition = new RangeAttributeDefinition();
        attributeDefinition.setMax(5);
        attributeDefinition.setMin(0);

        assertTrue(attributeDefinition.validateAttributeValue(Map.of("min", 3, "max", 5), Boolean.TRUE));
    }

    @Test
    void validateAttributeValue_shouldReturnFalseWhenProvidedRangeValueIsNotInRangeOfAttributeDefinition() {
        RangeAttributeDefinition attributeDefinition = new RangeAttributeDefinition();
        attributeDefinition.setMax(5);
        attributeDefinition.setMin(0);

        assertFalse(attributeDefinition.validateAttributeValue(Map.of("min", 3, "max", 6), Boolean.TRUE));
    }

    @Test
    void validateAttributeValue_shouldReturnFalseWhenProvidedValueIsNotInRangeOfAttributeDefinition() {
        RangeAttributeDefinition attributeDefinition = new RangeAttributeDefinition();
        attributeDefinition.setMax(5);
        attributeDefinition.setMin(0);

        assertFalse(attributeDefinition.validateAttributeValue(6, Boolean.TRUE));
    }

    @Test
    void validateAttributeValue_shouldReturnFalseWhenProvidedValueIsNull_RangeOfAttributeDefinition() {
        RangeAttributeDefinition attributeDefinition = new RangeAttributeDefinition();
        attributeDefinition.setMax(5);
        attributeDefinition.setMin(0);

        assertFalse(attributeDefinition.validateAttributeValue(null, Boolean.TRUE));
    }

    @Test
    void validateAttributeValue_shouldReturnTrueWhenProvidedValueIsNullAndIsNotRequired_RangeOfAttributeDefinition() {
        RangeAttributeDefinition attributeDefinition = new RangeAttributeDefinition();
        attributeDefinition.setMax(5);
        attributeDefinition.setMin(0);

        assertTrue(attributeDefinition.validateAttributeValue(null, Boolean.FALSE));
    }

}
