package com.ticketdaata.ordersservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String ticketId;
    
    private String ticketTitle;
    private String eventName;
    private String eventDate;
    private String seatInfo;
    
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    
    @Indexed
    private OrderStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Indexed
    private LocalDateTime expiresAt;
    
    private String paymentId;
    private String cancellationReason;
    
    // Seller information
    private String sellerId;
    private String sellerUsername;
    
    public enum OrderStatus {
        PENDING,
        COMPLETED,
        CANCELLED,
        EXPIRED
    }
    
    // Helper method to check if order is expired
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    // Helper method to calculate expiration time
    public void setExpirationTime(int minutes) {
        this.expiresAt = LocalDateTime.now().plusMinutes(minutes);
    }
}