package com.ticketdaata.ticketservice.dto;

import com.ticketdaata.ticketservice.entity.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {
    private Long id;
    private String eventName;
    private String category;
    private String location;
    private LocalDateTime eventDate;
    private String seatInfo;
    private Double price;
    private TicketStatus status;
    private Long sellerId;
    private Long version;
}
