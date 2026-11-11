package com.example.demo.service;

import com.example.demo.DTO.BrandRequest;
import com.example.demo.model.Brand;
import com.example.demo.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

/**
 * Service layer: Brand business logic (CRUD) — controllers never talk to DB directly.
 */
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    public List<Brand> search(String q) {
        return search(q, "name_asc");
    }

    public List<Brand> search(String q, String sort) {
        List<Brand> list;
        if (q == null || q.isBlank()) {
            list = findAll();
        } else {
            String term = q.trim();
            list = brandRepository.findByNameContainingIgnoreCaseOrSlugContainingIgnoreCaseOrderByNameAsc(term, term);
        }
        return list.stream().sorted(brandComparator(sort)).toList();
    }

    private Comparator<Brand> brandComparator(String sort) {
        String key = sort == null ? "name_asc" : sort;
        return switch (key) {
            case "name_desc", "name-desc" -> Comparator.comparing(Brand::getName, String.CASE_INSENSITIVE_ORDER).reversed();
            case "newest" -> Comparator.comparing(Brand::getId).reversed();
            default -> Comparator.comparing(Brand::getName, String.CASE_INSENSITIVE_ORDER);
        };
    }

    public List<Brand> findActive() {
        return brandRepository.findByActiveTrueOrderByNameAsc();
    }

    public Brand findById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
    }

    public Brand create(BrandRequest request) {
        String name = requireName(request.name());
        String slug = toSlug(name);
        if (brandRepository.existsByNameIgnoreCase(name) || brandRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Brand already exists");
        }
        Brand brand = Brand.builder()
                .name(name)
                .slug(slug)
                .logoUrl(blankToNull(request.logoUrl()))
                .active(request.active() == null || request.active())
                .build();
        return brandRepository.save(brand);
    }

    public Brand update(Long id, BrandRequest request) {
        Brand brand = findById(id);
        String name = requireName(request.name());
        String slug = toSlug(name);
        if (brandRepository.findBySlug(slug).filter(b -> !b.getId().equals(id)).isPresent()
                || brandRepository.findAll().stream()
                .anyMatch(b -> !b.getId().equals(id) && b.getName().equalsIgnoreCase(name))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Brand already exists");
        }
        brand.setName(name);
        brand.setSlug(slug);
        if (request.logoUrl() != null) {
            brand.setLogoUrl(blankToNull(request.logoUrl()));
        }
        if (request.active() != null) {
            brand.setActive(request.active());
        }
        return brandRepository.save(brand);
    }

    public void delete(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found");
        }
        try {
            brandRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete brand in use by products");
        }
    }

    public Brand createFromName(String name) {
        return create(new BrandRequest(name, null, true));
    }

    public Brand createFromName(String name, String logoUrl) {
        return create(new BrandRequest(name, logoUrl, true));
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand name is required");
        }
        return name.trim();
    }

    private static String toSlug(String name) {
        String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (slug.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid brand name");
        }
        return slug;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
