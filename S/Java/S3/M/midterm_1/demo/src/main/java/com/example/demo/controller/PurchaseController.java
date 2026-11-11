package com.example.demo.controller;

import com.example.demo.model.Purchase;
import com.example.demo.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService service;

    @GetMapping
    public List<Purchase> getAllPurchases() { return service.getAllPurchases(); }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(@PathVariable Long id) {
        Purchase purchase = service.getPurchaseById(id);
        return purchase != null ? ResponseEntity.ok(purchase) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Purchase> addPurchase(@RequestBody Purchase purchase) {
        Purchase savedPurchase = service.savePurchase(purchase);
        return ResponseEntity.ok(savedPurchase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Purchase> updatePurchase(@PathVariable Long id, @RequestBody Purchase purchaseDetails) {
        Purchase purchase = service.getPurchaseById(id);

        if (purchase != null) {
            purchase.setStaffId(purchaseDetails.getStaffId());
            purchase.setStaffUserName(purchaseDetails.getStaffUserName());
            purchase.setProductId(purchaseDetails.getProductId());
            purchase.setProductName(purchaseDetails.getProductName());
            purchase.setQty(purchaseDetails.getQty());
            purchase.setPrice(purchaseDetails.getPrice());
            purchase.setTotal(purchaseDetails.getTotal());
            purchase.setPurchaseDate(purchaseDetails.getPurchaseDate());

            return ResponseEntity.ok(service.savePurchase(purchase));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        service.deletePurchase(id);
        return ResponseEntity.noContent().build();
    }

}
