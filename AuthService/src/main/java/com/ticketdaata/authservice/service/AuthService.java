package com.ticketdaata.authservice.service;

import com.ticketdaata.authservice.dto.AuthResponse;
import com.ticketdaata.authservice.dto.LoginRequest;
import com.ticketdaata.authservice.dto.RegisterRequest;
import com.ticketdaata.authservice.entity.User;
import com.ticketdaata.authservice.repository.UserRepository;
import com.ticketdaata.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse(null, null, null, "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(null, null, null, "Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return new AuthResponse(token, user.getUsername(), user.getRole(), "User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
                return new AuthResponse(token, user.getUsername(), user.getRole(), "Login successful");
            }
        }

        return new AuthResponse(null, null, null, "Invalid username or password");
    }
}
