package com.deye.web.async.util.mapper;

import com.deye.web.async.message.DeletedProductsMessage;
import com.deye.web.async.util.RabbitMqUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RabbitMqMessageMapper {
    private final Gson gson;

    public String toDeletedProductMessage(Set<UUID> productsIds) {
        DeletedProductsMessage deletedProductsMessage = new DeletedProductsMessage();
        deletedProductsMessage.setId(UUID.randomUUID());
        deletedProductsMessage.setCreated_at(LocalDateTime.now());
        deletedProductsMessage.setEvent_type(RabbitMqUtil.PRODUCTS_DELETED_EVENT);
        DeletedProductsMessage.DeletedProductPayload deletedProductsPayload = new DeletedProductsMessage.DeletedProductPayload();
        deletedProductsPayload.setIds(productsIds);
        deletedProductsMessage.setData(deletedProductsPayload);
        return gson.toJson(deletedProductsMessage);
    }
}
