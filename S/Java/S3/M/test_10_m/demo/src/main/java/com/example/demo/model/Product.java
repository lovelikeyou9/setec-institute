package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.time.LocalDate;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long PId;
    private String PName;
    private int SQty;
    private double Price;
    private LocalDate ExpiredDate; // [ LocalDate ] is a Java type used to store a date only — no time.
    private Long CatID;

    public Product() {}

    public Product(String PName, int SQty, double Price, LocalDate ExpiredDate, Long CatID) {
        this.PName = PName;
        this.SQty = SQty;
        this.Price = Price;
        this.ExpiredDate = ExpiredDate;
        this.CatID = CatID;
    }

    public Long getPId() { return PId; }
    public void setPId(Long PId) { this.PId = PId; }

    public String getPName() { return PName; }
    public void setPName(String PName) { this.PName = PName; }

    public int getSQty() { return SQty; }
    public void setSQty(int SQty) { this.SQty = SQty; }

    public double getPrice() { return Price; }
    public void setPrice(double Price) { this.Price = Price; }

    public LocalDate getExpiredDate() { return ExpiredDate; }
    public void setExpiredDate(LocalDate expiredDate) { this.ExpiredDate = expiredDate; }

    public Long getCatID() { return CatID; }
    public void setCatID(Long CatID) { this.CatID = CatID; }

}
