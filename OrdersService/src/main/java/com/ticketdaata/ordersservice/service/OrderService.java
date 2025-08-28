package com.ticketdaata.ordersservice.service;

import com.ticketdaata.ordersservice.dto.CreateOrderRequest;
import com.ticketdaata.ordersservice.dto.OrderResponse;
import com.ticketdaata.ordersservice.dto.UpdateOrderRequest;
import com.ticketdaata.ordersservice.entity.Order;
import com.ticketdaata.ordersservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Value("${orders.reservation.ttl-minutes:15}")
    private int reservationTtlMinutes;
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {} and ticket: {}", request.getUserId(), request.getTicketId());
        
        // Check if user already has a pending order for this ticket
        Optional<Order> existingOrder = orderRepository.findByUserIdAndTicketIdAndStatus(
            request.getUserId(), request.getTicketId(), Order.OrderStatus.PENDING);
        
        if (existingOrder.isPresent()) {
            throw new RuntimeException("You already have a pending order for this ticket");
        }
        
        // Create new order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTicketId(request.getTicketId());
        order.setTicketTitle(request.getTicketTitle());
        order.setEventName(request.getEventName());
        order.setEventDate(request.getEventDate());
        order.setSeatInfo(request.getSeatInfo());
        order.setPrice(request.getPrice());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setSellerId(request.getSellerId());
        order.setSellerUsername(request.getSellerUsername());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setExpirationTime(reservationTtlMinutes);
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        
        return OrderResponse.fromEntity(savedOrder);
    }
    
    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        // Check if order is expired and update status
        if (order.getStatus() == Order.OrderStatus.PENDING && order.isExpired()) {
            order.setStatus(Order.OrderStatus.EXPIRED);
            order.setUpdatedAt(LocalDateTime.now());
            order = orderRepository.save(order);
            log.info("Order {} marked as expired", orderId);
        }
        
        return OrderResponse.fromEntity(order);
    }
    
    public List<OrderResponse> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    public List<OrderResponse> getOrdersBySellerId(String sellerId) {
        List<Order> orders = orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
        return orders.stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    public OrderResponse updateOrder(String orderId, UpdateOrderRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        // Validate status transition
        if (!isValidStatusTransition(order.getStatus(), request.getStatus())) {
            throw new RuntimeException("Invalid status transition from " + order.getStatus() + " to " + request.getStatus());
        }
        
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }
        
        if (request.getPaymentId() != null) {
            order.setPaymentId(request.getPaymentId());
        }
        
        if (request.getCancellationReason() != null) {
            order.setCancellationReason(request.getCancellationReason());
        }
        
        order.setUpdatedAt(LocalDateTime.now());
        
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} updated to status: {}", orderId, updatedOrder.getStatus());
        
        return OrderResponse.fromEntity(updatedOrder);
    }
    
    public OrderResponse completeOrder(String orderId, String paymentId) {
        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(Order.OrderStatus.COMPLETED);
        request.setPaymentId(paymentId);
        
        return updateOrder(orderId, request);
    }
    
    public OrderResponse cancelOrder(String orderId, String reason) {
        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(Order.OrderStatus.CANCELLED);
        request.setCancellationReason(reason);
        
        return updateOrder(orderId, request);
    }
    
    public void expireOrder(String orderId) {
        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(Order.OrderStatus.EXPIRED);
        
        updateOrder(orderId, request);
    }
    
    public List<Order> findExpiredOrders() {
        return orderRepository.findExpiredPendingOrders(LocalDateTime.now());
    }
    
    public void processExpiredOrders() {
        List<Order> expiredOrders = findExpiredOrders();
        log.info("Found {} expired orders to process", expiredOrders.size());
        
        for (Order order : expiredOrders) {
            try {
                expireOrder(order.getId());
                log.info("Expired order: {}", order.getId());
            } catch (Exception e) {
                log.error("Error expiring order {}: {}", order.getId(), e.getMessage());
            }
        }
    }
    
    private boolean isValidStatusTransition(Order.OrderStatus from, Order.OrderStatus to) {
        if (from == to) return true;
        
        switch (from) {
            case PENDING:
                return to == Order.OrderStatus.COMPLETED || 
                       to == Order.OrderStatus.CANCELLED || 
                       to == Order.OrderStatus.EXPIRED;
            case COMPLETED:
            case CANCELLED:
            case EXPIRED:
                return false; // Terminal states
            default:
                return false;
        }
    }
    
    public long countPendingOrdersForTicket(String ticketId) {
        return orderRepository.countByTicketIdAndStatus(ticketId, Order.OrderStatus.PENDING);
    }
    
    public Optional<OrderResponse> findOrderByPaymentId(String paymentId) {
        return orderRepository.findByPaymentId(paymentId)
            .map(OrderResponse::fromEntity);
    }
}