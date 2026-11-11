package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 150)
    private String storeName;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 500)
    private String bannerUrl;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String location;

    /** Explicit store country for Countries dashboard (not inferred from IP). */
    @Column(length = 80)
    private String country;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String ownerName;

    @Column(length = 500)
    private String businessInfo;

    @Column(length = 100)
    private String idDocument;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SellerStatus status = SellerStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SellerLevel level = SellerLevel.NEW;

    private Double rating = 0.0;
    private Integer followers = 0;
    private Integer totalProducts = 0;
    private Integer totalSales = 0;
    private Double responseRate = 95.0;

    @Column(length = 500)
    private String rejectionReason;

    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (joinedAt == null) joinedAt = createdAt;
    }

    public boolean isVerified() {
        return level == SellerLevel.VERIFIED || level == SellerLevel.TRUSTED || level == SellerLevel.TOP;
    }
}
