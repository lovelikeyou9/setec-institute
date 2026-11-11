package com.example.demo.controller;

import com.example.demo.model.Staff;
import com.example.demo.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService service;

    @GetMapping
    public List<Staff> getAllStaff() { return service.getAllStaff(); }

    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffBySId(@PathVariable Long id) {
        Staff staff = service.getStaffBySId(id);
        return staff != null ? ResponseEntity.ok(staff): ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Staff> addStaff(@RequestBody Staff staff) {
        Staff savedStudent = service.saveStaff(staff);
        return ResponseEntity.ok(savedStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable Long id, @RequestBody Staff staffDetails) {
        Staff staff = service.getStaffBySId(id);

        if (staff != null) {
            staff.setUserName(staffDetails.getUserName());
            staff.setPassword(staffDetails.getPassword());
            if (staffDetails.getRole() != null) {
                staff.setRole(staffDetails.getRole());
            }

            return ResponseEntity.ok(service.saveStaff(staff));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        service.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

}
