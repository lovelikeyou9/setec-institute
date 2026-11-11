package com.example.demo.service;

import com.example.demo.dto.brand.BrandRequest;
import com.example.demo.dto.brand.BrandResponse;
import com.example.demo.entity.Brand;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<BrandResponse> findAll() {
        return brandRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public BrandResponse findById(Long id) {
        return toResponse(getBrand(id));
    }

    @Transactional
    public BrandResponse create(BrandRequest request) {
        if (brandRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ApiException("Brand name already exists", HttpStatus.CONFLICT);
        }
        if (brandRepository.existsBySlug(request.getSlug())) {
            throw new ApiException("Brand slug already exists", HttpStatus.CONFLICT);
        }
        Brand brand = Brand.builder()
                .name(request.getName().trim())
                .slug(request.getSlug().trim().toLowerCase())
                .logoUrl(request.getLogoUrl())
                .description(request.getDescription())
                .active(request.getActive() == null || request.getActive())
                .build();
        return toResponse(brandRepository.save(brand));
    }

    @Transactional
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = getBrand(id);
        brandRepository.findByNameIgnoreCase(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ApiException("Brand name already exists", HttpStatus.CONFLICT);
            }
        });
        brandRepository.findBySlug(request.getSlug()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ApiException("Brand slug already exists", HttpStatus.CONFLICT);
            }
        });
        brand.setName(request.getName().trim());
        brand.setSlug(request.getSlug().trim().toLowerCase());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setDescription(request.getDescription());
        if (request.getActive() != null) {
            brand.setActive(request.getActive());
        }
        return toResponse(brandRepository.save(brand));
    }

    @Transactional
    public void delete(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ApiException("Brand not found", HttpStatus.NOT_FOUND);
        }
        brandRepository.deleteById(id);
    }

    public Brand getBrand(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ApiException("Brand not found", HttpStatus.NOT_FOUND));
    }

    private BrandResponse toResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .logoUrl(brand.getLogoUrl())
                .description(brand.getDescription())
                .active(brand.isActive())
                .build();
    }
}
