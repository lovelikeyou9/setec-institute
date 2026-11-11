package com.example.demo.controller;

import com.example.demo.exception.CustomException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test2")
public class Controller2 {

    @GetMapping("/{value}")
    public String getValue(@PathVariable(required = false) String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new CustomException("Value cannot be null or empty");
        }
        return "Test 2 with value: " + value;
    }


    @GetMapping
    public String emptyValue() {
        throw new CustomException("Value cannot be null or empty");
    }
}
