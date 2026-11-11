package com.example.demo.repository;

import com.example.demo.model.Commission;
import com.example.demo.model.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findAllByOrderByCreatedAtDesc();
    List<Commission> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    Optional<Commission> findByOrderId(Long orderId);

    @Query("SELECT COALESCE(SUM(c.commissionAmount), 0) FROM Commission c WHERE c.payoutStatus = 'RELEASED'")
    BigDecimal sumPlatformEarnings();

    @Query("SELECT COALESCE(SUM(c.commissionAmount), 0) FROM Commission c WHERE c.payoutStatus IN ('HELD', 'IN_PROTECTION')")
    BigDecimal sumPlatformFeesInEscrow();

    @Query("SELECT COALESCE(SUM(c.saleAmount), 0) FROM Commission c WHERE c.payoutStatus IN ('HELD', 'IN_PROTECTION')")
    BigDecimal sumSaleAmountInEscrow();

    @Query("SELECT COALESCE(SUM(c.sellerEarnings), 0) FROM Commission c WHERE c.seller.id = :sellerId AND c.payoutStatus = 'RELEASED'")
    BigDecimal sumSellerEarnings(@Param("sellerId") Long sellerId);

    @Query("SELECT COALESCE(SUM(c.sellerEarnings), 0) FROM Commission c WHERE c.seller.id = :sellerId AND c.payoutStatus IN ('HELD', 'IN_PROTECTION')")
    BigDecimal sumSellerHeld(@Param("sellerId") Long sellerId);

    @Query("SELECT COALESCE(SUM(c.commissionAmount), 0) FROM Commission c WHERE c.seller.id = :sellerId AND c.payoutStatus = 'RELEASED'")
    BigDecimal sumSellerFeesPaid(@Param("sellerId") Long sellerId);

    @Query("SELECT COALESCE(SUM(c.commissionAmount), 0) FROM Commission c WHERE c.seller.id = :sellerId AND c.payoutStatus IN ('HELD', 'IN_PROTECTION')")
    BigDecimal sumSellerFeesPending(@Param("sellerId") Long sellerId);

    long countByPayoutStatus(PayoutStatus payoutStatus);
}
