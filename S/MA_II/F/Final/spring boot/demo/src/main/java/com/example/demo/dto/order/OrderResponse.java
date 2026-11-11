package com.example.demo.dto.order;

import com.example.demo.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shipping;
    private BigDecimal discount;
    private BigDecimal total;
    private String shippingAddress;
    private String discountCode;
    private Instant createdAt;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private List<OrderItemResponse> items;
}
