package com.ticketdaata.ticketservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTicketRequest {
    private String location;
    private String seatInfo;

    @Positive(message = "Price must be positive")
    private Double price;
}
