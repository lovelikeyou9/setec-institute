package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(nullable = false, length = 80)
    private String province;

    @Column(nullable = false, length = 80)
    private String district;

    @Column(length = 80)
    private String commune;

    @Column(nullable = false, length = 250)
    private String street;

    /** Explicit country (required for accurate Countries dashboard). */
    @Column(length = 80)
    private String country;

    private boolean defaultAddress = false;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder(street);
        if (commune != null && !commune.isBlank()) sb.append(", ").append(commune);
        sb.append(", ").append(district).append(", ").append(province);
        if (country != null && !country.isBlank()) sb.append(", ").append(country);
        return sb.toString();
    }
}
