package com.ticketdaata.ordersservice.messaging.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusMessage {
    private String orderId;
    private String ticketId;
    private String userId;
    private String status; // PENDING, COMPLETED, CANCELLED, EXPIRED
    private String previousStatus;
    private BigDecimal totalAmount;
    private LocalDateTime timestamp;
    private String eventType; // ORDER_CREATED, ORDER_COMPLETED, ORDER_CANCELLED, ORDER_EXPIRED
    private String reason; // Optional reason for cancellation/expiration
}