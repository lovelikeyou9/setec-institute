package com.example.demo.dto.user;

import com.example.demo.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private Role role;
    private String avatarUrl;
    private LocalDate memberSince;
    private Instant createdAt;
    private boolean banned;
    private String banReason;
}
