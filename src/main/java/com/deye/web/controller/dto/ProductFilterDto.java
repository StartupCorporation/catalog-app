package com.deye.web.controller.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProductFilterDto {
    private String name;
    private List<UUID> categoriesIds;

    @Valid
    private PriceRangeFilterDto priceRange;
}
