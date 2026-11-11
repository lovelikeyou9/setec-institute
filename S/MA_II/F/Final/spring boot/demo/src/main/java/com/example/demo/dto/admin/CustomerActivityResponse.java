package com.example.demo.dto.admin;

import com.example.demo.dto.cart.CartItemResponse;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.dto.wishlist.WishlistItemResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CustomerActivityResponse {
    private UserResponse customer;
    private List<OrderResponse> orders;
    private List<CartItemResponse> cart;
    private List<WishlistItemResponse> favorites;
    private BigDecimal totalSpent;
    private int orderCount;
    private int cartItemCount;
    private int favoriteCount;
}
