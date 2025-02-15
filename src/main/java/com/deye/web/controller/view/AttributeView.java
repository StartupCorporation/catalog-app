package com.deye.web.controller.view;

import com.deye.web.entity.attribute.definition.AttributeDefinition;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AttributeView {
    private UUID id;
    private String name;
    private String description;
    private AttributeDefinition attributeDefinition;
}
