package com.ticketdaata.ordersservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private String id;
    private String eventName;
    private String category;
    private String location;
    private LocalDateTime eventDate;
    private String seatInfo;
    private Double price;
    private String status; // AVAILABLE, RESERVED, SOLD
    private String userId; // Owner/Creator of the ticket
    private Long sellerId;
    private Long version;
}