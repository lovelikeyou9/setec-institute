package com.example.demo.dto.admin;

import com.example.demo.entity.AuditLevel;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AuditLogResponse {
    private Long id;
    private String message;
    private AuditLevel level;
    private Instant createdAt;
    private String adminUsername;
}
