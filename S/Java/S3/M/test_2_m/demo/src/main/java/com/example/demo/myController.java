package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;


// // 1
//@RestController

@Controller
public class myController {
//    // 1
//    @GetMapping("/")
//    public String Test() {
//        return "Testing first Web application for SV2.6_1";
//    }

    @GetMapping("/user")
    public String user() {
        return "index";
    }

}
