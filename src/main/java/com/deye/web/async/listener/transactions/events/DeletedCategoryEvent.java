package com.deye.web.async.listener.transactions.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeletedCategoryEvent {
    private UUID categoryId;
    private Map<String, List<String>> filesToRemove;
    private Set<UUID> removedProductsIds;
}
