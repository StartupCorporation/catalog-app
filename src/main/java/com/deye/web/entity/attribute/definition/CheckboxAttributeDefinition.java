package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckboxAttributeDefinition extends AttributeDefinition {
    private boolean value;

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.CHECKBOX;
    }
}
