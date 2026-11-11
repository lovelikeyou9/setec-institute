package com.example.demo.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminStatsResponse {
    private BigDecimal valuation;
    private BigDecimal totalRevenue;
    private long activeSkus;
    private long totalProducts;
    private long criticalSkus;
    private long lowStockCount;
    private long totalOrders;
    private long pendingOrders;
    private long totalCustomers;
    private long categories;
}
