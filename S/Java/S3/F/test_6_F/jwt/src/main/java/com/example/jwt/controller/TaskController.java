package com.example.jwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class  qTaskController {
    @GetMapping("/task")
    public String performTask() {
        return "Task performed successfully!";
    }
}
