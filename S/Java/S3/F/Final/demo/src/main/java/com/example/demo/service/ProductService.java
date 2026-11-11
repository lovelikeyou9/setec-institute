package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public Page<Product> searchProducts(String q, Long categoryId, Long brandId,
                                        BigDecimal minPrice, BigDecimal maxPrice,
                                        String condition, Integer minRating,
                                        Boolean verifiedSeller, Boolean inStock,
                                        String sort, int page, int size) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            preds.add(cb.isTrue(root.get("active")));
            if (q != null && !q.isBlank()) {
                String like = "%" + q.toLowerCase() + "%";
                preds.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }
            if (categoryId != null) preds.add(cb.equal(root.get("category").get("id"), categoryId));
            if (brandId != null) preds.add(cb.equal(root.get("brand").get("id"), brandId));
            if (minPrice != null) preds.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            if (maxPrice != null) preds.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            if (condition != null && !condition.isBlank()) {
                preds.add(cb.equal(root.get("conditionType"), ProductCondition.valueOf(condition)));
            }
            if (minRating != null) preds.add(cb.greaterThanOrEqualTo(root.get("rating"), minRating.doubleValue()));
            if (Boolean.TRUE.equals(verifiedSeller)) {
                preds.add(root.get("seller").get("level").in(
                        SellerLevel.VERIFIED, SellerLevel.TRUSTED, SellerLevel.TOP));
            }
            if (Boolean.TRUE.equals(inStock)) preds.add(cb.greaterThan(root.get("stock"), 0));
            return cb.and(preds.toArray(new Predicate[0]));
        };

        Sort sortObj = switch (sort == null ? "recommended" : sort) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "popular" -> Sort.by(Sort.Direction.DESC, "soldCount");
            case "rating" -> Sort.by(Sort.Direction.DESC, "rating");
            case "price_asc", "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc", "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> Sort.by(Sort.Direction.DESC, "soldCount");
        };

        return productRepository.findAll(spec, PageRequest.of(page, size, sortObj));
    }

    public Product findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getFlashDeals() {
        return productRepository.findByFlashDealTrueAndActiveTrueOrderByDiscountPercentDesc();
    }

    public List<Product> getFeatured(int limit) {
        return productRepository.findByFeaturedTrueAndActiveTrueOrderBySoldCountDesc(PageRequest.of(0, limit));
    }

    public List<Product> getByCategorySlug(String slug, int limit) {
        return productRepository.findByCategorySlugAndActiveTrue(
                slug, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "soldCount"))).getContent();
    }

    public List<String> suggest(String q) {
        if (q == null || q.isBlank()) return List.of();
        return productRepository.suggestNames(q, PageRequest.of(0, 8));
    }

    public List<Category> getCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    public List<Brand> getBrands() {
        return brandRepository.findByActiveTrueOrderByNameAsc();
    }

    public Category findCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public Product save(Product product) {
        if (product.getCategory() != null
                && (product.getColorOptions() == null && product.getStorageOptions() == null && product.getRamOptions() == null)) {
            ProductVariantOptions.apply(product, product.getCategory());
        }
        return productRepository.save(product);
    }

    public List<Product> adminSearch(String q) {
        return adminSearch(q, null, null, "newest");
    }

    public List<Product> adminSearch(String q, Long categoryId, Long brandId, String sort) {
        List<Product> list = (q == null || q.isBlank())
                ? productRepository.findAll()
                : productRepository.adminSearch(q.trim());
        return filterAndSort(list, categoryId, brandId, sort);
    }

    public List<Product> sellerSearch(Long sellerId, String q) {
        return sellerSearch(sellerId, q, null, null, "newest");
    }

    public List<Product> sellerSearch(Long sellerId, String q, Long categoryId, Long brandId, String sort) {
        List<Product> list = (q == null || q.isBlank())
                ? productRepository.findBySellerIdOrderByIdDesc(sellerId)
                : productRepository.sellerSearch(sellerId, q.trim());
        return filterAndSort(list, categoryId, brandId, sort);
    }

    public long countBySellerAndCategory(Long sellerId, Long categoryId) {
        return productRepository.findBySellerIdOrderByIdDesc(sellerId).stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                .count();
    }

    public long countBySellerAndBrand(Long sellerId, Long brandId) {
        return productRepository.findBySellerIdOrderByIdDesc(sellerId).stream()
                .filter(p -> p.getBrand() != null && p.getBrand().getId().equals(brandId))
                .count();
    }

    private List<Product> filterAndSort(List<Product> list, Long categoryId, Long brandId, String sort) {
        return list.stream()
                .filter(p -> categoryId == null || (p.getCategory() != null && categoryId.equals(p.getCategory().getId())))
                .filter(p -> brandId == null || (p.getBrand() != null && brandId.equals(p.getBrand().getId())))
                .sorted(productComparator(sort))
                .toList();
    }

    private Comparator<Product> productComparator(String sort) {
        String key = sort == null ? "newest" : sort;
        return switch (key) {
            case "name_asc", "name-asc" -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
            case "name_desc", "name-desc" -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed();
            case "price_asc", "price-asc" -> Comparator.comparing(Product::getPrice);
            case "price_desc", "price-desc" -> Comparator.comparing(Product::getPrice).reversed();
            case "stock_asc", "stock-asc" -> Comparator.comparing(Product::getStock);
            case "stock_desc", "stock-desc" -> Comparator.comparing(Product::getStock).reversed();
            case "popular" -> Comparator.comparing(Product::getSoldCount, Comparator.nullsLast(Integer::compareTo)).reversed();
            case "oldest" -> Comparator.comparing(Product::getId);
            default -> Comparator.comparing(Product::getId).reversed(); // newest
        };
    }

    @Transactional
    public Product update(Long id, String name, Long categoryId, Long brandId, String description,
                          String specifications, BigDecimal price, BigDecimal originalPrice,
                          Integer stock, String sku, String warranty, String imageUrl, Boolean active) {
        Product product = findById(id);
        product.setName(name.trim());
        product.setCategory(categoryRepository.findById(categoryId).orElseThrow());
        product.setBrand(brandRepository.findById(brandId).orElseThrow());
        product.setDescription(description);
        product.setSpecifications(specifications);
        product.setPrice(price);
        product.setOriginalPrice(originalPrice != null ? originalPrice : price);
        if (originalPrice != null && originalPrice.compareTo(price) > 0) {
            product.setDiscountPercent(originalPrice.subtract(price).multiply(BigDecimal.valueOf(100))
                    .divide(originalPrice, 0, java.math.RoundingMode.HALF_UP).intValue());
        } else {
            product.setDiscountPercent(0);
        }
        product.setStock(stock);
        product.setSku(sku);
        product.setWarranty(warranty);
        if (imageUrl != null && !imageUrl.isBlank()) {
            product.setImageUrl(imageUrl.trim());
            product.setGalleryUrls(imageUrl.trim());
        }
        if (active != null) product.setActive(active);
        ProductVariantOptions.apply(product, product.getCategory());
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        try {
            productRepository.delete(product);
        } catch (Exception e) {
            // Fallback soft-delete if product is referenced by orders/cart
            product.setActive(false);
            productRepository.save(product);
        }
    }

    public Page<Product> findBySeller(Long sellerId, int page, int size) {
        return productRepository.findBySellerIdAndActiveTrue(sellerId, PageRequest.of(page, size));
    }

    public List<Product> findBySeller(Long sellerId) {
        return productRepository.findBySellerIdAndActiveTrue(sellerId);
    }

    public long countActive() {
        return productRepository.countByActiveTrue();
    }
}
