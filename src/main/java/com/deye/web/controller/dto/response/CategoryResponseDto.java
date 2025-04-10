package com.deye.web.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    private UUID id;
    private String name;
    private String description;
    private ImageResponseDto image;
    private List<CategoryAttributeResponseDto> attributes;
}
