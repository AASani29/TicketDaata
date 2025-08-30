package com.ticketdaata.ticketservice.service;

import com.ticketdaata.ticketservice.dto.CreateTicketRequest;
import com.ticketdaata.ticketservice.dto.TicketResponse;
import com.ticketdaata.ticketservice.dto.UpdateTicketRequest;
import com.ticketdaata.ticketservice.entity.Ticket;
import com.ticketdaata.ticketservice.entity.TicketStatus;
import com.ticketdaata.ticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public TicketResponse create(CreateTicketRequest request) {
        log.info("Creating ticket for event: {}", request.getEventName());

        Ticket ticket = Ticket.builder()
                .eventName(request.getEventName())
                .category(request.getCategory())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .seatInfo(request.getSeatInfo())
                .price(request.getPrice())
                .status(TicketStatus.AVAILABLE)
                .sellerId(request.getSellerId())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Ticket created successfully with ID: {}", savedTicket.getId());

        return convertToResponse(savedTicket);
    }

    public List<TicketResponse> listAvailable() {
        return ticketRepository.findByStatus(TicketStatus.AVAILABLE)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TicketResponse get(String id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));
        return convertToResponse(ticket);
    }

    @Transactional
    public TicketResponse update(String id, UpdateTicketRequest request) {
        log.info("Updating ticket: {}", id);

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));

        if (request.getLocation() != null) {
            ticket.setLocation(request.getLocation());
        }
        if (request.getSeatInfo() != null) {
            ticket.setSeatInfo(request.getSeatInfo());
        }
        if (request.getPrice() != null) {
            ticket.setPrice(request.getPrice());
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Ticket updated successfully: {}", id);

        return convertToResponse(savedTicket);
    }

    @Transactional
    public void delete(String id) {
        log.info("Deleting ticket: {}", id);

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));

        if (ticket.getStatus() == TicketStatus.RESERVED || ticket.getStatus() == TicketStatus.SOLD) {
            throw new IllegalStateException("Cannot delete ticket that is reserved or sold");
        }

        ticketRepository.delete(ticket);
        log.info("Ticket deleted successfully: {}", id);
    }

    @Transactional
    public TicketResponse reserve(String id, Long version) {
        log.info("Reserving ticket: {} with version: {}", id, version);

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));

        if (ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new IllegalStateException("Ticket is not available for reservation");
        }

        if (!ticket.getVersion().equals(version)) {
            throw new IllegalStateException("Ticket version mismatch. Ticket may have been updated by another user.");
        }

        ticket.setStatus(TicketStatus.RESERVED);
        Ticket savedTicket = ticketRepository.save(ticket);

        log.info("Ticket reserved successfully: {}", id);
        return convertToResponse(savedTicket);
    }

    @Transactional
    public TicketResponse release(String id) {
        log.info("Releasing ticket: {}", id);

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));

        if (ticket.getStatus() != TicketStatus.RESERVED) {
            throw new IllegalStateException("Ticket is not reserved");
        }

        ticket.setStatus(TicketStatus.AVAILABLE);
        Ticket savedTicket = ticketRepository.save(ticket);

        log.info("Ticket released successfully: {}", id);
        return convertToResponse(savedTicket);
    }

    @Transactional
    public TicketResponse markSold(String id) {
        log.info("Marking ticket as sold: {}", id);

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));

        if (ticket.getStatus() != TicketStatus.RESERVED) {
            throw new IllegalStateException("Ticket must be reserved before marking as sold");
        }

        ticket.setStatus(TicketStatus.SOLD);
        Ticket savedTicket = ticketRepository.save(ticket);

        log.info("Ticket marked as sold successfully: {}", id);
        return convertToResponse(savedTicket);
    }

    public List<TicketResponse> searchByEvent(String query) {
        return ticketRepository.findByEventNameContainingIgnoreCase(query)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> happeningBetween(LocalDateTime from, LocalDateTime to) {
        return ticketRepository.findByEventDateBetween(from, to)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private TicketResponse convertToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .eventName(ticket.getEventName())
                .category(ticket.getCategory())
                .location(ticket.getLocation())
                .eventDate(ticket.getEventDate())
                .seatInfo(ticket.getSeatInfo())
                .price(ticket.getPrice())
                .status(ticket.getStatus())
                .sellerId(ticket.getSellerId())
                .version(ticket.getVersion())
                .build();
    }
}