package com.ticketdaata.ordersservice.repository;

import com.ticketdaata.ordersservice.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    // Find orders by user ID
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
    
    // Find orders by status
    List<Order> findByStatus(Order.OrderStatus status);
    
    // Find orders by user and status
    List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status);
    
    // Find orders by ticket ID
    List<Order> findByTicketId(String ticketId);
    
    // Find orders by seller ID
    List<Order> findBySellerIdOrderByCreatedAtDesc(String sellerId);
    
    // Find expired orders that are still pending
    @Query("{'status': 'PENDING', 'expiresAt': {'$lt': ?0}}")
    List<Order> findExpiredPendingOrders(LocalDateTime currentTime);
    
    // Find orders expiring soon (for notifications)
    @Query("{'status': 'PENDING', 'expiresAt': {'$gte': ?0, '$lt': ?1}}")
    List<Order> findOrdersExpiringSoon(LocalDateTime from, LocalDateTime to);
    
    // Check if user has pending order for specific ticket
    Optional<Order> findByUserIdAndTicketIdAndStatus(String userId, String ticketId, Order.OrderStatus status);
    
    // Count pending orders for a ticket
    long countByTicketIdAndStatus(String ticketId, Order.OrderStatus status);
    
    // Find orders by payment ID
    Optional<Order> findByPaymentId(String paymentId);
}