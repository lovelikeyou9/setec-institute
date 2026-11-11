package com.example.demo.dto.brand;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String slug;
    private String logoUrl;
    private String description;
    private Boolean active;
}
