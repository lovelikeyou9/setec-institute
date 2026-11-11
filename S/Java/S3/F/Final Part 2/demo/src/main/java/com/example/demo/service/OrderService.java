package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CommissionRepository commissionRepository;
    private final ProductRepository productRepository;
    private final NotificationRepository notificationRepository;
    private final SellerRepository sellerRepository;
    private final CartService cartService;

    @Value("${app.commission.default:3.0}")
    private double defaultCommission;

    /** Buyer-protection window after delivery (days). */
    @Value("${app.buyer-protection.days:3}")
    private int buyerProtectionDays;

    public List<Order> getUserOrders(Long userId) {
        return getUserOrders(userId, "newest");
    }

    public List<Order> getUserOrders(Long userId, String sort) {
        return sortOrders(orderRepository.findByUserIdOrderByPlacedAtDesc(userId), sort);
    }

    public List<Order> getUserOrdersByStatus(Long userId, OrderStatus status) {
        return getUserOrdersByStatus(userId, status, "newest");
    }

    public List<Order> getUserOrdersByStatus(Long userId, OrderStatus status, String sort) {
        return sortOrders(orderRepository.findByUserIdAndStatusOrderByPlacedAtDesc(userId, status), sort);
    }

    public List<Order> getSellerOrders(Long sellerId) {
        return getSellerOrders(sellerId, "newest");
    }

    public List<Order> getSellerOrders(Long sellerId, String sort) {
        return sortOrders(orderRepository.findBySellerIdOrderByPlacedAtDesc(sellerId), sort);
    }

    private List<Order> sortOrders(List<Order> orders, String sort) {
        String key = sort == null ? "newest" : sort;
        Comparator<Order> cmp = switch (key) {
            case "oldest" -> Comparator.comparing(Order::getPlacedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "total_asc", "total-asc" -> Comparator.comparing(Order::getTotal, Comparator.nullsLast(Comparator.naturalOrder()));
            case "total_desc", "total-desc" -> Comparator.comparing(Order::getTotal, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
            default -> Comparator.comparing(Order::getPlacedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        };
        return orders.stream().sorted(cmp).toList();
    }

    public Order findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public Optional<Commission> findCommission(Order order) {
        return commissionRepository.findByOrderId(order.getId());
    }

    public int getBuyerProtectionDays() {
        return buyerProtectionDays;
    }

    /**
     * Place orders: customer pays → payment successful → platform HOLDS funds.
     * Commission is recorded as HELD (not yet paid to seller).
     */
    @Transactional
    public List<Order> placeOrders(User user, String recipientName, String recipientPhone,
                                   String shippingAddress, DeliveryType deliveryType,
                                   PaymentMethod paymentMethod) {
        return placeOrders(user, recipientName, recipientPhone, shippingAddress, null,
                deliveryType, paymentMethod);
    }

    @Transactional
    public List<Order> placeOrders(User user, String recipientName, String recipientPhone,
                                   String shippingAddress, String shippingCountry,
                                   DeliveryType deliveryType, PaymentMethod paymentMethod) {
        List<CartItem> selected = cartService.getSelected(user.getId());
        if (selected.isEmpty()) throw new RuntimeException("No items selected");

        Map<Seller, List<CartItem>> bySeller = selected.stream()
                .collect(Collectors.groupingBy(ci -> ci.getProduct().getSeller()));

        List<Order> orders = new ArrayList<>();
        BigDecimal shipping = deliveryType == DeliveryType.EXPRESS
                ? BigDecimal.valueOf(5.00) : BigDecimal.valueOf(2.00);

        for (Map.Entry<Seller, List<CartItem>> entry : bySeller.entrySet()) {
            Seller seller = entry.getKey();
            List<CartItem> items = entry.getValue();

            BigDecimal subtotal = items.stream()
                    .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal commissionAmt = subtotal.multiply(BigDecimal.valueOf(defaultCommission))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            String orderNumber = "CH" + System.currentTimeMillis() + seller.getId();

            boolean onlinePay = paymentMethod != PaymentMethod.CASH_ON_DELIVERY;

            Order order = Order.builder()
                    .orderNumber(orderNumber)
                    .user(user)
                    .seller(seller)
                    .status(onlinePay ? OrderStatus.PAYMENT_CONFIRMED : OrderStatus.PENDING_PAYMENT)
                    .paymentMethod(paymentMethod)
                    .deliveryType(deliveryType)
                    .subtotal(subtotal)
                    .shippingFee(shipping)
                    .discount(BigDecimal.ZERO)
                    .platformFee(commissionAmt)
                    .total(subtotal.add(shipping))
                    .recipientName(recipientName)
                    .recipientPhone(recipientPhone)
                    .shippingAddress(shippingAddress)
                    .shippingCountry(shippingCountry != null && !shippingCountry.isBlank()
                            ? shippingCountry.trim() : null)
                    .estimatedDelivery(LocalDateTime.now().plusDays(
                            deliveryType == DeliveryType.EXPRESS ? 1 : 3))
                    .build();

            if (onlinePay) {
                order.setPaidAt(LocalDateTime.now());
            }

            for (CartItem ci : items) {
                Product p = ci.getProduct();
                if (p.getStock() < ci.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for " + p.getName());
                }
                p.setStock(p.getStock() - ci.getQuantity());
                p.setSoldCount(p.getSoldCount() + ci.getQuantity());
                productRepository.save(p);

                OrderItem oi = OrderItem.builder()
                        .order(order)
                        .product(p)
                        .productName(p.getName())
                        .productImage(p.getImageUrl())
                        .quantity(ci.getQuantity())
                        .unitPrice(p.getPrice())
                        .lineTotal(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                        .build();
                order.getItems().add(oi);
            }

            order = orderRepository.save(order);

            // Escrow row created on pay; admin must still confirm hold before seller can accept
            Commission commission = Commission.builder()
                    .order(order)
                    .seller(seller)
                    .saleAmount(subtotal)
                    .commissionPercent(BigDecimal.valueOf(defaultCommission))
                    .commissionAmount(commissionAmt)
                    .sellerEarnings(subtotal.subtract(commissionAmt))
                    .payoutStatus(PayoutStatus.HELD)
                    .heldAt(LocalDateTime.now())
                    .build();
            commissionRepository.save(commission);

            seller.setTotalSales(seller.getTotalSales() + items.stream().mapToInt(CartItem::getQuantity).sum());
            sellerRepository.save(seller);

            String payMsg = onlinePay
                    ? "Payment successful. Waiting for MME admin to confirm and hold your funds."
                    : "Order placed (Cash on Delivery). Pay when you receive the package.";
            notificationRepository.save(Notification.builder()
                    .user(user)
                    .title("Order Confirmed")
                    .message("Order " + orderNumber + ": " + payMsg)
                    .type("ORDER")
                    .link("/orders/" + orderNumber)
                    .build());

            orders.add(order);
        }

        cartService.clearSelected(user.getId());
        return orders;
    }

    /**
     * Admin confirms payment and holds customer money in escrow.
     * Seller can then confirm stock.
     */
    @Transactional
    public Order adminConfirmHold(Long orderId) {
        Order order = findById(orderId);
        if (order.getStatus() != OrderStatus.PAYMENT_CONFIRMED
                && order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Only paid (or pending COD) orders can be confirmed for escrow hold");
        }
        LocalDateTime now = LocalDateTime.now();
        if (order.getPaidAt() == null) {
            order.setPaidAt(now);
        }
        order.setAdminHeldAt(now);
        order.setStatus(OrderStatus.FUNDS_HELD);
        ensureCommissionHeld(order);
        order = orderRepository.save(order);

        notificationRepository.save(Notification.builder()
                .user(order.getUser())
                .title("Payment Held by MME")
                .message("Admin confirmed order " + order.getOrderNumber()
                        + ". Your payment is held by MME until delivery and buyer protection finish.")
                .type("ORDER")
                .link("/orders/" + order.getOrderNumber())
                .build());

        if (order.getSeller().getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .user(order.getSeller().getUser())
                    .title("Confirm Stock")
                    .message("Order " + order.getOrderNumber()
                            + " — payment is held. Please confirm you have stock, or decline if out of stock.")
                    .type("ORDER")
                    .link("/seller-dashboard/orders")
                    .build());
        }
        return order;
    }

    /**
     * Seller confirms they have stock and will fulfill (starts processing).
     */
    @Transactional
    public Order sellerAcceptOrder(Long orderId, Long sellerId) {
        Order order = findById(orderId);
        assertSellerOwns(order, sellerId);
        if (order.getStatus() != OrderStatus.FUNDS_HELD) {
            throw new RuntimeException("You can only accept after admin has confirmed and held payment");
        }
        LocalDateTime now = LocalDateTime.now();
        order.setSellerConfirmedAt(now);
        order.setStatus(OrderStatus.SELLER_PROCESSING);
        order.setDisputeReason(null);
        order.setDisputeNote(null);
        order.setDisputeOpenedAt(null);
        order = orderRepository.save(order);

        commissionRepository.findByOrderId(order.getId()).ifPresent(c -> {
            if (c.getPayoutStatus() != PayoutStatus.RELEASED && c.getPayoutStatus() != PayoutStatus.REFUNDED) {
                c.setPayoutStatus(PayoutStatus.HELD);
                commissionRepository.save(c);
            }
        });

        notificationRepository.save(Notification.builder()
                .user(order.getUser())
                .title("Seller Confirmed")
                .message("Seller confirmed stock for order " + order.getOrderNumber()
                        + " and is preparing your package.")
                .type("ORDER")
                .link("/orders/" + order.getOrderNumber())
                .build());
        return order;
    }

    /**
     * Seller declines because product is out of stock → cancel + refund held funds.
     */
    @Transactional
    public Order sellerDeclineOutOfStock(Long orderId, Long sellerId) {
        Order order = findById(orderId);
        assertSellerOwns(order, sellerId);
        if (order.getStatus() != OrderStatus.FUNDS_HELD
                && order.getStatus() != OrderStatus.PAYMENT_CONFIRMED) {
            throw new RuntimeException("This order can no longer be declined for stock");
        }
        order.setCancelReason("OUT_OF_STOCK");
        order.setCancelledAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        Commission commission = commissionRepository.findByOrderId(order.getId()).orElse(null);
        if (commission != null && commission.getPayoutStatus() != PayoutStatus.RELEASED) {
            commission.setPayoutStatus(PayoutStatus.REFUNDED);
            commission.setRefundedAt(LocalDateTime.now());
            commissionRepository.save(commission);
        }
        order.setStatus(OrderStatus.REFUNDED);
        order = orderRepository.save(order);

        // restore stock for items
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product p = item.getProduct();
                int stock = p.getStock() != null ? p.getStock() : 0;
                int sold = p.getSoldCount() != null ? p.getSoldCount() : 0;
                p.setStock(stock + item.getQuantity());
                p.setSoldCount(Math.max(0, sold - item.getQuantity()));
                productRepository.save(p);
            }
        }

        notificationRepository.save(Notification.builder()
                .user(order.getUser())
                .title("Order Cancelled — Out of Stock")
                .message("Seller could not fulfill order " + order.getOrderNumber()
                        + " (out of stock). Your payment has been refunded.")
                .type("ORDER")
                .link("/orders/" + order.getOrderNumber())
                .build());
        return order;
    }

    /**
     * Seller fulfillment only: Processing → Shipped → Out for delivery.
     * Admin confirms Delivered, then pays seller.
     */
    @Transactional
    public Order sellerUpdateFulfillment(Long orderId, Long sellerId, OrderStatus status) {
        Order order = findById(orderId);
        assertSellerOwns(order, sellerId);
        Set<OrderStatus> allowed = switch (order.getStatus()) {
            case SELLER_PROCESSING -> Set.of(OrderStatus.SHIPPED);
            case SHIPPED -> Set.of(OrderStatus.OUT_FOR_DELIVERY);
            default -> Set.of();
        };
        if (!allowed.contains(status)) {
            throw new RuntimeException("Seller can only: Processing → Shipped → Out for delivery. Admin confirms Delivered and pays you.");
        }
        return updateStatus(orderId, status);
    }

    /** Admin confirms the customer received the package → starts buyer protection. */
    @Transactional
    public Order adminConfirmDelivered(Long orderId) {
        Order order = findById(orderId);
        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY
                && order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("Confirm delivered only after seller marks Out for delivery (or Shipped).");
        }
        return updateStatus(orderId, OrderStatus.DELIVERED);
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        OrderStatus previous = order.getStatus();
        order.setStatus(status);
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case PAYMENT_CONFIRMED -> {
                order.setPaidAt(now);
                ensureCommissionHeld(order);
            }
            case FUNDS_HELD -> {
                if (order.getPaidAt() == null) order.setPaidAt(now);
                order.setAdminHeldAt(now);
                ensureCommissionHeld(order);
            }
            case SELLER_PROCESSING -> {
                if (order.getSellerConfirmedAt() == null) order.setSellerConfirmedAt(now);
            }
            case SHIPPED -> {
                order.setShippedAt(now);
                if (order.getTrackingNumber() == null || order.getTrackingNumber().isBlank()) {
                    order.setTrackingNumber("TRK" + System.currentTimeMillis());
                }
            }
            case OUT_FOR_DELIVERY -> {
                if (order.getShippedAt() == null) order.setShippedAt(now);
                order.setOutForDeliveryAt(now);
            }
            case DELIVERED -> {
                order.setDeliveredAt(now);
                if (order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY && order.getPaidAt() == null) {
                    order.setPaidAt(now);
                }
                startBuyerProtection(order, now);
            }
            case CANCELLED -> order.setCancelledAt(now);
            default -> {}
        }
        order = orderRepository.save(order);

        notificationRepository.save(Notification.builder()
                .user(order.getUser())
                .title("Order Update")
                .message(statusMessage(order, previous, status))
                .type("ORDER")
                .link("/orders/" + order.getOrderNumber())
                .build());
        return order;
    }

    private String statusMessage(Order order, OrderStatus previous, OrderStatus status) {
        return switch (status) {
            case FUNDS_HELD -> "MME admin confirmed payment for order " + order.getOrderNumber()
                    + ". Funds are held until you receive the product.";
            case SELLER_PROCESSING -> "Seller confirmed stock and is processing order " + order.getOrderNumber() + ".";
            case SHIPPED -> "Order " + order.getOrderNumber() + " has been shipped"
                    + (order.getTrackingNumber() != null ? " · Tracking: " + order.getTrackingNumber() : "");
            case OUT_FOR_DELIVERY -> "Order " + order.getOrderNumber() + " is out for delivery.";
            case DELIVERED -> "Order " + order.getOrderNumber() + " delivered. Buyer protection is open for "
                    + buyerProtectionDays + " day(s) — you can report missing, damaged, or incorrect items.";
            case DISPUTED -> "A dispute was opened on order " + order.getOrderNumber() + ". Funds remain held.";
            case COMPLETED -> "Order " + order.getOrderNumber()
                    + " completed. Admin deducted commission/fees and paid the seller.";
            default -> "Order " + order.getOrderNumber() + " is now: " + status.name().replace('_', ' ');
        };
    }

    private void assertSellerOwns(Order order, Long sellerId) {
        if (sellerId == null || order.getSeller() == null
                || !Objects.equals(order.getSeller().getId(), sellerId)) {
            throw new RuntimeException("You can only manage orders for your own store");
        }
    }

    private void ensureCommissionHeld(Order order) {
        commissionRepository.findByOrderId(order.getId()).ifPresent(c -> {
            if (c.getPayoutStatus() == null
                    || c.getPayoutStatus() == PayoutStatus.DISPUTED
                    || c.getPayoutStatus() == PayoutStatus.HELD) {
                c.setPayoutStatus(PayoutStatus.HELD);
                if (c.getHeldAt() == null) {
                    c.setHeldAt(LocalDateTime.now());
                }
                commissionRepository.save(c);
            }
        });
    }

    /** After delivery: start buyer-protection window; funds stay held. */
    private void startBuyerProtection(Order order, LocalDateTime now) {
        order.setProtectionEndsAt(now.plusDays(Math.max(1, buyerProtectionDays)));
        commissionRepository.findByOrderId(order.getId()).ifPresent(c -> {
            if (c.getPayoutStatus() == PayoutStatus.HELD || c.getPayoutStatus() == null) {
                c.setPayoutStatus(PayoutStatus.IN_PROTECTION);
                c.setProtectionStartedAt(now);
                commissionRepository.save(c);
            }
        });
    }

    /** Customer confirms delivery (same as marking delivered if still in transit). */
    @Transactional
    public Order confirmDelivery(Long orderId, User buyer) {
        Order order = findById(orderId);
        assertBuyer(order, buyer);
        if (order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.DISPUTED
                || order.getStatus() == OrderStatus.COMPLETED
                || order.getStatus() == OrderStatus.REFUNDED) {
            return order;
        }
        if (order.getStatus() != OrderStatus.SHIPPED
                && order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            throw new RuntimeException("This order cannot be marked delivered yet");
        }
        return updateStatus(orderId, OrderStatus.DELIVERED);
    }

    /**
     * Buyer protection: report missing, damaged, or incorrect items.
     * Funds stay held (DISPUTED) until admin resolves.
     */
    @Transactional
    public Order openDispute(Long orderId, User buyer, DisputeReason reason, String note) {
        Order order = findById(orderId);
        assertBuyer(order, buyer);
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("You can only open a dispute after the order is delivered");
        }
        if (order.getProtectionEndsAt() != null && LocalDateTime.now().isAfter(order.getProtectionEndsAt())) {
            throw new RuntimeException("Buyer protection period has ended");
        }
        order.setStatus(OrderStatus.DISPUTED);
        order.setDisputeReason(reason);
        order.setDisputeNote(note);
        order.setDisputeOpenedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        commissionRepository.findByOrderId(order.getId()).ifPresent(c -> {
            c.setPayoutStatus(PayoutStatus.DISPUTED);
            commissionRepository.save(c);
        });

        notificationRepository.save(Notification.builder()
                .user(order.getUser())
                .title("Dispute Opened")
                .message("Your report (" + reason.name() + ") for order " + order.getOrderNumber()
                        + " was submitted. Payment remains held by MME.")
                .type("ORDER")
                .link("/orders/" + order.getOrderNumber())
                .build());

        if (order.getSeller().getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .user(order.getSeller().getUser())
                    .title("Order Disputed")
                    .message("Buyer reported " + reason.name().toLowerCase(Locale.ENGLISH)
                            + " on order " + order.getOrderNumber() + ". Payout is paused.")
                    .type("ORDER")
                    .link("/seller-dashboard/orders")
                    .build());
        }
        return order;
    }

    /**
     * Release payment after buyer protection: deduct platform commission, pay seller.
     */
    @Transactional
    public Order releasePayout(Long orderId) {
        return releasePayout(orderId, false);
    }

    @Transactional
    public Order releasePayout(Long orderId, boolean force) {
        Order order = findById(orderId);
        Commission commission = commissionRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new RuntimeException("No escrow/commission record for this order"));

        if (commission.getPayoutStatus() == PayoutStatus.RELEASED) {
            return order;
        }
        if (commission.getPayoutStatus() == PayoutStatus.REFUNDED) {
            throw new RuntimeException("This order was refunded — cannot release to seller");
        }
        if (commission.getPayoutStatus() == PayoutStatus.DISPUTED && !force) {
            throw new RuntimeException("Resolve the dispute before releasing payout");
        }
        if (order.getStatus() != OrderStatus.DELIVERED
                && order.getStatus() != OrderStatus.COMPLETED
                && !(force && order.getStatus() == OrderStatus.DISPUTED)) {
            throw new RuntimeException("Payout can only be released after delivery");
        }
        if (!force && order.getProtectionEndsAt() != null
                && LocalDateTime.now().isBefore(order.getProtectionEndsAt())) {
            throw new RuntimeException("Buyer protection is still active until "
                    + order.getProtectionEndsAt());
        }

        LocalDateTime now = LocalDateTime.now();
        commission.setPayoutStatus(PayoutStatus.RELEASED);
        commission.setReleasedAt(now);
        commissionRepository.save(commission);

        order.setPayoutReleasedAt(now);
        order.setStatus(OrderStatus.COMPLETED);
        order = orderRepository.save(order);

        if (order.getSeller().getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .user(order.getSeller().getUser())
                    .title("Payout Released")
                    .message("MME released $" + commission.getSellerEarnings()
                            + " for order " + order.getOrderNumber()
                            + " (commission " + commission.getCommissionPercent() + "% deducted).")
                    .type("PAYOUT")
                    .link("/seller-dashboard/earnings")
                    .build());
        }
        notificationRepository.save(Notification.builder()
                .user(order.getUser())
                .title("Order Complete")
                .message("Order " + order.getOrderNumber() + " is complete. Thank you for shopping on MME.")
                .type("ORDER")
                .link("/orders/" + order.getOrderNumber())
                .build());
        return order;
    }

    /** Admin: release even if protection window is still open or dispute is open (seller wins). */
    @Transactional
    public Order forceReleasePayout(Long orderId) {
        Order order = findById(orderId);
        if (order.getStatus() == OrderStatus.DISPUTED) {
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
            commissionRepository.findByOrderId(order.getId()).ifPresent(c -> {
                c.setPayoutStatus(PayoutStatus.IN_PROTECTION);
                commissionRepository.save(c);
            });
        }
        return releasePayout(orderId, true);
    }

    @Transactional
    public Order refundOrder(Long orderId) {
        Order order = findById(orderId);
        Commission commission = commissionRepository.findByOrderId(order.getId()).orElse(null);
        LocalDateTime now = LocalDateTime.now();
        if (commission != null) {
            if (commission.getPayoutStatus() == PayoutStatus.RELEASED) {
                throw new RuntimeException("Payout already released to seller — cannot refund automatically");
            }
            commission.setPayoutStatus(PayoutStatus.REFUNDED);
            commission.setRefundedAt(now);
            commissionRepository.save(commission);
        }
        order.setStatus(OrderStatus.REFUNDED);
        order.setCancelledAt(now);
        order = orderRepository.save(order);

        notificationRepository.save(Notification.builder()
                .user(order.getUser())
                .title("Refund Issued")
                .message("Order " + order.getOrderNumber() + " was refunded. Held funds were returned.")
                .type("ORDER")
                .link("/orders/" + order.getOrderNumber())
                .build());
        return order;
    }

    /** Auto-release when protection window ends with no dispute. */
    @Transactional
    public int autoReleaseEligibleOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> due = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .filter(o -> o.getProtectionEndsAt() != null && !now.isBefore(o.getProtectionEndsAt()))
                .toList();
        int count = 0;
        for (Order o : due) {
            try {
                releasePayout(o.getId());
                count++;
            } catch (Exception ignored) {
                // leave for next run / admin
            }
        }
        return count;
    }

    public boolean canOpenDispute(Order order) {
        if (order.getStatus() != OrderStatus.DELIVERED) return false;
        if (order.getProtectionEndsAt() == null) return false;
        return !LocalDateTime.now().isAfter(order.getProtectionEndsAt());
    }

    /** Buyer cannot mark delivered — admin confirms delivery in this flow. */
    public boolean canConfirmDelivery(Order order) {
        return false;
    }

    private void assertBuyer(Order order, User buyer) {
        if (buyer == null || order.getUser() == null
                || !Objects.equals(order.getUser().getId(), buyer.getId())) {
            throw new RuntimeException("You can only manage your own orders");
        }
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
