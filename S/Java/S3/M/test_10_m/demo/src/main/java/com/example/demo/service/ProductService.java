package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public List<Product> getAllProducts() { return repository.findAll(); }

    public Product getProductByPId(Long PId) { return repository.findById(PId).orElse(null); }

    public Product saveProduct(Product product) { return repository.save(product); }

    public void deleteProduct(Long PId) { repository.deleteById(PId); }

}
