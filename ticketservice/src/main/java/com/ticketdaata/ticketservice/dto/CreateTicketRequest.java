package com.ticketdaata.ticketservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTicketRequest {
    @NotBlank
    private String eventName;

    @NotBlank
    private String category; // Concert, Sports, etc.

    @NotBlank
    private String location;

    @NotNull
    @Future
    private LocalDateTime eventDate;

    private String seatInfo;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    private Long sellerId; // from Auth service
}
