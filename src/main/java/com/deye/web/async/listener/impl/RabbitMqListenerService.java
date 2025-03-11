package com.deye.web.async.listener.impl;

import com.deye.web.async.listener.ListenerService;
import com.deye.web.async.message.OrderCreatedMessage;
import com.deye.web.async.message.OrderSubmittedMessage;
import com.deye.web.controller.dto.ReservationDto;
import com.deye.web.service.impl.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.deye.web.async.message.OrderCreatedMessage.OrderCreatedPayload;
import static com.deye.web.async.message.OrderSubmittedMessage.OrderSubmittedPayload;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqListenerService implements ListenerService {
    private final ProductService productService;

    @Override
    @RabbitListener(queues = "${rabbitmq.reservation.queue}")
    public void onOrderCreated(OrderCreatedMessage orderCreatedMessage) {
        try {
            OrderCreatedPayload orderCreatedPayload = orderCreatedMessage.getData();
            UUID orderId = orderCreatedPayload.getOrder_id();
            log.info("Starting reservations for order: {}", orderId);
            ReservationDto reservationDto = new ReservationDto(orderCreatedPayload);
            productService.reserveProducts(reservationDto);
            log.info("Finished reservations for order: {}", orderId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @Override
    public void onOrderSubmitted(OrderSubmittedMessage orderSubmittedMessage) {
        try {
            log.info("Order is submitter. Trying to decrease the products stock quantity");
            List<OrderSubmittedPayload> orderSubmittedPayload = orderSubmittedMessage.getData();
            ReservationDto reservationDto = new ReservationDto(orderSubmittedPayload);
            productService.finishReservation(reservationDto);
            log.info("Products stock quantity decreasing is finished");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
