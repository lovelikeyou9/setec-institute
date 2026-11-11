package com.example.demo.service;

import com.example.demo.model.Purchase;
import com.example.demo.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository repository;

    public Purchase save(Purchase purchase) {
        return repository.save(purchase);
    }

    public List<Purchase> getByStaff(Long staffId) {
        return repository.findByStaffIdOrderByPurchaseDateDesc(staffId);
    }

    public List<Purchase> getAll() {
        return repository.findAllByOrderByPurchaseDateDesc();
    }

    public double getTotalRevenue() {
        return repository.findAll().stream().mapToDouble(Purchase::getTotal).sum();
    }

    public int getTotalQtySold() {
        return repository.findAll().stream().mapToInt(Purchase::getQty).sum();
    }

    public long getOrderCount() {
        return repository.count();
    }
}
