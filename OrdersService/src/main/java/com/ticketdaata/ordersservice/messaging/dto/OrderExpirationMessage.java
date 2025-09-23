package com.ticketdaata.ordersservice.messaging.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderExpirationMessage {
    private String orderId;
    private String ticketId;
    private String userId;
    private LocalDateTime expirationTime;
    private LocalDateTime timestamp;
    private String eventType; // ORDER_EXPIRATION_SCHEDULED
}