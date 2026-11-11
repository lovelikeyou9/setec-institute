package com.example.demo.controller;

import com.example.demo.exception.CustomException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/test1")
public class Controller1 {

    @GetMapping("/{id}")
    public String getById(@PathVariable int id) {
        if (id < 0) {
            throw new CustomException("ID cannot be negative");
        }
        return "Test 1 with ID: " + id;
    }
}
