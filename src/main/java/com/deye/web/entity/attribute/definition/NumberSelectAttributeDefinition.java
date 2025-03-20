package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@Slf4j
public class NumberSelectAttributeDefinition extends AttributeDefinition {

    @NotNull(message = "Attribute values can't be null")
    @NotEmpty(message = "Please provide values")
    private List<Number> values;

    @Override
    public boolean validateAttributeValue(Object value, boolean isRequiredForCategory) {
        if (isRequiredForCategory && value == null) {
            log.info("Attribute is not valid, because it is required for specified category and the value was not provided");
            return false;
        }
        if (value != null && !(value instanceof Number)) {
            log.info("Attribute is not valid, because it should be a number, but is {}", value.getClass());
            return false;
        }

        if (value != null) {
            Number numberValue = (Number) value;
            boolean contain = this.values.contains(numberValue);
            if (!contain) {
                log.info("Number select attribute does not contain provided value: {}", value);
            }
            return contain;
        }
        return true;
    }

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.NUMBER_SELECT;
    }
}
