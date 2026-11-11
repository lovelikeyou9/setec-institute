package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class AdminProfileService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final AddressRepository addressRepository;
    private final CommissionRepository commissionRepository;
    private final SellerFollowRepository sellerFollowRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> userProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderRepository.findByUserIdOrderByPlacedAtDesc(userId).stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED
                        && o.getStatus() != OrderStatus.PENDING_PAYMENT)
                .toList();

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime yearStart = LocalDate.of(year, 1, 1).atStartOfDay();

        BigDecimal totalSpent = sumOrders(orders);
        long orderCount = orders.size();
        int unitsBought = orders.stream()
                .flatMap(o -> o.getItems() == null ? java.util.stream.Stream.empty() : o.getItems().stream())
                .mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0)
                .sum();

        List<Order> todayOrders = filterFrom(orders, dayStart);
        List<Order> monthOrders = filterFrom(orders, monthStart);
        List<Order> yearOrders = filterFrom(orders, yearStart);

        Long linkedSellerId = sellerRepository.findByUserId(userId)
                .map(Seller::getId)
                .orElse(null);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("profileUser", user);
        m.put("linkedSellerId", linkedSellerId);
        m.put("addresses", addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userId));
        m.put("wishlistCount", wishlistItemRepository.countByUserId(userId));
        m.put("orderCount", orderCount);
        m.put("unitsBought", unitsBought);
        m.put("totalSpent", totalSpent);
        m.put("avgOrder", orderCount == 0 ? BigDecimal.ZERO
                : totalSpent.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP));

        m.put("ordersToday", todayOrders.size());
        m.put("spentToday", sumOrders(todayOrders));
        m.put("ordersMonth", monthOrders.size());
        m.put("spentMonth", sumOrders(monthOrders));
        m.put("ordersYear", yearOrders.size());
        m.put("spentYear", sumOrders(yearOrders));

        m.put("topCategories", topCategoriesFromOrders(orders, 5));
        m.put("topProducts", topProductsFromOrders(orders, 5));
        m.put("recentOrders", orders.stream().limit(10).toList());

        m.put("months", monthLabels());
        m.put("ordersTrend", monthlyOrderCounts(orders, year));
        m.put("spendTrend", monthlyOrderSpend(orders, year));
        m.put("year", year);
        return m;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> sellerProfile(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        if (seller.getUser() != null) {
            seller.getUser().getEmail(); // initialize lazy user for the view
        }
        List<Product> products = productRepository.findBySellerIdOrderByIdDesc(sellerId);
        List<Order> orders = orderRepository.findBySellerIdOrderByPlacedAtDesc(sellerId).stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED
                        && o.getStatus() != OrderStatus.PENDING_PAYMENT
                        && o.getStatus() != OrderStatus.REFUNDED)
                .toList();

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime yearStart = LocalDate.of(year, 1, 1).atStartOfDay();

        // Real order metrics only (no fake catalog revenue)
        BigDecimal revenue = sumOrders(orders);
        int unitsSold = orders.stream()
                .flatMap(o -> o.getItems() == null ? java.util.stream.Stream.empty() : o.getItems().stream())
                .mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0)
                .sum();

        List<Order> todayOrders = filterFrom(orders, dayStart);
        List<Order> monthOrders = filterFrom(orders, monthStart);
        List<Order> yearOrders = filterFrom(orders, yearStart);

        long wishlistAdds = wishlistItemRepository.countByProductSellerId(sellerId);
        long uniqueBuyers = orders.stream()
                .map(o -> o.getUser() != null ? o.getUser().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        long followers = sellerFollowRepository.countBySellerId(sellerId);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("seller", seller);
        m.put("productCount", products.size());
        m.put("activeProducts", products.stream().filter(Product::isActive).count());
        m.put("unitsSold", unitsSold);
        m.put("revenue", revenue);
        m.put("orderCount", orders.size());
        m.put("uniqueBuyers", uniqueBuyers);
        m.put("wishlistAdds", wishlistAdds);
        m.put("usingEstimate", false);
        m.put("rating", seller.getRating());
        m.put("followers", followers);

        m.put("soldToday", unitsFrom(todayOrders));
        m.put("revenueToday", sumOrders(todayOrders));
        m.put("soldMonth", unitsFrom(monthOrders));
        m.put("revenueMonth", sumOrders(monthOrders));
        m.put("soldYear", unitsFrom(yearOrders));
        m.put("revenueYear", sumOrders(yearOrders));

        m.put("topCategories", topCategoriesFromOrders(orders, 5));
        m.put("topProducts", topProductsFromOrders(orders, 5));
        m.put("recentOrders", orders.stream().limit(10).toList());

        m.put("months", monthLabels());
        m.put("ordersTrend", monthlyOrderCounts(orders, year));
        m.put("revenueTrend", monthlyOrderSpend(orders, year));
        m.put("year", year);
        return m;
    }

    private List<Order> filterFrom(List<Order> orders, LocalDateTime from) {
        return orders.stream()
                .filter(o -> o.getPlacedAt() != null && !o.getPlacedAt().isBefore(from))
                .toList();
    }

    private BigDecimal sumOrders(List<Order> orders) {
        return orders.stream()
                .map(o -> o.getTotal() != null ? o.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private int unitsFrom(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems() == null ? java.util.stream.Stream.empty() : o.getItems().stream())
                .mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0)
                .sum();
    }

    private List<Map<String, Object>> topCategoriesFromOrders(List<Order> orders, int limit) {
        Map<String, Integer> qty = new HashMap<>();
        Map<String, BigDecimal> spend = new HashMap<>();
        for (Order o : orders) {
            if (o.getItems() == null) continue;
            for (OrderItem item : o.getItems()) {
                String label = item.getProduct() != null && item.getProduct().getCategory() != null
                        ? item.getProduct().getCategory().getName()
                        : "Other";
                int q = item.getQuantity() != null ? item.getQuantity() : 0;
                qty.merge(label, q, Integer::sum);
                spend.merge(label, item.getLineTotal() != null ? item.getLineTotal() : BigDecimal.ZERO, BigDecimal::add);
            }
        }
        return ranked(qty, spend, limit);
    }

    private List<Map<String, Object>> topProductsFromOrders(List<Order> orders, int limit) {
        Map<String, Integer> qty = new HashMap<>();
        Map<String, BigDecimal> spend = new HashMap<>();
        for (Order o : orders) {
            if (o.getItems() == null) continue;
            for (OrderItem item : o.getItems()) {
                String label = item.getProductName() != null ? item.getProductName() : "Product";
                int q = item.getQuantity() != null ? item.getQuantity() : 0;
                qty.merge(label, q, Integer::sum);
                spend.merge(label, item.getLineTotal() != null ? item.getLineTotal() : BigDecimal.ZERO, BigDecimal::add);
            }
        }
        return ranked(qty, spend, limit);
    }

    private List<Map<String, Object>> topCategoriesFromProducts(List<Product> products, int limit) {
        Map<String, Integer> qty = new HashMap<>();
        Map<String, BigDecimal> spend = new HashMap<>();
        for (Product p : products) {
            if (p.getCategory() == null) continue;
            String label = p.getCategory().getName();
            int sold = p.getSoldCount() != null ? p.getSoldCount() : 0;
            BigDecimal rev = (p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO)
                    .multiply(BigDecimal.valueOf(sold));
            qty.merge(label, sold, Integer::sum);
            spend.merge(label, rev, BigDecimal::add);
        }
        return ranked(qty, spend, limit);
    }

    private List<Map<String, Object>> topProductsFromCatalog(List<Product> products, int limit) {
        return products.stream()
                .sorted(Comparator.comparing((Product p) -> p.getSoldCount() != null ? p.getSoldCount() : 0).reversed())
                .limit(limit)
                .map(p -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    int sold = p.getSoldCount() != null ? p.getSoldCount() : 0;
                    BigDecimal rev = (p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO)
                            .multiply(BigDecimal.valueOf(sold)).setScale(2, RoundingMode.HALF_UP);
                    row.put("label", p.getName());
                    row.put("qty", sold);
                    row.put("amount", rev);
                    return row;
                }).toList();
    }

    private List<Map<String, Object>> ranked(Map<String, Integer> qty, Map<String, BigDecimal> spend, int limit) {
        return qty.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("label", e.getKey());
                    row.put("qty", e.getValue());
                    row.put("amount", spend.getOrDefault(e.getKey(), BigDecimal.ZERO)
                            .setScale(2, RoundingMode.HALF_UP));
                    return row;
                }).toList();
    }

    private List<String> monthLabels() {
        List<String> labels = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            labels.add(Month.of(i).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        }
        return labels;
    }

    private List<Double> monthlyOrderCounts(List<Order> orders, int year) {
        double[] buckets = new double[12];
        for (Order o : orders) {
            if (o.getPlacedAt() == null || o.getPlacedAt().getYear() != year) continue;
            buckets[o.getPlacedAt().getMonthValue() - 1] += 1;
        }
        return Arrays.stream(buckets).boxed().collect(Collectors.toList());
    }

    private List<Double> monthlyOrderSpend(List<Order> orders, int year) {
        double[] buckets = new double[12];
        for (Order o : orders) {
            if (o.getPlacedAt() == null || o.getPlacedAt().getYear() != year) continue;
            buckets[o.getPlacedAt().getMonthValue() - 1] += o.getTotal() != null ? o.getTotal().doubleValue() : 0;
        }
        List<Double> out = new ArrayList<>();
        for (double b : buckets) out.add(Math.round(b * 100.0) / 100.0);
        return out;
    }

    private List<Double> estimateMonthly(double total, boolean countMode) {
        double[] weights = {0.05, 0.06, 0.07, 0.07, 0.08, 0.08, 0.09, 0.09, 0.1, 0.1, 0.1, 0.11};
        List<Double> out = new ArrayList<>();
        for (double w : weights) {
            out.add(countMode ? (double) Math.round(total * w) : Math.round(total * w * 100.0) / 100.0);
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> countriesList() {
        return countriesList("money_desc");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> countriesList(String sort) {
        List<Order> orders = validOrders(orderRepository.findAll());
        List<Seller> sellers = sellerRepository.findAll();
        List<Address> addresses = addressRepository.findAll();
        List<WishlistItem> wishlists = wishlistItemRepository.findAll();

        Map<String, CountryAgg> map = new LinkedHashMap<>();

        for (Seller s : sellers) {
            if (s.getStatus() == SellerStatus.PENDING) continue;
            String sellCountry = CountryResolver.resolveSeller(s);
            if ("Unknown".equals(sellCountry)) continue;
            CountryAgg agg = map.computeIfAbsent(sellCountry, k -> new CountryAgg());
            agg.sellerIds.add(s.getId());
        }

        Map<Long, String> userCountry = resolveUserCountries(addresses, orders);
        for (Map.Entry<Long, String> e : userCountry.entrySet()) {
            if ("Unknown".equals(e.getValue())) continue;
            CountryAgg agg = map.computeIfAbsent(e.getValue(), k -> new CountryAgg());
            agg.buyerIds.add(e.getKey());
        }

        for (Order o : orders) {
            // Buyer destination only — never seller store location
            String buyCountry = CountryResolver.resolveBuyer(o);
            if (!"Unknown".equals(buyCountry)) {
                CountryAgg buyAgg = map.computeIfAbsent(buyCountry, k -> new CountryAgg());
                buyAgg.buyOrders++;
                buyAgg.buyMoney = buyAgg.buyMoney.add(nz(o.getTotal()));
                buyAgg.buyUnits += unitsFrom(List.of(o));
                if (o.getUser() != null) buyAgg.buyerIds.add(o.getUser().getId());
            }

            if (o.getSeller() != null) {
                String sellCountry = CountryResolver.resolveSeller(o.getSeller());
                if ("Unknown".equals(sellCountry)) continue;
                CountryAgg sellAgg = map.computeIfAbsent(sellCountry, k -> new CountryAgg());
                sellAgg.sellOrders++;
                sellAgg.sellMoney = sellAgg.sellMoney.add(nz(o.getTotal()));
                sellAgg.sellUnits += unitsFrom(List.of(o));
                sellAgg.sellerIds.add(o.getSeller().getId());
            }
        }

        for (WishlistItem w : wishlists) {
            if (w.getUser() != null) {
                String c = userCountry.get(w.getUser().getId());
                if (c != null && !"Unknown".equals(c)) {
                    map.computeIfAbsent(c, k -> new CountryAgg()).wishlistViews++;
                }
            }
            if (w.getProduct() != null && w.getProduct().getSeller() != null) {
                Seller s = w.getProduct().getSeller();
                String c = CountryResolver.resolveSeller(s);
                if (!"Unknown".equals(c)) {
                    map.computeIfAbsent(c, k -> new CountryAgg()).wishlistOnSellers++;
                }
            }
        }

        List<Map<String, Object>> rows = map.entrySet().stream()
                .map(e -> {
                    CountryAgg a = e.getValue();
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("name", e.getKey());
                    row.put("sellerCount", a.sellerIds.size());
                    row.put("buyerCount", a.buyerIds.size());
                    row.put("buyOrders", a.buyOrders);
                    row.put("sellOrders", a.sellOrders);
                    row.put("buyMoney", a.buyMoney.setScale(2, RoundingMode.HALF_UP));
                    row.put("sellMoney", a.sellMoney.setScale(2, RoundingMode.HALF_UP));
                    row.put("buyUnits", a.buyUnits);
                    row.put("sellUnits", a.sellUnits);
                    row.put("wishlistViews", a.wishlistViews);
                    row.put("usingEstimate", false);
                    return row;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        sortCountryRows(rows, sort);
        return rows;
    }

    public void sortCountryRows(List<Map<String, Object>> rows, String sort) {
        if (rows == null || rows.isEmpty()) return;
        String key = sort != null ? sort : "money_desc";
        Comparator<Map<String, Object>> cmp = switch (key) {
            case "name_asc" -> Comparator.comparing(r -> str(r.get("name")), String.CASE_INSENSITIVE_ORDER);
            case "name_desc" -> Comparator.comparing((Map<String, Object> r) -> str(r.get("name")),
                    String.CASE_INSENSITIVE_ORDER).reversed();
            case "sellers_asc" -> Comparator.comparingLong(r -> numLong(r.get("sellerCount")));
            case "sellers_desc" -> Comparator.comparingLong((Map<String, Object> r) -> numLong(r.get("sellerCount"))).reversed();
            case "buyers_asc" -> Comparator.comparingLong(r -> numLong(r.get("buyerCount")));
            case "buyers_desc" -> Comparator.comparingLong((Map<String, Object> r) -> numLong(r.get("buyerCount"))).reversed();
            case "buy_money_desc" -> Comparator.comparing((Map<String, Object> r) -> money(r.get("buyMoney"))).reversed();
            case "sell_money_desc" -> Comparator.comparing((Map<String, Object> r) -> money(r.get("sellMoney"))).reversed();
            default -> Comparator.comparing((Map<String, Object> r) ->
                    money(r.get("buyMoney")).add(money(r.get("sellMoney")))).reversed();
        };
        rows.sort(cmp);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> sortSellerRows(List<Map<String, Object>> rows, String sort) {
        if (rows == null) return List.of();
        List<Map<String, Object>> copy = new ArrayList<>(rows);
        String key = sort != null ? sort : "revenue_desc";
        Comparator<Map<String, Object>> cmp = switch (key) {
            case "name_asc" -> Comparator.comparing(r -> str(r.get("storeName")), String.CASE_INSENSITIVE_ORDER);
            case "name_desc" -> Comparator.comparing((Map<String, Object> r) -> str(r.get("storeName")),
                    String.CASE_INSENSITIVE_ORDER).reversed();
            case "units_desc" -> Comparator.comparingLong((Map<String, Object> r) -> numLong(r.get("units"))).reversed();
            case "units_asc" -> Comparator.comparingLong(r -> numLong(r.get("units")));
            case "revenue_asc" -> Comparator.comparing(r -> money(r.get("revenue")));
            default -> Comparator.comparing((Map<String, Object> r) -> money(r.get("revenue"))).reversed();
        };
        copy.sort(cmp);
        return copy;
    }

    public List<Map<String, Object>> sortBuyerRows(List<Map<String, Object>> rows, String sort) {
        if (rows == null) return List.of();
        List<Map<String, Object>> copy = new ArrayList<>(rows);
        String key = sort != null ? sort : "spent_desc";
        Comparator<Map<String, Object>> cmp = switch (key) {
            case "name_asc" -> Comparator.comparing(r -> str(r.get("fullName")), String.CASE_INSENSITIVE_ORDER);
            case "name_desc" -> Comparator.comparing((Map<String, Object> r) -> str(r.get("fullName")),
                    String.CASE_INSENSITIVE_ORDER).reversed();
            case "orders_desc" -> Comparator.comparingLong((Map<String, Object> r) -> numLong(r.get("orders"))).reversed();
            case "orders_asc" -> Comparator.comparingLong(r -> numLong(r.get("orders")));
            case "spent_asc" -> Comparator.comparing(r -> money(r.get("spent")));
            default -> Comparator.comparing((Map<String, Object> r) -> money(r.get("spent"))).reversed();
        };
        copy.sort(cmp);
        return copy;
    }

    private static String str(Object v) {
        return v != null ? v.toString() : "";
    }

    private static long numLong(Object v) {
        if (v instanceof Number n) return n.longValue();
        return 0L;
    }

    private static BigDecimal money(Object v) {
        if (v instanceof BigDecimal bd) return bd;
        if (v instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> countryProfile(String countryName) {
        String country = normalizeCountryName(countryName);
        List<Order> allOrders = validOrders(orderRepository.findAll());
        List<Seller> allSellers = sellerRepository.findAll();
        List<Address> addresses = addressRepository.findAll();
        List<WishlistItem> wishlists = wishlistItemRepository.findAll();

        Map<Long, String> userCountry = resolveUserCountries(addresses, allOrders);

        List<Seller> sellers = allSellers.stream()
                .filter(s -> CountryResolver.matchesSeller(country, s))
                .toList();
        Set<Long> sellerIds = sellers.stream().map(Seller::getId).collect(Collectors.toSet());

        List<Order> buyOrders = allOrders.stream()
                .filter(o -> CountryResolver.matchesBuyer(country, o))
                .toList();
        List<Order> sellOrders = allOrders.stream()
                .filter(o -> o.getSeller() != null && sellerIds.contains(o.getSeller().getId()))
                .toList();

        Set<Long> buyerIds = new LinkedHashSet<>();
        userCountry.forEach((uid, c) -> {
            if (country.equalsIgnoreCase(c)) buyerIds.add(uid);
        });
        buyOrders.stream().filter(o -> o.getUser() != null).forEach(o -> buyerIds.add(o.getUser().getId()));

        boolean usingEstimate = false; // real orders only
        BigDecimal buyMoney = sumOrders(buyOrders);
        BigDecimal sellMoney = sumOrders(sellOrders);
        int buyUnits = unitsFrom(buyOrders);
        int sellUnits = unitsFrom(sellOrders);

        long wishlistViews = wishlists.stream()
                .filter(w -> w.getUser() != null && buyerIds.contains(w.getUser().getId()))
                .count();
        long wishlistOnSellers = wishlists.stream()
                .filter(w -> w.getProduct() != null && w.getProduct().getSeller() != null
                        && sellerIds.contains(w.getProduct().getSeller().getId()))
                .count();

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime yearStart = LocalDate.of(year, 1, 1).atStartOfDay();

        List<Map<String, Object>> sellerRows = sellers.stream()
                .map(s -> {
                    List<Order> sOrders = sellOrders.stream()
                            .filter(o -> o.getSeller() != null && o.getSeller().getId().equals(s.getId()))
                            .toList();
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", s.getId());
                    row.put("storeName", s.getStoreName());
                    row.put("location", s.getLocation() != null ? s.getLocation() : "—");
                    row.put("status", s.getStatus().name());
                    row.put("orders", sOrders.size());
                    row.put("units", unitsFrom(sOrders));
                    row.put("revenue", sumOrders(sOrders));
                    return row;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")))
                .toList();

        List<Map<String, Object>> buyerRows = buyerIds.stream()
                .map(uid -> {
                    User u = userRepository.findById(uid).orElse(null);
                    if (u == null) return null;
                    List<Order> uOrders = buyOrders.stream()
                            .filter(o -> o.getUser() != null && o.getUser().getId().equals(uid))
                            .toList();
                    long wl = wishlists.stream()
                            .filter(w -> w.getUser() != null && w.getUser().getId().equals(uid))
                            .count();
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", u.getId());
                    row.put("fullName", u.getFullName());
                    row.put("email", u.getEmail());
                    row.put("orders", uOrders.size());
                    row.put("units", unitsFrom(uOrders));
                    row.put("spent", sumOrders(uOrders));
                    row.put("wishlist", wl);
                    return row;
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> ((BigDecimal) b.get("spent")).compareTo((BigDecimal) a.get("spent")))
                .limit(50)
                .toList();

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("countryName", country);
        m.put("sellerCount", sellers.size());
        m.put("buyerCount", buyerIds.size());
        m.put("buyOrders", buyOrders.size());
        m.put("sellOrders", sellOrders.size());
        m.put("buyMoney", buyMoney);
        m.put("sellMoney", sellMoney);
        m.put("buyUnits", buyUnits);
        m.put("sellUnits", sellUnits);
        m.put("wishlistViews", wishlistViews);
        m.put("wishlistOnSellers", wishlistOnSellers);
        m.put("usingEstimate", usingEstimate);

        // Platform 3% fees on sales from sellers in this country
        Set<Long> sellOrderIds = sellOrders.stream().map(Order::getId).collect(Collectors.toSet());
        List<Commission> countryCommissions = commissionRepository.findAll().stream()
                .filter(c -> c.getOrder() != null && sellOrderIds.contains(c.getOrder().getId()))
                .toList();
        BigDecimal feesEarned = countryCommissions.stream()
                .filter(c -> c.getPayoutStatus() == PayoutStatus.RELEASED)
                .map(c -> c.getCommissionAmount() != null ? c.getCommissionAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal feesInEscrow = countryCommissions.stream()
                .filter(c -> c.getPayoutStatus() == PayoutStatus.HELD
                        || c.getPayoutStatus() == PayoutStatus.IN_PROTECTION)
                .map(c -> c.getCommissionAmount() != null ? c.getCommissionAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal feesPendingTotal = feesEarned.add(feesInEscrow);
        m.put("feesEarned", feesEarned);
        m.put("feesInEscrow", feesInEscrow);
        m.put("feesTotal", feesPendingTotal);
        m.put("commissionRate", "3%");
        m.put("feeOrders", countryCommissions.size());

        m.put("buyMonth", sumOrders(filterFrom(buyOrders, monthStart)));
        m.put("sellMonth", sumOrders(filterFrom(sellOrders, monthStart)));
        m.put("buyYear", sumOrders(filterFrom(buyOrders, yearStart)));
        m.put("sellYear", sumOrders(filterFrom(sellOrders, yearStart)));
        m.put("ordersMonth", filterFrom(buyOrders, monthStart).size());
        m.put("ordersYear", filterFrom(buyOrders, yearStart).size());

        m.put("topBuyCategories", topCategoriesFromOrders(buyOrders, 5));
        m.put("topBuyProducts", topProductsFromOrders(buyOrders, 5));
        m.put("topSellCategories", topCategoriesFromOrders(sellOrders, 5));
        m.put("topSellProducts", topProductsFromOrders(sellOrders, 5));

        m.put("sellers", sellerRows);
        m.put("buyers", buyerRows);
        m.put("recentOrders", buyOrders.stream().limit(12).toList());

        m.put("months", monthLabels());
        m.put("buyTrend", monthlyOrderSpend(buyOrders, year));
        m.put("sellTrend", monthlyOrderSpend(sellOrders, year));
        m.put("ordersTrend", monthlyOrderCounts(buyOrders, year));
        m.put("year", year);
        return m;
    }

    private String normalizeCountryName(String name) {
        if (name == null || name.isBlank()) throw new RuntimeException("Country not found");
        String decoded = java.net.URLDecoder.decode(name, java.nio.charset.StandardCharsets.UTF_8).trim();
        // Canonicalize against known resolver labels
        for (String known : List.of("Cambodia", "United States", "United Kingdom", "Germany", "Canada",
                "Thailand", "Singapore", "Vietnam", "Japan", "Australia", "China", "Other")) {
            if (known.equalsIgnoreCase(decoded)) return known;
        }
        return decoded;
    }

    private List<Order> validOrders(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED
                        && o.getStatus() != OrderStatus.PENDING_PAYMENT)
                .toList();
    }

    private Map<Long, String> resolveUserCountries(List<Address> addresses, List<Order> orders) {
        Map<Long, String> map = new LinkedHashMap<>();
        for (Address a : addresses) {
            if (a.getUser() == null) continue;
            Long uid = a.getUser().getId();
            if (map.containsKey(uid) && !a.isDefaultAddress()) continue;
            map.put(uid, CountryResolver.resolveAddress(a));
        }
        for (Order o : orders) {
            if (o.getUser() == null || map.containsKey(o.getUser().getId())) continue;
            map.put(o.getUser().getId(), CountryResolver.resolveBuyer(o));
        }
        return map;
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private static final class CountryAgg {
        final Set<Long> sellerIds = new LinkedHashSet<>();
        final Set<Long> buyerIds = new LinkedHashSet<>();
        int buyOrders;
        int sellOrders;
        int buyUnits;
        int sellUnits;
        BigDecimal buyMoney = BigDecimal.ZERO;
        BigDecimal sellMoney = BigDecimal.ZERO;
        long wishlistViews;
        long wishlistOnSellers;
    }
}
