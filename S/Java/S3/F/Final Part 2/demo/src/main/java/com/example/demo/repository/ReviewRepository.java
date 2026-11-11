package com.example.demo.repository;

import com.example.demo.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndHiddenFalseOrderByCreatedAtDesc(Long productId);
    List<Review> findByProductSellerIdOrderByCreatedAtDesc(Long sellerId);
    List<Review> findByProductSellerIdAndHiddenFalseOrderByCreatedAtDesc(Long sellerId);
    List<Review> findByUserIdAndHiddenFalseOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndHiddenFalse(Long userId);
    long countByProductSellerIdAndHiddenFalse(Long sellerId);
    long countByHiddenFalse();
}
