package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<CartItem> getCart(Long userId) {
        return cartItemRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Map<Seller, List<CartItem>> getCartGroupedBySeller(Long userId) {
        return getCart(userId).stream()
                .collect(Collectors.groupingBy(ci -> ci.getProduct().getSeller(),
                        LinkedHashMap::new, Collectors.toList()));
    }

    public long count(Long userId) {
        return cartItemRepository.countByUserId(userId);
    }

    @Transactional
    public CartItem addToCart(Long userId, Long productId, int qty) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + qty);
            item.setSelected(true);
            return cartItemRepository.save(item);
        }
        CartItem item = CartItem.builder()
                .user(user).product(product).quantity(qty).selected(true).build();
        return cartItemRepository.save(item);
    }

    @Transactional
    public void updateQuantity(Long itemId, int qty) {
        CartItem item = cartItemRepository.findById(itemId).orElseThrow();
        if (qty <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(qty);
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public void toggleSelect(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId).orElseThrow();
        item.setSelected(!item.isSelected());
        cartItemRepository.save(item);
    }

    @Transactional
    public void selectAll(Long userId, boolean selected) {
        getCart(userId).forEach(item -> {
            item.setSelected(selected);
            cartItemRepository.save(item);
        });
    }

    @Transactional
    public void remove(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    public BigDecimal selectedSubtotal(Long userId) {
        return cartItemRepository.findByUserIdAndSelectedTrue(userId).stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CartItem> getSelected(Long userId) {
        return cartItemRepository.findByUserIdAndSelectedTrue(userId);
    }

    @Transactional
    public void clearSelected(Long userId) {
        cartItemRepository.deleteByUserIdAndSelectedTrue(userId);
    }
}
