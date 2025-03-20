package com.deye.web.async.listener.transactions.events;

import com.deye.web.controller.dto.DeleteImageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeletedProductEvent {
    private UUID productId;
    private List<DeleteImageDto> deleteImages;
}
