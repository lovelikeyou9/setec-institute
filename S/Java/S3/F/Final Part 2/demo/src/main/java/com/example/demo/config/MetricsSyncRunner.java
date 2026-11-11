package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SellerRepository;
import com.example.demo.service.CountryResolver;
import com.example.demo.service.ProductVariantOptions;
import com.example.demo.service.SellerFollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Align seller followers / totalSales and product soldCount with real orders & follows.
 * Backfills explicit country fields from address text when missing (never invents Cambodia).
 */
@Component
@Order(500)
@RequiredArgsConstructor
@Slf4j
public class MetricsSyncRunner implements ApplicationRunner {

    private final SellerFollowService sellerFollowService;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        sellerFollowService.syncAllFollowerCounts();
        backfillCountries();

        Map<Long, Integer> soldByProduct = new HashMap<>();
        orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED
                        && o.getStatus() != OrderStatus.PENDING_PAYMENT
                        && o.getStatus() != OrderStatus.REFUNDED)
                .forEach(o -> {
                    if (o.getItems() == null) return;
                    o.getItems().forEach(item -> {
                        if (item.getProduct() == null || item.getProduct().getId() == null) return;
                        int qty = item.getQuantity() != null ? item.getQuantity() : 0;
                        soldByProduct.merge(item.getProduct().getId(), qty, Integer::sum);
                    });
                });

        List<Product> products = productRepository.findAll();
        int variantsFixed = 0;
        for (Product p : products) {
            boolean changed = false;
            int real = soldByProduct.getOrDefault(p.getId(), 0);
            if (p.getSoldCount() == null || !p.getSoldCount().equals(real)) {
                p.setSoldCount(real);
                changed = true;
            }
            ProductVariantOptions.Options opts = ProductVariantOptions.forCategory(p.getCategory());
            if (!Objects.equals(blankToNull(p.getColorOptions()), blankToNull(opts.color()))
                    || !Objects.equals(blankToNull(p.getStorageOptions()), blankToNull(opts.storage()))
                    || !Objects.equals(blankToNull(p.getRamOptions()), blankToNull(opts.ram()))) {
                p.setColorOptions(opts.color());
                p.setStorageOptions(opts.storage());
                p.setRamOptions(opts.ram());
                changed = true;
                variantsFixed++;
            }
            if (changed) {
                productRepository.save(p);
            }
        }
        if (variantsFixed > 0) {
            log.info("Aligned product variant options for {} products by category", variantsFixed);
        }

        for (Seller s : sellerRepository.findAll()) {
            long units = orderRepository.sumSellerUnitsSold(s.getId());
            int sales = (int) Math.min(units, Integer.MAX_VALUE);
            if (s.getTotalSales() == null || s.getTotalSales() != sales) {
                s.setTotalSales(sales);
                sellerRepository.save(s);
            }
        }

        log.info("Synced real metrics: {} products soldCount, {} sellers followers/sales",
                products.size(), sellerRepository.count());
    }

    private void backfillCountries() {
        int sellersFixed = 0;
        for (Seller s : sellerRepository.findAll()) {
            if (s.getCountry() != null && !s.getCountry().isBlank()) continue;
            String resolved = CountryResolver.resolve(s.getAddress(), s.getLocation(), null);
            if ("Unknown".equals(resolved) || "Other".equals(resolved)) continue;
            s.setCountry(resolved);
            sellerRepository.save(s);
            sellersFixed++;
        }

        int addressesFixed = 0;
        for (Address a : addressRepository.findAll()) {
            if (a.getCountry() != null && !a.getCountry().isBlank()) continue;
            String resolved = CountryResolver.resolve(a.getFullAddress(), null, null);
            if ("Unknown".equals(resolved) || "Other".equals(resolved)) continue;
            a.setCountry(resolved);
            addressRepository.save(a);
            addressesFixed++;
        }

        int ordersFixed = 0;
        for (com.example.demo.model.Order o : orderRepository.findAll()) {
            if (o.getShippingCountry() != null && !o.getShippingCountry().isBlank()) continue;
            // Only from shipping address text — never seller store location
            String resolved = CountryResolver.resolve(o.getShippingAddress(), null, null);
            if ("Unknown".equals(resolved) || "Other".equals(resolved)) continue;
            o.setShippingCountry(resolved);
            orderRepository.save(o);
            ordersFixed++;
        }

        if (sellersFixed + addressesFixed + ordersFixed > 0) {
            log.info("Backfilled countries: {} sellers, {} addresses, {} orders",
                    sellersFixed, addressesFixed, ordersFixed);
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
