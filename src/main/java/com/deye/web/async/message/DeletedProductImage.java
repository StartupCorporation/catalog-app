package com.deye.web.async.message;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeletedProductImage extends RabbitMqMessage {
    private DeletedProductPayload data;

    @Getter
    @Setter
    public static class DeletedProductPayload {
        private UUID id;
    }
}
