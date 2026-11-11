package com.example.demo.model;

/**
 * Escrow payout lifecycle for marketplace payments.
 * HELD → IN_PROTECTION → RELEASED (or DISPUTED / REFUNDED).
 */
public enum PayoutStatus {
    HELD,
    IN_PROTECTION,
    DISPUTED,
    RELEASED,
    REFUNDED
}
