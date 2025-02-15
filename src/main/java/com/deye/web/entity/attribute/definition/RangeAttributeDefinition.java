package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RangeAttributeDefinition extends AttributeDefinition {

    @NotNull(message = "Start value can't be null")
    private Double start;

    @NotNull(message = "Start value can't be null")
    private Double end;

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.RANGE;
    }
}
