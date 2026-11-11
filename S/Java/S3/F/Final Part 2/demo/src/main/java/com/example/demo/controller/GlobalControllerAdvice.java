package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;
    private final CartService cartService;
    private final WishlistService wishlistService;
    private final NotificationService notificationService;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final CatalogNavService catalogNavService;

    @ModelAttribute("currentUser")
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                return userService.findByEmail(auth.getName());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /** Seller entity id for the logged-in seller (public store profile URL). */
    @ModelAttribute("currentSellerId")
    public Long currentSellerId() {
        User user = currentUser();
        if (user == null || user.getRole() != Role.SELLER) {
            return null;
        }
        return sellerRepository.findByUserId(user.getId()).map(s -> s.getId()).orElse(null);
    }

    @ModelAttribute("cartCount")
    public long cartCount() {
        User user = currentUser();
        return user != null ? cartService.count(user.getId()) : 0;
    }

    @ModelAttribute("wishlistCount")
    public long wishlistCount() {
        User user = currentUser();
        return user != null ? wishlistService.count(user.getId()) : 0;
    }

    @ModelAttribute("unreadNotifications")
    public long unreadNotifications() {
        User user = currentUser();
        return user != null ? notificationService.unreadCount(user.getId()) : 0;
    }

    @ModelAttribute("navCategories")
    public Object navCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    @ModelAttribute("navMenuGroups")
    public Object navMenuGroups() {
        return catalogNavService.megaMenu();
    }

    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("popularSearches")
    public List<String> popularSearches() {
        return List.of("Wi-Fi 7 Router", "DJI Mini 4 Pro", "Xbox Series X", "RTX 5070",
                "AirPods Pro", "Arduino", "PowerEdge Server", "GaN Charger");
    }

    @ModelAttribute("countryOptions")
    public List<String> countryOptions() {
        return CountryResolver.OPTIONS;
    }
}
