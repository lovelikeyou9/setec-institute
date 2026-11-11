package com.example.demo.repository;

import com.example.demo.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<WishlistItem> findByUserIdAndProductId(Long userId, Long productId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    long countByUserId(Long userId);
    void deleteByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT COUNT(w) FROM WishlistItem w WHERE w.product.seller.id = :sellerId")
    long countByProductSellerId(@Param("sellerId") Long sellerId);
}
