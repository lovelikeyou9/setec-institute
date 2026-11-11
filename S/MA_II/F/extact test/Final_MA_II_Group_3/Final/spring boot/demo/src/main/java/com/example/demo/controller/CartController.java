package com.example.demo.controller;

import com.example.demo.dto.cart.CartItemRequest;
import com.example.demo.dto.cart.CartItemResponse;
import com.example.demo.dto.cart.UpdateCartQuantityRequest;
import com.example.demo.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public List<CartItemResponse> getCart() {
        return cartService.getCart();
    }

    @PostMapping
    public CartItemResponse add(@Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(request);
    }

    @PutMapping("/{id}")
    public CartItemResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCartQuantityRequest request) {
        return cartService.updateQuantity(id, request.getQuantity());
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        cartService.removeItem(id);
    }

    @DeleteMapping
    public void clear() {
        cartService.clearCart();
    }
}
