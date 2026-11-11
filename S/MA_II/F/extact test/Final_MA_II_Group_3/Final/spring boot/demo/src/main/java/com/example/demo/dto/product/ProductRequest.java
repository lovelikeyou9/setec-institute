package com.example.demo.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank
    private String name;
    private String brand;
    @NotBlank
    private String sku;
    @NotNull
    private Long categoryId;
    private String description;
    @NotNull
    private BigDecimal price;
    private BigDecimal originalPrice;
    @NotNull
    private Integer stock;
    private Integer warnMin;
    private String binLocation;
    private String imageUrl;
    private String architecture;
    private Double rating;
    private Integer reviewCount;
    private Boolean featured;
    private Boolean flashDrop;
    private BigDecimal costPrice;
    private String specsJson;
    private String tags;
}
