package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PublicProfileService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final SellerFollowRepository sellerFollowRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> sellerProfile(Long sellerId) {
        return sellerProfile(sellerId, null);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> sellerProfile(Long sellerId, Long viewerUserId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new RuntimeException("This seller store is not available");
        }
        if (seller.getUser() != null) {
            seller.getUser().getId();
        }

        long productsSelling = productRepository.countBySellerIdAndActiveTrue(sellerId);
        long unitsSold = orderRepository.sumSellerUnitsSold(sellerId);
        BigDecimal revenue = orderRepository.sumSellerRevenue(sellerId);
        if (revenue == null) revenue = BigDecimal.ZERO;
        long orderCount = orderRepository.countBySellerId(sellerId);
        long followers = sellerFollowRepository.countBySellerId(sellerId);
        boolean following = viewerUserId != null
                && sellerFollowRepository.existsByUserIdAndSellerId(viewerUserId, sellerId);

        List<Review> reviews = reviewRepository
                .findByProductSellerIdAndHiddenFalseOrderByCreatedAtDesc(sellerId);
        reviews.forEach(r -> {
            if (r.getUser() != null) r.getUser().getFullName();
            if (r.getProduct() != null) r.getProduct().getName();
        });

        double avgStars = seller.getRating() != null ? seller.getRating() : 0;
        if (!reviews.isEmpty()) {
            avgStars = reviews.stream()
                    .mapToInt(r -> r.getRating() != null ? r.getRating() : 0)
                    .average().orElse(avgStars);
            avgStars = Math.round(avgStars * 10.0) / 10.0;
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("seller", seller);
        m.put("rankLabel", rankLabel(seller.getLevel()));
        m.put("rankTone", rankTone(seller.getLevel()));
        m.put("stars", avgStars);
        m.put("starFull", (int) Math.floor(avgStars));
        m.put("starHalf", avgStars - Math.floor(avgStars) >= 0.4);
        m.put("productsSelling", productsSelling);
        m.put("unitsSold", unitsSold);
        m.put("revenue", revenue);
        m.put("orderCount", orderCount);
        m.put("followers", followers);
        m.put("following", following);
        m.put("reviewCount", reviews.size());
        m.put("reviews", reviews.stream().limit(12).toList());
        m.put("responseRate", seller.getResponseRate() != null ? seller.getResponseRate() : 0);
        m.put("ownerUserId", seller.getUser() != null ? seller.getUser().getId() : null);
        return m;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> customerProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isEnabled()) {
            throw new RuntimeException("This profile is not available");
        }
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("This profile is not available");
        }

        long deliveredOrders = orderRepository.countByUserIdAndStatus(userId, OrderStatus.DELIVERED);
        long purchaseOrders = orderRepository.findByUserIdOrderByPlacedAtDesc(userId).stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED
                        && o.getStatus() != OrderStatus.PENDING_PAYMENT
                        && o.getStatus() != OrderStatus.REFUNDED)
                .count();
        long completedOrders = deliveredOrders > 0 ? deliveredOrders : purchaseOrders;
        List<Review> reviews = reviewRepository.findByUserIdAndHiddenFalseOrderByCreatedAtDesc(userId);
        reviews.forEach(r -> {
            if (r.getProduct() != null) {
                r.getProduct().getName();
                if (r.getProduct().getSeller() != null) {
                    r.getProduct().getSeller().getStoreName();
                }
            }
        });

        Optional<Seller> store = sellerRepository.findByUserId(userId)
                .filter(s -> s.getStatus() == SellerStatus.APPROVED);

        String buyerRank = buyerRank(completedOrders, reviews.size());
        double avgGiven = reviews.isEmpty() ? 0
                : Math.round(reviews.stream()
                .mapToInt(r -> r.getRating() != null ? r.getRating() : 0)
                .average().orElse(0) * 10.0) / 10.0;

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("profileUser", user);
        m.put("displayName", user.getFullName());
        m.put("initial", user.getFullName() != null && !user.getFullName().isBlank()
                ? user.getFullName().substring(0, 1).toUpperCase(Locale.ENGLISH) : "?");
        m.put("memberSince", user.getCreatedAt());
        m.put("buyerRank", buyerRank);
        m.put("deliveredOrders", completedOrders);
        m.put("totalOrders", purchaseOrders);
        m.put("reviewCount", reviews.size());
        m.put("avgRatingGiven", avgGiven);
        m.put("reviews", reviews.stream().limit(12).toList());
        m.put("sellerStore", store.orElse(null));
        m.put("isSeller", store.isPresent());
        return m;
    }

    private static String rankLabel(SellerLevel level) {
        if (level == null) return "New Seller";
        return switch (level) {
            case NEW -> "New Seller";
            case VERIFIED -> "Verified Seller";
            case TRUSTED -> "Trusted Seller";
            case TOP -> "Top Seller";
        };
    }

    private static String rankTone(SellerLevel level) {
        if (level == null) return "gray";
        return switch (level) {
            case NEW -> "gray";
            case VERIFIED -> "blue";
            case TRUSTED -> "green";
            case TOP -> "gold";
        };
    }

    private static String buyerRank(long delivered, long reviews) {
        if (delivered >= 15 || reviews >= 10) return "Trusted Buyer";
        if (delivered >= 5 || reviews >= 5) return "Active Buyer";
        if (delivered >= 1 || reviews >= 1) return "Regular Buyer";
        return "New Shopper";
    }
}
