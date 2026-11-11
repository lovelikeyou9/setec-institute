package com.example.demo.service;

import com.example.demo.dto.admin.BanRequest;
import com.example.demo.dto.admin.CustomerActivityResponse;
import com.example.demo.dto.admin.CustomerRequest;
import com.example.demo.dto.cart.CartItemResponse;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.dto.wishlist.WishlistItemResponse;
import com.example.demo.entity.OrderEntity;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ApiException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WishlistItemRepository;
import com.example.demo.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerAdminService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public List<UserResponse> listCustomers() {
        return userRepository.findByRoleOrderByCreatedAtDesc(Role.CUSTOMER).stream()
                .map(EntityMapper::toUserResponse)
                .toList();
    }

    public UserResponse getCustomer(Long id) {
        return EntityMapper.toUserResponse(getCustomerEntity(id));
    }

    @Transactional
    public UserResponse create(CustomerRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ApiException("Password is required", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already registered", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Username already taken", HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .fullName(request.getFullName().trim())
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .avatarUrl(request.getAvatarUrl())
                .banned(false)
                .build();
        return EntityMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, CustomerRequest request) {
        User user = getCustomerEntity(id);
        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ApiException("Email already registered", HttpStatus.CONFLICT);
            }
        });
        userRepository.findByUsername(request.getUsername()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ApiException("Username already taken", HttpStatus.CONFLICT);
            }
        });
        user.setFullName(request.getFullName().trim());
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setAvatarUrl(request.getAvatarUrl());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return EntityMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = getCustomerEntity(id);
        Long currentId = currentUserService.getCurrentUser().getId();
        if (user.getId().equals(currentId)) {
            throw new ApiException("Cannot delete your own account", HttpStatus.BAD_REQUEST);
        }
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse ban(Long id, BanRequest request) {
        User user = getCustomerEntity(id);
        user.setBanned(true);
        user.setBanReason(request != null && request.getReason() != null ? request.getReason().trim() : "Banned by admin");
        return EntityMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse unban(Long id) {
        User user = getCustomerEntity(id);
        user.setBanned(false);
        user.setBanReason(null);
        return EntityMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public CustomerActivityResponse getActivity(Long id) {
        User user = getCustomerEntity(id);

        List<CartItemResponse> cart = cartItemRepository.findByUserId(id).stream()
                .map(EntityMapper::toCartItemResponse)
                .toList();

        List<WishlistItemResponse> favorites = wishlistItemRepository.findByUserId(id).stream()
                .map(EntityMapper::toWishlistItemResponse)
                .toList();

        List<OrderEntity> orderEntities = orderRepository.findByUserIdOrderByCreatedAtDesc(id);
        List<OrderResponse> orders = orderEntities.stream()
                .map(order -> EntityMapper.toOrderResponse(order, orderItemRepository.findByOrderId(order.getId())))
                .toList();

        BigDecimal totalSpent = orders.stream()
                .map(OrderResponse::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int cartQty = cart.stream().mapToInt(CartItemResponse::getQuantity).sum();

        return CustomerActivityResponse.builder()
                .customer(EntityMapper.toUserResponse(user))
                .orders(orders)
                .cart(cart)
                .favorites(favorites)
                .totalSpent(totalSpent)
                .orderCount(orders.size())
                .cartItemCount(cartQty)
                .favoriteCount(favorites.size())
                .build();
    }

    private User getCustomerEntity(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));
        if (user.getRole() != Role.CUSTOMER) {
            throw new ApiException("User is not a customer", HttpStatus.BAD_REQUEST);
        }
        return user;
    }
}
