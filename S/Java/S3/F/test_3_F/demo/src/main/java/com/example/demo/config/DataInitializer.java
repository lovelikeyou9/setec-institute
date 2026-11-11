

// The [ config ] folder contains classes that configure how your application works.
// What goes in config?
// Things such as:
// - Application configuration
// - @Bean definitions
// - CORS configuration
// - Database-related configuration
// - General application settings
// - Other setup/configuration that isn't specifically security

// Model      → what data does the application have?
//           // Example: User, Product, Category
//
// Repository → how does the application access the database?
//           // Example: find, save, update, delete
//
// Service    → what business logic should be performed?
//           // Example: check username, hash password, update user
//
// Controller → what requests does the application receive?
//           // Example: /users, /products, /login
//
// Security   → who is the user and what can the user access?
//           // Authentication → who is the user?
//           // Authorization  → what can the user access?
//
// Config     → how should the application be configured?
//           // Example: Beans, CORS, general settings


package com.example.demo.config;

import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// [ @Component ] tells Spring to create and manage an object (bean) automatically.
// "I should create an instance of CustomSuccessHandler and manage it."
// [ CommandLineRunner ] is used to run some Java code automatically when your Spring Boot application starts.
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    public DataInitializer(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userService.create("admin", "admin123", "admin");
            userService.create("user", "1234", "user");
        }
    }
}
