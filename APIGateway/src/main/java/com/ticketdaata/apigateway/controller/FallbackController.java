package com.ticketdaata.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> authFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Auth Service is currently unavailable", "service", "Auth Service"));
    }

    @GetMapping("/orders")
    public ResponseEntity<Map<String, String>> ordersFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Orders Service is currently unavailable", "service", "Orders Service"));
    }

    @GetMapping("/tickets")
    public ResponseEntity<Map<String, String>> ticketsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Ticket Service is currently unavailable", "service", "Ticket Service"));
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, String>> eventsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Event Service is currently unavailable", "service", "Event Service"));
    }
}