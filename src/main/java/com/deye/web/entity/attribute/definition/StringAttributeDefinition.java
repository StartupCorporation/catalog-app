package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringAttributeDefinition extends AttributeDefinition {

    @NotBlank(message = "Attribute value can't be blank")
    private String value;

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.STRING;
    }
}
