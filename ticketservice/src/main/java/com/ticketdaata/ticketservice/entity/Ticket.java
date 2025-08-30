package com.ticketdaata.ticketservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    private String id;

    @Indexed
    private String eventName;

    @Indexed
    private String category;

    private String location;

    @Indexed
    private LocalDateTime eventDate;

    private String seatInfo;

    private Double price;

    @Indexed
    private TicketStatus status;

    @Indexed
    private Long sellerId;

    /** Optimistic locking to avoid double-sell */
    @Version
    private Long version;
}