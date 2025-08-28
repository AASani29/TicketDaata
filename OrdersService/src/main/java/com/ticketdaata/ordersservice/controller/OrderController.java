package com.ticketdaata.ordersservice.controller;

import com.ticketdaata.ordersservice.dto.CreateOrderRequest;
import com.ticketdaata.ordersservice.dto.OrderResponse;
import com.ticketdaata.ordersservice.dto.UpdateOrderRequest;
import com.ticketdaata.ordersservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderResponse order = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable String orderId) {
        try {
            OrderResponse order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error fetching order {}: {}", orderId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable String userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OrderResponse>> getSellerOrders(@PathVariable String sellerId) {
        List<OrderResponse> orders = orderService.getOrdersBySellerId(sellerId);
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable String orderId, 
                                        @Valid @RequestBody UpdateOrderRequest request) {
        try {
            OrderResponse order = orderService.updateOrder(orderId, request);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error updating order {}: {}", orderId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable String orderId, 
                                          @RequestBody Map<String, String> payload) {
        try {
            String paymentId = payload.get("paymentId");
            if (paymentId == null || paymentId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Payment ID is required"));
            }
            
            OrderResponse order = orderService.completeOrder(orderId, paymentId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error completing order {}: {}", orderId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId, 
                                        @RequestBody Map<String, String> payload) {
        try {
            String reason = payload.getOrDefault("reason", "Cancelled by user");
            OrderResponse order = orderService.cancelOrder(orderId, reason);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error cancelling order {}: {}", orderId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/ticket/{ticketId}/pending-count")
    public ResponseEntity<Map<String, Long>> getPendingOrdersCount(@PathVariable String ticketId) {
        long count = orderService.countPendingOrdersForTicket(ticketId);
        return ResponseEntity.ok(Map.of("pendingOrders", count));
    }
    
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<?> getOrderByPaymentId(@PathVariable String paymentId) {
        return orderService.findOrderByPaymentId(paymentId)
            .map(order -> ResponseEntity.ok(order))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Orders Service"));
    }
}