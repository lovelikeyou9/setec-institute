package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 250)
    private String name;

    @Column(nullable = false, unique = true, length = 280)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String specifications;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 12, scale = 2)
    private BigDecimal originalPrice;

    private Integer discountPercent = 0;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(length = 80)
    private String sku;

    @Column(length = 80)
    private String warranty;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProductCondition conditionType = ProductCondition.NEW;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 1000)
    private String galleryUrls;

    @Column(length = 100)
    private String colorOptions;

    @Column(length = 100)
    private String storageOptions;

    @Column(length = 100)
    private String ramOptions;

    private Double rating = 0.0;
    private Integer reviewCount = 0;
    private Integer soldCount = 0;

    private boolean flashDeal = false;
    private LocalDateTime flashDealEndsAt;
    private boolean featured = false;
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (discountPercent != null && discountPercent > 0 && originalPrice != null) {
            // keep as provided
        } else if (originalPrice != null && price != null && originalPrice.compareTo(price) > 0) {
            discountPercent = originalPrice.subtract(price)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(originalPrice, 0, java.math.RoundingMode.HALF_UP)
                    .intValue();
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isInStock() {
        return stock != null && stock > 0;
    }

    public BigDecimal getEffectivePrice() {
        return price;
    }
}
