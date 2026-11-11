package com.example.demo.mapper;

import com.example.demo.dto.admin.AuditLogResponse;
import com.example.demo.dto.cart.CartItemResponse;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.dto.order.OrderItemResponse;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.dto.wishlist.WishlistItemResponse;
import com.example.demo.entity.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public final class EntityMapper {

    private EntityMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .memberSince(user.getMemberSince())
                .createdAt(user.getCreatedAt())
                .banned(user.isBanned())
                .banReason(user.getBanReason())
                .build();
    }

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .iconName(category.getIconName())
                .description(category.getDescription())
                .sortOrder(category.getSortOrder())
                .build();
    }

    public static ProductResponse toProductResponse(Product product) {
        Category category = product.getCategory();
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .sku(product.getSku())
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .description(product.getDescription())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .stock(product.getStock())
                .warnMin(product.getWarnMin())
                .binLocation(product.getBinLocation())
                .imageUrl(product.getImageUrl())
                .architecture(product.getArchitecture())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .featured(product.isFeatured())
                .flashDrop(product.isFlashDrop())
                .costPrice(product.getCostPrice())
                .specsJson(product.getSpecsJson())
                .tags(product.getTags())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static CartItemResponse toCartItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .product(toProductResponse(item.getProduct()))
                .build();
    }

    public static WishlistItemResponse toWishlistItemResponse(WishlistItem item) {
        return WishlistItemResponse.builder()
                .id(item.getId())
                .product(toProductResponse(item.getProduct()))
                .build();
    }

    public static OrderItemResponse toOrderItemResponse(OrderItem item) {
        var product = item.getProduct();
        BigDecimal qty = BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0);
        BigDecimal unit = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(product != null ? product.getId() : null)
                .productName(item.getProductName())
                .brand(product != null ? product.getBrand() : null)
                .sku(product != null ? product.getSku() : null)
                .imageUrl(product != null ? product.getImageUrl() : null)
                .unitPrice(unit)
                .quantity(item.getQuantity())
                .lineTotal(unit.multiply(qty))
                .build();
    }

    public static OrderResponse toOrderResponse(OrderEntity order, List<OrderItem> items) {
        var user = order.getUser();
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .shipping(order.getShipping())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .shippingAddress(order.getShippingAddress())
                .discountCode(order.getDiscountCode())
                .createdAt(order.getCreatedAt())
                .customerId(user != null ? user.getId() : null)
                .customerName(user != null ? user.getFullName() : null)
                .customerEmail(user != null ? user.getEmail() : null)
                .items(items.stream().map(EntityMapper::toOrderItemResponse).collect(Collectors.toList()))
                .build();
    }

    public static AuditLogResponse toAuditLogResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .message(log.getMessage())
                .level(log.getLevel())
                .createdAt(log.getCreatedAt())
                .adminUsername(log.getAdminUsername())
                .build();
    }
}
