package com.deye.web.entity.attribute.definition;

import com.deye.web.enumerated.AttributeTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public abstract class AttributeDefinition {

    @JsonIgnore
    public abstract AttributeTypeEnum getAttributeType();

    @JsonIgnore
    public boolean validateAttributeValue(Object value, boolean isRequiredForCategory) {
        if (isRequiredForCategory && value == null) {
            log.info("Attribute  is not valid, because it is required for specified category and the value was not provided");
            return false;
        }
        boolean isValidType = getJavaType().isInstance(value);
        if (!isValidType) {
            log.info("Attribute : is not valid, because it is not a valid type of attribute");
        }
        return isValidType;
    }

    @JsonIgnore
    protected Class<?> getJavaType() {
        return Object.class;
    }
}
