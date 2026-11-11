
// The [ service ] folder is where you put classes that contain the application's business logic.
// that is a good example of service logic, because it:
// - Finds the user
// - Checks whether the username already exists
// - Normalizes the role
// - Hashes the password
// - Saves the updated user

// Service = handles the application's business logic between Controller and Repository.

package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// [ @Service ] tells Spring that a class contains business logic and should be created and managed as a Spring bean.
// [ @Transactional ] is used to make a group of database operations act as one transaction.
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    // [ Optional ] is used because the user with that [ id ] might exist or might not exist.
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User create(String userName, String rawPassword, String role) {
        if (userRepository.existsByUserName(userName)) {
            // [ IllegalArgumentException() ] “The value given to this method is invalid.”
            // It is a built-in Java exception.
            throw new IllegalArgumentException("Username already exists");
        }
        // [ encode() ] is used to hash the raw password before saving it to the database.
        // 123 -> $2a$10$...
        // [ normalizeRole() ] is used to convert the role into a standard/consistent format before saving it.
        // "admin"      → "ROLE_ADMIN"
        // "Admin"      → "ROLE_ADMIN"
        // " ADMIN "    → "ROLE_ADMIN"
        // "stock"      → "ROLE_STOCK"
        // "ROLE_USER"  → "ROLE_USER"
        User user = new User(userName, passwordEncoder.encode(rawPassword), normalizeRole(role));
        return userRepository.save(user);
    }

    public User update(Long id, String userName, String rawPassword, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userRepository.existsByUserNameAndIdNot(userName, id)) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setUserName(userName);
        user.setRole(normalizeRole(role));
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "user";
        }
        return role.trim().toLowerCase();
    }
}
