package com.example.demo.DTO;

public record AuthResponse(String token, String email, String role, String fullName) {}
