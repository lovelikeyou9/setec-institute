package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.ProductVariantOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Keeps categories, brands, seller/customer media, and upload-backed products
 * in sync every startup (works with an already-seeded DB).
 */
@Component
@Order(200)
@RequiredArgsConstructor
public class CatalogSyncRunner implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) return; // DataSeeder creates fresh DB first
        ensureExtraCustomers();
        ensureDemoLogins();
        Map<String, Category> cats = ensureCategories();
        Map<String, Brand> brands = ensureBrands();
        syncSellerMedia();
        syncCustomerAvatars();
        syncExtraProducts(cats, brands);
        refreshSellerProductCounts();
    }

    /**
     * Create 5 extra demo customers (one per unused customer avatar).
     * Sokha Meas already uses mm.jpg.
     */
    private void ensureExtraCustomers() {
        String[][] customers = {
                {"Rithy Sok", "rithy@computerhub.com", "012111222", "/uploads/avatars/customers/pckdv.jpg",
                        "/uploads/avatars/cover photos/photo_2026-10-09_09-47-15.jpg"},
                {"Chenda Phan", "chenda@computerhub.com", "012222333", "/uploads/avatars/customers/intro-1641239073.jpg",
                        "/uploads/avatars/cover photos/photo_2026-10-09_09-47-19.jpg"},
                {"Piseth Hor", "piseth@computerhub.com", "012333444", "/uploads/avatars/customers/wolf-wall-street.jpg",
                        "/uploads/avatars/cover photos/photo_2026-10-09_09-48-27.jpg"},
                {"Sothea Lim", "sothea@computerhub.com", "012444555", "/uploads/avatars/customers/lord-of-war.jpg",
                        "/uploads/avatars/cover photos/playstation cover.jpg"},
                {"Bopha Keo", "bopha@computerhub.com", "012555666", "/uploads/avatars/customers/movie-portrait.jpg",
                        "/uploads/avatars/cover photos/photo_2026-10-09_09-48-23.jpg"}
        };
        for (String[] c : customers) {
            if (userRepository.existsByEmailIgnoreCase(c[1])) {
                userRepository.findByEmailIgnoreCase(c[1]).ifPresent(u -> {
                    boolean dirty = false;
                    if (u.getRole() != Role.CUSTOMER) {
                        u.setRole(Role.CUSTOMER);
                        dirty = true;
                    }
                    if (u.getAvatarUrl() == null || u.getAvatarUrl().isBlank()
                            || !u.getAvatarUrl().equals(c[3])) {
                        u.setAvatarUrl(c[3]);
                        dirty = true;
                    }
                    if (u.getCoverUrl() == null || u.getCoverUrl().isBlank()) {
                        u.setCoverUrl(c[4]);
                        dirty = true;
                    }
                    if (dirty) userRepository.save(u);
                });
                continue;
            }
            userRepository.save(User.builder()
                    .fullName(c[0])
                    .email(c[1])
                    .phone(c[2])
                    .password(passwordEncoder.encode("customer123"))
                    .role(Role.CUSTOMER)
                    .enabled(true)
                    .avatarUrl(c[3])
                    .coverUrl(c[4])
                    .build());
        }

        // Keep primary demo customer on mm.jpg
        userRepository.findByEmailIgnoreCase("customer@computerhub.com").ifPresent(u -> {
            boolean dirty = false;
            if (u.getAvatarUrl() == null || u.getAvatarUrl().isBlank()
                    || !u.getAvatarUrl().contains("mm.jpg")) {
                u.setAvatarUrl("/uploads/avatars/customers/mm.jpg");
                dirty = true;
            }
            if (u.getCoverUrl() == null || u.getCoverUrl().isBlank()) {
                u.setCoverUrl("/uploads/avatars/cover photos/photo_2026-10-09_09-47-07.jpg");
                dirty = true;
            }
            if (dirty) userRepository.save(u);
        });
    }

    /** Keep demo logins working even if passwords/enabled were changed in admin. */
    private void ensureDemoLogins() {
        restoreDemoUser("admin@computerhub.com", "admin123", Role.ADMIN);
        restoreDemoUser("customer@computerhub.com", "customer123", Role.CUSTOMER);
        restoreDemoUser("rithy@computerhub.com", "customer123", Role.CUSTOMER);
        restoreDemoUser("chenda@computerhub.com", "customer123", Role.CUSTOMER);
        restoreDemoUser("piseth@computerhub.com", "customer123", Role.CUSTOMER);
        restoreDemoUser("sothea@computerhub.com", "customer123", Role.CUSTOMER);
        restoreDemoUser("bopha@computerhub.com", "customer123", Role.CUSTOMER);
        restoreDemoUser("techzone@computerhub.com", "seller123", Role.SELLER);
        restoreDemoUser("pcmaster@computerhub.com", "seller123", Role.SELLER);
        restoreDemoUser("digitech@computerhub.com", "seller123", Role.SELLER);

        for (Seller seller : sellerRepository.findAll()) {
            if (seller.getUser() == null) continue;
            User u = seller.getUser();
            if (u.getRole() == Role.SELLER && seller.getStatus() == SellerStatus.SUSPENDED) {
                // leave banned stores alone
                continue;
            }
            if (u.getRole() == Role.SELLER && seller.getStatus() != SellerStatus.APPROVED
                    && (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == null)) {
                continue;
            }
            if ("techzone@computerhub.com".equalsIgnoreCase(u.getEmail())
                    || "pcmaster@computerhub.com".equalsIgnoreCase(u.getEmail())
                    || "digitech@computerhub.com".equalsIgnoreCase(u.getEmail())) {
                seller.setStatus(SellerStatus.APPROVED);
                u.setEnabled(true);
                u.setRole(Role.SELLER);
                userRepository.save(u);
                sellerRepository.save(seller);
            }
        }
    }

    private void restoreDemoUser(String email, String rawPassword, Role role) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setEnabled(true);
            user.setRole(role);
            userRepository.save(user);
        });
    }

    private Map<String, Category> ensureCategories() {
        String[][] defs = {
                {"Computers", "computers", "💻", "/uploads/products/asus-rog-gaming-pc-starter.jpg"},
                {"Gaming PC", "gaming-pc", "🎮", "/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif"},
                {"Laptop", "laptop", "💻", "/uploads/products/lenovo-legion-pro-7i.webp"},
                {"CPU", "cpu", "🔧", "/uploads/products/amd-ryzen-7-9800x3d.png"},
                {"GPU", "gpu", "🖥️", "/uploads/products/msi-geforce-rtx-5070-gaming-x-12gb.jpg"},
                {"Motherboard", "motherboard", "⬛", "/uploads/products/asus-tuf-b850-plus-wifi.jpg"},
                {"RAM", "ram", "📀", "/uploads/products/corsair-vengeance-ddr5-32gb.jpg"},
                {"SSD", "ssd", "💾", "/uploads/products/samsung-990-pro-2tb.jpg"},
                {"HDD", "hdd", "🗄️", "/uploads/products/Seagate IronWolf Pro.png"},
                {"Power Supply", "power-supply", "⚡", "/uploads/products/corsair-rm850x-psu.avif"},
                {"PC Case", "pc-case", "📦", "/uploads/products/nzxt-h9-flow.webp"},
                {"Monitor", "monitor", "🖥️", "/uploads/products/asus-rog-strix-27-monitor.webp"},
                {"Keyboard", "keyboard", "⌨️", "/uploads/products/razer-blackwidow-v4-pro.jpg"},
                {"Mouse", "mouse", "🖱️", "/uploads/products/logitech-g-pro-x-superlight-2.webp"},
                {"Headset", "headset", "🎧", "/uploads/products/hyperx-cloud-iii.webp"},
                {"Cooling", "cooling", "❄️", "/uploads/products/noctua-nh-d15-chromax.jpg"},
                {"Accessories", "accessories", "🔌", "/uploads/products/usb c.jpg"},
                {"Networking", "networking", "📡", "/uploads/products/tp-link-archer-ax55.jpg"},
                {"Wi-Fi Routers", "wifi-routers", "📶", "/uploads/products/BE9300 Tri-Band Wi-Fi 7 Router.jpg"},
                {"Network Switches", "network-switches", "🔀", "/uploads/products/Cisco Catalyst 9300 Series.webp"},
                {"NAS & Storage", "nas-storage", "🗄️", "/uploads/products/Synology DiskStation DS923+.jpg"},
                {"Gaming Consoles", "gaming-consoles", "🕹️", "/uploads/products/Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif"},
                {"Controllers", "controllers", "🎮", "/uploads/products/Xbox Series X Wireless Controller (Black).jpg"},
                {"Game Cards", "game-cards", "🃏", "/uploads/products/steam-gift-card-50usd.jpg"},
                {"Cameras", "cameras", "📷", "/uploads/products/camaera.jpg"},
                {"Action Cams", "action-cams", "🎬", "/uploads/products/GoPro HERO13 Black.jpg"},
                {"Drones", "drones", "🚁", "/uploads/products/DJI Mini 4 Pro.jpg"},
                {"Capture Cards", "capture-cards", "📼", "/uploads/products/Elgato 4K X.jpg"},
                {"Security Cameras", "security-cameras", "📹", "/uploads/products/indoor-security-cameras-023-preview-662bee34c6a12.avif"},
                {"Chargers", "chargers", "🔌", "/uploads/products/Anker Prime 100W GaN Charger.webp"},
                {"UPS", "ups", "🔋", "/uploads/products/APC Back-UPS Pro BR1500MS2.jpg"},
                {"Power Banks", "power-banks", "🔋", "/uploads/products/zyron-powaflex-10000mah-power-bank-built-in-cables-front.webp"},
                {"Cables", "cables", "🔗", "/uploads/products/usb c.jpg"},
                {"Microcontrollers", "microcontrollers", "🤖", "/uploads/products/Arduino UNO R4 WiFi.webp"},
                {"Audio", "audio", "🎙️", "/uploads/products/Shure SM7B.jpg"},
                {"Earbuds", "earbuds", "🎧", "/uploads/products/airpod pro 3.webp"},
                {"Smart TV", "smart-tv", "📺", "/uploads/products/LG NANO80 65 (164cm) 4K SMART NANOCELL .webp"},
                {"Servers", "servers", "🖥️", "/uploads/products/Dell PowerEdge R760.jpg"},
                {"Workstations", "workstations", "💼", "/uploads/products/Lenovo ThinkStation PX.jpg"},
                {"Enterprise Networking", "enterprise-networking", "🏢", "/uploads/products/HPE Aruba Networking CX 6300.png"}
        };
        Map<String, Category> map = new LinkedHashMap<>();
        int order = 1;
        for (String[] d : defs) {
            Category cat = categoryRepository.findBySlug(d[1]).orElse(null);
            if (cat == null) {
                cat = categoryRepository.save(Category.builder()
                        .name(d[0]).slug(d[1]).icon(d[2]).imageUrl(d[3])
                        .sortOrder(order).active(true).build());
            } else {
                boolean dirty = false;
                if (cat.getImageUrl() == null || cat.getImageUrl().startsWith("https://")) {
                    cat.setImageUrl(d[3]);
                    dirty = true;
                }
                if (cat.getIcon() == null || cat.getIcon().isBlank()) {
                    cat.setIcon(d[2]);
                    dirty = true;
                }
                if (dirty) categoryRepository.save(cat);
            }
            map.put(d[1], cat);
            order++;
        }
        return map;
    }

    private Map<String, Brand> ensureBrands() {
        String[] names = {
                "ASUS", "MSI", "Gigabyte", "Lenovo", "Dell", "HP", "Acer", "Intel", "AMD", "NVIDIA",
                "Samsung", "Kingston", "Corsair", "Logitech", "Razer", "TP-Link", "LG", "Apple",
                "DJI", "GoPro", "Sony", "Microsoft", "Nintendo", "Anker", "APC", "Arduino",
                "Synology", "Seagate", "WD", "Cisco", "Elgato", "Shure", "RODE", "Steam", "Generic"
        };
        Map<String, Brand> map = new LinkedHashMap<>();
        for (String name : names) {
            Brand b = brandRepository.findBySlug(slugify(name)).orElse(null);
            if (b == null) {
                b = brandRepository.findAll().stream()
                        .filter(x -> x.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
            }
            if (b == null) {
                b = brandRepository.save(Brand.builder()
                        .name(name).slug(slugify(name))
                        .logoUrl(brandLogoPath(name))
                        .active(true).build());
            } else {
                String logo = brandLogoPath(name);
                if (logo != null && (b.getLogoUrl() == null || !b.getLogoUrl().equals(logo))) {
                    b.setLogoUrl(logo);
                    brandRepository.save(b);
                }
            }
            map.put(name, b);
        }
        return map;
    }

    private void syncSellerMedia() {
        List<Seller> sellers = sellerRepository.findAll();
        if (sellers.isEmpty()) return;
        String[] logos = {
                "/uploads/avatars/sellers/photo_2026-10-09_10-04-10.jpg",
                "/uploads/avatars/sellers/photo_2026-10-09_10-04-14.jpg",
                "/uploads/avatars/sellers/photo_2026-10-09_10-04-17.jpg",
                "/uploads/avatars/sellers/TPLINK_Logo_2.png",
                "/uploads/avatars/sellers/Apple Logo.png"
        };
        String[] banners = {
                "/uploads/avatars/cover photos/photo_2026-10-09_09-47-07.jpg",
                "/uploads/avatars/cover photos/photo_2026-10-09_09-47-15.jpg",
                "/uploads/avatars/cover photos/photo_2026-10-09_09-47-19.jpg",
                "/uploads/avatars/cover photos/playstation cover.jpg",
                "/uploads/avatars/cover photos/photo_2026-10-09_09-48-23.jpg"
        };
        int i = 0;
        for (Seller s : sellers) {
            if (s.getStatus() != SellerStatus.APPROVED) continue;
            String logo = logos[i % logos.length];
            String banner = banners[i % banners.length];
            boolean dirty = false;
            if (s.getLogoUrl() == null || s.getLogoUrl().contains("ui-avatars") || s.getLogoUrl().contains("unsplash")) {
                s.setLogoUrl(logo);
                dirty = true;
            }
            if (s.getBannerUrl() == null || s.getBannerUrl().contains("unsplash")) {
                s.setBannerUrl(banner);
                dirty = true;
            }
            if (dirty) sellerRepository.save(s);
            i++;
        }
    }

    private void syncCustomerAvatars() {
        // Prefer email→avatar map so each demo customer keeps a unique photo
        Map<String, String> avatarByEmail = Map.of(
                "customer@computerhub.com", "/uploads/avatars/customers/mm.jpg",
                "rithy@computerhub.com", "/uploads/avatars/customers/pckdv.jpg",
                "chenda@computerhub.com", "/uploads/avatars/customers/intro-1641239073.jpg",
                "piseth@computerhub.com", "/uploads/avatars/customers/wolf-wall-street.jpg",
                "sothea@computerhub.com", "/uploads/avatars/customers/lord-of-war.jpg",
                "bopha@computerhub.com", "/uploads/avatars/customers/movie-portrait.jpg"
        );
        String[] covers = {
                "/uploads/avatars/cover photos/photo_2026-10-09_09-47-07.jpg",
                "/uploads/avatars/cover photos/photo_2026-10-09_09-47-15.jpg",
                "/uploads/avatars/cover photos/photo_2026-10-09_09-48-27.jpg",
                "/uploads/avatars/cover photos/playstation cover.jpg"
        };
        List<User> customers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER)
                .toList();
        int i = 0;
        for (User u : customers) {
            boolean dirty = false;
            String mapped = avatarByEmail.get(u.getEmail() != null ? u.getEmail().toLowerCase(Locale.ENGLISH) : "");
            if (mapped != null && (u.getAvatarUrl() == null || u.getAvatarUrl().isBlank() || !u.getAvatarUrl().equals(mapped))) {
                u.setAvatarUrl(mapped);
                dirty = true;
            } else if (u.getAvatarUrl() == null || u.getAvatarUrl().isBlank()) {
                String[] fallback = {
                        "/uploads/avatars/customers/mm.jpg",
                        "/uploads/avatars/customers/pckdv.jpg",
                        "/uploads/avatars/customers/intro-1641239073.jpg",
                        "/uploads/avatars/customers/wolf-wall-street.jpg",
                        "/uploads/avatars/customers/lord-of-war.jpg",
                        "/uploads/avatars/customers/movie-portrait.jpg"
                };
                u.setAvatarUrl(fallback[i % fallback.length]);
                dirty = true;
            }
            if (u.getCoverUrl() == null || u.getCoverUrl().isBlank()) {
                u.setCoverUrl(covers[i % covers.length]);
                dirty = true;
            }
            if (dirty) userRepository.save(u);
            i++;
        }
        // Also give sellers personal avatars if missing
        List<User> sellers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.SELLER)
                .toList();
        String[] sellerAvatars = {
                "/uploads/avatars/sellers/photo_2026-10-09_09-48-44.jpg",
                "/uploads/avatars/sellers/photo_2026-10-09_09-48-53.jpg",
                "/uploads/avatars/sellers/photo_2026-10-09_09-48-56.jpg"
        };
        i = 0;
        for (User u : sellers) {
            boolean dirty = false;
            if (u.getAvatarUrl() == null || u.getAvatarUrl().isBlank()) {
                u.setAvatarUrl(sellerAvatars[i % sellerAvatars.length]);
                dirty = true;
            }
            if (u.getCoverUrl() == null || u.getCoverUrl().isBlank()) {
                u.setCoverUrl(covers[i % covers.length]);
                dirty = true;
            }
            if (dirty) userRepository.save(u);
            i++;
        }
    }

    private void syncExtraProducts(Map<String, Category> cats, Map<String, Brand> brands) {
        List<Seller> sellers = sellerRepository.findAll().stream()
                .filter(s -> s.getStatus() == SellerStatus.APPROVED)
                .toList();
        if (sellers.isEmpty()) return;
        Seller s0 = sellers.get(0);
        Seller s1 = sellers.size() > 1 ? sellers.get(1) : s0;
        Seller s2 = sellers.size() > 2 ? sellers.get(2) : s0;

        List<Extra> extras = List.of(
                extra("TP-Link Archer AX55 WiFi 6 Router", "wifi-routers", "TP-Link", s0, "89.00", "109.00",
                        "/uploads/products/tp-link-archer-ax55.jpg"),
                extra("TP-Link AX3000 Dual Band Wi-Fi 6 Router", "wifi-routers", "TP-Link", s0, "79.00", "99.00",
                        "/uploads/products/AX3000 Dual Band Gigabit Wi-Fi 6 Router.jpg"),
                extra("TP-Link BE9300 Tri-Band Wi-Fi 7 Router", "wifi-routers", "TP-Link", s1, "299.00", "349.00",
                        "/uploads/products/BE9300 Tri-Band Wi-Fi 7 Router.jpg"),
                extra("TP-Link Deco BE63 BE10000 Mesh", "wifi-routers", "TP-Link", s1, "449.00", "499.00",
                        "/uploads/products/TP-Link Deco BE63 BE10000 Wireless .jpg"),
                extra("Cisco Catalyst 9300 Series Switch", "enterprise-networking", "Cisco", s2, "1899.00", "2199.00",
                        "/uploads/products/Cisco Catalyst 9300 Series.webp"),
                extra("HPE Aruba Networking CX 6300", "enterprise-networking", "Generic", s2, "1299.00", "1499.00",
                        "/uploads/products/HPE Aruba Networking CX 6300.png"),
                extra("Synology DiskStation DS923+", "nas-storage", "Synology", s0, "599.00", "649.00",
                        "/uploads/products/Synology DiskStation DS923+.jpg"),
                extra("Synology DiskStation DS1522+", "nas-storage", "Synology", s1, "849.00", "899.00",
                        "/uploads/products/Synology DiskStation DS1522+.jpg"),
                extra("Seagate IronWolf Pro HDD", "hdd", "Seagate", s0, "249.00", "279.00",
                        "/uploads/products/Seagate IronWolf Pro.png"),
                extra("WD Red Plus NAS HDD", "hdd", "WD", s1, "189.00", "219.00",
                        "/uploads/products/WD Red Plus.png"),
                extra("Xbox Series X Console", "gaming-consoles", "Microsoft", s0, "499.00", "549.00",
                        "/uploads/products/Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif"),
                extra("Xbox Series X Wireless Controller", "controllers", "Microsoft", s0, "59.00", "69.00",
                        "/uploads/products/Xbox Series X Wireless Controller (Black).jpg"),
                extra("PlayStation DualSense Controller", "controllers", "Sony", s1, "69.00", "79.00",
                        "/uploads/products/dualsense-controller-product-thumbnail-01-en-14sep21.webp"),
                extra("Steam Gift Card $50", "game-cards", "Steam", s2, "50.00", "50.00",
                        "/uploads/products/steam-gift-card-50usd.jpg"),
                extra("Xbox Gift Card $100", "game-cards", "Microsoft", s2, "100.00", "100.00",
                        "/uploads/products/xbox-gift-card-100.jpg"),
                extra("PlayStation Store Digital Card", "game-cards", "Sony", s1, "50.00", "50.00",
                        "/uploads/products/c4353_playstation_store_200_hong_kong_m6ns_euro_053122.jpg"),
                extra("Call of Duty Black Ops 7", "game-cards", "Generic", s0, "69.00", "79.00",
                        "/uploads/products/call of duty black ops 7.jpg"),
                extra("Battlefield 5 Digital", "game-cards", "Generic", s1, "29.00", "39.00",
                        "/uploads/products/battlefield 5.jpg"),
                extra("DJI Mini 4 Pro Drone", "drones", "DJI", s0, "759.00", "799.00",
                        "/uploads/products/DJI Mini 4 Pro.jpg"),
                extra("DJI Mavic 4 Pro", "drones", "DJI", s1, "1599.00", "1799.00",
                        "/uploads/products/DJI Mavic 4 Pro.jpg"),
                extra("DJI Avata 2", "drones", "DJI", s2, "999.00", "1099.00",
                        "/uploads/products/DJI Avata 2.webp"),
                extra("GoPro HERO13 Black", "action-cams", "GoPro", s0, "399.00", "449.00",
                        "/uploads/products/GoPro HERO13 Black.jpg"),
                extra("DJI Osmo Action 5 Pro", "action-cams", "DJI", s1, "349.00", "399.00",
                        "/uploads/products/DJI Osmo Action 5 Pro.png"),
                extra("Security Camera Bundle", "security-cameras", "Generic", s2, "129.00", "159.00",
                        "/uploads/products/indoor-security-cameras-023-preview-662bee34c6a12.avif"),
                extra("Compact Digital Camera", "cameras", "Generic", s0, "449.00", "499.00",
                        "/uploads/products/camaera.jpg"),
                extra("Elgato 4K X Capture Card", "capture-cards", "Elgato", s1, "229.00", "259.00",
                        "/uploads/products/Elgato 4K X.jpg"),
                extra("Elgato Cam Link 4K", "capture-cards", "Elgato", s2, "129.00", "149.00",
                        "/uploads/products/Elgato Cam Link 4K.jpg"),
                extra("AVerMedia Live Gamer ULTRA 2.1", "capture-cards", "Generic", s0, "199.00", "229.00",
                        "/uploads/products/AVerMedia Live Gamer ULTRA 2.1.jpg"),
                extra("Anker Prime 100W GaN Charger", "chargers", "Anker", s0, "79.00", "99.00",
                        "/uploads/products/Anker Prime 100W GaN Charger.webp"),
                extra("20W USB-C Fast Charger", "chargers", "Apple", s1, "19.00", "29.00",
                        "/uploads/products/20W-USB-C-iPhone-12-Head.jpg"),
                extra("APC Back-UPS Pro BR1500MS2", "ups", "APC", s0, "249.00", "289.00",
                        "/uploads/products/APC Back-UPS Pro BR1500MS2.jpg"),
                extra("CyberPower CP1500PFCLCD UPS", "ups", "Generic", s1, "219.00", "249.00",
                        "/uploads/products/CyberPower CP1500PFCLCD.jpg"),
                extra("Zyron 10000mAh Power Bank", "power-banks", "Generic", s2, "29.00", "39.00",
                        "/uploads/products/zyron-powaflex-10000mah-power-bank-built-in-cables-front.webp"),
                extra("USB-C Cable", "cables", "Generic", s0, "12.00", "18.00",
                        "/uploads/products/usb c.jpg"),
                extra("USB-B Printer Cable", "cables", "Generic", s1, "9.00", "14.00",
                        "/uploads/products/usb b.jpg"),
                extra("Belkin Surge Protector", "cables", "Generic", s2, "24.00", "34.00",
                        "/uploads/products/Belkin Surge Protector.jpg"),
                extra("Arduino UNO R4 WiFi", "microcontrollers", "Arduino", s0, "27.00", "32.00",
                        "/uploads/products/Arduino UNO R4 WiFi.webp"),
                extra("Arduino Nano ESP32", "microcontrollers", "Arduino", s1, "19.00", "24.00",
                        "/uploads/products/Arduino Nano ESP32.webp"),
                extra("Shure SM7B Microphone", "audio", "Shure", s0, "399.00", "449.00",
                        "/uploads/products/Shure SM7B.jpg"),
                extra("Shure MV7+ Microphone", "audio", "Shure", s1, "279.00", "319.00",
                        "/uploads/products/Shure MV7+.jpg"),
                extra("RODE NT1 5th Generation", "audio", "RODE", s2, "249.00", "279.00",
                        "/uploads/products/RODE NT1 5th Generation.png"),
                extra("Audio-Technica AT2020", "audio", "Generic", s0, "99.00", "119.00",
                        "/uploads/products/Audio-Technica AT2020.jpg"),
                extra("AirPods Pro 3", "earbuds", "Apple", s1, "249.00", "279.00",
                        "/uploads/products/airpod pro 3.webp"),
                extra("AirPods Max", "earbuds", "Apple", s2, "549.00", "599.00",
                        "/uploads/products/airpod max.jpg"),
                extra("LG NANO80 65\" 4K Smart TV", "smart-tv", "LG", s0, "799.00", "949.00",
                        "/uploads/products/LG NANO80 65 (164cm) 4K SMART NANOCELL .webp"),
                extra("55\" 4K Ultra HD Smart TV", "smart-tv", "Generic", s1, "449.00", "529.00",
                        "/uploads/products/LG NANO80 65 (164cm) 4K SMART NANOCELL .webp"),
                extra("Dell PowerEdge R760 Server", "servers", "Dell", s0, "4999.00", "5499.00",
                        "/uploads/products/Dell PowerEdge R760.jpg"),
                extra("Lenovo ThinkStation PX", "workstations", "Lenovo", s1, "3499.00", "3899.00",
                        "/uploads/products/Lenovo ThinkStation PX.jpg"),
                extra("MSI MAG 32CQ6PF Curved Monitor", "monitor", "MSI", s2, "329.00", "379.00",
                        "/uploads/products/MSI MAG 32CQ6PF 32 WQHD CURVED VA 180Hz.jpg"),
                extra("ROG Swift OLED PG32UCDP", "monitor", "ASUS", s0, "1299.00", "1499.00",
                        "/uploads/products/ROG Swift OLED PG32UCDP Gaming Monitor WOLED.jpg"),
                extra("MSI Optix G27C Gaming Monitor", "monitor", "MSI", s1, "249.00", "299.00",
                        "/uploads/products/Optix G27C MSI.webp")
        );

        Set<String> existing = new HashSet<>();
        for (Product p : productRepository.findAll()) {
            existing.add(p.getName().toLowerCase(Locale.ENGLISH));
        }

        for (Extra e : extras) {
            if (existing.contains(e.name().toLowerCase(Locale.ENGLISH))) continue;
            Category cat = cats.get(e.catSlug());
            Brand brand = brands.getOrDefault(e.brand(), brands.get("Generic"));
            if (cat == null || brand == null) continue;
            BigDecimal price = new BigDecimal(e.price());
            BigDecimal original = new BigDecimal(e.original());
            int discount = original.compareTo(price) > 0
                    ? original.subtract(price).multiply(BigDecimal.valueOf(100))
                    .divide(original, 0, java.math.RoundingMode.HALF_UP).intValue()
                    : 0;
            ProductVariantOptions.Options opts = ProductVariantOptions.forCategory(cat);
            productRepository.save(Product.builder()
                    .name(e.name())
                    .slug(uniqueSlug(e.name()))
                    .description(e.name() + " — available on MME from verified sellers.")
                    .specifications("Brand: " + brand.getName() + "\nCategory: " + cat.getName() + "\nWarranty: 1 Year")
                    .price(price)
                    .originalPrice(original)
                    .discountPercent(discount)
                    .stock(25 + new Random(e.name().hashCode()).nextInt(80))
                    .soldCount(0)
                    .rating(4.3 + (Math.abs(e.name().hashCode() % 7) / 10.0))
                    .reviewCount(Math.abs(e.name().hashCode() % 120))
                    .conditionType(ProductCondition.NEW)
                    .imageUrl(e.image())
                    .galleryUrls(e.image())
                    .colorOptions(opts.color())
                    .storageOptions(opts.storage())
                    .ramOptions(opts.ram())
                    .active(true)
                    .featured(Math.abs(e.name().hashCode()) % 5 == 0)
                    .flashDeal(discount >= 10 && Math.abs(e.name().hashCode()) % 4 == 0)
                    .category(cat)
                    .brand(brand)
                    .seller(e.seller())
                    .build());
            existing.add(e.name().toLowerCase(Locale.ENGLISH));
        }
    }

    private void refreshSellerProductCounts() {
        for (Seller s : sellerRepository.findAll()) {
            long count = productRepository.countBySellerIdAndActiveTrue(s.getId());
            if (s.getTotalProducts() == null || s.getTotalProducts() != count) {
                s.setTotalProducts((int) count);
                sellerRepository.save(s);
            }
        }
    }

    private String uniqueSlug(String name) {
        String base = slugify(name);
        String slug = base;
        int i = 2;
        while (productRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + i++;
        }
        return slug;
    }

    private static String slugify(String name) {
        return name.toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    private static String brandLogoPath(String brandName) {
        if (brandName == null) return null;
        return switch (brandName.toLowerCase(Locale.ENGLISH)) {
            case "asus" -> "/uploads/logo/asus.png";
            case "msi" -> "/uploads/logo/msi.png";
            case "gigabyte" -> "/uploads/logo/gigabyte.png";
            case "lenovo" -> "/uploads/logo/lenovo.png";
            case "dell" -> "/uploads/logo/dell.png";
            case "hp" -> "/uploads/logo/hp.png";
            case "acer" -> "/uploads/logo/acer.png";
            case "intel" -> "/uploads/logo/intel.png";
            case "amd" -> "/uploads/logo/amd.png";
            case "nvidia" -> "/uploads/logo/nvidia.png";
            case "samsung" -> "/uploads/logo/samsung.webp";
            case "kingston" -> "/uploads/logo/kingston.png";
            case "corsair" -> "/uploads/logo/corsair.png";
            case "logitech" -> "/uploads/logo/logitech.png";
            case "razer" -> "/uploads/logo/razer.png";
            case "tp-link" -> "/uploads/logo/TPLINK_Logo_2.png";
            case "lg" -> "/uploads/logo/LG_symbol.svg.webp";
            case "apple" -> "/uploads/avatars/sellers/Apple Logo.png";
            case "anker" -> "/uploads/logo/anker.webp";
            case "apc" -> "/uploads/logo/APC.jpg";
            case "cisco" -> "/uploads/logo/CISCO.jpg";
            case "dji" -> "/uploads/logo/dji.webp";
            case "elgato" -> "/uploads/logo/Elgato_logo.svg.webp";
            case "gopro" -> "/uploads/logo/GoPro.png";
            case "microsoft" -> "/uploads/logo/Microsoft.png";
            case "rode" -> "/uploads/logo/Rode-Logo.jpg";
            case "seagate" -> "/uploads/logo/Seagate.png";
            case "shure" -> "/uploads/logo/Shure.png";
            case "sony" -> "/uploads/logo/Sony-Logo.wine.png";
            case "steam" -> "/uploads/logo/steam.png";
            case "wd" -> "/uploads/logo/WD.jpg";
            default -> null;
        };
    }

    private static Extra extra(String name, String cat, String brand, Seller seller,
                               String price, String original, String image) {
        return new Extra(name, cat, brand, seller, price, original, image);
    }

    private record Extra(String name, String catSlug, String brand, Seller seller,
                         String price, String original, String image) {}
}
