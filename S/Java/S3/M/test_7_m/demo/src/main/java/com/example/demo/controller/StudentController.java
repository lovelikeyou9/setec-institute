package com.example.demo.controller;

import java.util.*;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Student;

@RestController
// [ @RestController ] tells Spring that this class handles HTTP requests and that the return
// values of its methods should be sent directly as the HTTP response body (usually JSON).

// When you visit:
//   GET /students
// then
// the response is:
//   Hello

//  Without [ @RestController ]:
//  Spring thinks "Hello" is the name of a view/page (like hello.html), not the response content.


@RequestMapping("/students")
//  [ @RequestMapping ] is used to define the URL path (route) for a controller or a method.
//  Usually, it sets the base URL for all endpoints inside a controller.
//  GET http://localhost:8080/students

//  Without [ @RequestMapping ]:
//  @RestController
//  public class StudentController {
//
//      @GetMapping("/students")
//      public List<Student> all() {
//          return list;
//      }
//
//  }

public class StudentController {

    List<Student> list = new ArrayList<>(List.of
        (
        new Student(1L, "Alice"),
        new Student(2L, "Bob")
        )
    );

    @GetMapping
    public List<Student> all() { return list; }
    // @RestController makes Spring automatically convert

    @GetMapping("/{id}")
    public Student one(@PathVariable Long id) {
        return list.stream().filter(s->s.getId().equals(id)).findFirst().orElse(null);
    }
    // [ @GetMapping ] is used in Spring Boot to handle HTTP GET requests.
    // A GET request is used to retrieve (read) data from the server.

    // [ .stream() ] is a Java feature used to process collections (List, Set, etc.) in a cleaner way.
    // It creates a Stream from a collection, allowing you to filter, transform, sort, or collect data.

    // [ .filter() ] is used to select only the elements that match a condition.

    // [ s->s ] is a lambda expression in Java.
    // (parameter) -> (expression)

    // [ @PathVariable ] is used to get a value from the URL path and put it into a Java variable.
    // GET /students/2

    @PostMapping
    public Student add(@RequestBody Student s) {
        list.add(s);
        return s;
    }
    // [ @PostMapping ] is used in Spring Boot to handle HTTP POST requests.
    // A [ POST ] request is usually used to send data to the server to create something new.
    // Meaning:
    // POST /students
    // When the client sends student data:
    // {
    //     "id": 3,
    //         "name": "Charlie"
    // }

    // [ @RequestBody ] is used to get data from the HTTP request body and convert it into a Java object.
    // [ @RequestBody ] takes the JSON sent by the client (Postman, frontend, mobile app, etc.) and
    // converts it into a [ Student ] object.

    @PutMapping("/{id}")
    public Student upd(@PathVariable Long id, @RequestBody Student s) {
        for(Student x : list) {
            if(x.getId().equals(id)) {
                x.setName(s.getName());
                return x;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public String del(@PathVariable Long id) {
        list.removeIf(s->s.getId().equals(id));
        return "Deleted";
    }


}
