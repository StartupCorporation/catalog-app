package com.deye.web.unit.service.factories;

import com.deye.web.controller.dto.CreateAttributeDto;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.attribute.definition.StringAttributeDefinition;

import java.util.UUID;

public class AttributeTestFactory {

    public static CreateAttributeDto createCreateAttributeDto() {
        CreateAttributeDto createAttributeDto = new CreateAttributeDto();
        createAttributeDto.setName("name");
        createAttributeDto.setDescription("description");
        createAttributeDto.setDefinition(new StringAttributeDefinition());
        return createAttributeDto;
    }

    public static AttributeEntity createAttributeEntity() {
        AttributeEntity attributeEntity = new AttributeEntity();
        attributeEntity.setId(UUID.randomUUID());
        attributeEntity.setName("name");
        attributeEntity.setDescription("description");
        attributeEntity.setDefinition(new StringAttributeDefinition());
        return attributeEntity;
    }
}
