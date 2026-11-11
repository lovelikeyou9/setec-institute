package com.example.demo.dto.cart;

import com.example.demo.dto.product.ProductResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private Long id;
    private Integer quantity;
    private ProductResponse product;
}
