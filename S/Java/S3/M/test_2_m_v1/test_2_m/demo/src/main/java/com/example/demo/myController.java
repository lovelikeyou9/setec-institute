package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class myController {

    @GetMapping("/")
    public String cv() {
        return "cv";
    }

    @GetMapping("/user")
    public String user() {
        return "index";
    }

    @GetMapping("/myUser")
    public String myUser(Model model) {
        model.addAttribute("username", "John Doe");
        model.addAttribute("email", "john.doe@example.com");
        model.addAttribute("role", "Developer");
        model.addAttribute("joinDate", "January 2024");
        model.addAttribute("bio", "Passionate software developer with experience in Java, Spring Boot, and web technologies.");
        return "myUser";
    }

    @GetMapping("/myAdmin")
    public String myAdmin(Model model) {
        model.addAttribute("totalUsers", 1248);
        model.addAttribute("activeSessions", 87);
        model.addAttribute("revenue", 45230);
        model.addAttribute("orders", 356);
        return "myAdmin";
    }
}
