package com.example.demo.util;

import com.example.demo.model.OrderStatus;
import com.example.demo.model.PayoutStatus;

/** Human-readable labels for order / payout status in UI. */
public final class StatusLabels {

    private StatusLabels() {}

    public static String order(OrderStatus status) {
        if (status == null) return "—";
        return switch (status) {
            case PENDING_PAYMENT -> "Pending payment";
            case PAYMENT_CONFIRMED -> "Paid — await hold";
            case FUNDS_HELD -> "Funds held";
            case SELLER_PROCESSING -> "Processing";
            case SHIPPED -> "Shipped";
            case OUT_FOR_DELIVERY -> "Out for delivery";
            case DELIVERED -> "Delivered";
            case DISPUTED -> "Disputed";
            case CANCELLED -> "Cancelled";
            case REFUNDED -> "Refunded";
            case COMPLETED -> "Seller paid";
        };
    }

    public static String orderTone(OrderStatus status) {
        if (status == null) return "gray";
        return switch (status) {
            case DELIVERED, COMPLETED -> "green";
            case CANCELLED, REFUNDED, DISPUTED -> "red";
            case FUNDS_HELD, OUT_FOR_DELIVERY, SHIPPED -> "blue";
            case PAYMENT_CONFIRMED, SELLER_PROCESSING, PENDING_PAYMENT -> "yellow";
        };
    }

    public static String payout(PayoutStatus status) {
        if (status == null) return "Held";
        return switch (status) {
            case HELD -> "Held";
            case IN_PROTECTION -> "Buyer protection";
            case DISPUTED -> "Disputed";
            case RELEASED -> "Released";
            case REFUNDED -> "Refunded";
        };
    }

    public static String payoutTone(PayoutStatus status) {
        if (status == null) return "yellow";
        return switch (status) {
            case RELEASED -> "green";
            case DISPUTED, REFUNDED -> "red";
            case IN_PROTECTION -> "blue";
            case HELD -> "yellow";
        };
    }
}
