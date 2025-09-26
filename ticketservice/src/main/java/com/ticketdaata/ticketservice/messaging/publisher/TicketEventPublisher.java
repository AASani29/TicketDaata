package com.ticketdaata.ticketservice.messaging.publisher;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.ticketdaata.ticketservice.messaging.config.RabbitMQConfig;
import com.ticketdaata.ticketservice.messaging.dto.TicketStatusUpdateMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.mode", havingValue = "rabbitmq")
public class TicketEventPublisher implements TicketEventPublisherInterface {

    private final RabbitTemplate rabbitTemplate;

    public void publishTicketReserved(String ticketId, String orderId, String userId) {
        TicketStatusUpdateMessage message = TicketStatusUpdateMessage.builder()
                .ticketId(ticketId)
                .orderId(orderId)
                .status("RESERVED")
                .previousStatus("AVAILABLE")
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .eventType("TICKET_RESERVED")
                .build();

        publishTicketStatusUpdate(message);
        log.info("Published ticket reserved event for ticket: {}, order: {}", ticketId, orderId);
    }

    public void publishTicketReleased(String ticketId, String orderId, String userId) {
        TicketStatusUpdateMessage message = TicketStatusUpdateMessage.builder()
                .ticketId(ticketId)
                .orderId(orderId)
                .status("AVAILABLE")
                .previousStatus("RESERVED")
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .eventType("TICKET_RELEASED")
                .build();

        publishTicketStatusUpdate(message);
        log.info("Published ticket released event for ticket: {}, order: {}", ticketId, orderId);
    }

    public void publishTicketSold(String ticketId, String orderId, String userId) {
        TicketStatusUpdateMessage message = TicketStatusUpdateMessage.builder()
                .ticketId(ticketId)
                .orderId(orderId)
                .status("SOLD")
                .previousStatus("RESERVED")
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .eventType("TICKET_SOLD")
                .build();

        publishTicketStatusUpdate(message);
        log.info("Published ticket sold event for ticket: {}, order: {}", ticketId, orderId);
    }

    private void publishTicketStatusUpdate(TicketStatusUpdateMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TICKET_EXCHANGE,
                    RabbitMQConfig.TICKET_STATUS_UPDATE_ROUTING_KEY,
                    message
            );
        } catch (Exception e) {
            log.error("Failed to publish ticket status update: {}", e.getMessage(), e);
            // In a production system, you might want to implement retry logic or dead letter queues
        }
    }
}