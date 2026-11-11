package com.example.demo.repository;

import com.example.demo.model.Seller;
import com.example.demo.model.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByUserId(Long userId);
    List<Seller> findByStatus(SellerStatus status);
    long countByStatus(SellerStatus status);
    List<Seller> findByStatusOrderByRatingDesc(SellerStatus status);

    @Query("SELECT s FROM Seller s WHERE " +
           "LOWER(s.storeName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.ownerName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.location) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.phone) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.user.email) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "ORDER BY s.id DESC")
    List<Seller> search(@Param("q") String q);
}
