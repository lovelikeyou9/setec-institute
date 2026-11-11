package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final OrderService orderService;
    private final WishlistService wishlistService;
    private final CartService cartService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final SellerService sellerService;
    private final FileStorageService fileStorageService;

    @GetMapping({"/account", "/dashboard"})
    public String dashboard(@ModelAttribute("currentUser") User user, Model model) {
        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin";
        }
        if (user.getRole() == Role.SELLER) {
            return "redirect:/seller-dashboard";
        }
        List<Order> orders = orderService.getUserOrders(user.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("pendingOrders", orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING_PAYMENT
                        || o.getStatus() == OrderStatus.PAYMENT_CONFIRMED
                        || o.getStatus() == OrderStatus.FUNDS_HELD
                        || o.getStatus() == OrderStatus.SELLER_PROCESSING
                        || o.getStatus() == OrderStatus.SHIPPED
                        || o.getStatus() == OrderStatus.OUT_FOR_DELIVERY).count());
        model.addAttribute("completedOrders", orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED
                        || o.getStatus() == OrderStatus.COMPLETED).count());
        model.addAttribute("wishlistItems", wishlistService.count(user.getId()));
        model.addAttribute("pageTitle", "My Account — MME");
        model.addAttribute("activeNav", "overview");
        return "account/dashboard";
    }

    @GetMapping("/account/orders")
    public String orders(@ModelAttribute("currentUser") User user,
                         @RequestParam(required = false) String status,
                         @RequestParam(defaultValue = "newest") String sort,
                         Model model) {
        List<Order> orders;
        if (status != null && !status.isBlank()) {
            orders = orderService.getUserOrdersByStatus(user.getId(), OrderStatus.valueOf(status), sort);
        } else {
            orders = orderService.getUserOrders(user.getId(), sort);
        }
        model.addAttribute("orders", orders);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("pageTitle", "My Orders — MME");
        model.addAttribute("activeNav", "my-orders");
        return "account/orders";
    }

    @GetMapping("/orders/{orderNumber}")
    public String trackOrder(@PathVariable String orderNumber,
                             @ModelAttribute("currentUser") User user,
                             Model model) {
        Order order = orderService.findByOrderNumber(orderNumber);
        model.addAttribute("order", order);
        model.addAttribute("commission", orderService.findCommission(order).orElse(null));
        model.addAttribute("canConfirmDelivery", user != null && orderService.canConfirmDelivery(order)
                && order.getUser() != null && order.getUser().getId().equals(user.getId()));
        model.addAttribute("canOpenDispute", user != null && orderService.canOpenDispute(order)
                && order.getUser() != null && order.getUser().getId().equals(user.getId()));
        model.addAttribute("protectionDays", orderService.getBuyerProtectionDays());
        model.addAttribute("disputeReasons", DisputeReason.values());
        model.addAttribute("pageTitle", "Order " + orderNumber + " — MME");
        return "account/order-detail";
    }

    @PostMapping("/orders/{orderNumber}/confirm-delivery")
    public String confirmDelivery(@PathVariable String orderNumber,
                                  @ModelAttribute("currentUser") User user,
                                  RedirectAttributes ra) {
        try {
            Order order = orderService.findByOrderNumber(orderNumber);
            orderService.confirmDelivery(order.getId(), user);
            ra.addFlashAttribute("message", "Delivery confirmed. Buyer protection is now active — you can report issues if needed.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + orderNumber;
    }

    @PostMapping("/orders/{orderNumber}/dispute")
    public String openDispute(@PathVariable String orderNumber,
                              @ModelAttribute("currentUser") User user,
                              @RequestParam String reason,
                              @RequestParam(required = false) String note,
                              RedirectAttributes ra) {
        try {
            Order order = orderService.findByOrderNumber(orderNumber);
            orderService.openDispute(order.getId(), user, DisputeReason.valueOf(reason), note);
            ra.addFlashAttribute("message", "Dispute submitted. Your payment remains held by MME until this is resolved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + orderNumber;
    }

    @GetMapping("/account/wishlist")
    public String wishlist(@ModelAttribute("currentUser") User user, Model model) {
        model.addAttribute("items", wishlistService.getWishlist(user.getId()));
        model.addAttribute("pageTitle", "Wishlist — MME");
        model.addAttribute("activeNav", "wishlist");
        return "account/wishlist";
    }

    @GetMapping("/account/cart")
    public String cart(@ModelAttribute("currentUser") User user, Model model) {
        model.addAttribute("groupedCart", cartService.getCartGroupedBySeller(user.getId()));
        model.addAttribute("subtotal", cartService.selectedSubtotal(user.getId()));
        model.addAttribute("pageTitle", "Shopping Cart — MME");
        model.addAttribute("activeNav", "cart");
        return "account/cart";
    }

    @GetMapping("/account/addresses")
    public String addresses(@ModelAttribute("currentUser") User user, Model model) {
        model.addAttribute("addresses", userService.getAddresses(user.getId()));
        model.addAttribute("pageTitle", "Addresses — MME");
        model.addAttribute("activeNav", "addresses");
        return "account/addresses";
    }

    @PostMapping("/account/addresses")
    public String saveAddress(@ModelAttribute("currentUser") User user,
                              @RequestParam String fullName,
                              @RequestParam String phone,
                              @RequestParam String province,
                              @RequestParam String district,
                              @RequestParam(required = false) String commune,
                              @RequestParam String street,
                              @RequestParam String country,
                              RedirectAttributes ra) {
        if (country == null || country.isBlank()) {
            ra.addFlashAttribute("error", "Country is required");
            return "redirect:/account/addresses";
        }
        Address addr = Address.builder()
                .user(user).fullName(fullName).phone(phone)
                .province(province).district(district).commune(commune)
                .street(street).country(country.trim())
                .defaultAddress(true).build();
        userService.saveAddress(addr);
        ra.addFlashAttribute("message", "Address added successfully");
        return "redirect:/account/addresses";
    }

    @PostMapping("/account/addresses/{id}/edit")
    public String editAddress(@ModelAttribute("currentUser") User user,
                              @PathVariable Long id,
                              @RequestParam String fullName,
                              @RequestParam String phone,
                              @RequestParam String province,
                              @RequestParam String district,
                              @RequestParam(required = false) String commune,
                              @RequestParam String street,
                              @RequestParam String country,
                              RedirectAttributes ra) {
        try {
            userService.updateAddress(user.getId(), id, fullName, phone, province, district, commune, street, country);
            ra.addFlashAttribute("message", "Address updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account/addresses";
    }

    @PostMapping("/account/addresses/{id}/delete")
    public String deleteAddress(@ModelAttribute("currentUser") User user,
                                @PathVariable Long id,
                                RedirectAttributes ra) {
        try {
            userService.deleteAddress(user.getId(), id);
            ra.addFlashAttribute("message", "Address deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account/addresses";
    }

    @GetMapping("/account/settings")
    public String settings(@ModelAttribute("currentUser") User user, Model model) {
        Seller seller = sellerService.findByUserId(user.getId());
        if (seller != null) model.addAttribute("seller", seller);
        model.addAttribute("pageTitle", "Account Settings — MME");
        model.addAttribute("activeNav", "account-settings");
        return "account/settings";
    }

    @PostMapping("/account/settings")
    public String updateSettings(@ModelAttribute("currentUser") User user,
                                 @RequestParam String fullName,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam(required = false) String avatarUrl,
                                 @RequestParam(required = false) MultipartFile avatarFile,
                                 @RequestParam(required = false) String coverUrl,
                                 @RequestParam(required = false) MultipartFile coverFile,
                                 RedirectAttributes ra) {
        try {
            String resolvedAvatar = avatarUrl;
            if (avatarFile != null && !avatarFile.isEmpty()) {
                resolvedAvatar = fileStorageService.storeImage(avatarFile, "avatars");
            }
            String resolvedCover = coverUrl;
            if (coverFile != null && !coverFile.isEmpty()) {
                resolvedCover = fileStorageService.storeImage(coverFile, "avatars/cover photos");
            }
            userService.updateProfile(user.getId(), fullName, phone, resolvedAvatar, resolvedCover);
            ra.addFlashAttribute("message", "Profile updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Could not update profile");
        }
        return "redirect:/account/settings";
    }

    @GetMapping("/notifications")
    public String notifications(@ModelAttribute("currentUser") User user, Model model) {
        model.addAttribute("notifications", notificationService.getUserNotifications(user.getId()));
        model.addAttribute("pageTitle", "Notifications — MME");
        model.addAttribute("activeNav", "notifications");
        return "account/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllRead(@ModelAttribute("currentUser") User user) {
        notificationService.markAllRead(user.getId());
        return "redirect:/notifications";
    }

    @GetMapping("/become-seller")
    public String becomeSeller(@ModelAttribute("currentUser") User user, Model model,
                               RedirectAttributes ra) {
        // Must register / log in as a customer (or existing seller viewing status) first
        if (user == null) {
            ra.addFlashAttribute("error", "Please register or log in as a customer before applying to become a seller.");
            return "redirect:/login";
        }
        Seller existing = sellerService.findByUserId(user.getId());
        model.addAttribute("existingSeller", existing);
        model.addAttribute("pageTitle", "Become a Seller — MME");
        return "sellers/become";
    }

    @PostMapping("/become-seller")
    public String submitSellerApplication(@ModelAttribute("currentUser") User user,
                                          @RequestParam String storeName,
                                          @RequestParam String ownerName,
                                          @RequestParam String phone,
                                          @RequestParam String address,
                                          @RequestParam String location,
                                          @RequestParam String country,
                                          @RequestParam(required = false) String businessInfo,
                                          @RequestParam(required = false) String description,
                                          @RequestParam(required = false) String idDocument,
                                          RedirectAttributes ra) {
        if (user == null) {
            ra.addFlashAttribute("error", "Please register or log in as a customer before applying to become a seller.");
            return "redirect:/login";
        }
        try {
            sellerService.apply(user, storeName, ownerName, phone, address, location, country,
                    businessInfo, description, idDocument);
            ra.addFlashAttribute("message", "Application submitted! Pending admin review.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/become-seller";
    }
}
