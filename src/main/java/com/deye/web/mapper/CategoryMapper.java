package com.deye.web.mapper;

import com.deye.web.controller.view.CategoryView;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.service.impl.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    private final MinioService minioService;


    public CategoryView toCategoryView(CategoryEntity category) {
        return CategoryView.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageLink(minioService.generateFileLink(category.getImage().getName()))
                .build();
    }
}
