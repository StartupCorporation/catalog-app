package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberAttributeDefinition extends AttributeDefinition {

    @NotNull(message = "Attribute value can't be null")
    private Double value;

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.NUMBER;
    }
}
