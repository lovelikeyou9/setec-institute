package com.example.demo.service;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> withFilters(
            Long categoryId,
            String search,
            Boolean featured,
            Boolean flashDrop,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock
    ) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (categoryId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("category").get("id"), categoryId));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("brand")), pattern),
                        cb.like(cb.lower(root.get("sku")), pattern),
                        cb.like(cb.lower(root.get("tags")), pattern)
                ));
            }
            if (featured != null) {
                predicates = cb.and(predicates, cb.equal(root.get("featured"), featured));
            }
            if (flashDrop != null) {
                predicates = cb.and(predicates, cb.equal(root.get("flashDrop"), flashDrop));
            }
            if (brand != null && !brand.isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));
            }
            if (minPrice != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (Boolean.TRUE.equals(inStock)) {
                predicates = cb.and(predicates, cb.greaterThan(root.get("stock"), 0));
            }
            return predicates;
        };
    }
}
