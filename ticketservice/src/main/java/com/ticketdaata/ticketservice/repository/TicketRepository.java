package com.ticketdaata.ticketservice.repository;

import com.ticketdaata.ticketservice.entity.Ticket;
import com.ticketdaata.ticketservice.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByEventNameContainingIgnoreCase(String eventName);
    List<Ticket> findByEventDateBetween(LocalDateTime from, LocalDateTime to);
}
