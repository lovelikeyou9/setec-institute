package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public List<Product> getAllProducts() { return repository.findAll(); }

    public Product getProductByPId(Long PId) { return repository.findById(PId).orElse(null); }

    public Product saveProduct(Product product) { return repository.save(product); }

    public void deleteProduct(Long PId) { repository.deleteById(PId); }

    public List<Product> getExpiredProducts() {
        LocalDate today = LocalDate.now();
        return repository.findAll().stream()
                .filter(p -> p.getExpiredDate() != null && p.getExpiredDate().isBefore(today))
                .collect(Collectors.toList());
    }

    public Product buyProduct(Long PId, int qty) {
        Product product = getProductByPId(PId);
        if (product == null || qty <= 0 || product.getSQty() < qty) {
            return null;
        }
        product.setSQty(product.getSQty() - qty);
        product.setSoldQty(product.getSoldQty() + qty);
        return repository.save(product);
    }

    public Map<Long, Integer> getSoldByCategory() {
        Map<Long, Integer> soldByCat = new HashMap<>();
        for (Product p : repository.findAll()) {
            if (p.getCatID() == null) continue;
            soldByCat.merge(p.getCatID(), p.getSoldQty(), Integer::sum);
        }
        return soldByCat;
    }

    public Long getTopSaleCategoryId() {
        Map<Long, Integer> soldByCat = getSoldByCategory();
        return soldByCat.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllProducts();
        }
        String q = keyword.toLowerCase().trim();
        return repository.findAll().stream()
                .filter(p -> p.getPName() != null && p.getPName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

}
