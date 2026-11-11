package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.ProductService;
import com.example.demo.service.PublicProfileService;
import com.example.demo.service.SellerFollowService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final PublicProfileService publicProfileService;
    private final ProductService productService;
    private final SellerFollowService sellerFollowService;
    private final UserService userService;

    /** Resolve logged-in user — do not bind @ModelAttribute User on POST (empty form overwrites). */
    private User loggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            return userService.findByEmail(auth.getName());
        } catch (Exception e) {
            return null;
        }
    }

    /** Public seller profile / storefront */
    @GetMapping({"/seller/{id}", "/profile/seller/{id}"})
    public String sellerProfile(@PathVariable Long id,
                                @RequestParam(defaultValue = "0") int page,
                                Model model,
                                RedirectAttributes ra) {
        try {
            User viewer = loggedInUser();
            Long viewerId = viewer != null ? viewer.getId() : null;
            model.addAllAttributes(publicProfileService.sellerProfile(id, viewerId));
            model.addAttribute("products", productService.findBySeller(id, page, 12));
            Object sellerName = model.getAttribute("seller") != null
                    ? ((com.example.demo.model.Seller) model.getAttribute("seller")).getStoreName()
                    : "Seller";
            model.addAttribute("pageTitle", sellerName + " — Profile");
            return "profiles/seller";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/sellers";
        }
    }

    @PostMapping("/seller/{id}/follow")
    public String toggleFollow(@PathVariable Long id,
                               @RequestParam(required = false) String redirect,
                               RedirectAttributes ra) {
        try {
            boolean nowFollowing = sellerFollowService.toggle(loggedInUser(), id);
            ra.addFlashAttribute("message", nowFollowing ? "You are now following this store." : "Unfollowed this store.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        if (redirect != null && !redirect.isBlank() && redirect.startsWith("/") && !redirect.startsWith("//")) {
            return "redirect:" + redirect;
        }
        return "redirect:/seller/" + id;
    }

    /** Public customer / user profile (privacy-friendly) */
    @GetMapping({"/profile/{id}", "/u/{id}"})
    public String customerProfile(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAllAttributes(publicProfileService.customerProfile(id));
            model.addAttribute("pageTitle", model.getAttribute("displayName") + " — Profile");
            return "profiles/customer";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }
}
