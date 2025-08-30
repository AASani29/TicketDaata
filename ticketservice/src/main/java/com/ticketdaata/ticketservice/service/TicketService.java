package com.ticketdaata.ticketservice.service;

import com.ticketdaata.ticketservice.dto.CreateTicketRequest;
import com.ticketdaata.ticketservice.dto.TicketResponse;
import com.ticketdaata.ticketservice.dto.UpdateTicketRequest;
import com.ticketdaata.ticketservice.entity.Ticket;
import com.ticketdaata.ticketservice.entity.TicketStatus;
import com.ticketdaata.ticketservice.repository.TicketRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository repo;

    private TicketResponse toDto(Ticket t) {
        return TicketResponse.builder()
                .id(t.getId())
                .eventName(t.getEventName())
                .category(t.getCategory())
                .location(t.getLocation())
                .eventDate(t.getEventDate())
                .seatInfo(t.getSeatInfo())
                .price(t.getPrice())
                .status(t.getStatus())
                .sellerId(t.getSellerId())
                .version(t.getVersion())
                .build();
    }

    @Transactional
    public TicketResponse create(CreateTicketRequest req) {
        Ticket t = Ticket.builder()
                .eventName(req.getEventName())
                .category(req.getCategory())
                .location(req.getLocation())
                .eventDate(req.getEventDate())
                .seatInfo(req.getSeatInfo())
                .price(req.getPrice())
                .status(TicketStatus.AVAILABLE)
                .sellerId(req.getSellerId())
                .build();
        return toDto(repo.save(t));
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> listAvailable() {
        return repo.findByStatus(TicketStatus.AVAILABLE).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse get(Long id) {
        Ticket t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        return toDto(t);
    }

    @Transactional
    public TicketResponse update(Long id, UpdateTicketRequest req) {
        Ticket t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (req.getLocation() != null) t.setLocation(req.getLocation());
        if (req.getSeatInfo() != null) t.setSeatInfo(req.getSeatInfo());
        if (req.getPrice() != null) t.setPrice(req.getPrice());
        return toDto(repo.save(t));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Ticket not found");
        repo.deleteById(id);
    }

    /** Reserve a ticket atomically (optimistic lock backed) */
    @Transactional
    public TicketResponse reserve(Long id, Long expectedVersion) {
        Ticket t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (!t.getVersion().equals(expectedVersion)) {
            throw new OptimisticLockException("Ticket version mismatch");
        }
        if (t.getStatus() != TicketStatus.AVAILABLE) {
            throw new IllegalStateException("Ticket is not AVAILABLE");
        }
        t.setStatus(TicketStatus.RESERVED);
        return toDto(repo.save(t));
    }

    @Transactional
    public TicketResponse release(Long id) {
        Ticket t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (t.getStatus() == TicketStatus.RESERVED) {
            t.setStatus(TicketStatus.AVAILABLE);
        }
        return toDto(repo.save(t));
    }

    @Transactional
    public TicketResponse markSold(Long id) {
        Ticket t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (t.getStatus() == TicketStatus.SOLD) return toDto(t);
        t.setStatus(TicketStatus.SOLD);
        return toDto(repo.save(t));
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> searchByEvent(String q) {
        return repo.findByEventNameContainingIgnoreCase(q).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> happeningBetween(LocalDateTime from, LocalDateTime to) {
        return repo.findByEventDateBetween(from, to).stream().map(this::toDto).toList();
    }
}
