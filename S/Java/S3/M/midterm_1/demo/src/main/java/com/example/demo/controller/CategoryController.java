package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService service;
    // [ @Autowired ] “Give this class the object/dependency it needs.”
    // It is mainly used for Dependency Injection (DI).

    @GetMapping
    public List<Category> getAllCategory() { return service.getAllCategories(); }
    // [ @GetMapping ]

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryByCatId(@PathVariable Long id) {
        Category category = service.getCategoryByCatID(id);
        return category != null ? ResponseEntity.ok(category): ResponseEntity.notFound().build();
    }
    // [ @PathVariable ] is used to get a value from the URL path and use it inside your controller method.


    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category savedCategory = service.saveCategory(category);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Category category = service.getCategoryByCatID(id);

        if (category != null) {
            category.setCatName(categoryDetails.getCatName());

            return ResponseEntity.ok(service.saveCategory(category));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
