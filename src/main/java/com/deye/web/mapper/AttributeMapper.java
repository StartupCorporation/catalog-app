package com.deye.web.mapper;

import com.deye.web.controller.view.AttributeView;
import com.deye.web.entity.AttributeEntity;
import org.springframework.stereotype.Component;

@Component
public class AttributeMapper {

    public AttributeView toAttributeView(AttributeEntity attribute) {
        AttributeView attributeView = new AttributeView();
        attributeView.setDefinition(attribute.getDefinition());
        attributeView.setName(attribute.getName());
        attributeView.setDescription(attribute.getDescription());
        attributeView.setId(attribute.getId());
        return attributeView;
    }
}
