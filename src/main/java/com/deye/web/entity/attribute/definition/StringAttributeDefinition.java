package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
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
            return false;
        }
        return true;
    }

    @Override
    public Object getJavaType() {
        return String.class;
    }
}
