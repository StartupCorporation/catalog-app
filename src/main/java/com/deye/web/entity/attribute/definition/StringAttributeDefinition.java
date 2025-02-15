package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringAttributeDefinition extends AttributeDefinition {

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.STRING;
    }
}
