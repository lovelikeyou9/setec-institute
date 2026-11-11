package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsBySku(String sku);

    @Query("SELECT COALESCE(SUM(p.stock * p.costPrice), 0) FROM Product p WHERE p.costPrice IS NOT NULL")
    BigDecimal sumInventoryValuation();

    long countByStockGreaterThan(int stock);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.warnMin IS NOT NULL AND p.stock <= p.warnMin")
    long countCriticalSkus();
}
