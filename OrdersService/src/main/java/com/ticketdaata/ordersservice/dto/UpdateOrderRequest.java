package com.ticketdaata.ordersservice.dto;

import com.ticketdaata.ordersservice.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    
    private Order.OrderStatus status;
    private String paymentId;
    private String cancellationReason;
}