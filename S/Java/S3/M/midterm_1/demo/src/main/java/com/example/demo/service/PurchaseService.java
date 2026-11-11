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

    public List<Purchase> getAllPurchases() { return repository.findAllByOrderByPurchaseDateDesc(); }

    public Purchase getPurchaseById(Long id) { return repository.findById(id).orElse(null); }

    public Purchase savePurchase(Purchase purchase) { return repository.save(purchase); }

    public void deletePurchase(Long id) { repository.deleteById(id); }

    public List<Purchase> getPurchasesByStaffId(Long staffId) {
        return repository.findByStaffIdOrderByPurchaseDateDesc(staffId);
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
