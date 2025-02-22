package com.deye.web.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryAttributeDto {

    @NotNull(message = "Attribute id can't be null")
    private UUID id;
    private boolean isRequired;
    private boolean isFilterable;
}
