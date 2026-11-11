package com.example.demo.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    private String avatarUrl;
}
