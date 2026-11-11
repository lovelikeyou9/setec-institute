package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> search(String q) {
        return search(q, "sort");
    }

    public List<Category> search(String q, String sort) {
        List<Category> list = (q == null || q.isBlank())
                ? findAll()
                : categoryRepository.findByNameContainingIgnoreCaseOrSlugContainingIgnoreCaseOrderBySortOrderAsc(q.trim(), q.trim());
        return list.stream().sorted(categoryComparator(sort)).toList();
    }

    private Comparator<Category> categoryComparator(String sort) {
        String key = sort == null ? "sort" : sort;
        return switch (key) {
            case "name_asc", "name-asc" -> Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER);
            case "name_desc", "name-desc" -> Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER).reversed();
            case "newest" -> Comparator.comparing(Category::getId).reversed();
            default -> Comparator.comparing(Category::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                    .thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER);
        };
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    public Category create(String name, String icon) {
        String trimmed = requireName(name);
        String slug = toSlug(trimmed);
        if (categoryRepository.existsByNameIgnoreCase(trimmed) || categoryRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }
        return categoryRepository.save(Category.builder()
                .name(trimmed)
                .slug(slug)
                .icon(icon == null || icon.isBlank() ? "📦" : icon.trim())
                .sortOrder((int) categoryRepository.count() + 1)
                .active(true)
                .build());
    }

    public Category update(Long id, String name, String icon, Boolean active) {
        Category category = findById(id);
        String trimmed = requireName(name);
        String slug = toSlug(trimmed);
        categoryRepository.findBySlug(slug).filter(c -> !c.getId().equals(id)).ifPresent(c -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        });
        if (categoryRepository.findAll().stream()
                .anyMatch(c -> !c.getId().equals(id) && c.getName().equalsIgnoreCase(trimmed))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }
        category.setName(trimmed);
        category.setSlug(slug);
        if (icon != null && !icon.isBlank()) category.setIcon(icon.trim());
        if (active != null) category.setActive(active);
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete category in use by products");
        }
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name is required");
        }
        return name.trim();
    }

    private static String toSlug(String name) {
        String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (slug.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category name");
        }
        return slug;
    }
}
