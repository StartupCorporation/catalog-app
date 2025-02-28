package com.deye.web.util.mapper;

import com.deye.web.controller.view.AttributeView;
import com.deye.web.controller.view.CategoryAttributeView;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.AttributeProductValuesEntity;
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

    public AttributeView toAttributeView(AttributeProductValuesEntity attributeProductValues) {
        AttributeEntity attribute = attributeProductValues.getAttribute();
        AttributeView attributeView = new AttributeView();
        attributeView.setDefinition(attribute.getDefinition());
        attributeView.setName(attribute.getName());
        attributeView.setDescription(attribute.getDescription());
        attributeView.setId(attribute.getId());
        attributeView.setValue(attributeProductValues.getValue());
        return attributeView;
    }

    public CategoryAttributeView toCategoryAttributeView(AttributeEntity attribute) {
        CategoryAttributeView attributeView = new CategoryAttributeView();
        attributeView.setDefinition(attribute.getDefinition());
        attributeView.setName(attribute.getName());
        attributeView.setId(attribute.getId());
        return attributeView;
    }
}
