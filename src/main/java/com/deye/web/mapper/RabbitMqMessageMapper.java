package com.deye.web.mapper;

import com.deye.web.async.message.AskedCallbackRequestMessage;
import com.deye.web.async.message.DeletedCategoryMessage;
import com.deye.web.async.message.SavedCategoryMessage;
import com.deye.web.async.message.SavedProductMessage;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.entity.ProductEntity;
import com.deye.web.exception.EventMessageException;
import com.deye.web.utils.error.ErrorCodeUtils;
import com.deye.web.utils.error.ErrorMessageUtils;
import com.deye.web.utils.rabbitmq.RabbitMqUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.deye.web.async.message.DeletedCategoryMessage.DeleteCategoryPayload;
import static com.deye.web.async.message.SavedCategoryMessage.SavedCategoryPayload;
import static com.deye.web.async.message.SavedProductMessage.SavedProductPayload;

@Component
@RequiredArgsConstructor
public class RabbitMqMessageMapper {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqMessageMapper.class);
    private final Gson gson;

    public String toSavedCategoryMessage(CategoryEntity category) {
        // payload
        SavedCategoryPayload savedCategoryPayload = new SavedCategoryPayload();
        savedCategoryPayload.setId(category.getId());
        savedCategoryPayload.setName(category.getName());
        savedCategoryPayload.setDescription(category.getDescription());
        savedCategoryPayload.setImage(category.getImage().getName());

        // message
        SavedCategoryMessage savedCategoryMessage = new SavedCategoryMessage();
        savedCategoryMessage.setId(UUID.randomUUID());
        savedCategoryMessage.setCreated_at(LocalDateTime.now());
        savedCategoryMessage.setEvent_type(RabbitMqUtil.CATEGORY_SAVED_EVENT);
        savedCategoryMessage.setData(savedCategoryPayload);

        return gson.toJson(savedCategoryMessage);
    }

    public String toDeleteCategoryMessage(UUID categoryId) {
        // payload
        DeleteCategoryPayload deleteCategoryPayload = new DeleteCategoryPayload();
        deleteCategoryPayload.setId(categoryId);

        // message
        DeletedCategoryMessage deletedCategoryMessage = new DeletedCategoryMessage();
        deletedCategoryMessage.setId(UUID.randomUUID());
        deletedCategoryMessage.setCreated_at(LocalDateTime.now());
        deletedCategoryMessage.setEvent_type(RabbitMqUtil.CATEGORY_DELETED_EVENT);
        deletedCategoryMessage.setData(deleteCategoryPayload);
        return gson.toJson(deletedCategoryMessage);
    }

    public AskedCallbackRequestMessage toAskedCallbackRequestMessage(String message) {
        try {
            return gson.fromJson(message, AskedCallbackRequestMessage.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new EventMessageException(ErrorCodeUtils.EVENT_MESSAGE_NOT_PROCEED_ERROR_CODE, ErrorMessageUtils.EVENT_MESSAGE_NOT_PROCEED_ERROR_MESSAGE);
        }
    }

    public String toSavedProductMessage(ProductEntity product) {
        // payload
        SavedProductPayload savedProductPayload = new SavedProductPayload();
        savedProductPayload.setId(product.getId());
        savedProductPayload.setName(product.getName());
        savedProductPayload.setDescription(product.getDescription());
        savedProductPayload.setPrice(product.getPrice());
        savedProductPayload.setStock_quantity(product.getStockQuantity());
        savedProductPayload.setCategory_id(product.getCategory().getId());
        savedProductPayload.setImages(product.getImagesNames());

        //message
        SavedProductMessage savedProductMessage = new SavedProductMessage();
        savedProductMessage.setId(UUID.randomUUID());
        savedProductMessage.setCreated_at(LocalDateTime.now());
        savedProductMessage.setEvent_type(RabbitMqUtil.PRODUCT_SAVED_EVENT);
        savedProductMessage.setData(savedProductPayload);
        return gson.toJson(savedProductMessage);
    }

    public String toDeletedProductMessage(UUID productId) {
        DeleteCategoryPayload payload = new DeleteCategoryPayload();
        payload.setId(productId);

        DeletedCategoryMessage message = new DeletedCategoryMessage();
        message.setId(UUID.randomUUID());
        message.setCreated_at(LocalDateTime.now());
        message.setEvent_type(RabbitMqUtil.PRODUCT_DELETED_EVENT);
        message.setData(payload);
        return gson.toJson(message);
    }
}
