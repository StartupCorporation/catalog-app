package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@Slf4j
public class StringAttributeDefinition extends AttributeDefinition {

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.STRING;
    }

    @Override
    public boolean validateAttributeValue(Object value, boolean isRequiredForCategory) {
        boolean isValid = super.validateAttributeValue(value, isRequiredForCategory);
        if (!isValid) {
            return false;
        }
        if (isRequiredForCategory && StringUtils.isBlank((String) value)) {
            log.info("String attribute is not valid, because it is required for specified category, but provided instance is blank");
            return false;
        }
        return true;
    }

    @Override
    public Class getJavaType() {
        return String.class;
    }
}
