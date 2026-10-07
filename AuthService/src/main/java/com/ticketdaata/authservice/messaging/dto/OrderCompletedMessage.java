package com.ticketdaata.authservice.messaging.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mirrors the subset of fields OrdersService's OrderStatusMessage publishes
 * on the order.completed routing key that this service actually needs.
 * Spring AMQP's Jackson2JsonMessageConverter deserializes by matching field
 * names against this class (via the @RabbitListener method's declared
 * parameter type), not by the sender's original class name - the same
 * cross-service DTO pattern already used between OrdersService and
 * ticketservice.
 */
public class OrderCompletedMessage {

    private String orderId;
    private String ticketId;
    private String userId;
    private String sellerId;
    private BigDecimal totalAmount;
    private String eventType;
    private LocalDateTime timestamp;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
