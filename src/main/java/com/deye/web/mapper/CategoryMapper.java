package com.deye.web.mapper;

import com.deye.web.controller.view.CategoryView;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.service.impl.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    private final ConfigService configService;

    public CategoryView toCategoryView(CategoryEntity category) {
        return CategoryView.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .image(configService.getMinioBucketName() + "/" + category.getImage().getName())
                .build();
    }
}
