package com.example.demo.repository;

import com.example.demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    List<Category> findByActiveTrueOrderBySortOrderAsc();
    boolean existsBySlug(String slug);
    boolean existsByNameIgnoreCase(String name);
    List<Category> findByNameContainingIgnoreCaseOrSlugContainingIgnoreCaseOrderBySortOrderAsc(String name, String slug);
}
