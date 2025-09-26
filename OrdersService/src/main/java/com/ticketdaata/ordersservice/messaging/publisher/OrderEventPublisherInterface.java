package com.ticketdaata.ordersservice.messaging.publisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderEventPublisherInterface {
    void publishOrderCreated(String orderId, String ticketId, String userId, BigDecimal totalAmount);
    void publishOrderCompleted(String orderId, String ticketId, String userId, BigDecimal totalAmount);
    void publishOrderCancelled(String orderId, String ticketId, String userId, String reason);
    void publishOrderExpired(String orderId, String ticketId, String userId);
    void publishTicketReservationRequest(String ticketId, String orderId, String userId, Long version);
    void publishTicketReleaseRequest(String ticketId, String orderId, String userId, String reason);
    void publishTicketSoldRequest(String ticketId, String orderId, String userId);
    void scheduleOrderExpiration(String orderId, String ticketId, String userId, LocalDateTime expirationTime);
}