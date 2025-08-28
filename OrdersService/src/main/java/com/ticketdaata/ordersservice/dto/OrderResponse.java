package com.ticketdaata.ordersservice.dto;

import com.ticketdaata.ordersservice.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private String id;
    private String userId;
    private String ticketId;
    private String ticketTitle;
    private String eventName;
    private String eventDate;
    private String seatInfo;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private String paymentId;
    private String sellerId;
    private String sellerUsername;
    private String cancellationReason;
    
    // Time remaining in minutes
    private Long timeRemainingMinutes;
    
    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setTicketId(order.getTicketId());
        response.setTicketTitle(order.getTicketTitle());
        response.setEventName(order.getEventName());
        response.setEventDate(order.getEventDate());
        response.setSeatInfo(order.getSeatInfo());
        response.setPrice(order.getPrice());
        response.setQuantity(order.getQuantity());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setExpiresAt(order.getExpiresAt());
        response.setPaymentId(order.getPaymentId());
        response.setSellerId(order.getSellerId());
        response.setSellerUsername(order.getSellerUsername());
        response.setCancellationReason(order.getCancellationReason());
        
        // Calculate time remaining
        if (order.getExpiresAt() != null && order.getStatus() == Order.OrderStatus.PENDING) {
            long minutes = java.time.Duration.between(LocalDateTime.now(), order.getExpiresAt()).toMinutes();
            response.setTimeRemainingMinutes(Math.max(0, minutes));
        }
        
        return response;
    }
}