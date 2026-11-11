package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CartCheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;
    private final WishlistService wishlistService;

    @GetMapping("/cart")
    public String cart() {
        return "redirect:/account/cart";
    }

    @GetMapping("/wishlist")
    public String wishlist() {
        return "redirect:/account/wishlist";
    }

    @PostMapping("/cart/add")
    public String addToCart(@ModelAttribute("currentUser") User user,
                            @RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(required = false) String redirect,
                            RedirectAttributes ra) {
        cartService.addToCart(user.getId(), productId, quantity);
        ra.addFlashAttribute("message", "Product added to cart");
        return redirect != null ? "redirect:" + redirect : "redirect:/account/cart";
    }

    @PostMapping("/cart/update")
    public String updateQty(@RequestParam Long itemId,
                            @RequestParam int quantity,
                            @RequestParam(required = false) String redirect) {
        cartService.updateQuantity(itemId, quantity);
        return redirect != null && !redirect.isBlank() ? "redirect:" + redirect : "redirect:/account/cart";
    }

    @PostMapping("/cart/toggle")
    public String toggle(@RequestParam Long itemId,
                         @RequestParam(required = false) String redirect) {
        cartService.toggleSelect(itemId);
        return redirect != null && !redirect.isBlank() ? "redirect:" + redirect : "redirect:/account/cart";
    }

    @PostMapping("/cart/select-all")
    public String selectAll(@ModelAttribute("currentUser") User user,
                            @RequestParam boolean selected,
                            @RequestParam(required = false) String redirect) {
        cartService.selectAll(user.getId(), selected);
        return redirect != null && !redirect.isBlank() ? "redirect:" + redirect : "redirect:/account/cart";
    }

    @PostMapping("/cart/remove")
    public String remove(@RequestParam Long itemId,
                         @RequestParam(required = false) String redirect) {
        cartService.remove(itemId);
        return redirect != null && !redirect.isBlank() ? "redirect:" + redirect : "redirect:/account/cart";
    }

    @GetMapping("/checkout")
    public String checkout(@ModelAttribute("currentUser") User user, Model model,
                           RedirectAttributes ra) {
        List<CartItem> selected = cartService.getSelected(user.getId());
        if (selected.isEmpty()) {
            ra.addFlashAttribute("error", "Select at least one product to checkout");
            return "redirect:/account/cart";
        }
        model.addAttribute("groupedCart", cartService.getCartGroupedBySeller(user.getId())
                .entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(CartItem::isSelected))
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().filter(CartItem::isSelected).toList(),
                        (a, b) -> a, java.util.LinkedHashMap::new)));
        model.addAttribute("subtotal", cartService.selectedSubtotal(user.getId()));
        model.addAttribute("addresses", userService.getAddresses(user.getId()));
        model.addAttribute("pageTitle", "Checkout — MME");
        return "cart/checkout";
    }

    @PostMapping("/checkout/place")
    public String placeOrder(@ModelAttribute("currentUser") User user,
                             @RequestParam String fullName,
                             @RequestParam String phone,
                             @RequestParam String province,
                             @RequestParam String district,
                             @RequestParam(required = false) String commune,
                             @RequestParam String street,
                             @RequestParam String country,
                             @RequestParam String deliveryType,
                             @RequestParam String paymentMethod,
                             RedirectAttributes ra) {
        try {
            if (country == null || country.isBlank()) {
                throw new RuntimeException("Country is required");
            }
            String address = street + (commune != null && !commune.isBlank() ? ", " + commune : "")
                    + ", " + district + ", " + province + ", " + country.trim();
            List<Order> orders = orderService.placeOrders(
                    user, fullName, phone, address, country.trim(),
                    DeliveryType.valueOf(deliveryType),
                    PaymentMethod.valueOf(paymentMethod));
            ra.addFlashAttribute("message", "Order placed successfully!");
            ra.addFlashAttribute("orders", orders);
            return "redirect:/orders/" + orders.get(0).getOrderNumber() + "?placed=true";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @PostMapping("/wishlist/toggle")
    public String toggleWishlist(@ModelAttribute("currentUser") User user,
                                 @RequestParam Long productId,
                                 @RequestParam(required = false) String redirect) {
        wishlistService.toggle(user.getId(), productId);
        return redirect != null ? "redirect:" + redirect : "redirect:/account/wishlist";
    }
}
