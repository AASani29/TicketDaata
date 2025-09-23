package com.ticketdaata.authservice.controller;

import com.ticketdaata.authservice.dto.AuthResponse;
import com.ticketdaata.authservice.dto.LoginRequest;
import com.ticketdaata.authservice.dto.RegisterRequest;
import com.ticketdaata.authservice.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody(required = false) RegisterRequest request) {
        try {
            logger.info("=== REGISTER ENDPOINT CALLED ===");
            
            if (request == null) {
                logger.error("Request body is null");
                return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Request body is required"));
            }
            
            logger.info("Registration request received for username: {}, email: {}", request.getUsername(), request.getEmail());
            
            // Validate request data
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                logger.warn("Registration failed: Username is null or empty");
                return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Username is required"));
            }
            
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                logger.warn("Registration failed: Email is null or empty");
                return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Email is required"));
            }
            
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                logger.warn("Registration failed: Password is null or empty");
                return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Password is required"));
            }
            
            AuthResponse response = authService.register(request);
            
            logger.info("Registration response: token={}, message={}", 
                       response.getToken() != null ? "present" : "null", response.getMessage());

            if (response.getToken() != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.error("Registration error: ", e);
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        // In a stateless JWT system, logout is handled on the client side
        // by removing the token from storage
        return ResponseEntity.ok(new AuthResponse(null, null, null, "Logout successful"));
    }
}
