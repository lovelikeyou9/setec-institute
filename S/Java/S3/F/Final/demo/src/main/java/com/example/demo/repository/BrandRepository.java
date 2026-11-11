package com.example.demo.repository;

import com.example.demo.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findBySlug(String slug);
    List<Brand> findByActiveTrueOrderByNameAsc();
    boolean existsBySlug(String slug);
    boolean existsByNameIgnoreCase(String name);
    List<Brand> findByNameContainingIgnoreCaseOrSlugContainingIgnoreCaseOrderByNameAsc(String name, String slug);
}
