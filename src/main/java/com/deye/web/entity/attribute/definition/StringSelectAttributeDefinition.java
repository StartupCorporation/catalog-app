package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Setter
public class StringSelectAttributeDefinition extends AttributeDefinition {

    @NotNull(message = "Attribute values can't be null")
    @NotEmpty(message = "Please provide values")
    public List<String> values;

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.STRING_SELECT;
    }

    @Override
    public boolean validateAttributeValue(Object value, boolean isRequiredForCategory) {
        if (isRequiredForCategory && value == null) {
            return false;
        }
        if (value != null && !(value instanceof String)) {
            return false;
        }

        if (isRequiredForCategory && StringUtils.isBlank((String) value)) {
            return false;
        }

        if (value != null) {
            String stringValue = (String) value;
            return this.values.contains(stringValue);
        }
        return true;
    }
}
