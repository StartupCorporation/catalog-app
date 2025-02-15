package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "attributeType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CheckboxAttributeDefinition.class, name = "CHECKBOX"),
        @JsonSubTypes.Type(value = NumberAttributeDefinition.class, name = "NUMBER"),
        @JsonSubTypes.Type(value = StringAttributeDefinition.class, name = "STRING"),
        @JsonSubTypes.Type(value = NumberSelectAttributeDefinition.class, name = "NUMBER_SELECT"),
        @JsonSubTypes.Type(value = StringSelectAttributeDefinition.class, name = "STRING_SELECT"),
        @JsonSubTypes.Type(value = RangeAttributeDefinition.class, name = "RANGE")
})
public abstract class AttributeDefinition {

    @JsonIgnore
    public abstract AttributeTypeEnum getAttributeType();
}
