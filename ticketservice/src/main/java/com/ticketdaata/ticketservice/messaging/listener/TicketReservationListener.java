package com.ticketdaata.ticketservice.messaging.listener;

import com.ticketdaata.ticketservice.messaging.dto.TicketReservationMessage;
import com.ticketdaata.ticketservice.messaging.publisher.TicketEventPublisher;
import com.ticketdaata.ticketservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketReservationListener {

    private final TicketService ticketService;
    private final TicketEventPublisher ticketEventPublisher;

    @RabbitListener(queues = "ticket.reservation.queue")
    public void handleTicketReservation(TicketReservationMessage message) {
        log.info("Received ticket reservation message: {}", message);

        try {
            switch (message.getEventType()) {
                case "RESERVE_TICKET":
                    handleReserveTicket(message);
                    break;
                case "RELEASE_TICKET":
                    handleReleaseTicket(message);
                    break;
                case "MARK_SOLD":
                    handleMarkTicketSold(message);
                    break;
                default:
                    log.warn("Unknown event type: {}", message.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to process ticket reservation message: {}", e.getMessage(), e);
            // In a production system, you might want to send to a dead letter queue
        }
    }

    private void handleReserveTicket(TicketReservationMessage message) {
        try {
            ticketService.reserve(message.getTicketId(), message.getVersion());
            ticketEventPublisher.publishTicketReserved(
                    message.getTicketId(),
                    message.getOrderId(),
                    message.getUserId()
            );
            log.info("Successfully reserved ticket: {} for order: {}", message.getTicketId(), message.getOrderId());
        } catch (Exception e) {
            log.error("Failed to reserve ticket {}: {}", message.getTicketId(), e.getMessage());
            // You could publish a ticket reservation failed event here
            throw e;
        }
    }

    private void handleReleaseTicket(TicketReservationMessage message) {
        try {
            ticketService.release(message.getTicketId());
            ticketEventPublisher.publishTicketReleased(
                    message.getTicketId(),
                    message.getOrderId(),
                    message.getUserId()
            );
            log.info("Successfully released ticket: {} for order: {}", message.getTicketId(), message.getOrderId());
        } catch (Exception e) {
            log.error("Failed to release ticket {}: {}", message.getTicketId(), e.getMessage());
            // Continue processing as release failures are less critical
        }
    }

    private void handleMarkTicketSold(TicketReservationMessage message) {
        try {
            ticketService.markSold(message.getTicketId());
            ticketEventPublisher.publishTicketSold(
                    message.getTicketId(),
                    message.getOrderId(),
                    message.getUserId()
            );
            log.info("Successfully marked ticket sold: {} for order: {}", message.getTicketId(), message.getOrderId());
        } catch (Exception e) {
            log.error("Failed to mark ticket sold {}: {}", message.getTicketId(), e.getMessage());
            throw e;
        }
    }
}