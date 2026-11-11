package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.ProductVariantOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Always refresh product images from local uploads when DB already exists
        if (userRepository.count() > 0) {
            syncLocalProductImages();
            syncLocalBrandLogos();
            return;
        }

        // Admin
        User admin = userRepository.save(User.builder()
                .fullName("Platform Admin").email("admin@computerhub.com")
                .phone("012000000").password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN).enabled(true).build());

        // Customers (6 avatars under uploads/avatars/customers — mm.jpg for Sokha)
        User customer = userRepository.save(User.builder()
                .fullName("Sokha Meas").email("customer@computerhub.com")
                .phone("012345678").password(passwordEncoder.encode("customer123"))
                .role(Role.CUSTOMER).enabled(true)
                .avatarUrl("/uploads/avatars/customers/mm.jpg")
                .coverUrl("/uploads/avatars/cover photos/photo_2026-10-09_09-47-07.jpg")
                .build());
        userRepository.save(User.builder()
                .fullName("Rithy Sok").email("rithy@computerhub.com")
                .phone("012111222").password(passwordEncoder.encode("customer123"))
                .role(Role.CUSTOMER).enabled(true)
                .avatarUrl("/uploads/avatars/customers/pckdv.jpg")
                .coverUrl("/uploads/avatars/cover photos/photo_2026-10-09_09-47-15.jpg")
                .build());
        userRepository.save(User.builder()
                .fullName("Chenda Phan").email("chenda@computerhub.com")
                .phone("012222333").password(passwordEncoder.encode("customer123"))
                .role(Role.CUSTOMER).enabled(true)
                .avatarUrl("/uploads/avatars/customers/intro-1641239073.jpg")
                .coverUrl("/uploads/avatars/cover photos/photo_2026-10-09_09-47-19.jpg")
                .build());
        userRepository.save(User.builder()
                .fullName("Piseth Hor").email("piseth@computerhub.com")
                .phone("012333444").password(passwordEncoder.encode("customer123"))
                .role(Role.CUSTOMER).enabled(true)
                .avatarUrl("/uploads/avatars/customers/wolf-wall-street.jpg")
                .coverUrl("/uploads/avatars/cover photos/photo_2026-10-09_09-48-27.jpg")
                .build());
        userRepository.save(User.builder()
                .fullName("Sothea Lim").email("sothea@computerhub.com")
                .phone("012444555").password(passwordEncoder.encode("customer123"))
                .role(Role.CUSTOMER).enabled(true)
                .avatarUrl("/uploads/avatars/customers/lord-of-war.jpg")
                .coverUrl("/uploads/avatars/cover photos/playstation cover.jpg")
                .build());
        userRepository.save(User.builder()
                .fullName("Bopha Keo").email("bopha@computerhub.com")
                .phone("012555666").password(passwordEncoder.encode("customer123"))
                .role(Role.CUSTOMER).enabled(true)
                .avatarUrl("/uploads/avatars/customers/movie-portrait.jpg")
                .coverUrl("/uploads/avatars/cover photos/photo_2026-10-09_09-48-23.jpg")
                .build());

        // Sellers
        User sellerUser1 = userRepository.save(User.builder()
                .fullName("Vannak Chen").email("techzone@computerhub.com")
                .phone("098765432").password(passwordEncoder.encode("seller123"))
                .role(Role.SELLER).enabled(true).build());

        User sellerUser2 = userRepository.save(User.builder()
                .fullName("Dara Kim").email("pcmaster@computerhub.com")
                .phone("097111222").password(passwordEncoder.encode("seller123"))
                .role(Role.SELLER).enabled(true).build());

        User sellerUser3 = userRepository.save(User.builder()
                .fullName("Sreyneat Ly").email("digitech@computerhub.com")
                .phone("096333444").password(passwordEncoder.encode("seller123"))
                .role(Role.SELLER).enabled(true).build());

        Seller techZone = sellerRepository.save(Seller.builder()
                .user(sellerUser1).storeName("TechZone Cambodia").ownerName("Vannak Chen")
                .description("Premium PC components and gaming gear. Authorized reseller of ASUS, MSI, and NVIDIA products.")
                .phone("098765432").address("Street 271, Boeng Keng Kang").location("Phnom Penh")
                .country("Cambodia")
                .businessInfo("Registered computer retail business")
                .logoUrl("https://ui-avatars.com/api/?name=TechZone&background=0d7377&color=fff&size=128")
                .bannerUrl("https://images.unsplash.com/photo-1587831990711-23ca6441447b?w=1200&h=280&fit=crop")
                .status(SellerStatus.APPROVED).level(SellerLevel.TOP)
                .rating(4.9).followers(0).totalProducts(0).totalSales(0).responseRate(98.5)
                .joinedAt(LocalDateTime.now().minusYears(2)).build());

        Seller pcMaster = sellerRepository.save(Seller.builder()
                .user(sellerUser2).storeName("PC Master").ownerName("Dara Kim")
                .description("Custom gaming PCs, laptops, and peripherals. Build your dream rig with us.")
                .phone("097111222").address("Russian Blvd, Toul Kork").location("Phnom Penh")
                .country("Cambodia")
                .businessInfo("PC assembly and retail")
                .logoUrl("https://ui-avatars.com/api/?name=PC+Master&background=e85d04&color=fff&size=128")
                .bannerUrl("https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=1200&h=280&fit=crop")
                .status(SellerStatus.APPROVED).level(SellerLevel.TRUSTED)
                .rating(4.7).followers(0).totalProducts(0).totalSales(0).responseRate(96.0)
                .joinedAt(LocalDateTime.now().minusMonths(14)).build());

        Seller digiTech = sellerRepository.save(Seller.builder()
                .user(sellerUser3).storeName("DigiTech Store").ownerName("Sreyneat Ly")
                .description("Laptops, monitors, and office computing solutions at competitive prices.")
                .phone("096333444").address("Siem Reap City Center").location("Siem Reap")
                .country("Cambodia")
                .businessInfo("Electronics retail")
                .logoUrl("https://ui-avatars.com/api/?name=DigiTech&background=023e8a&color=fff&size=128")
                .bannerUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=1200&h=280&fit=crop")
                .status(SellerStatus.APPROVED).level(SellerLevel.VERIFIED)
                .rating(4.5).followers(0).totalProducts(0).totalSales(0).responseRate(94.0)
                .joinedAt(LocalDateTime.now().minusMonths(8)).build());

        // Categories
        String[][] cats = {
                {"Computers", "computers", "💻"}, {"Gaming PC", "gaming-pc", "🎮"},
                {"Laptop", "laptop", "💻"}, {"CPU", "cpu", "🔧"}, {"GPU", "gpu", "🖥️"},
                {"Motherboard", "motherboard", "⬛"}, {"RAM", "ram", "📀"},
                {"SSD", "ssd", "💾"}, {"HDD", "hdd", "🗄️"}, {"Power Supply", "power-supply", "⚡"},
                {"PC Case", "pc-case", "📦"}, {"Monitor", "monitor", "🖥️"},
                {"Keyboard", "keyboard", "⌨️"}, {"Mouse", "mouse", "🖱️"},
                {"Headset", "headset", "🎧"}, {"Cooling", "cooling", "❄️"},
                {"Accessories", "accessories", "🔌"}, {"Networking", "networking", "📡"}
        };
        Map<String, Category> catMap = new LinkedHashMap<>();
        int order = 1;
        for (String[] c : cats) {
            Category cat = categoryRepository.save(Category.builder()
                    .name(c[0]).slug(c[1]).icon(c[2]).sortOrder(order++)
                    .imageUrl("https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=400&h=300&fit=crop")
                    .active(true).build());
            catMap.put(c[1], cat);
        }

        // Brands (logos from uploads/logo)
        String[] brands = {"ASUS", "MSI", "Gigabyte", "Lenovo", "Dell", "HP", "Acer",
                "Intel", "AMD", "NVIDIA", "Samsung", "Kingston", "Corsair", "Logitech", "Razer"};
        Map<String, Brand> brandMap = new LinkedHashMap<>();
        for (String b : brands) {
            brandMap.put(b, brandRepository.save(Brand.builder()
                    .name(b).slug(b.toLowerCase())
                    .logoUrl(brandLogoPath(b))
                    .active(true).build()));
        }

        // Products
        List<ProductDef> defs = List.of(
                new ProductDef("ASUS ROG Strix Gaming PC RTX 5070", "gaming-pc", "ASUS", techZone,
                        "1499.00", "1699.00", 12, 45, "Complete gaming PC with Intel Core Ultra 7, RTX 5070 12GB, 32GB DDR5, 1TB NVMe SSD.",
                        "CPU: Intel Core Ultra 7\nGPU: NVIDIA RTX 5070 12GB\nRAM: 32GB DDR5\nStorage: 1TB NVMe SSD\nMotherboard: ASUS ROG B850\nPSU: 850W 80+ Gold\nCase: ASUS ROG Strix\nWarranty: 2 Years",
                        "/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif",
                        true, true, 4.9, 235, 1520),
                new ProductDef("MSI GeForce RTX 5070 Gaming X 12GB", "gpu", "MSI", techZone,
                        "699.00", "799.00", 12, 80, "High-performance MSI RTX 5070 graphics card with dual-fan cooling and RGB.",
                        "GPU: NVIDIA GeForce RTX 5070\nVRAM: 12GB GDDR7\nBoost Clock: 2.6 GHz\nInterface: PCIe 5.0\nOutputs: 3x DP, 1x HDMI\nWarranty: 3 Years",
                        "/uploads/products/msi-geforce-rtx-5070-gaming-x-12gb.jpg",
                        true, true, 4.8, 412, 2800),
                new ProductDef("AMD Ryzen 7 9800X3D", "cpu", "AMD", techZone,
                        "479.00", "529.00", 9, 120, "Flagship AMD gaming CPU with 3D V-Cache technology for unmatched gaming performance.",
                        "Cores: 8\nThreads: 16\nBase Clock: 4.7 GHz\nBoost: 5.2 GHz\nCache: 96MB\nSocket: AM5\nTDP: 120W\nWarranty: 3 Years",
                        "/uploads/products/amd-ryzen-7-9800x3d.png",
                        true, true, 4.9, 580, 4200),
                new ProductDef("Intel Core Ultra 7 265K", "cpu", "Intel", pcMaster,
                        "399.00", "449.00", 11, 90, "Latest Intel Core Ultra 7 processor for content creation and gaming.",
                        "Cores: 20 (8P+12E)\nThreads: 20\nBoost: 5.5 GHz\nCache: 30MB\nSocket: LGA 1851\nTDP: 125W\nWarranty: 3 Years",
                        "/uploads/products/intel-core-ultra-7-265k.jpg",
                        false, true, 4.7, 198, 980),
                new ProductDef("Corsair Vengeance DDR5 32GB (2x16GB) 6000MHz", "ram", "Corsair", pcMaster,
                        "119.00", "149.00", 20, 200, "High-speed DDR5 memory kit optimized for AMD and Intel platforms.",
                        "Capacity: 32GB (2x16GB)\nSpeed: 6000MHz\nType: DDR5\nLatency: CL30\nVoltage: 1.35V\nWarranty: Lifetime",
                        "/uploads/products/corsair-vengeance-ddr5-32gb.jpg",
                        true, true, 4.8, 890, 6500),
                new ProductDef("Samsung 990 Pro 2TB NVMe SSD", "ssd", "Samsung", techZone,
                        "169.00", "199.00", 15, 150, "Blazing-fast PCIe 4.0 NVMe SSD for gaming and professional workloads.",
                        "Capacity: 2TB\nInterface: PCIe 4.0 x4 NVMe\nRead: 7450 MB/s\nWrite: 6900 MB/s\nForm Factor: M.2 2280\nWarranty: 5 Years",
                        "/uploads/products/samsung-990-pro-2tb.jpg",
                        true, false, 4.9, 1200, 8900),
                new ProductDef("ASUS ROG Strix 27\" 1440p 180Hz Monitor", "monitor", "ASUS", digiTech,
                        "349.00", "399.00", 12, 60, "Competitive gaming monitor with Fast IPS panel, 1ms response, and G-Sync Compatible.",
                        "Size: 27 inch\nResolution: 2560x1440\nRefresh Rate: 180Hz\nPanel: Fast IPS\nResponse: 1ms\nHDR: HDR400\nWarranty: 3 Years",
                        "/uploads/products/asus-rog-strix-27-monitor.webp",
                        true, true, 4.7, 340, 2100),
                new ProductDef("Lenovo Legion Pro 7i Gaming Laptop", "laptop", "Lenovo", digiTech,
                        "2199.00", "2499.00", 12, 25, "Flagship gaming laptop with RTX 5080, Intel Core Ultra 9, and 240Hz Mini-LED display.",
                        "CPU: Intel Core Ultra 9\nGPU: NVIDIA RTX 5080 16GB\nRAM: 32GB DDR5\nStorage: 1TB SSD\nDisplay: 16\" 2560x1600 240Hz Mini-LED\nWarranty: 1 Year",
                        "/uploads/products/lenovo-legion-pro-7i.webp",
                        false, true, 4.8, 156, 420),
                new ProductDef("Logitech G Pro X Superlight 2 Mouse", "mouse", "Logitech", pcMaster,
                        "149.00", "169.00", 12, 100, "Ultra-lightweight wireless gaming mouse used by pro esports athletes.",
                        "Weight: 60g\nSensor: HERO 2\nDPI: 32000\nBattery: 95 hours\nConnectivity: Lightspeed Wireless\nWarranty: 2 Years",
                        "/uploads/products/logitech-g-pro-x-superlight-2.webp",
                        false, true, 4.9, 2100, 12000),
                new ProductDef("Razer BlackWidow V4 Pro Keyboard", "keyboard", "Razer", pcMaster,
                        "229.00", "259.00", 11, 70, "Premium mechanical gaming keyboard with Razer Orange switches and command dial.",
                        "Switches: Razer Orange Tactile\nLayout: Full-size\nConnectivity: Wired USB-C\nRGB: Per-key Chroma\nWrist Rest: Magnetic\nWarranty: 2 Years",
                        "/uploads/products/razer-blackwidow-v4-pro.jpg",
                        false, false, 4.6, 780, 3400),
                new ProductDef("ASUS TUF Gaming B850-Plus WiFi Motherboard", "motherboard", "ASUS", techZone,
                        "229.00", "259.00", 11, 55, "AMD AM5 ATX motherboard with WiFi 7, DDR5 support, and robust VRM.",
                        "Socket: AM5\nChipset: B850\nForm Factor: ATX\nMemory: 4x DDR5\nPCIe: 5.0 x16\nWiFi: WiFi 7\nWarranty: 3 Years",
                        "/uploads/products/asus-tuf-b850-plus-wifi.jpg",
                        false, true, 4.7, 210, 890),
                new ProductDef("Corsair RM850x 850W 80+ Gold PSU", "power-supply", "Corsair", pcMaster,
                        "149.00", "179.00", 16, 85, "Fully modular 850W power supply with quiet operation and 10-year warranty.",
                        "Wattage: 850W\nEfficiency: 80+ Gold\nModular: Fully Modular\nFan: 135mm\nRails: Single +12V\nWarranty: 10 Years",
                        "/uploads/products/corsair-rm850x-psu.avif",
                        false, false, 4.8, 560, 4200),
                new ProductDef("NZXT H9 Flow Mid-Tower Case", "pc-case", "Corsair", pcMaster,
                        "159.00", "179.00", 11, 40, "Premium mid-tower case with excellent airflow and dual-chamber design.",
                        "Form Factor: Mid-Tower\nMotherboard: E-ATX / ATX / mATX\nGPU Clearance: 435mm\nRadiator: Up to 360mm\nFans Included: 4x 120mm\nWarranty: 2 Years",
                        "/uploads/products/nzxt-h9-flow.webp",
                        false, true, 4.6, 320, 1500),
                new ProductDef("Kingston Fury Beast DDR5 16GB 5600MHz", "ram", "Kingston", digiTech,
                        "59.00", "75.00", 21, 180, "Reliable DDR5 single stick for upgrades and budget builds.",
                        "Capacity: 16GB\nSpeed: 5600MHz\nType: DDR5\nLatency: CL36\nWarranty: Lifetime",
                        "/uploads/products/kingston-fury-beast-ddr5-16gb.webp",
                        true, false, 4.5, 450, 5600),
                new ProductDef("HyperX Cloud III Gaming Headset", "headset", "Kingston", digiTech,
                        "99.00", "119.00", 16, 95, "Comfortable gaming headset with 53mm drivers and detachable noise-cancelling mic.",
                        "Drivers: 53mm\nConnection: 3.5mm / USB\nMic: Detachable noise-cancelling\nSurround: Spatial Audio\nWarranty: 2 Years",
                        "/uploads/products/hyperx-cloud-iii.webp",
                        false, true, 4.7, 980, 7800),
                new ProductDef("Noctua NH-D15 Chromax Black Cooler", "cooling", "Corsair", techZone,
                        "109.00", "129.00", 15, 50, "Legendary dual-tower air cooler in stealth black finish.",
                        "Type: Dual Tower Air Cooler\nFans: 2x NF-A15\nHeight: 165mm\nTDP: 220W+\nCompatibility: Intel & AMD\nWarranty: 6 Years",
                        "/uploads/products/noctua-nh-d15-chromax.jpg",
                        false, false, 4.9, 670, 3200),
                new ProductDef("Gigabyte AORUS RTX 5070 Ti 16GB", "gpu", "Gigabyte", techZone,
                        "849.00", "949.00", 10, 35, "High-end AORUS RTX 5070 Ti with WINDFORCE cooling and RGB fusion.",
                        "GPU: NVIDIA GeForce RTX 5070 Ti\nVRAM: 16GB GDDR7\nCooling: WINDFORCE 3X\nOutputs: 3x DP, 1x HDMI\nWarranty: 4 Years",
                        "/uploads/products/gigabyte-aorus-rtx-5070-ti.png",
                        true, true, 4.8, 145, 680),
                new ProductDef("Dell XPS 15 OLED Creator Laptop", "laptop", "Dell", digiTech,
                        "1899.00", "2099.00", 9, 20, "Premium creator laptop with stunning OLED display and Intel Core Ultra processors.",
                        "CPU: Intel Core Ultra 7\nGPU: NVIDIA RTX 4050\nRAM: 32GB LPDDR5\nStorage: 1TB SSD\nDisplay: 15.6\" OLED 3.5K\nWarranty: 1 Year",
                        "/uploads/products/dell-xps-15-oled.webp",
                        false, true, 4.6, 89, 310),
                new ProductDef("ASUS ROG Gaming PC Starter Build", "computers", "ASUS", pcMaster,
                        "899.00", "999.00", 10, 30, "Affordable prebuilt gaming PC ready for 1080p and 1440p gaming.",
                        "CPU: AMD Ryzen 5 7600\nGPU: RTX 4060 8GB\nRAM: 16GB DDR5\nStorage: 1TB SSD\nPSU: 650W Bronze\nWarranty: 1 Year",
                        "/uploads/products/asus-rog-gaming-pc-starter.jpg",
                        true, true, 4.5, 210, 1100),
                new ProductDef("TP-Link Archer AX55 WiFi 6 Router", "networking", "ASUS", digiTech,
                        "79.00", "99.00", 20, 110, "Fast dual-band WiFi 6 router for home and small office networks.",
                        "Standard: WiFi 6 (802.11ax)\nSpeed: AX3000\nBands: Dual-band\nPorts: 4x Gigabit LAN, 1x WAN\nWarranty: 2 Years",
                        "/uploads/products/tp-link-archer-ax55.jpg",
                        false, false, 4.4, 560, 4500)
        );

        List<Product> products = new ArrayList<>();
        for (ProductDef d : defs) {
            String slug = d.name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-$", "");
            Category cat = catMap.get(d.catSlug);
            ProductVariantOptions.Options opts = ProductVariantOptions.forCategory(cat);
            Product p = productRepository.save(Product.builder()
                    .name(d.name).slug(slug).description(d.description).specifications(d.specs)
                    .category(cat).brand(brandMap.get(d.brand))
                    .seller(d.seller)
                    .price(new BigDecimal(d.price)).originalPrice(new BigDecimal(d.original))
                    .discountPercent(d.discount).stock(d.stock).sku("SKU-" + slug.substring(0, Math.min(12, slug.length())).toUpperCase())
                    .warranty(d.specs.contains("Warranty:") ? d.specs.split("Warranty:")[1].trim().split("\n")[0] : "1 Year")
                    .conditionType(ProductCondition.NEW).imageUrl(d.image)
                    .galleryUrls(d.image + "," + d.image)
                    .colorOptions(opts.color()).storageOptions(opts.storage()).ramOptions(opts.ram())
                    .rating(d.rating).reviewCount(d.reviews).soldCount(0)
                    .flashDeal(d.flash).flashDealEndsAt(d.flash ? LocalDateTime.now().plusHours(18) : null)
                    .featured(d.featured).active(true).build());
            products.add(p);
        }

        // Update seller product counts
        for (Seller s : List.of(techZone, pcMaster, digiTech)) {
            long count = products.stream().filter(p -> p.getSeller().getId().equals(s.getId())).count();
            s.setTotalProducts((int) count);
            sellerRepository.save(s);
        }

        // Sample reviews
        reviewRepository.save(Review.builder()
                .product(products.get(0)).user(customer).rating(5)
                .comment("Excellent build quality! Runs Cyberpunk at ultra settings with ray tracing. Fast delivery from TechZone.")
                .verifiedPurchase(true).build());
        reviewRepository.save(Review.builder()
                .product(products.get(1)).user(customer).rating(5)
                .comment("RTX 5070 performs amazingly. Cool and quiet under load. Verified seller, highly recommend.")
                .verifiedPurchase(true)
                .sellerResponse("Thank you for your purchase! Enjoy your new GPU.")
                .sellerRespondedAt(LocalDateTime.now().minusDays(2)).build());
        reviewRepository.save(Review.builder()
                .product(products.get(2)).user(customer).rating(5)
                .comment("Best gaming CPU on the market. 3D V-Cache makes a huge difference in FPS.")
                .verifiedPurchase(true).build());

        // Notifications for customer
        notificationRepository.save(Notification.builder()
                .user(customer).title("Welcome to MME!")
                .message("Discover computers and components from trusted Cambodian sellers.")
                .type("SYSTEM").link("/products").build());
        notificationRepository.save(Notification.builder()
                .user(customer).title("Flash Deal Alert")
                .message("MSI RTX 5070 is 12% OFF — limited stock remaining!")
                .type("PROMO").link("/deals").build());

        notificationRepository.save(Notification.builder()
                .user(admin).title("Marketplace Ready")
                .message("MME marketplace has been seeded with sample data.")
                .type("SYSTEM").link("/admin").build());
    }

    /** Map seeded product names → local upload files (for existing DB). */
    private void syncLocalProductImages() {
        Map<String, String> images = Map.ofEntries(
                Map.entry("ASUS ROG Strix Gaming PC RTX 5070", "/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif"),
                Map.entry("MSI GeForce RTX 5070 Gaming X 12GB", "/uploads/products/msi-geforce-rtx-5070-gaming-x-12gb.jpg"),
                Map.entry("AMD Ryzen 7 9800X3D", "/uploads/products/amd-ryzen-7-9800x3d.png"),
                Map.entry("Intel Core Ultra 7 265K", "/uploads/products/intel-core-ultra-7-265k.jpg"),
                Map.entry("Corsair Vengeance DDR5 32GB (2x16GB) 6000MHz", "/uploads/products/corsair-vengeance-ddr5-32gb.jpg"),
                Map.entry("Samsung 990 Pro 2TB NVMe SSD", "/uploads/products/samsung-990-pro-2tb.jpg"),
                Map.entry("ASUS ROG Strix 27\" 1440p 180Hz Monitor", "/uploads/products/asus-rog-strix-27-monitor.webp"),
                Map.entry("Lenovo Legion Pro 7i Gaming Laptop", "/uploads/products/lenovo-legion-pro-7i.webp"),
                Map.entry("Logitech G Pro X Superlight 2 Mouse", "/uploads/products/logitech-g-pro-x-superlight-2.webp"),
                Map.entry("Razer BlackWidow V4 Pro Keyboard", "/uploads/products/razer-blackwidow-v4-pro.jpg"),
                Map.entry("ASUS TUF Gaming B850-Plus WiFi Motherboard", "/uploads/products/asus-tuf-b850-plus-wifi.jpg"),
                Map.entry("Corsair RM850x 850W 80+ Gold PSU", "/uploads/products/corsair-rm850x-psu.avif"),
                Map.entry("NZXT H9 Flow Mid-Tower Case", "/uploads/products/nzxt-h9-flow.webp"),
                Map.entry("Kingston Fury Beast DDR5 16GB 5600MHz", "/uploads/products/kingston-fury-beast-ddr5-16gb.webp"),
                Map.entry("HyperX Cloud III Gaming Headset", "/uploads/products/hyperx-cloud-iii.webp"),
                Map.entry("Noctua NH-D15 Chromax Black Cooler", "/uploads/products/noctua-nh-d15-chromax.jpg"),
                Map.entry("Gigabyte AORUS RTX 5070 Ti 16GB", "/uploads/products/gigabyte-aorus-rtx-5070-ti.png"),
                Map.entry("Dell XPS 15 OLED Creator Laptop", "/uploads/products/dell-xps-15-oled.webp"),
                Map.entry("ASUS ROG Gaming PC Starter Build", "/uploads/products/asus-rog-gaming-pc-starter.jpg"),
                Map.entry("TP-Link Archer AX55 WiFi 6 Router", "/uploads/products/tp-link-archer-ax55.jpg")
        );
        for (Product p : productRepository.findAll()) {
            String url = images.get(p.getName());
            if (url != null && !url.equals(p.getImageUrl())) {
                p.setImageUrl(url);
                p.setGalleryUrls(url);
                productRepository.save(p);
            }
        }
    }

    private void syncLocalBrandLogos() {
        for (Brand brand : brandRepository.findAll()) {
            String logo = brandLogoPath(brand.getName());
            if (logo != null && !logo.equals(brand.getLogoUrl())) {
                brand.setLogoUrl(logo);
                brandRepository.save(brand);
            }
        }
    }

    /** Brand name → /uploads/logo/{file} */
    private static String brandLogoPath(String brandName) {
        if (brandName == null) return null;
        return switch (brandName.toLowerCase()) {
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

    private record ProductDef(String name, String catSlug, String brand, Seller seller,
                              String price, String original, int discount, int stock,
                              String description, String specs, String image,
                              boolean flash, boolean featured, double rating, int reviews, int sold) {}
}
