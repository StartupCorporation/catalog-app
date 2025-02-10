package com.deye.web.listener.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeletedProductEvent {
    private UUID productId;
    private Set<String> imageNames;
}
