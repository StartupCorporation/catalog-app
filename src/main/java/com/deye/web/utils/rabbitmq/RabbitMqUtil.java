package com.deye.web.utils.rabbitmq;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RabbitMqUtil {
    public static final String EXCHANGE_NAME = "exchange";
    public static final String CATEGORY_CONSUMER_ROUTING_KEY = "CATEGORY_CONSUMER_QUEUE";
    public static final String CATEGORY_UPSERT_EVENT = "CATEGORY_UPSERT";
    public static final String CATEGORY_DELETE_EVENT = "CATEGORY_DELETE";
}
