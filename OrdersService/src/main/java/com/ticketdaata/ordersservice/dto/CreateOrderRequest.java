package com.ticketdaata.ordersservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Ticket ID is required")
    private String ticketId;
    
    @NotBlank(message = "Ticket title is required")
    private String ticketTitle;
    
    @NotBlank(message = "Event name is required")
    private String eventName;
    
    @NotBlank(message = "Event date is required")
    private String eventDate;
    
    private String seatInfo;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotBlank(message = "Seller ID is required")
    private String sellerId;
    
    @NotBlank(message = "Seller username is required")
    private String sellerUsername;
}