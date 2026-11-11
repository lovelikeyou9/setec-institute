package com.hun.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    // [ Model ] means Spring MVC gives your controller a Model object so you can send data to the HTML View.
    @GetMapping("/home")
    public String home(Model model) {

        // Add authentication info to model if needed
        // [ Authentication ] is used to represent the currently logged-in user's authentication information.
        // [ SecurityContextHolder ] is used to get the security information of the currently logged-in user
        // anywhere in your Spring application.
        // [ isAuthenticated() ] is used to check whether the current user is authenticated (logged in).
        // [ getContext() ] is used to get the current SecurityContext from SecurityContextHolder.
        // “Give me the security context for the current user/request.”
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // [ addAttribute() ] is used to add data to the Model so the data can be used by the View (HTML/Thymeleaf page).
            // Put "admin" into the model with the name "username" so the home.html page can use it.
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
        }
        return "home"; // This will look for templates/home.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // This will look for templates/login.html
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
        }
        return "admin"; // This will look for templates/admin.html
    }

    @GetMapping("/user")
    public String user(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
        }
        return "user/index"; // This will look for templates/user/index.html
    }

    @GetMapping("/cv")
    public String cv(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
        }
        return "user/cv"; // This will look for templates/user/cv.html
    }

    @GetMapping("/index")
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
        }
        return "user/index"; // This will look for templates/user/index.html
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // Create this template for 403 errors
    }
}