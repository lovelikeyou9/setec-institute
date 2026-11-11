package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping
    public List<Product> getAllProducts() { return service.getAllProducts(); }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductByPId(@PathVariable Long id) {
        Product product = service.getProductByPId(id);
        return product != null ? ResponseEntity.ok(product): ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = service.saveProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product product = service.getProductByPId(id);

        if (product != null) {
            product.setPName(productDetails.getPName());
            product.setSQty(productDetails.getSQty());
            product.setPrice(productDetails.getPrice());
            product.setExpiredDate(productDetails.getExpiredDate());
            product.setCatID(productDetails.getCatID());

            return ResponseEntity.ok(service.saveProduct(product));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


}
