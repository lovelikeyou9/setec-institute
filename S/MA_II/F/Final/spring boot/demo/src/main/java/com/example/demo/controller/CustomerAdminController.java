package com.example.demo.controller;

import com.example.demo.dto.admin.BanRequest;
import com.example.demo.dto.admin.CustomerActivityResponse;
import com.example.demo.dto.admin.CustomerRequest;
import com.example.demo.dto.cart.CartItemResponse;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.dto.wishlist.WishlistItemResponse;
import com.example.demo.service.CustomerAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
public class CustomerAdminController {

    private final CustomerAdminService customerAdminService;

    @GetMapping
    public List<UserResponse> list() {
        return customerAdminService.listCustomers();
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return customerAdminService.getCustomer(id);
    }

    @GetMapping("/{id}/activity")
    public CustomerActivityResponse activity(@PathVariable Long id) {
        return customerAdminService.getActivity(id);
    }

    @GetMapping("/{id}/orders")
    public List<OrderResponse> orders(@PathVariable Long id) {
        return customerAdminService.getActivity(id).getOrders();
    }

    @GetMapping("/{id}/cart")
    public List<CartItemResponse> cart(@PathVariable Long id) {
        return customerAdminService.getActivity(id).getCart();
    }

    @GetMapping("/{id}/favorites")
    public List<WishlistItemResponse> favorites(@PathVariable Long id) {
        return customerAdminService.getActivity(id).getFavorites();
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody CustomerRequest request) {
        return customerAdminService.create(request);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return customerAdminService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        customerAdminService.delete(id);
    }

    @PutMapping("/{id}/ban")
    public UserResponse ban(@PathVariable Long id, @RequestBody(required = false) BanRequest request) {
        return customerAdminService.ban(id, request != null ? request : new BanRequest());
    }

    @PutMapping("/{id}/unban")
    public UserResponse unban(@PathVariable Long id) {
        return customerAdminService.unban(id);
    }
}
