package com.deye.web.async.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RabbitMqUtil {
    public static final String EXCHANGE_NAME = "deye_web.direct";
    public static final String PRODUCTS_DELETED_EVENT = "PRODUCTS_DELETED";
    public static final String PRODUCT_CONSUMER_ROUTING_KEY = "comment.product";
}
