package com.example.demo.controller;

import com.example.demo.dto.wishlist.WishlistItemResponse;
import com.example.demo.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public List<WishlistItemResponse> list() {
        return wishlistService.getWishlist();
    }

    @PostMapping("/{productId}")
    public WishlistItemResponse add(@PathVariable Long productId) {
        return wishlistService.add(productId);
    }

    @DeleteMapping("/{productId}")
    public void remove(@PathVariable Long productId) {
        wishlistService.remove(productId);
    }
}
