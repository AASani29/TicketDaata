package com.ticketdaata.ticketservice.messaging.publisher;

import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "messaging.mode", havingValue = "inmemory", matchIfMissing = true)
public class InMemoryTicketEventPublisher implements TicketEventPublisherInterface {

    public void publishTicketReserved(String ticketId, String orderId, String userId) {
        log.info("📤 [InMemory] Published ticket reserved event for ticket: {}, order: {}", ticketId, orderId);
    }

    public void publishTicketReleased(String ticketId, String orderId, String userId) {
        log.info("📤 [InMemory] Published ticket released event for ticket: {}, order: {}", ticketId, orderId);
    }

    public void publishTicketSold(String ticketId, String orderId, String userId) {
        log.info("📤 [InMemory] Published ticket sold event for ticket: {}, order: {}", ticketId, orderId);
    }
}