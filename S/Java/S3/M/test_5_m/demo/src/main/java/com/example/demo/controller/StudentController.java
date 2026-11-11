
package com.example.demo.controller;

import com.example.demo.model.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class StudentController {

    @GetMapping("/student")
    public String showStudent(Model model) {

        // Student student = new Student( 1, "Veasna", "Information Technology");

        List<Student> students = Arrays.asList(
            new Student(1, "Chou Yamata", "Software Engineer", "Male", 95.5),
            new Student(2, "Sokha", "Computer Science", "Male", 88.0),
            new Student(3, "Dara", "Software Engineering", "Female", 91.5)
        );

        model.addAttribute("students", students);
        // [ model.addAttribute() ] is used to send data from the Controller to the View (Thymeleaf page).

        return "student";
    }

}