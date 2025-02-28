package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NumberSelectAttributeDefinition extends AttributeDefinition {

    @NotNull(message = "Attribute values can't be null")
    @NotEmpty(message = "Please provide values")
    private List<Number> values;

    @Override
    public boolean validateAttributeValue(Object value, boolean isRequiredForCategory) {
        if (isRequiredForCategory && value == null) {
            return false;
        }
        if (value != null && !(value instanceof Number)) {
            return false;
        }

        if (value != null) {
            Number numberValue = (Number) value;
            return this.values.contains(numberValue);
        }
        return true;
    }

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.NUMBER_SELECT;
    }
}
