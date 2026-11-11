package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CatID;
    private String CatName;

    public Category() {}

    public Category(String CatName) {
        this.CatName = CatName;
    }

    public Long getCatID() { return CatID; }
    public void setCatID(Long CatID) { this.CatID = CatID; }

    public String getCatName() { return CatName; }
    public void setCatName(String CatName) { this.CatName = CatName; }

}

