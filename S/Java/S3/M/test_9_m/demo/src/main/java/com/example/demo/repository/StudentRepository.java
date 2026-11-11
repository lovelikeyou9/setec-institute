package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long>{
}

// [ JpaRepository ] is used to communicate with the database without writing SQL CRUD queries manually.
// It is provided by Spring Data JPA.