package com.example.demo.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String slug;
    private String iconName;
    private String description;
    @NotNull
    private Integer sortOrder;
}
