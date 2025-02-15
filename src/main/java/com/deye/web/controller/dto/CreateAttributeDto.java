package com.deye.web.controller.dto;

import com.deye.web.entity.attribute.definition.AttributeDefinition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAttributeDto {

    @NotBlank(message = "Attribute name can't be null or blank")
    private String name;
    private String description;

    @NotNull(message = "Attribute definition can't be null")
    @Valid
    private AttributeDefinition definition;
}
