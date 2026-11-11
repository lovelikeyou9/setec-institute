package com.example.demo.service;

import com.example.demo.model.Staff;
import com.example.demo.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    @Autowired
    private StaffRepository repository;

    public List<Staff> getAllStaff() { return repository.findAll(); }

    public Staff getStaffBySId(Long SId) { return repository.findById(SId).orElse(null); }

    public Staff saveStaff(Staff staff) { return repository.save(staff); }

    public void deleteStaff(Long SId) { repository.deleteById(SId); }

}
