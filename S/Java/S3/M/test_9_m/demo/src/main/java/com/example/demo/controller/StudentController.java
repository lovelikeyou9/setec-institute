package com.example.demo.controller;

import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService service;


//    List<Student> list = new ArrayList<>(List.of
//            (
//                    new Student(1L, "Alice", "Male", "22", 50),
//                    new Student(2L, "Bob", "Female", "33", 74),
//                    new Student(3L,"Chou Yamata", "Male", "30", 100),
//                    new Student(4L,"Sok", "Male", "30", 90),
//                    new Student(5L,"Kaka Chan", "Female", "30", 65)
//            )
//    );

    @GetMapping
    public List<Student> getAllStudents() { return service.getAllStudents(); }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = service.getStudentById(id);
        return student != null ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
    }
    // [ ResponseEntity ] is used to control the HTTP response that your API sends back to the client
    // (Postman, frontend, mobile app, etc.).
    // It allows you to return:
    //  - Response body (data)
    //  - HTTP status code (200, 404, 201, etc.)
    //  - HTTP headers

    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student s) {
        Student savedStudent = service.saveStudent(s);
        return ResponseEntity.ok(savedStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        Student student = service.getStudentById(id);

        if(student != null) {
            student.setName(studentDetails.getName());
            student.setEmail(studentDetails.getEmail());
            student.setGender(studentDetails.getGender());
            student.setAddress(studentDetails.getAddress());

            return ResponseEntity.ok(service.saveStudent(student));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        service.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }


}
