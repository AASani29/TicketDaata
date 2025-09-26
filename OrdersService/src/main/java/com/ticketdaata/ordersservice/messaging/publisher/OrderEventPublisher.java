package com.ticketdaata.ordersservice.messaging.publisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.ticketdaata.ordersservice.messaging.config.RabbitMQConfig;
import com.ticketdaata.ordersservice.messaging.dto.OrderExpirationMessage;
import com.ticketdaata.ordersservice.messaging.dto.OrderStatusMessage;
import com.ticketdaata.ordersservice.messaging.dto.TicketReservationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.mode", havingValue = "rabbitmq")
public class OrderEventPublisher implements OrderEventPublisherInterface {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(String orderId, String ticketId, String userId, BigDecimal totalAmount) {
        OrderStatusMessage message = OrderStatusMessage.builder()
                .orderId(orderId)
                .ticketId(ticketId)
                .userId(userId)
                .status("PENDING")
                .previousStatus("NONE")
                .totalAmount(totalAmount)
                .timestamp(LocalDateTime.now())
                .eventType("ORDER_CREATED")
                .build();

        publishOrderStatus(message, RabbitMQConfig.ORDER_CREATED_ROUTING_KEY);
        log.info("Published order created event for order: {}, ticket: {}", orderId, ticketId);
    }

    public void publishOrderCompleted(String orderId, String ticketId, String userId, BigDecimal totalAmount) {
        OrderStatusMessage message = OrderStatusMessage.builder()
                .orderId(orderId)
                .ticketId(ticketId)
                .userId(userId)
                .status("COMPLETED")
                .previousStatus("PENDING")
                .totalAmount(totalAmount)
                .timestamp(LocalDateTime.now())
                .eventType("ORDER_COMPLETED")
                .build();

        publishOrderStatus(message, RabbitMQConfig.ORDER_COMPLETED_ROUTING_KEY);
        log.info("Published order completed event for order: {}, ticket: {}", orderId, ticketId);
    }

    public void publishOrderCancelled(String orderId, String ticketId, String userId, String reason) {
        OrderStatusMessage message = OrderStatusMessage.builder()
                .orderId(orderId)
                .ticketId(ticketId)
                .userId(userId)
                .status("CANCELLED")
                .previousStatus("PENDING")
                .timestamp(LocalDateTime.now())
                .eventType("ORDER_CANCELLED")
                .reason(reason)
                .build();

        publishOrderStatus(message, RabbitMQConfig.ORDER_CANCELLED_ROUTING_KEY);
        log.info("Published order cancelled event for order: {}, ticket: {}, reason: {}", orderId, ticketId, reason);
    }

    public void publishOrderExpired(String orderId, String ticketId, String userId) {
        OrderStatusMessage message = OrderStatusMessage.builder()
                .orderId(orderId)
                .ticketId(ticketId)
                .userId(userId)
                .status("EXPIRED")
                .previousStatus("PENDING")
                .timestamp(LocalDateTime.now())
                .eventType("ORDER_EXPIRED")
                .build();

        publishOrderStatus(message, RabbitMQConfig.ORDER_EXPIRED_ROUTING_KEY);
        log.info("Published order expired event for order: {}, ticket: {}", orderId, ticketId);
    }

    public void publishTicketReservationRequest(String ticketId, String orderId, String userId, Long version) {
        TicketReservationMessage message = TicketReservationMessage.builder()
                .ticketId(ticketId)
                .orderId(orderId)
                .userId(userId)
                .version(version)
                .eventType("RESERVE_TICKET")
                .timestamp(LocalDateTime.now())
                .build();

        publishTicketReservation(message, RabbitMQConfig.TICKET_RESERVE_ROUTING_KEY);
        log.info("Published ticket reservation request for ticket: {}, order: {}", ticketId, orderId);
    }

    public void publishTicketReleaseRequest(String ticketId, String orderId, String userId, String reason) {
        TicketReservationMessage message = TicketReservationMessage.builder()
                .ticketId(ticketId)
                .orderId(orderId)
                .userId(userId)
                .eventType("RELEASE_TICKET")
                .timestamp(LocalDateTime.now())
                .reason(reason)
                .build();

        publishTicketReservation(message, RabbitMQConfig.TICKET_RELEASE_ROUTING_KEY);
        log.info("Published ticket release request for ticket: {}, order: {}, reason: {}", ticketId, orderId, reason);
    }

    public void publishTicketSoldRequest(String ticketId, String orderId, String userId) {
        TicketReservationMessage message = TicketReservationMessage.builder()
                .ticketId(ticketId)
                .orderId(orderId)
                .userId(userId)
                .eventType("MARK_SOLD")
                .timestamp(LocalDateTime.now())
                .build();

        publishTicketReservation(message, RabbitMQConfig.TICKET_SOLD_ROUTING_KEY);
        log.info("Published ticket sold request for ticket: {}, order: {}", ticketId, orderId);
    }

    public void scheduleOrderExpiration(String orderId, String ticketId, String userId, LocalDateTime expirationTime) {
        OrderExpirationMessage message = OrderExpirationMessage.builder()
                .orderId(orderId)
                .ticketId(ticketId)
                .userId(userId)
                .expirationTime(expirationTime)
                .timestamp(LocalDateTime.now())
                .eventType("ORDER_EXPIRATION_SCHEDULED")
                .build();

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_EXPIRATION_ROUTING_KEY,
                    message
            );
            log.info("Scheduled order expiration for order: {}, expiration: {}", orderId, expirationTime);
        } catch (Exception e) {
            log.error("Failed to schedule order expiration: {}", e.getMessage(), e);
        }
    }

    private void publishOrderStatus(OrderStatusMessage message, String routingKey) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    routingKey,
                    message
            );
        } catch (Exception e) {
            log.error("Failed to publish order status: {}", e.getMessage(), e);
        }
    }

    private void publishTicketReservation(TicketReservationMessage message, String routingKey) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TICKET_EXCHANGE,
                    routingKey,
                    message
            );
        } catch (Exception e) {
            log.error("Failed to publish ticket reservation: {}", e.getMessage(), e);
        }
    }
}