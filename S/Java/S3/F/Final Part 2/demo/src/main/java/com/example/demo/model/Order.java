package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DeliveryType deliveryType = DeliveryType.STANDARD;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 12, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal platformFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(length = 120)
    private String recipientName;

    @Column(length = 30)
    private String recipientPhone;

    @Column(length = 400)
    private String shippingAddress;

    /** Buyer destination country selected at checkout. */
    @Column(length = 80)
    private String shippingCountry;

    @Column(length = 80)
    private String trackingNumber;

    private LocalDateTime estimatedDelivery;
    private LocalDateTime placedAt;
    private LocalDateTime paidAt;
    /** Admin confirmed payment and holds funds in escrow. */
    private LocalDateTime adminHeldAt;
    /** Seller confirmed they have stock and will fulfill. */
    private LocalDateTime sellerConfirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime outForDeliveryAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    /** e.g. OUT_OF_STOCK when seller declines. */
    @Column(length = 40)
    private String cancelReason;

    /** End of buyer-protection window after delivery (escrow still held). */
    private LocalDateTime protectionEndsAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DisputeReason disputeReason;

    @Column(length = 500)
    private String disputeNote;

    private LocalDateTime disputeOpenedAt;
    private LocalDateTime payoutReleasedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (placedAt == null) placedAt = LocalDateTime.now();
    }
}
