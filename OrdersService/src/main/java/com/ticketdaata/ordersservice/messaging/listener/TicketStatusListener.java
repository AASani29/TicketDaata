package com.ticketdaata.ordersservice.messaging.listener;

import com.ticketdaata.ordersservice.messaging.dto.TicketStatusUpdateMessage;
import com.ticketdaata.ordersservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketStatusListener {

    private final OrderService orderService;

    @RabbitListener(queues = "ticket.status.update.queue")
    public void handleTicketStatusUpdate(TicketStatusUpdateMessage message) {
        log.info("Received ticket status update: {}", message);

        try {
            switch (message.getEventType()) {
                case "TICKET_RESERVED":
                    handleTicketReserved(message);
                    break;
                case "TICKET_RELEASED":
                    handleTicketReleased(message);
                    break;
                case "TICKET_SOLD":
                    handleTicketSold(message);
                    break;
                default:
                    log.warn("Unknown ticket event type: {}", message.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to process ticket status update: {}", e.getMessage(), e);
        }
    }

    private void handleTicketReserved(TicketStatusUpdateMessage message) {
        log.info("Ticket {} reserved for order {}", message.getTicketId(), message.getOrderId());
        // Additional logic for when ticket is successfully reserved
        // For example, you could update order status or send notification
    }

    private void handleTicketReleased(TicketStatusUpdateMessage message) {
        log.info("Ticket {} released for order {}", message.getTicketId(), message.getOrderId());
        // Additional logic for when ticket is released
        // For example, you could clean up pending orders or send notification
    }

    private void handleTicketSold(TicketStatusUpdateMessage message) {
        log.info("Ticket {} marked as sold for order {}", message.getTicketId(), message.getOrderId());
        // Additional logic for when ticket is sold
        // For example, you could trigger payment processing or send confirmation
    }
}