package com.example.jwt.repository;

import com.example.jwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// [ JpaRepository ] is a Spring Data JPA interface that gives your repository ready-made database operations.
public interface UserRepository extends JpaRepository<User, Long> {
    // [ Optional ] is a Java class used to represent a value that may exist or may not exist.
    // "Search for a User. There might be a User, or there might be no User."
    Optional<User> findByUsername(String username);
}
