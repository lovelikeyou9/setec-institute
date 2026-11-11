package com.example.demo.repository;

import com.example.demo.model.SellerFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerFollowRepository extends JpaRepository<SellerFollow, Long> {
    long countBySellerId(Long sellerId);
    boolean existsByUserIdAndSellerId(Long userId, Long sellerId);
    Optional<SellerFollow> findByUserIdAndSellerId(Long userId, Long sellerId);
    void deleteByUserIdAndSellerId(Long userId, Long sellerId);
}
