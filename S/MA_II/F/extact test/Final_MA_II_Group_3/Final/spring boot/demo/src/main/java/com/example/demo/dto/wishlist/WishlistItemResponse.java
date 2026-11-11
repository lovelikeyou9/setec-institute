package com.example.demo.dto.wishlist;

import com.example.demo.dto.product.ProductResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistItemResponse {
    private Long id;
    private ProductResponse product;
}
