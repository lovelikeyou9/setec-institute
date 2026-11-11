
// The [ repository ] folder contains classes/interfaces that communicate with the database.
// The repository can do database operations such as:
// findById()       → find user
// save()           → insert/update user
// delete()         → delete user
// findAll()        → get all users

// Mean
// Ask the repository to find the User with this ID in the database.

// Repository = handles communication with the database.

package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// [ JpaRepository ] is used to communicate with the database and perform CRUD operations without writing SQL manually.
public interface UserRepository extends JpaRepository<User, Long> {

    // [ Optional ] in Java is a container that may contain a value or may be empty.
    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);

    boolean existsByUserNameAndIdNot(String userName, Long id);
}
