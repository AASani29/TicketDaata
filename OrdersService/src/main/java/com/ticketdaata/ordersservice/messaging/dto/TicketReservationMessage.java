package com.ticketdaata.ordersservice.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketReservationMessage {
    private String ticketId;
    private String orderId;
    private String userId;
    private Long version;
    private String eventType; // RESERVE_TICKET, RELEASE_TICKET, MARK_SOLD
    private LocalDateTime timestamp;
    private String reason; // Optional reason for release/cancellation
}