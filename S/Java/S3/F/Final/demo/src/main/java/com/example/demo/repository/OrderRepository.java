package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByUserIdOrderByPlacedAtDesc(Long userId);
    List<Order> findByUserIdAndStatusOrderByPlacedAtDesc(Long userId, OrderStatus status);
    List<Order> findBySellerIdOrderByPlacedAtDesc(Long sellerId);
    List<Order> findBySellerIdAndStatusOrderByPlacedAtDesc(Long sellerId, OrderStatus status);
    long countByStatus(OrderStatus status);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, OrderStatus status);
    long countBySellerId(Long sellerId);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status NOT IN ('CANCELLED', 'PENDING_PAYMENT', 'REFUNDED')")
    BigDecimal sumTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.seller.id = :sellerId AND o.status NOT IN ('CANCELLED', 'PENDING_PAYMENT', 'REFUNDED')")
    BigDecimal sumSellerRevenue(Long sellerId);

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM OrderItem i WHERE i.order.seller.id = :sellerId AND i.order.status NOT IN ('CANCELLED', 'PENDING_PAYMENT', 'REFUNDED')")
    long sumSellerUnitsSold(Long sellerId);

    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.seller.id = :sellerId AND o.status NOT IN ('CANCELLED', 'PENDING_PAYMENT', 'REFUNDED')")
    long countDistinctBuyersBySellerId(Long sellerId);
}
