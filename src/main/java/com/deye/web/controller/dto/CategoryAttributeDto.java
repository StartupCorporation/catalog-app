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

    @NotNull(message = "isRequired field can't be null")
    private Boolean isRequired;

    @NotNull(message = "isFilterable field can't be null")
    private Boolean isFilterable;
}
