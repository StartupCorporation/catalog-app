package com.deye.web.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RangeDto {

    @NotNull(message = "Minimum cannot be null")
    @PositiveOrZero(message = "Minimum must be zero or positive")
    private Number min;

    @NotNull(message = "Maximum cannot be null")
    @PositiveOrZero(message = "Maximum must be zero or positive")
    private Number max;
}
