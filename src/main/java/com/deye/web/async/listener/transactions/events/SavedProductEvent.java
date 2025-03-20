package com.deye.web.async.listener.transactions.events;

import com.deye.web.controller.dto.CreateImageDto;
import com.deye.web.controller.dto.DeleteImageDto;
import com.deye.web.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SavedProductEvent {
    private ProductEntity product;
    private List<CreateImageDto> createImages;
    private List<DeleteImageDto> deleteImages;
}
