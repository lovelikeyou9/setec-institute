package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            String msg = switch (error) {
                case "disabled" -> "This account is banned or disabled. Contact support or use another account.";
                case "true", "bad_credentials" -> "Invalid email or password. Try a demo seller: techzone@computerhub.com / seller123";
                default -> "Invalid email or password";
            };
            model.addAttribute("error", msg);
        }
        if (logout != null) model.addAttribute("message", "You have been logged out");
        model.addAttribute("pageTitle", "Login — MME");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("pageTitle", "Register — MME");
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           RedirectAttributes ra) {
        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/register";
        }
        try {
            userService.register(fullName, email, phone, password);
            ra.addFlashAttribute("message", "Customer account created! Log in, then you can apply to Become a Seller.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}
