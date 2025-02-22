package com.deye.web.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceRangeFilterDto {

    @NotNull(message = "Minimum price cannot be null")
    @PositiveOrZero(message = "Minimum price must be zero or positive")
    private Float min;

    @NotNull(message = "Maximum price cannot be null")
    @PositiveOrZero(message = "Maximum price must be zero or positive")
    private Float max;
}
