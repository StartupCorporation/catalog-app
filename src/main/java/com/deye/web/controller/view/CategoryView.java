package com.deye.web.controller.view;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CategoryView {
    private UUID id;
    private String name;
    private String description;
    private String image;
    private List<CategoryAttributeView> attributes;
}
