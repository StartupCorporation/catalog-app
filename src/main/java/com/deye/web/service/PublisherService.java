package com.deye.web.service;

import com.deye.web.entity.CategoryEntity;

import java.util.UUID;

/**
 * This interface provides publisher contracts for pub/sub systems
 */
public interface PublisherService {
    void onCategoryUpsert(CategoryEntity category);
    void onCategoryDelete(UUID categoryId);
}
