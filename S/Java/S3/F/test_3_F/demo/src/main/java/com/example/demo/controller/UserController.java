package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// [ @Controller ] tells Spring Boot that a Java class handles HTTP requests from the browser.
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    // [ Model ] is used to send data from the Controller to the HTML View.
    public String list(Model model) {
        // [ addAttribute() ] is used to put data into the Model so the View (HTML page) can use it.
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("isEdit", false);
        return "users/form";
    }

    @GetMapping("/edit/{id}")
    // [ RedirectAttributes ] is used to send temporary data from one Controller method to another
    // Controller method when you redirect to a new URL.
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return userService.findById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("isEdit", true);
                    return "users/form";
                })
                .orElseGet(() -> {
                    // [ addFlashAttribute() ] is used to send temporary data through a redirect.
                    redirectAttributes.addFlashAttribute("error", "User not found");
                    return "redirect:/users";
                });
    }

    @PostMapping("/save")
    // [ @RequestParam ] is used to get a value from a URL request parameter and put it into a Controller method.
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String userName,
                       @RequestParam(required = false) String password,
                       @RequestParam String role,
                       RedirectAttributes redirectAttributes) {
        try {
            if (id == null) {
                if (password == null || password.isBlank()) {
                    redirectAttributes.addFlashAttribute("error", "Password is required");
                    return "redirect:/users/new";
                }
                userService.create(userName, password, role);
                redirectAttributes.addFlashAttribute("success", "User created");
            } else {
                userService.update(id, userName, password, role);
                redirectAttributes.addFlashAttribute("success", "User updated");
            }
        // [ IllegalArgumentException ] is a Java exception used when a method receives an argument (input value)
        // that is not valid.
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return id == null ? "redirect:/users/new" : "redirect:/users/edit/" + id;
        }
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "User deleted");
        return "redirect:/users";
    }
}
