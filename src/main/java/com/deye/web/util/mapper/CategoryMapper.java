package com.deye.web.util.mapper;

import com.deye.web.controller.dto.response.CategoryResponseDto;
import com.deye.web.controller.dto.response.ImageResponseDto;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    private final AttributeMapper attributeMapper;
    private final FileService fileService;

    public CategoryResponseDto toCategoryView(CategoryEntity category) {
        ImageResponseDto imageResponseDto = new ImageResponseDto();
        imageResponseDto.setId(category.getImageId());
        imageResponseDto.setLink(fileService.getAccessLink(category.getImageDirectory(), category.getImageName()));
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .image(imageResponseDto)
                .attributes(category.getCategoryAttributes().stream()
                        .map(categoryAttributeEntity -> attributeMapper.toCategoryAttributeView(categoryAttributeEntity.getAttribute()))
                        .toList())
                .build();
    }
}
