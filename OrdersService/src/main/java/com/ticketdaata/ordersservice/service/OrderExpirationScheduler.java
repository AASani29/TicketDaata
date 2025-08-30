package com.ticketdaata.ordersservice.service;

import com.ticketdaata.ordersservice.entity.Order;
import com.ticketdaata.ordersservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExpirationScheduler {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Value("${orders.reservation.cleanup-interval-minutes:5}")
    private int cleanupIntervalMinutes;

    // Run every 1 minute to check for expired orders
    @Scheduled(fixedRate = 60000)
    public void processExpiredOrders() {
        log.debug("Starting expired orders cleanup process");

        try {
            List<Order> expiredOrders = orderRepository.findExpiredPendingOrders(LocalDateTime.now());

            log.info("Found {} expired orders to process", expiredOrders.size());

            for (Order order : expiredOrders) {
                try {
                    orderService.expireOrder(order.getId());
                } catch (Exception e) {
                    log.error("Failed to expire order {}: {}", order.getId(), e.getMessage());
                }
            }

            log.debug("Expired orders cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during expired orders cleanup: {}", e.getMessage(), e);
        }
    }

    // Optional: Run at startup to clean any orders that expired while service was
    // down
    @Scheduled(initialDelay = 30000, fixedDelay = Long.MAX_VALUE) // Run once after 30 seconds
    public void initialCleanup() {
        log.info("Performing initial cleanup of expired orders");
        processExpiredOrders();
    }
}