package com.example.demo.service;

import com.example.demo.dto.order.CreateOrderRequest;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.ApiException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.07");
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("500");
    private static final BigDecimal DEFAULT_SHIPPING = new BigDecimal("15.00");
    private static final BigDecimal CYBER_DISCOUNT = new BigDecimal("50.00");
    private static final String CYBER_CODE = "CYBER2025";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<OrderResponse> findUserOrders() {
        User user = currentUserService.getCurrentUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toOrderWithItems)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        User user = currentUserService.getCurrentUser();
        OrderEntity order = orderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));
        return toOrderWithItems(order);
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = currentUserService.getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new ApiException("Cart is empty", HttpStatus.BAD_REQUEST);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new ApiException("Insufficient stock for " + product.getName(), HttpStatus.BAD_REQUEST);
            }
            subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        BigDecimal discount = BigDecimal.ZERO;
        String discountCode = null;
        if (request.getDiscountCode() != null && CYBER_CODE.equalsIgnoreCase(request.getDiscountCode().trim())) {
            discount = CYBER_DISCOUNT;
            discountCode = CYBER_CODE;
        }

        BigDecimal shipping = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                ? BigDecimal.ZERO
                : DEFAULT_SHIPPING;

        BigDecimal taxable = subtotal.subtract(discount).max(BigDecimal.ZERO);
        BigDecimal tax = taxable.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = taxable.add(tax).add(shipping).setScale(2, RoundingMode.HALF_UP);

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .orderNumber("MMT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(OrderStatus.PENDING)
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .tax(tax)
                .shipping(shipping.setScale(2, RoundingMode.HALF_UP))
                .discount(discount.setScale(2, RoundingMode.HALF_UP))
                .total(total)
                .shippingAddress(request.getShippingAddress())
                .discountCode(discountCode)
                .build();
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();
            orderItems.add(orderItemRepository.save(orderItem));
        }

        cartItemRepository.deleteByUserId(user.getId());
        return EntityMapper.toOrderResponse(order, orderItems);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toOrderWithItems)
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        OrderStatus current = order.getStatus();
        if (current == newStatus) {
            return toOrderWithItems(order);
        }
        if (!isAllowedTransition(current, newStatus)) {
            throw new ApiException(
                    "Cannot change status from " + current + " to " + newStatus,
                    HttpStatus.BAD_REQUEST
            );
        }

        if (newStatus == OrderStatus.CANCELLED && current != OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        String admin = currentUserService.getCurrentUsername();
        auditLogRepository.save(AuditLog.builder()
                .message("Order " + order.getOrderNumber() + " status: " + current + " → " + newStatus)
                .level(newStatus == OrderStatus.CANCELLED ? AuditLevel.WARN : AuditLevel.INFO)
                .adminUsername(admin)
                .build());

        return toOrderWithItems(order);
    }

    private boolean isAllowedTransition(OrderStatus from, OrderStatus to) {
        if (from == OrderStatus.DELIVERED || from == OrderStatus.CANCELLED) {
            return false;
        }
        return switch (from) {
            case PENDING -> Set.of(
                    OrderStatus.PAID,
                    OrderStatus.PROCESSING,
                    OrderStatus.SHIPPED,
                    OrderStatus.DELIVERED,
                    OrderStatus.CANCELLED
            ).contains(to);
            case PAID -> Set.of(
                    OrderStatus.PROCESSING,
                    OrderStatus.SHIPPED,
                    OrderStatus.DELIVERED,
                    OrderStatus.CANCELLED
            ).contains(to);
            case PROCESSING -> Set.of(OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.CANCELLED).contains(to);
            case SHIPPED -> Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED).contains(to);
            default -> false;
        };
    }

    private void restoreStock(OrderEntity order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
        }
    }

    private OrderResponse toOrderWithItems(OrderEntity order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return EntityMapper.toOrderResponse(order, items);
    }
}
