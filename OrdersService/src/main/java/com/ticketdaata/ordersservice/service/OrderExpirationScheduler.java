package com.ticketdaata.ordersservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderExpirationScheduler {
    
    @Autowired
    private OrderService orderService;
    
    @Value("${orders.reservation.cleanup-interval-minutes:5}")
    private int cleanupIntervalMinutes;
    
    // Run every 5 minutes by default (configurable)
    @Scheduled(fixedRateString = "#{${orders.reservation.cleanup-interval-minutes:5} * 60 * 1000}")
    public void processExpiredOrders() {
        log.debug("Starting expired orders cleanup process");
        
        try {
            orderService.processExpiredOrders();
            log.debug("Expired orders cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during expired orders cleanup: {}", e.getMessage(), e);
        }
    }
    
    // Optional: Run at startup to clean any orders that expired while service was down
    @Scheduled(initialDelay = 30000, fixedDelay = Long.MAX_VALUE) // Run once after 30 seconds
    public void initialCleanup() {
        log.info("Performing initial cleanup of expired orders");
        processExpiredOrders();
    }
}