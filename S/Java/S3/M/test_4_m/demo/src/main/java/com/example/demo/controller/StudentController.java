
package com.example.demo.controller;

import com.example.demo.model.Student;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class StudentController {

    @GetMapping("/student")
    public String showStudent(Model model) {

        Student student = new Student( 1, "Veasna", "Information Technology");

        model.addAttribute("student", student);

        return "student";
    }

}