package com.deye.web.controller.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductShortResponseDto {
    private UUID id;
    private String name;
    private Integer price;
}
