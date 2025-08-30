package com.ticketdaata.ticketservice.repository;

import com.ticketdaata.ticketservice.entity.Ticket;
import com.ticketdaata.ticketservice.entity.TicketStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    List<Ticket> findByStatus(TicketStatus status);

    @Query("{'eventName': {$regex: ?0, $options: 'i'}}")
    List<Ticket> findByEventNameContainingIgnoreCase(String eventName);

    @Query("{'eventDate': {$gte: ?0, $lte: ?1}}")
    List<Ticket> findByEventDateBetween(LocalDateTime from, LocalDateTime to);
}