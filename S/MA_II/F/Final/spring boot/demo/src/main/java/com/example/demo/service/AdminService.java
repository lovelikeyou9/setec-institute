package com.example.demo.service;

import com.example.demo.dto.admin.AdminStatsResponse;
import com.example.demo.dto.admin.AuditLogResponse;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.entity.AuditLevel;
import com.example.demo.entity.AuditLog;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Product;
import com.example.demo.entity.Role;
import com.example.demo.exception.ApiException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;

    public AdminStatsResponse getStats() {
        BigDecimal valuation = productRepository.sumInventoryValuation();
        if (valuation == null) {
            valuation = BigDecimal.ZERO;
        }
        BigDecimal revenue = orderRepository.sumTotalRevenue();
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        long critical = productRepository.countCriticalSkus();
        return AdminStatsResponse.builder()
                .valuation(valuation)
                .totalRevenue(revenue)
                .activeSkus(productRepository.countByStockGreaterThan(0))
                .totalProducts(productRepository.count())
                .criticalSkus(critical)
                .lowStockCount(critical)
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDING))
                .totalCustomers(userRepository.countByRole(Role.CUSTOMER))
                .categories(categoryRepository.count())
                .build();
    }

    public List<AuditLogResponse> getAuditLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(EntityMapper::toAuditLogResponse)
                .toList();
    }

    @Transactional
    public ProductResponse updateStock(Long productId, Integer stock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("Product not found", HttpStatus.NOT_FOUND));
        int previous = product.getStock();
        product.setStock(stock);
        Product saved = productRepository.save(product);

        String adminUsername = currentUserService.getCurrentUsername();
        auditLogRepository.save(AuditLog.builder()
                .message("Stock updated for SKU " + product.getSku() + ": " + previous + " -> " + stock)
                .level(stock <= (product.getWarnMin() != null ? product.getWarnMin() : 0) ? AuditLevel.WARN : AuditLevel.INFO)
                .adminUsername(adminUsername)
                .build());

        return EntityMapper.toProductResponse(saved);
    }
}
