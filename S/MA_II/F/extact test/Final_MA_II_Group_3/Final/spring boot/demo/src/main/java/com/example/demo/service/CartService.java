package com.example.demo.service;

import com.example.demo.dto.cart.CartItemRequest;
import com.example.demo.dto.cart.CartItemResponse;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.ApiException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart() {
        User user = currentUserService.getCurrentUser();
        return cartItemRepository.findByUserId(user.getId()).stream()
                .map(EntityMapper::toCartItemResponse)
                .toList();
    }

    @Transactional
    public CartItemResponse addItem(CartItemRequest request) {
        User user = currentUserService.getCurrentUser();
        Product product = productService.getProduct(request.getProductId());
        if (product.getStock() < request.getQuantity()) {
            throw new ApiException("Insufficient stock", HttpStatus.BAD_REQUEST);
        }

        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElse(CartItem.builder().user(user).product(product).quantity(0).build());
        item.setQuantity(item.getQuantity() + request.getQuantity());
        if (item.getQuantity() > product.getStock()) {
            throw new ApiException("Insufficient stock", HttpStatus.BAD_REQUEST);
        }
        return EntityMapper.toCartItemResponse(cartItemRepository.save(item));
    }

    @Transactional
    public CartItemResponse updateQuantity(Long id, Integer quantity) {
        User user = currentUserService.getCurrentUser();
        CartItem item = cartItemRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Cart item not found", HttpStatus.NOT_FOUND));
        if (item.getProduct().getStock() < quantity) {
            throw new ApiException("Insufficient stock", HttpStatus.BAD_REQUEST);
        }
        item.setQuantity(quantity);
        return EntityMapper.toCartItemResponse(cartItemRepository.save(item));
    }

    @Transactional
    public void removeItem(Long id) {
        User user = currentUserService.getCurrentUser();
        CartItem item = cartItemRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Cart item not found", HttpStatus.NOT_FOUND));
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart() {
        User user = currentUserService.getCurrentUser();
        cartItemRepository.deleteByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public List<CartItem> getCartEntitiesForUser(User user) {
        return cartItemRepository.findByUserId(user.getId());
    }
}
