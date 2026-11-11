package com.example.demo.service;

import com.example.demo.dto.product.ProductRequest;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exception.ApiException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public List<ProductResponse> findAll(
            Long categoryId,
            String search,
            Boolean featured,
            Boolean flashDrop,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            String sort
    ) {
        var spec = ProductSpecification.withFilters(
                categoryId, search, featured, flashDrop, brand, minPrice, maxPrice, inStock
        );
        return productRepository.findAll(spec, resolveSort(sort))
                .stream()
                .map(EntityMapper::toProductResponse)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return EntityMapper.toProductResponse(getProduct(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new ApiException("SKU already exists", HttpStatus.CONFLICT);
        }
        Category category = categoryService.getCategory(request.getCategoryId());
        Product product = mapRequestToEntity(new Product(), request, category);
        return EntityMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);
        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new ApiException("SKU already exists", HttpStatus.CONFLICT);
        }
        Category category = categoryService.getCategory(request.getCategoryId());
        mapRequestToEntity(product, request, category);
        return EntityMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ApiException("Product not found", HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found", HttpStatus.NOT_FOUND));
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "id");
        }
        return switch (sort) {
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            case "nameAsc" -> Sort.by(Sort.Direction.ASC, "name");
            case "nameDesc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.by(Sort.Direction.DESC, "id");
        };
    }

    private Product mapRequestToEntity(Product product, ProductRequest request, Category category) {
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setCategory(category);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setWarnMin(request.getWarnMin());
        product.setBinLocation(request.getBinLocation());
        product.setImageUrl(request.getImageUrl());
        product.setArchitecture(request.getArchitecture());
        product.setRating(request.getRating());
        product.setReviewCount(request.getReviewCount());
        product.setFeatured(Boolean.TRUE.equals(request.getFeatured()));
        product.setFlashDrop(Boolean.TRUE.equals(request.getFlashDrop()));
        product.setCostPrice(request.getCostPrice());
        product.setSpecsJson(request.getSpecsJson());
        product.setTags(request.getTags());
        return product;
    }
}
