package com.ticketdaata.ticketservice.controller;

import com.ticketdaata.ticketservice.dto.CreateTicketRequest;
import com.ticketdaata.ticketservice.dto.TicketResponse;
import com.ticketdaata.ticketservice.dto.UpdateTicketRequest;
import com.ticketdaata.ticketservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@RequestBody CreateTicketRequest request) {
        return ticketService.create(request);
    }

    @GetMapping
    public List<TicketResponse> getAvailableTickets() {
        return ticketService.listAvailable();
    }

    @GetMapping("/{id}")
    public TicketResponse getTicket(@PathVariable String id) {
        return ticketService.get(id);
    }

    @PutMapping("/{id}")
    public TicketResponse updateTicket(@PathVariable String id, @RequestBody UpdateTicketRequest request) {
        return ticketService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(@PathVariable String id) {
        ticketService.delete(id);
    }

    @PostMapping("/{id}/reserve")
    public TicketResponse reserveTicket(@PathVariable String id, @RequestParam Long version) {
        return ticketService.reserve(id, version);
    }

    @PostMapping("/{id}/release")
    public TicketResponse releaseTicket(@PathVariable String id) {
        return ticketService.release(id);
    }

    @PostMapping("/{id}/sold")
    public TicketResponse markTicketSold(@PathVariable String id) {
        return ticketService.markSold(id);
    }

    @GetMapping("/search")
    public List<TicketResponse> searchTickets(@RequestParam String query) {
        return ticketService.searchByEvent(query);
    }

    @GetMapping("/happening-between")
    public List<TicketResponse> getTicketsHappeningBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ticketService.happeningBetween(from, to);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Ticket Service"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleNotFound(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleBadState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}