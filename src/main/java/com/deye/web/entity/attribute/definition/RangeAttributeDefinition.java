package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@Setter
@Slf4j
public class RangeAttributeDefinition extends AttributeDefinition {

    @NotNull(message = "Minimum cannot be null")
    @PositiveOrZero(message = "Minimum must be zero or positive")
    private Number min;

    @NotNull(message = "Maximum cannot be null")
    @PositiveOrZero(message = "Maximum must be zero or positive")
    private Number max;

    @Override
    public boolean validateAttributeValue(Object value, boolean isRequiredForCategory) {
        if (isRequiredForCategory && value != null) {
            if (value instanceof Number) {
                boolean result = validateNumber((Number) value);
                if (!result) {
                    log.info("Provided value: {} is not valid for range", value);
                }
                return result;
            } else if (value instanceof Map<?, ?>) {
                boolean result = validateMap((Map<?, ?>) value);
                if (!result) {
                    log.info("Provided value: {} is not valid for range", value);
                }
                return result;
            }
        }

        return true;
    }

    private boolean validateNumber(Number value) {
        double numValue = value.doubleValue();
        double minValue = this.min.doubleValue();
        double maxValue = this.max.doubleValue();
        return minValue <= numValue && numValue <= maxValue;
    }

    private boolean validateMap(Map<?, ?> mapValue) {
        if (mapValue.containsKey("min") && mapValue.containsKey("max")) {
            Object minValue = mapValue.get("min");
            Object maxValue = mapValue.get("max");
            if (minValue instanceof Number && maxValue instanceof Number) {
                double minDouble = ((Number) minValue).doubleValue();
                double maxDouble = ((Number) maxValue).doubleValue();
                return minDouble < maxDouble && minDouble >= this.min.doubleValue() && maxDouble <= this.max.doubleValue();
            }
        }
        return false;
    }


    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.RANGE;
    }
}
