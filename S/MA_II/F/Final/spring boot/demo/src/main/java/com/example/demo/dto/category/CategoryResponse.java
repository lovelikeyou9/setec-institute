package com.example.demo.dto.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String iconName;
    private String description;
    private Integer sortOrder;
}
