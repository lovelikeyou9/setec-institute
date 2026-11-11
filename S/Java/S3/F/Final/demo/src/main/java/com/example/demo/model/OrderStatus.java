package com.example.demo.model;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_CONFIRMED,   // 1. customer paid — waiting admin
    FUNDS_HELD,          // 2. admin confirmed — MME holds money — waiting seller
    SELLER_PROCESSING,   // 3–4. seller confirmed stock & is processing
    SHIPPED,             // 5. seller shipped
    OUT_FOR_DELIVERY,    // 6. out for delivery
    DELIVERED,           // 7. delivered — buyer protection starts
    DISPUTED,            // buyer reported missing / damaged / incorrect
    CANCELLED,           // seller out of stock / cancelled
    REFUNDED,
    COMPLETED            // protection ended — seller paid (after 3% commission)
}
