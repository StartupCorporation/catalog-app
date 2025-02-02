package com.deye.web.async;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqProducer {
    private final RabbitTemplate rabbitTemplate;

    public void send(String exchange, String routingKey, Object data) {
        rabbitTemplate.convertAndSend(exchange, routingKey, data, message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setContentType("application/json");
            return message;
        });
    }
}
