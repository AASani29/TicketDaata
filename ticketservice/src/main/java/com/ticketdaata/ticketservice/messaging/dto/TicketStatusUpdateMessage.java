package com.ticketdaata.ticketservice.messaging.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusUpdateMessage {
    private String ticketId;
    private String orderId;
    private String status; // AVAILABLE, RESERVED, SOLD
    private String previousStatus;
    private String userId;
    private LocalDateTime timestamp;
    private String eventType; // TICKET_RESERVED, TICKET_RELEASED, TICKET_SOLD
}