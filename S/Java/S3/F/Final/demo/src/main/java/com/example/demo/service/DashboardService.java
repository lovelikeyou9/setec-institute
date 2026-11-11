package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CommissionRepository commissionRepository;
    private final SellerFollowRepository sellerFollowRepository;

    public Map<String, Object> adminDashboard() {
        Map<String, Object> m = new LinkedHashMap<>();
        List<Order> orders = orderRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Seller> sellers = sellerRepository.findAll();
        List<User> users = userRepository.findAll();

        BigDecimal orderGmv = nz(orderRepository.sumTotalRevenue());
        boolean usingEstimate = false; // always real order GMV (0 if none)
        BigDecimal gmv = orderGmv;
        BigDecimal feesEarned = nz(commissionRepository.sumPlatformEarnings());
        BigDecimal feesInEscrow = nz(commissionRepository.sumPlatformFeesInEscrow());
        BigDecimal escrowSales = nz(commissionRepository.sumSaleAmountInEscrow());
        long escrowOrders = commissionRepository.countByPayoutStatus(PayoutStatus.HELD)
                + commissionRepository.countByPayoutStatus(PayoutStatus.IN_PROTECTION);

        long activeProducts = products.stream().filter(Product::isActive).count();
        long pendingSellers = sellers.stream().filter(s -> s.getStatus() == SellerStatus.PENDING).count();
        long approvedSellers = sellers.stream().filter(s -> s.getStatus() == SellerStatus.APPROVED).count();
        long awaitHold = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAYMENT_CONFIRMED
                        || o.getStatus() == OrderStatus.PENDING_PAYMENT).count();
        long awaitDelivered = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                        || o.getStatus() == OrderStatus.SHIPPED).count();
        long awaitPayout = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED
                        || o.getStatus() == OrderStatus.DISPUTED).count();
        long pendingOrders = awaitHold + awaitDelivered + awaitPayout
                + orders.stream().filter(o -> o.getStatus() == OrderStatus.FUNDS_HELD
                        || o.getStatus() == OrderStatus.SELLER_PROCESSING).count();

        m.put("gmv", gmv);
        m.put("fees", feesEarned);
        m.put("feesEarned", feesEarned);
        m.put("feesInEscrow", feesInEscrow);
        m.put("escrowSales", escrowSales);
        m.put("escrowOrders", escrowOrders);
        m.put("commissionRate", "3%");
        m.put("awaitHold", awaitHold);
        m.put("awaitDelivered", awaitDelivered);
        m.put("awaitPayout", awaitPayout);
        m.put("totalUsers", users.size());
        m.put("totalSellers", approvedSellers);
        m.put("totalProducts", activeProducts);
        m.put("totalOrders", orders.size());
        m.put("pendingSellers", pendingSellers);
        m.put("pendingOrders", pendingOrders);
        m.put("usingEstimate", usingEstimate);
        m.put("gmvDelta", "+14.2%");
        m.put("feesDelta", escrowOrders + " in escrow");
        m.put("usersDelta", "+" + Math.max(1, users.size() / 10));
        m.put("productsDelta", "+" + Math.max(1, activeProducts / 8));

        m.put("months", monthLabels());
        m.put("ordersTrend", monthlySeries(orders, gmv, true));
        m.put("revenueTrend", monthlySeries(orders, gmv, false));

        List<Map<String, Object>> categoryRev = categoryBreakdown(products, orders, usingEstimate);
        m.put("categoryLabels", categoryRev.stream().map(x -> x.get("label")).toList());
        m.put("categoryValues", categoryRev.stream().map(x -> x.get("value")).toList());
        m.put("categoryPercents", categoryRev.stream().map(x -> x.get("percent")).toList());
        m.put("categoryRows", categoryRev);

        long customers = users.stream().filter(u -> u.getRole() == Role.CUSTOMER).count();
        m.put("funnelLabels", List.of("Registered Users", "Customers", "Approved Sellers", "Active Products", "Orders"));
        m.put("funnelValues", List.of(
                (long) users.size(),
                customers,
                approvedSellers,
                activeProducts,
                Math.max(orders.size(), products.stream().mapToInt(p -> p.getSoldCount() != null ? p.getSoldCount() : 0).sum() / 100)
        ));

        List<Map<String, Object>> countries = countryBreakdown(orders, sellers, products, gmv, usingEstimate);
        m.put("countryLabels", countries.stream().map(x -> x.get("label")).toList());
        m.put("countryValues", countries.stream().map(x -> x.get("value")).toList());
        m.put("countryRows", countries);

        m.put("perfRows", sellerPerformance(sellers, products, orders, usingEstimate));
        m.put("recentOrders", orders.stream()
                .sorted(Comparator.comparing(Order::getPlacedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(8).toList());
        m.put("year", LocalDate.now().getYear());
        return m;
    }

    public Map<String, Object> sellerDashboard(Seller seller) {
        Map<String, Object> m = new LinkedHashMap<>();
        List<Order> orders = orderRepository.findBySellerIdOrderByPlacedAtDesc(seller.getId());
        List<Product> products = productRepository.findBySellerIdOrderByIdDesc(seller.getId());

        BigDecimal orderRevenue = nz(orderRepository.sumSellerRevenue(seller.getId()));
        boolean usingEstimate = false;
        BigDecimal revenue = orderRevenue;
        BigDecimal earnings = nz(commissionRepository.sumSellerEarnings(seller.getId()));
        BigDecimal heldEarnings = nz(commissionRepository.sumSellerHeld(seller.getId()));
        BigDecimal feesPaid = nz(commissionRepository.sumSellerFeesPaid(seller.getId()));
        BigDecimal feesPending = nz(commissionRepository.sumSellerFeesPending(seller.getId()));

        long pending = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.FUNDS_HELD
                        || o.getStatus() == OrderStatus.SELLER_PROCESSING
                        || o.getStatus() == OrderStatus.SHIPPED
                        || o.getStatus() == OrderStatus.OUT_FOR_DELIVERY).count();
        long awaitAdminPayout = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED
                        || o.getStatus() == OrderStatus.OUT_FOR_DELIVERY).count();
        long lowStock = products.stream().filter(p -> p.getStock() != null && p.getStock() < 10).count();
        long soldUnits = orderRepository.sumSellerUnitsSold(seller.getId());

        m.put("revenue", revenue);
        m.put("earnings", earnings);
        m.put("heldEarnings", heldEarnings);
        m.put("feesPaid", feesPaid);
        m.put("feesPending", feesPending);
        m.put("commissionRate", "3%");
        m.put("awaitAdminPayout", awaitAdminPayout);
        m.put("pendingOrders", pending);
        m.put("lowStock", lowStock);
        m.put("productCount", products.size());
        m.put("soldUnits", soldUnits);
        m.put("rating", seller.getRating());
        m.put("usingEstimate", usingEstimate);
        m.put("followers", sellerFollowRepository.countBySellerId(seller.getId()));
        m.put("revenueDelta", "+11.4%");
        m.put("earningsDelta", "after " + "3%" + " fee");
        m.put("ordersDelta", pending > 0 ? "+" + pending : "+0");
        m.put("stockDelta", lowStock > 0 ? lowStock + " low" : "Healthy");

        m.put("months", monthLabels());
        m.put("ordersTrend", monthlySeries(orders, revenue, true));
        m.put("revenueTrend", monthlySeries(orders, revenue, false));

        List<Map<String, Object>> categoryRev = categoryBreakdown(products, orders, usingEstimate);
        m.put("categoryLabels", categoryRev.stream().map(x -> x.get("label")).toList());
        m.put("categoryValues", categoryRev.stream().map(x -> x.get("value")).toList());
        m.put("categoryPercents", categoryRev.stream().map(x -> x.get("percent")).toList());
        m.put("categoryRows", categoryRev);

        long active = products.stream().filter(Product::isActive).count();
        long withSales = products.stream().filter(p -> p.getSoldCount() != null && p.getSoldCount() > 0).count();
        long delivered = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        m.put("funnelLabels", List.of("Listed Products", "Active Listings", "Products with Sales", "Orders", "Delivered"));
        m.put("funnelValues", List.of(
                (long) products.size(),
                active,
                withSales,
                Math.max((long) orders.size(), withSales),
                Math.max(delivered, withSales > 0 ? withSales / 2 : 0)
        ));

        List<Map<String, Object>> countries = countryBreakdown(orders, List.of(seller), products, revenue, usingEstimate);
        m.put("countryLabels", countries.stream().map(x -> x.get("label")).toList());
        m.put("countryValues", countries.stream().map(x -> x.get("value")).toList());
        m.put("countryRows", countries);

        m.put("topProducts", products.stream()
                .sorted(Comparator.comparing((Product p) -> p.getSoldCount() != null ? p.getSoldCount() : 0).reversed())
                .limit(5)
                .map(p -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("name", p.getName());
                    row.put("sold", p.getSoldCount() != null ? p.getSoldCount() : 0);
                    row.put("stock", p.getStock());
                    row.put("revenue", productVolume(p));
                    row.put("health", healthForProduct(p));
                    return row;
                }).toList());

        m.put("orders", orders);
        m.put("products", products);
        m.put("year", LocalDate.now().getYear());
        return m;
    }

    private BigDecimal productVolume(Product p) {
        int sold = p.getSoldCount() != null ? p.getSoldCount() : 0;
        BigDecimal price = p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(sold)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private List<String> monthLabels() {
        List<String> labels = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            labels.add(Month.of(i).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        }
        return labels;
    }

    private List<Double> monthlySeries(List<Order> orders, BigDecimal totalFallback, boolean countMode) {
        double[] buckets = new double[12];
        int year = LocalDate.now().getYear();
        boolean any = false;
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.PENDING_PAYMENT) continue;
            LocalDateTime at = o.getPlacedAt();
            if (at == null || at.getYear() != year) continue;
            any = true;
            int idx = at.getMonthValue() - 1;
            buckets[idx] += countMode ? 1 : (o.getTotal() != null ? o.getTotal().doubleValue() : 0);
        }
        if (!any && totalFallback != null && totalFallback.compareTo(BigDecimal.ZERO) > 0) {
            double total = totalFallback.doubleValue();
            double[] weights = {0.05, 0.06, 0.07, 0.07, 0.08, 0.08, 0.09, 0.09, 0.1, 0.1, 0.1, 0.11};
            for (int i = 0; i < 12; i++) {
                buckets[i] = countMode ? Math.round(total / 120.0 * weights[i] * 12) : round2(total * weights[i]);
            }
        }
        List<Double> out = new ArrayList<>();
        for (double b : buckets) out.add(round2(b));
        return out;
    }

    private List<Map<String, Object>> categoryBreakdown(List<Product> products, List<Order> orders, boolean estimate) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        if (!estimate) {
            for (Order o : orders) {
                if (o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.PENDING_PAYMENT) continue;
                if (o.getItems() == null) continue;
                for (OrderItem item : o.getItems()) {
                    String label = item.getProduct() != null && item.getProduct().getCategory() != null
                            ? item.getProduct().getCategory().getName() : "Other";
                    map.merge(label, item.getLineTotal() != null ? item.getLineTotal() : BigDecimal.ZERO, BigDecimal::add);
                }
            }
        }
        if (map.isEmpty()) {
            for (Product p : products) {
                if (p.getCategory() == null) continue;
                map.merge(p.getCategory().getName(), productVolume(p), BigDecimal::add);
            }
        }
        BigDecimal sum = map.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return map.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(4)
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("label", e.getKey());
                    row.put("value", e.getValue().setScale(2, RoundingMode.HALF_UP));
                    double pct = sum.compareTo(BigDecimal.ZERO) == 0 ? 0
                            : e.getValue().multiply(BigDecimal.valueOf(100))
                            .divide(sum, 1, RoundingMode.HALF_UP).doubleValue();
                    row.put("percent", pct);
                    return row;
                }).toList();
    }

    private List<Map<String, Object>> countryBreakdown(List<Order> orders, List<Seller> sellers,
                                                       List<Product> products, BigDecimal totalFallback,
                                                       boolean estimate) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        if (!estimate) {
            for (Order o : orders) {
                if (o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.PENDING_PAYMENT) continue;
                // Destination country of the buyer (checkout shipping), not seller store
                String country = CountryResolver.resolveBuyer(o);
                if ("Unknown".equals(country)) continue;
                map.merge(country, o.getTotal() != null ? o.getTotal() : BigDecimal.ZERO, BigDecimal::add);
            }
        }
        BigDecimal sum = map.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal finalSum = sum;
        return map.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("label", e.getKey());
                    row.put("value", e.getValue().setScale(2, RoundingMode.HALF_UP));
                    double pct = finalSum.compareTo(BigDecimal.ZERO) == 0 ? 0
                            : e.getValue().multiply(BigDecimal.valueOf(100))
                            .divide(finalSum, 1, RoundingMode.HALF_UP).doubleValue();
                    row.put("percent", pct);
                    return row;
                }).toList();
    }

    private List<Map<String, Object>> sellerPerformance(List<Seller> sellers, List<Product> products,
                                                       List<Order> orders, boolean estimate) {
        Map<Long, BigDecimal> vol = products.stream().collect(Collectors.groupingBy(
                p -> p.getSeller() != null ? p.getSeller().getId() : -1L,
                Collectors.mapping(this::productVolume,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        Map<Long, Long> productCounts = products.stream().collect(Collectors.groupingBy(
                p -> p.getSeller() != null ? p.getSeller().getId() : -1L, Collectors.counting()));

        return sellers.stream()
                .filter(s -> s.getStatus() == SellerStatus.APPROVED)
                .sorted((a, b) -> vol.getOrDefault(b.getId(), BigDecimal.ZERO)
                        .compareTo(vol.getOrDefault(a.getId(), BigDecimal.ZERO)))
                .limit(5)
                .map(s -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    BigDecimal revenue = vol.getOrDefault(s.getId(), BigDecimal.ZERO);
                    long count = productCounts.getOrDefault(s.getId(), 0L);
                    row.put("name", s.getStoreName());
                    row.put("products", count);
                    row.put("rating", s.getRating());
                    row.put("arpu", count == 0 ? BigDecimal.ZERO
                            : revenue.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
                    row.put("revenue", revenue);
                    row.put("health", healthForSeller(s, revenue));
                    return row;
                }).toList();
    }

    private String healthForSeller(Seller s, BigDecimal revenue) {
        if (s.getRating() >= 4.8 && revenue.compareTo(BigDecimal.valueOf(10000)) > 0) return "Optimal";
        if (s.getRating() >= 4.5) return "Strong";
        if (s.getRating() >= 4.0) return "Good";
        if (s.getRating() >= 3.5) return "Stable";
        return "Watch";
    }

    private String healthForProduct(Product p) {
        int stock = p.getStock() != null ? p.getStock() : 0;
        int sold = p.getSoldCount() != null ? p.getSoldCount() : 0;
        if (stock < 5) return "Watch";
        if (sold > 1000 && stock >= 10) return "Optimal";
        if (sold > 200) return "Strong";
        if (p.isActive()) return "Good";
        return "Stable";
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
