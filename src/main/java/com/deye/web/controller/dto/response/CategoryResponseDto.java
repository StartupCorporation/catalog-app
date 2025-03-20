package com.deye.web.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CategoryResponseDto {
    private UUID id;
    private String name;
    private String description;
    private ImageResponseDto image;
    private List<CategoryAttributeResponseDto> attributes;
}
