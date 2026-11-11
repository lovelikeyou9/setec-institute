package com.example.jwt.controller;

import com.example.jwt.DTO.AuthRequest;
import com.example.jwt.DTO.AuthResponse;
import com.example.jwt.model.User;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    // [ PasswordEncoder ] is an interface from Spring Security used to encode (hash) passwords
    // and verify passwords.
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTER
    // [ ResponseEntity ] is used in a Spring Boot REST API to control and return the HTTP response
    // sent back to the client.
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {

            return ResponseEntity
                    .badRequest()
                    .body("Username already exists");
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        user.setRole("USER");

        userRepository.save(user);

        return ResponseEntity.ok(
                "User registered successfully"
        );
    }


    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request) {

        User existing = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid username or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                existing.getPassword())) {

            throw new RuntimeException(
                    "Invalid username or password"
            );
        }

        // [ generateToken() ] is usually a method you create in your JWT service. Its job is to create
        // a JWT token after the user has successfully logged in.
        String token =
                jwtUtil.generateToken(existing.getUsername());

        return ResponseEntity.ok(
                new AuthResponse(token)
        );
    }
}