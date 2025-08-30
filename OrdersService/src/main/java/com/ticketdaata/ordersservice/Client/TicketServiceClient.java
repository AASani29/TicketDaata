package com.ticketdaata.ordersservice.Client;

import com.ticketdaata.ordersservice.dto.TicketResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "TICKETDAATA-TICKET-SERVICE")
public interface TicketServiceClient {

    @GetMapping("/tickets/{id}")
    ResponseEntity<TicketResponse> getTicket(@PathVariable("id") Long ticketId);

    @PostMapping("/tickets/{id}/reserve")
    ResponseEntity<TicketResponse> reserveTicket(@PathVariable("id") Long ticketId,
            @RequestParam("version") Long version);

    @PostMapping("/tickets/{id}/release")
    ResponseEntity<TicketResponse> releaseTicket(@PathVariable("id") Long ticketId);

    @PostMapping("/tickets/{id}/sold")
    ResponseEntity<TicketResponse> markTicketSold(@PathVariable("id") Long ticketId);
}