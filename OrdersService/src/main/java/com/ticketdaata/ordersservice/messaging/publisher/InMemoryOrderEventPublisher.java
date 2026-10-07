package com.ticketdaata.ordersservice.messaging.publisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "messaging.mode", havingValue = "inmemory", matchIfMissing = true)
public class InMemoryOrderEventPublisher implements OrderEventPublisherInterface {

    @Override
    public void publishOrderCreated(String orderId, String ticketId, String userId, BigDecimal totalAmount) {
        log.info("📤 [InMemory] Published order created event for order: {}, ticket: {}, user: {}, amount: {}", 
                orderId, ticketId, userId, totalAmount);
    }

    @Override
    public void publishOrderCompleted(String orderId, String ticketId, String userId, String sellerId, BigDecimal totalAmount) {
        log.info("📤 [InMemory] Published order completed event for order: {}, ticket: {}, user: {}, seller: {}, amount: {}",
                orderId, ticketId, userId, sellerId, totalAmount);
    }

    @Override
    public void publishOrderCancelled(String orderId, String ticketId, String userId, String reason) {
        log.info("📤 [InMemory] Published order cancelled event for order: {}, ticket: {}, user: {}, reason: {}", 
                orderId, ticketId, userId, reason);
    }

    @Override
    public void publishOrderExpired(String orderId, String ticketId, String userId) {
        log.info("📤 [InMemory] Published order expired event for order: {}, ticket: {}, user: {}", 
                orderId, ticketId, userId);
    }

    @Override
    public void publishTicketReservationRequest(String ticketId, String orderId, String userId, Long version) {
        log.info("📤 [InMemory] Published ticket reservation request for ticket: {}, order: {}, user: {}, version: {}", 
                ticketId, orderId, userId, version);
    }

    @Override
    public void publishTicketReleaseRequest(String ticketId, String orderId, String userId, String reason) {
        log.info("📤 [InMemory] Published ticket release request for ticket: {}, order: {}, user: {}, reason: {}", 
                ticketId, orderId, userId, reason);
    }

    @Override
    public void publishTicketSoldRequest(String ticketId, String orderId, String userId) {
        log.info("📤 [InMemory] Published ticket sold request for ticket: {}, order: {}, user: {}", 
                ticketId, orderId, userId);
    }

    @Override
    public void scheduleOrderExpiration(String orderId, String ticketId, String userId, LocalDateTime expirationTime) {
        log.info("📤 [InMemory] Scheduled order expiration for order: {}, ticket: {}, user: {}, expiration: {}", 
                orderId, ticketId, userId, expirationTime);
    }
}