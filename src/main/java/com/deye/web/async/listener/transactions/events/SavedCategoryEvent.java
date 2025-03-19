package com.deye.web.async.listener.transactions.events;

import com.deye.web.controller.dto.CreateImageDto;
import com.deye.web.controller.dto.UpdateImageDto;
import com.deye.web.entity.CategoryEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavedCategoryEvent {
    private CategoryEntity category;
    private UpdateImageDto updateImageDto;
    private CreateImageDto createImageDto;

    public SavedCategoryEvent(CategoryEntity category, CreateImageDto createImageDto) {
        this.category = category;
        this.createImageDto = createImageDto;
    }

    public SavedCategoryEvent(CategoryEntity category, UpdateImageDto updateImageDto) {
        this.category = category;
        this.updateImageDto = updateImageDto;
    }
}
