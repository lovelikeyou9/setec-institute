
package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<Category> getAllCategories() { return repository.findAll(); }

    public Category getCategoryByCatID(Long CatID) { return repository.findById(CatID).orElse(null); }

    // POST PUT
    public Category saveCategory(Category category) { return repository.save(category); }

    public void deleteCategory(Long CatID) { repository.deleteById(CatID); }

}
