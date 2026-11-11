package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long staffId;
    private String staffUserName;
    private Long productId;
    private String productName;
    private int qty;
    private double price;
    private double total;
    private LocalDateTime purchaseDate;

    public Purchase() {}

    public Purchase(Long staffId, String staffUserName, Long productId, String productName,
                    int qty, double price, double total, LocalDateTime purchaseDate) {
        this.staffId = staffId;
        this.staffUserName = staffUserName;
        this.productId = productId;
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.total = total;
        this.purchaseDate = purchaseDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getStaffUserName() { return staffUserName; }
    public void setStaffUserName(String staffUserName) { this.staffUserName = staffUserName; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
}
