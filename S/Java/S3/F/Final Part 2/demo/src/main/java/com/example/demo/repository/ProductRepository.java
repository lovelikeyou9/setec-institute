package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlug(String slug);
    List<Product> findByFlashDealTrueAndActiveTrueOrderByDiscountPercentDesc();
    List<Product> findByFeaturedTrueAndActiveTrueOrderBySoldCountDesc(Pageable pageable);
    List<Product> findBySellerIdAndActiveTrue(Long sellerId);
    List<Product> findBySellerIdOrderByIdDesc(Long sellerId);
    Page<Product> findBySellerIdAndActiveTrue(Long sellerId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.seller.storeName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "ORDER BY p.id DESC")
    List<Product> adminSearch(@Param("q") String q);

    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId AND (" +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY p.id DESC")
    List<Product> sellerSearch(@Param("sellerId") Long sellerId, @Param("q") String q);
    Page<Product> findByCategorySlugAndActiveTrue(String slug, Pageable pageable);
    long countBySellerId(Long sellerId);
    long countBySellerIdAndActiveTrue(Long sellerId);
    long countByActiveTrue();

    @Query("SELECT COALESCE(SUM(p.soldCount), 0) FROM Product p WHERE p.seller.id = :sellerId")
    long sumSoldCountBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Product> search(@Param("q") String q, Pageable pageable);

    @Query("SELECT p.name FROM Product p WHERE p.active = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY p.soldCount DESC")
    List<String> suggestNames(@Param("q") String q, Pageable pageable);
}
