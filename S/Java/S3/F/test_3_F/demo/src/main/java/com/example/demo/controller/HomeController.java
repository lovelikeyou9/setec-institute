
// The [ controller ] folder contains classes that receive requests from the user's browser/client and decide
// what response to return.
// Controller = handles requests and responses between the user/client and your application.

package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        // [ Authentication ] in Spring Security represents the currently authenticated (logged-in) user.
        // [ SecurityContextHolder ] is used to store and access information about the currently logged-in user
        // in Spring Security.
        // [ getContext() ] is used to get the current SecurityContext from SecurityContextHolder.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // [ isAuthenticated() ] is used to check whether the current user is authenticated (logged in) in Spring Security.
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
        }
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
        }

        long totalUsers = userService.findAll().size();
        long adminCount = userService.findAll().stream()
                // [ filter() ] in Spring Security, it is used to apply a security filter to HTTP requests.
                // [ equalsIgnoreCase() ] is used in Java to compare two strings without caring about
                // uppercase or lowercase letters.
                .filter(u -> "admin".equalsIgnoreCase(u.getRole()))
                .count();
        long userCount = userService.findAll().stream()
                .filter(u -> "user".equalsIgnoreCase(u.getRole()))
                .count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("users", userService.findAll());
        return "admin";
    }

    @GetMapping("/user")
    public String user(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
        }
        return "user/index";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
