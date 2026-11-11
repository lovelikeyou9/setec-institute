package com.example.demo.service;

import com.example.demo.dto.wishlist.WishlistItemResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.entity.WishlistItem;
import com.example.demo.exception.ApiException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.repository.WishlistItemRepository;
import com.example.demo.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductService productService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist() {
        User user = currentUserService.getCurrentUser();
        return wishlistItemRepository.findByUserId(user.getId()).stream()
                .map(EntityMapper::toWishlistItemResponse)
                .toList();
    }

    @Transactional
    public WishlistItemResponse add(Long productId) {
        User user = currentUserService.getCurrentUser();
        Product product = productService.getProduct(productId);
        wishlistItemRepository.findByUserIdAndProductId(user.getId(), productId)
                .ifPresent(item -> {
                    throw new ApiException("Product already in wishlist", HttpStatus.CONFLICT);
                });
        WishlistItem item = WishlistItem.builder().user(user).product(product).build();
        return EntityMapper.toWishlistItemResponse(wishlistItemRepository.save(item));
    }

    @Transactional
    public void remove(Long productId) {
        User user = currentUserService.getCurrentUser();
        if (wishlistItemRepository.findByUserIdAndProductId(user.getId(), productId).isEmpty()) {
            throw new ApiException("Wishlist item not found", HttpStatus.NOT_FOUND);
        }
        wishlistItemRepository.deleteByUserIdAndProductId(user.getId(), productId);
    }
}
