package com.ticketdaata.ticketservice.dto;

import com.ticketdaata.ticketservice.entity.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {
    private String id;
    private String eventName;
    private String category;
    private String location;
    private LocalDateTime eventDate;
    private String seatInfo;
    private Double price;
    private TicketStatus status;
    private String userId;
    private Long sellerId;
    private Long version;
}
