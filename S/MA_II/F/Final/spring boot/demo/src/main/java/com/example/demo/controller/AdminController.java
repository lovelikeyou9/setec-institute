package com.example.demo.controller;

import com.example.demo.dto.admin.AdminStatsResponse;
import com.example.demo.dto.admin.AuditLogResponse;
import com.example.demo.dto.admin.OrderStatusUpdateRequest;
import com.example.demo.dto.admin.StockUpdateRequest;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.service.AdminService;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;

    @GetMapping("/stats")
    public AdminStatsResponse stats() {
        return adminService.getStats();
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders() {
        return orderService.findAllOrders();
    }

    @PutMapping("/orders/{id}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        return orderService.updateStatus(id, request.getStatus());
    }

    @GetMapping("/audit-logs")
    public List<AuditLogResponse> auditLogs() {
        return adminService.getAuditLogs();
    }

    @PutMapping("/products/{id}/stock")
    public ProductResponse updateStock(@PathVariable Long id, @Valid @RequestBody StockUpdateRequest request) {
        return adminService.updateStock(id, request.getStock());
    }
}
