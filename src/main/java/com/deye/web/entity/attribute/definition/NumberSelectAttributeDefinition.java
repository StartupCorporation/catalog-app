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
    private List<Integer> values;

    @Override
    public AttributeTypeEnum getAttributeType() {
        return AttributeTypeEnum.NUMBER_SELECT;
    }
}
