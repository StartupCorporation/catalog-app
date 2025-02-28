package com.deye.web.async.service.impl;

import com.deye.web.async.RabbitMqProducer;
import com.deye.web.async.service.PublisherService;
import com.deye.web.async.util.RabbitMqUtil;
import com.deye.web.async.util.mapper.RabbitMqMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMqPublisherService implements PublisherService {
    private final RabbitMqProducer rabbitMqProducer;
    private final RabbitMqMessageMapper rabbitMqMessageMapper;

    @Override
    public void onProductsDeleted(Set<UUID> productsIds) {
        String message = rabbitMqMessageMapper.toDeletedProductMessage(productsIds);
        try {
            rabbitMqProducer.send(RabbitMqUtil.EXCHANGE_NAME, RabbitMqUtil.PRODUCT_CONSUMER_ROUTING_KEY, message);
            log.info("Message on product deleted successfully sent to RabbitMQ, message:{}", message);
        } catch (Exception e) {
            log.error("Unable to send message: {} to RabbitMQ", message, e);
            throw e;
        }
    }
}
