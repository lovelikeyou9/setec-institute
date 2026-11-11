package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final BrandRepository brandRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean mmtReady = userRepository.findByEmail("admin@mmt.store").isPresent();
        if (mmtReady && productRepository.count() > 0) {
            if (brandRepository.count() == 0) {
                seedBrandsFromProducts();
            }
            return;
        }

        wipeAllTables();
        Map<String, Category> categories = seedCategories();
        seedProducts(categories);
        seedBrandsFromProducts();
        seedUsers();
        seedAuditLogs();
    }

    private void wipeAllTables() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.update("DELETE FROM cart_items");
        jdbcTemplate.update("DELETE FROM wishlist_items");
        jdbcTemplate.update("DELETE FROM order_items");
        jdbcTemplate.update("DELETE FROM orders");
        jdbcTemplate.update("DELETE FROM products");
        jdbcTemplate.update("DELETE FROM categories");
        try {
            jdbcTemplate.update("DELETE FROM brands");
        } catch (Exception ignored) {
            // table may not exist on first boot before ddl-auto creates it
        }
        jdbcTemplate.update("DELETE FROM audit_logs");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private void seedBrandsFromProducts() {
        productRepository.findAll().stream()
                .map(Product::getBrand)
                .filter(b -> b != null && !b.isBlank())
                .map(String::trim)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEach(name -> {
                    String slug = name.toLowerCase()
                            .replaceAll("[^a-z0-9]+", "-")
                            .replaceAll("(^-|-$)", "");
                    if (!brandRepository.existsByNameIgnoreCase(name)) {
                        brandRepository.save(Brand.builder()
                                .name(name)
                                .slug(slug.isBlank() ? "brand-" + System.nanoTime() : slug)
                                .description(name + " products at MMT")
                                .active(true)
                                .build());
                    }
                });
    }

    private Map<String, Category> seedCategories() {
        List<Category> list = List.of(
                category("Laptops & PCs", "laptops-pcs", "laptop", "Laptops, desktops, and workstations", 1),
                category("Components", "components", "memory", "GPUs, CPUs, RAM, and boards", 2),
                category("Monitors & TVs", "monitors-tvs", "tv", "Gaming monitors and smart TVs", 3),
                category("Networking", "networking", "wifi", "Routers, mesh, and switches", 4),
                category("Drones & Cameras", "drones-cameras", "videocam", "Drones, action cams, and capture", 5),
                category("Audio & Streaming", "audio-streaming", "mic", "Mics, headsets, and capture cards", 6),
                category("Gaming & Consoles", "gaming-consoles", "sports_esports", "Consoles, controllers, and games", 7),
                category("Storage & NAS", "storage-nas", "sd_storage", "SSDs, HDDs, and NAS", 8),
                category("Accessories", "accessories", "cable", "Chargers, UPS, and power gear", 9),
                category("Gift Cards", "gift-cards", "card_giftcard", "Steam, Xbox, and PlayStation cards", 10)
        );
        categoryRepository.saveAll(list);
        Map<String, Category> map = new LinkedHashMap<>();
        for (Category c : list) {
            map.put(c.getSlug(), c);
        }
        return map;
    }

    private Category category(String name, String slug, String icon, String description, int sort) {
        return Category.builder()
                .name(name)
                .slug(slug)
                .iconName(icon)
                .description(description)
                .sortOrder(sort)
                .build();
    }

    private String img(String fileName) {
        return "/uploads/products/" + UriUtils.encodePath(fileName, StandardCharsets.UTF_8);
    }

    private String avatar(String fileName) {
        return "/uploads/avatars/customers/" + UriUtils.encodePath(fileName, StandardCharsets.UTF_8);
    }

    private void seedProducts(Map<String, Category> categories) {
        List<Product> products = new ArrayList<>();
        products.add(p(categories, "Lenovo Legion Pro 7i", "Lenovo", "MMT-LAP-LEGION7I", "laptops-pcs",
                "High-refresh gaming laptop for creators and esports.",
                "1899.00", "2099.00", 8, 3, "B1-LAP", "lenovo-legion-pro-7i.webp", "Intel Core Ultra",
                4.8, 96, true, true, "1550.00",
                "{\"Display\":\"16\\\" QHD+\",\"GPU\":\"RTX 4070\",\"RAM\":\"32GB\",\"Storage\":\"1TB SSD\"}",
                "laptop,gaming,lenovo"));
        products.add(p(categories, "Dell XPS 15 OLED", "Dell", "MMT-LAP-XPS15", "laptops-pcs",
                "Premium OLED ultrabook for design and productivity.",
                "1699.00", "1899.00", 12, 4, "B1-LAP", "dell-xps-15-oled.webp", "Intel Core Ultra",
                4.7, 74, true, false, "1400.00",
                "{\"Display\":\"15.6\\\" OLED\",\"RAM\":\"16GB\",\"Storage\":\"512GB SSD\",\"Weight\":\"1.86kg\"}",
                "laptop,dell,oled"));
        products.add(p(categories, "ASUS ROG Strix Gaming PC RTX 5070", "ASUS", "MMT-PC-ROG-5070", "laptops-pcs",
                "Prebuilt ROG tower ready for 1440p / 4K gaming.",
                "2199.00", "2499.00", 5, 2, "A2-RIG", "asus-rog-strix-gaming-pc-rtx-5070.avif", "Blackwell",
                4.9, 41, true, true, "1800.00",
                "{\"GPU\":\"RTX 5070\",\"CPU\":\"Ryzen 7\",\"RAM\":\"32GB DDR5\",\"Cooling\":\"AIO 360\"}",
                "desktop,rog,prebuilt"));
        products.add(p(categories, "Gigabyte AORUS RTX 5070 Ti", "Gigabyte", "MMT-GPU-5070TI", "components",
                "Next-gen AORUS graphics for enthusiast gaming.",
                "899.00", "999.00", 7, 3, "A1-GPU", "gigabyte-aorus-rtx-5070-ti.png", "Blackwell",
                4.8, 58, true, true, "720.00",
                "{\"VRAM\":\"16GB\",\"TDP\":\"300W\",\"Bus\":\"256-bit\",\"Cooling\":\"WINDFORCE\"}",
                "gpu,nvidia,aorus"));
        products.add(p(categories, "MSI GeForce RTX 5070 Gaming X", "MSI", "MMT-GPU-5070-MSI", "components",
                "TRI FROZR cooled RTX 5070 for high-FPS builds.",
                "649.00", "699.00", 2, 5, "A1-GPU", "msi-geforce-rtx-5070-gaming-x-12gb.jpg", "Blackwell",
                4.7, 33, false, true, "520.00",
                "{\"VRAM\":\"12GB\",\"TDP\":\"250W\",\"Fans\":\"3X\",\"Boost\":\"High OC\"}",
                "gpu,msi,gaming"));
        products.add(p(categories, "AMD Ryzen 7 9800X3D", "AMD", "MMT-CPU-9800X3D", "components",
                "3D V-Cache gaming CPU for max FPS.",
                "479.00", "519.00", 15, 5, "A3-CPU", "amd-ryzen-7-9800x3d.png", "Zen 5",
                4.9, 210, true, false, "390.00",
                "{\"Cores\":\"8\",\"Threads\":\"16\",\"Cache\":\"96MB\",\"Socket\":\"AM5\"}",
                "cpu,amd,gaming"));
        products.add(p(categories, "Intel Core Ultra 7 265K", "Intel", "MMT-CPU-265K", "components",
                "Arrow Lake desktop CPU for creators and gamers.",
                "399.00", "429.00", 18, 6, "A3-CPU", "intel-core-ultra-7-265k.jpg", "Arrow Lake",
                4.6, 88, false, false, "320.00",
                "{\"Cores\":\"20\",\"Socket\":\"LGA1851\",\"iGPU\":\"Yes\",\"TDP\":\"125W\"}",
                "cpu,intel,ultra"));
        products.add(p(categories, "Corsair Vengeance DDR5 32GB", "Corsair", "MMT-RAM-VEN-32", "components",
                "Fast DDR5 kit for gaming and content creation.",
                "129.00", "149.00", 40, 10, "A4-RAM", "corsair-vengeance-ddr5-32gb.jpg", "DDR5",
                4.8, 156, false, false, "95.00",
                "{\"Capacity\":\"32GB\",\"Speed\":\"6000MT/s\",\"Kit\":\"2x16GB\",\"RGB\":\"No\"}",
                "ram,ddr5,corsair"));
        products.add(p(categories, "ROG Swift OLED PG32UCDP", "ASUS", "MMT-MON-PG32", "monitors-tvs",
                "32\" WOLED gaming monitor with stunning contrast.",
                "1299.00", "1499.00", 4, 2, "C1-MON",
                "ROG Swift OLED PG32UCDP Gaming Monitor WOLED.jpg", "WOLED",
                4.9, 67, true, true, "1050.00",
                "{\"Size\":\"32\\\"\",\"Refresh\":\"240Hz\",\"Res\":\"4K\",\"Panel\":\"WOLED\"}",
                "monitor,oled,rog"));
        products.add(p(categories, "ASUS ROG Strix 27 Monitor", "ASUS", "MMT-MON-ROG27", "monitors-tvs",
                "Fast IPS gaming monitor for competitive play.",
                "449.00", "499.00", 11, 4, "C1-MON", "asus-rog-strix-27-monitor.webp", "Fast IPS",
                4.7, 102, false, false, "340.00",
                "{\"Size\":\"27\\\"\",\"Refresh\":\"180Hz\",\"Res\":\"QHD\",\"GSync\":\"Compatible\"}",
                "monitor,gaming,asus"));
        products.add(p(categories, "LG NANO80 65\" 4K Smart TV", "LG", "MMT-TV-NANO80-65", "monitors-tvs",
                "Nanocell 4K smart TV for movies and console gaming.",
                "899.00", "1099.00", 6, 2, "C2-TV",
                "LG NANO80 65 (164cm) 4K SMART NANOCELL .webp", "NanoCell",
                4.6, 49, true, false, "700.00",
                "{\"Size\":\"65\\\"\",\"Res\":\"4K\",\"HDR\":\"Yes\",\"Smart\":\"webOS\"}",
                "tv,lg,4k"));
        products.add(p(categories, "TP-Link Archer AX55", "TP-Link", "MMT-NET-AX55", "networking",
                "AX3000 Wi-Fi 6 router for modern homes.",
                "99.00", "129.00", 25, 8, "D1-NET", "tp-link-archer-ax55.jpg", "Wi-Fi 6",
                4.5, 188, false, false, "65.00",
                "{\"WiFi\":\"AX3000\",\"Bands\":\"Dual\",\"Ports\":\"Gigabit\",\"VPN\":\"Yes\"}",
                "router,wifi6,tplink"));
        products.add(p(categories, "BE9300 Tri-Band Wi-Fi 7 Router", "TP-Link", "MMT-NET-BE9300", "networking",
                "Next-gen Wi-Fi 7 router for multi-gig homes.",
                "349.00", "399.00", 9, 3, "D1-NET", "BE9300 Tri-Band Wi-Fi 7 Router.jpg", "Wi-Fi 7",
                4.7, 36, true, true, "270.00",
                "{\"WiFi\":\"BE9300\",\"Bands\":\"Tri\",\"Ports\":\"2.5G\",\"Mesh\":\"Compatible\"}",
                "router,wifi7,tplink"));
        products.add(p(categories, "TP-Link Deco BE63 Mesh", "TP-Link", "MMT-NET-DECO-BE63", "networking",
                "BE10000 whole-home Wi-Fi 7 mesh system.",
                "499.00", "559.00", 7, 3, "D1-NET", "TP-Link Deco BE63 BE10000 Wireless .jpg", "Wi-Fi 7 Mesh",
                4.8, 28, true, false, "390.00",
                "{\"System\":\"Mesh\",\"WiFi\":\"BE10000\",\"Coverage\":\"Whole home\",\"App\":\"Deco\"}",
                "mesh,wifi7,deco"));
        products.add(p(categories, "DJI Mini 4 Pro", "DJI", "MMT-DRN-MINI4", "drones-cameras",
                "Compact cinematic drone with omnidirectional sensing.",
                "759.00", "859.00", 10, 3, "E1-DRN", "DJI Mini 4 Pro.jpg", "Mini Series",
                4.9, 143, true, true, "620.00",
                "{\"Camera\":\"4K/60fps\",\"Weight\":\"249g\",\"Obstacle\":\"Omni\",\"Flight\":\"34 min\"}",
                "drone,dji,camera"));
        products.add(p(categories, "DJI Mavic 4 Pro", "DJI", "MMT-DRN-MAVIC4", "drones-cameras",
                "Pro aerial platform for creators and survey work.",
                "1899.00", "2099.00", 4, 2, "E1-DRN", "DJI Mavic 4 Pro.jpg", "Mavic Series",
                4.8, 52, true, false, "1550.00",
                "{\"Camera\":\"Hasselblad\",\"Res\":\"5.1K\",\"Range\":\"Long\",\"Battery\":\"Intelligent\"}",
                "drone,pro,dji"));
        products.add(p(categories, "GoPro HERO13 Black", "GoPro", "MMT-CAM-HERO13", "drones-cameras",
                "Rugged action camera for sport and adventure.",
                "399.00", "449.00", 16, 5, "E2-CAM", "GoPro HERO13 Black.jpg", "HERO13",
                4.7, 201, false, true, "300.00",
                "{\"Video\":\"5.3K\",\"Stabilization\":\"HyperSmooth\",\"Waterproof\":\"Yes\",\"Mods\":\"Compatible\"}",
                "gopro,action,camera"));
        products.add(p(categories, "DJI Osmo Action 5 Pro", "DJI", "MMT-CAM-OSMO5", "drones-cameras",
                "High-dynamic-range action cam with dual screens.",
                "349.00", "379.00", 14, 4, "E2-CAM", "DJI Osmo Action 5 Pro.png", "Osmo Action",
                4.6, 77, false, false, "260.00",
                "{\"Video\":\"4K/120fps\",\"Screens\":\"Dual\",\"Color\":\"10-bit\",\"Battery\":\"Long\"}",
                "action,dji,camera"));
        products.add(p(categories, "Shure SM7B", "Shure", "MMT-AUD-SM7B", "audio-streaming",
                "Broadcast microphone for podcasts and vocals.",
                "399.00", "429.00", 12, 4, "F1-AUD", "Shure SM7B.jpg", "Dynamic",
                4.9, 320, true, false, "300.00",
                "{\"Type\":\"Dynamic\",\"Pattern\":\"Cardioid\",\"Use\":\"Broadcast\",\"XLR\":\"Yes\"}",
                "mic,podcast,shure"));
        products.add(p(categories, "Elgato 4K X", "Elgato", "MMT-STR-4KX", "audio-streaming",
                "USB capture card for 4K60 streaming and recording.",
                "199.00", "229.00", 20, 6, "F2-STR", "Elgato 4K X.jpg", "Capture",
                4.8, 165, true, true, "145.00",
                "{\"Capture\":\"4K60\",\"HDR\":\"Yes\",\"Interface\":\"USB-C\",\"Latency\":\"Ultra-low\"}",
                "capture,streaming,elgato"));
        products.add(p(categories, "HyperX Cloud III", "HyperX", "MMT-AUD-CLOUD3", "audio-streaming",
                "Comfort gaming headset with clear mic.",
                "99.00", "119.00", 30, 8, "F1-AUD", "hyperx-cloud-iii.webp", "Gaming Audio",
                4.6, 240, false, false, "65.00",
                "{\"Type\":\"Wired\",\"Driver\":\"53mm\",\"Mic\":\"Detachable\",\"Compat\":\"PC/Console\"}",
                "headset,hyperx,gaming"));
        products.add(p(categories, "Xbox Series X", "Microsoft", "MMT-CON-XSX", "gaming-consoles",
                "Flagship 4K console for next-gen gaming.",
                "499.00", "549.00", 9, 3, "G1-CON",
                "Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif",
                "Series X", 4.8, 410, true, false, "420.00",
                "{\"Res\":\"4K\",\"Storage\":\"1TB SSD\",\"RayTracing\":\"Yes\",\"FPS\":\"Up to 120\"}",
                "console,xbox,gaming"));
        products.add(p(categories, "DualSense Wireless Controller", "Sony", "MMT-CON-DSENSE", "gaming-consoles",
                "PS5 DualSense with haptic feedback.",
                "69.00", "79.00", 35, 10, "G2-ACC",
                "dualsense-controller-product-thumbnail-01-en-14sep21.webp", "DualSense",
                4.7, 500, false, false, "45.00",
                "{\"Platform\":\"PS5\",\"Haptics\":\"Yes\",\"Battery\":\"Rechargeable\",\"Color\":\"White\"}",
                "controller,ps5,sony"));
        products.add(p(categories, "Samsung 990 PRO 2TB", "Samsung", "MMT-SSD-990P-2T", "storage-nas",
                "Flagship PCIe 4.0 NVMe SSD.",
                "189.00", "229.00", 22, 6, "H1-SSD", "samsung-990-pro-2tb.jpg", "PCIe 4.0",
                4.9, 290, true, false, "140.00",
                "{\"Capacity\":\"2TB\",\"Interface\":\"NVMe\",\"SeqRead\":\"7450MB/s\",\"DRAM\":\"Yes\"}",
                "ssd,samsung,nvme"));
        products.add(p(categories, "Synology DiskStation DS923+", "Synology", "MMT-NAS-DS923", "storage-nas",
                "4-bay NAS for home lab and small business.",
                "599.00", "649.00", 8, 3, "H2-NAS", "Synology DiskStation DS923+.jpg", "DSM",
                4.8, 91, true, false, "470.00",
                "{\"Bays\":\"4\",\"RAM\":\"4GB\",\"LAN\":\"2.5GbE\",\"Expandable\":\"Yes\"}",
                "nas,synology,storage"));
        products.add(p(categories, "Anker Prime 100W GaN Charger", "Anker", "MMT-ACC-ANKER100", "accessories",
                "Compact multi-port GaN charger for laptops and phones.",
                "79.00", "99.00", 40, 10, "I1-PWR", "Anker Prime 100W GaN Charger.webp", "GaN",
                4.7, 180, false, true, "48.00",
                "{\"Power\":\"100W\",\"Ports\":\"Multi\",\"Tech\":\"GaN\",\"Travel\":\"Yes\"}",
                "charger,anker,usb-c"));
        products.add(p(categories, "APC Back-UPS Pro BR1500MS2", "APC", "MMT-ACC-UPS1500", "accessories",
                "Battery backup UPS for PCs and networking gear.",
                "249.00", "279.00", 10, 3, "I2-UPS", "APC Back-UPS Pro BR1500MS2.jpg", "UPS",
                4.6, 64, false, false, "185.00",
                "{\"VA\":\"1500\",\"LCD\":\"Yes\",\"Outlets\":\"10\",\"AVR\":\"Yes\"}",
                "ups,apc,power"));
        products.add(p(categories, "Steam Gift Card $50", "Steam", "MMT-GC-STEAM50", "gift-cards",
                "Digital Steam wallet top-up card.",
                "50.00", null, 100, 20, "J1-GC", "steam-gift-card-50usd.jpg", "Digital",
                5.0, 80, false, true, "47.00",
                "{\"Value\":\"$50\",\"Platform\":\"Steam\",\"Type\":\"Gift Card\",\"Region\":\"Global\"}",
                "gift,steam,digital"));
        products.add(p(categories, "Xbox Gift Card $100", "Microsoft", "MMT-GC-XBOX100", "gift-cards",
                "Xbox / Microsoft Store digital credit.",
                "100.00", null, 80, 15, "J1-GC", "xbox-gift-card-100.jpg", "Digital",
                5.0, 55, false, false, "95.00",
                "{\"Value\":\"$100\",\"Platform\":\"Xbox\",\"Type\":\"Gift Card\",\"Region\":\"Global\"}",
                "gift,xbox,digital"));
        products.add(p(categories, "AirPods Pro 3", "Apple", "MMT-ACC-APP-PRO3", "accessories",
                "Active noise cancelling earbuds with USB-C case.",
                "249.00", "279.00", 18, 5, "I3-AUD", "airpod pro 3.webp", "H2 Audio",
                4.8, 220, true, false, "180.00",
                "{\"ANC\":\"Yes\",\"Chip\":\"H2\",\"Case\":\"USB-C\",\"Water\":\"IP54\"}",
                "earbuds,apple,audio"));
        productRepository.saveAll(products);
    }

    private Product p(
            Map<String, Category> categories,
            String name, String brand, String sku, String categorySlug, String description,
            String price, String originalPrice, int stock, int warnMin, String bin,
            String imageFile, String architecture, double rating, int reviews,
            boolean featured, boolean flashDrop, String cost, String specsJson, String tags
    ) {
        return Product.builder()
                .name(name)
                .brand(brand)
                .sku(sku)
                .category(categories.get(categorySlug))
                .description(description)
                .price(new BigDecimal(price))
                .originalPrice(originalPrice == null ? null : new BigDecimal(originalPrice))
                .stock(stock)
                .warnMin(warnMin)
                .binLocation(bin)
                .imageUrl(img(imageFile))
                .architecture(architecture)
                .rating(rating)
                .reviewCount(reviews)
                .featured(featured)
                .flashDrop(flashDrop)
                .costPrice(new BigDecimal(cost))
                .specsJson(specsJson)
                .tags(tags)
                .build();
    }

    private void seedUsers() {
        userRepository.saveAll(List.of(
                User.builder()
                        .fullName("MMT Admin")
                        .username("mmt_admin")
                        .email("admin@mmt.store")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .avatarUrl(avatar("mm.jpg"))
                        .build(),
                User.builder()
                        .fullName("MMT Customer")
                        .username("mmt_customer")
                        .email("customer@mmt.store")
                        .password(passwordEncoder.encode("customer123"))
                        .role(Role.CUSTOMER)
                        .avatarUrl(avatar("intro-1641239073.jpg"))
                        .build()
        ));
    }

    private void seedAuditLogs() {
        auditLogRepository.saveAll(List.of(
                AuditLog.builder()
                        .message("Modern Multiple Technology (MMT) catalog seeded from local uploads")
                        .level(AuditLevel.INFO)
                        .adminUsername("admin")
                        .build(),
                AuditLog.builder()
                        .message("Critical stock watch: MSI GeForce RTX 5070 Gaming X at 2 units")
                        .level(AuditLevel.WARN)
                        .adminUsername("admin")
                        .build(),
                AuditLog.builder()
                        .message("Flash promotions enabled for drones, Wi-Fi 7, and OLED displays")
                        .level(AuditLevel.INFO)
                        .adminUsername("admin")
                        .build()
        ));
    }
}
