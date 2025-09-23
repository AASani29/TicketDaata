package com.ticketdaata.ordersservice.service;

import com.ticketdaata.ordersservice.Client.TicketServiceClient;
import com.ticketdaata.ordersservice.dto.*;
import com.ticketdaata.ordersservice.entity.Order;
import com.ticketdaata.ordersservice.messaging.publisher.OrderEventPublisher;
import com.ticketdaata.ordersservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TicketServiceClient ticketServiceClient;
    private final OrderEventPublisher orderEventPublisher;

    private static final int ORDER_EXPIRATION_MINUTES = 15;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for ticket ID: {}", request.getTicketId());

        // 1. Get ticket details from Ticket Service
        TicketResponse ticket = ticketServiceClient.getTicket(request.getTicketId())
                .getBody();

        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found with ID: " + request.getTicketId());
        }

        if (!"AVAILABLE".equals(ticket.getStatus())) {
            throw new IllegalStateException("Ticket is not available for purchase");
        }

        // 2. Validate ownership - prevent users from buying their own tickets
        String buyerUserId = request.getUserId();
        String ticketOwnerUserId = ticket.getUserId();

        if (buyerUserId.equals(ticketOwnerUserId)) {
            log.warn("Purchase attempt blocked: User {} tried to buy their own ticket {}",
                    buyerUserId, request.getTicketId());
            throw new IllegalArgumentException(
                    "Purchase not allowed: You cannot buy tickets that you listed for sale.");
        }

        log.info("Ownership validation passed: Buyer {} is different from ticket owner {}",
                buyerUserId, ticketOwnerUserId);

        // 3. Reserve the ticket using messaging instead of direct HTTP call
        try {
            orderEventPublisher.publishTicketReservationRequest(
                    ticket.getId(), 
                    null, // orderId will be set after creating the order
                    request.getUserId(), 
                    ticket.getVersion()
            );
        } catch (Exception e) {
            log.error("Failed to send ticket reservation request {}: {}", ticket.getId(), e.getMessage());
            throw new IllegalStateException("Unable to reserve ticket. It may have been sold to another buyer.");
        }

        // 4. Create the order using ticket details
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTicketId(request.getTicketId());
        order.setTicketTitle(ticket.getEventName());
        order.setEventName(ticket.getEventName());
        order.setEventDate(ticket.getEventDate().toString());
        order.setSeatInfo(ticket.getSeatInfo());
        order.setPrice(BigDecimal.valueOf(ticket.getPrice()));
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(BigDecimal.valueOf(ticket.getPrice()).multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setExpirationTime(ORDER_EXPIRATION_MINUTES);
        order.setSellerId(ticket.getSellerId().toString());
        order.setSellerUsername(""); // You'll get this from user service later

        Order savedOrder = orderRepository.save(order);
        
        // Now update the reservation request with the actual order ID
        orderEventPublisher.publishTicketReservationRequest(
                request.getTicketId(), 
                savedOrder.getId(), 
                request.getUserId(), 
                ticket.getVersion()
        );
        
        // Publish order created event
        orderEventPublisher.publishOrderCreated(
                savedOrder.getId(),
                request.getTicketId(),
                request.getUserId(),
                savedOrder.getTotalAmount()
        );
        
        // Schedule order expiration
        LocalDateTime expirationTime = savedOrder.getCreatedAt().plusMinutes(ORDER_EXPIRATION_MINUTES);
        orderEventPublisher.scheduleOrderExpiration(
                savedOrder.getId(),
                request.getTicketId(),
                request.getUserId(),
                expirationTime
        );
        
        log.info("Order created successfully: {}", savedOrder.getId());

        return OrderResponse.fromEntity(savedOrder);
    }

    @Transactional
    public OrderResponse completeOrder(String orderId, String paymentId) {
        log.info("Completing order: {} with payment: {}", orderId, paymentId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in pending status");
        }

        if (order.isExpired()) {
            throw new IllegalStateException("Order has expired");
        }

        // Mark ticket as sold in Ticket Service using messaging
        orderEventPublisher.publishTicketSoldRequest(
                order.getTicketId(),
                order.getId(),
                order.getUserId()
        );

        // Update order status
        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setPaymentId(paymentId);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        
        // Publish order completed event
        orderEventPublisher.publishOrderCompleted(
                orderId,
                order.getTicketId(),
                order.getUserId(),
                order.getTotalAmount()
        );
        
        log.info("Order completed successfully: {}", orderId);

        return OrderResponse.fromEntity(savedOrder);
    }

    @Transactional
    public OrderResponse cancelOrder(String orderId, String reason) {
        log.info("Cancelling order: {} with reason: {}", orderId, reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be cancelled");
        }

        // Release ticket reservation in Ticket Service using messaging
        orderEventPublisher.publishTicketReleaseRequest(
                order.getTicketId(),
                order.getId(),
                order.getUserId(),
                reason
        );

        // Update order status
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        
        // Publish order cancelled event
        orderEventPublisher.publishOrderCancelled(
                orderId,
                order.getTicketId(),
                order.getUserId(),
                reason
        );
        
        log.info("Order cancelled successfully: {}", orderId);

        return OrderResponse.fromEntity(savedOrder);
    }

    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> getOrdersByUserId(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersBySellerId(String sellerId) {
        return orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId)
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrder(String orderId, UpdateOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be updated");
        }

        // Update fields if provided
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
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.fromEntity(savedOrder);
    }

    public long countPendingOrdersForTicket(String ticketId) {
        return orderRepository.countByTicketIdAndStatus(ticketId, Order.OrderStatus.PENDING);
    }

    public Optional<OrderResponse> findOrderByPaymentId(String paymentId) {
        return orderRepository.findByPaymentId(paymentId)
                .map(OrderResponse::fromEntity);
    }

    /**
     * Called by Expiration Service to expire orders
     */
    @Transactional
    public void expireOrder(String orderId) {
        log.info("Expiring order: {}", orderId);

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() != Order.OrderStatus.PENDING) {
            return;
        }

        // Release ticket reservation using messaging
        orderEventPublisher.publishTicketReleaseRequest(
                order.getTicketId(),
                order.getId(),
                order.getUserId(),
                "Order expired"
        );

        // Update order status
        order.setStatus(Order.OrderStatus.EXPIRED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        
        // Publish order expired event
        orderEventPublisher.publishOrderExpired(
                orderId,
                order.getTicketId(),
                order.getUserId()
        );

        log.info("Order expired successfully: {}", orderId);
    }
}