package com.ticketdaata.ticketservice.messaging.publisher;

public interface TicketEventPublisherInterface {
    void publishTicketReserved(String ticketId, String orderId, String userId);
    void publishTicketReleased(String ticketId, String orderId, String userId);
    void publishTicketSold(String ticketId, String orderId, String userId);
}