package com.example.demo.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String brand;
    private String sku;
    private Long categoryId;
    private String categoryName;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer warnMin;
    private String binLocation;
    private String imageUrl;
    private String architecture;
    private Double rating;
    private Integer reviewCount;
    private boolean featured;
    private boolean flashDrop;
    private BigDecimal costPrice;
    private String specsJson;
    private String tags;
    private Instant createdAt;
    private Instant updatedAt;
}
