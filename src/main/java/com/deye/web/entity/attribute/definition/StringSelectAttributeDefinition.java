package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Setter
@Slf4j
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
            log.info("Attribute is not valid, because it is required for specified category and the value was not provided");
            return false;
        }
        if (value != null && !(value instanceof String)) {
            log.info("Attribute is not valid, because it should be a string, but is {}", value.getClass());
            return false;
        }

        if (isRequiredForCategory && StringUtils.isBlank((String) value)) {
            log.info("String attribute is not valid, because it is required for specified category, but provided instance is blank");
            return false;
        }

        if (value != null) {
            String stringValue = (String) value;
            boolean contain = this.values.contains(stringValue);
            if (!contain) {
                log.info("String select attribute does not contain provided value: {}", value);
            }
            return contain;
        }
        return true;
    }
}
