package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seller-dashboard")
@RequiredArgsConstructor
public class SellerDashboardController {

    private final SellerService sellerService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final CommissionRepository commissionRepository;
    private final ReviewRepository reviewRepository;
    private final DashboardService dashboardService;
    private final AdminProfileService adminProfileService;
    private final FileStorageService fileStorageService;
    private final UserService userService;

    /**
     * Resolve logged-in user from security context.
     * Do not use @ModelAttribute("currentUser") on POST — Spring binds an empty User from the form.
     */
    private User loggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            return userService.findByEmail(auth.getName());
        } catch (Exception e) {
            return null;
        }
    }

    private Seller requireSeller() {
        return requireSeller(loggedInUser());
    }

    private Seller requireSeller(User user) {
        if (user == null) {
            throw new RuntimeException("Please log in again");
        }
        Seller seller = sellerService.findByUserId(user.getId());
        if (seller == null || seller.getStatus() != SellerStatus.APPROVED) {
            throw new RuntimeException("Seller account required");
        }
        return seller;
    }

    @GetMapping({"", "/"})
    public String dashboard(@ModelAttribute("currentUser") User user, Model model,
                            RedirectAttributes ra) {
        Seller seller;
        try {
            seller = requireSeller(user);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage() + ". Your seller store may be pending or banned.");
            return "redirect:/account";
        }
        if (seller.getUser() != null) {
            seller.getUser().getEmail();
        }
        model.addAttribute("seller", seller);
        model.addAllAttributes(dashboardService.sellerDashboard(seller));
        // Personal store insights for this seller only
        Map<String, Object> me = adminProfileService.sellerProfile(seller.getId());
        model.addAttribute("wishlistAdds", me.get("wishlistAdds"));
        model.addAttribute("uniqueBuyers", me.get("uniqueBuyers"));
        model.addAttribute("activeProducts", me.get("activeProducts"));
        model.addAttribute("soldToday", me.get("soldToday"));
        model.addAttribute("revenueToday", me.get("revenueToday"));
        model.addAttribute("soldMonth", me.get("soldMonth"));
        model.addAttribute("revenueMonth", me.get("revenueMonth"));
        model.addAttribute("soldYear", me.get("soldYear"));
        model.addAttribute("revenueYear", me.get("revenueYear"));
        model.addAttribute("usuallyCategories", me.get("topCategories"));
        model.addAttribute("usuallyProducts", me.get("topProducts"));
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("pageTitle", "My Store Dashboard — MME");
        return "seller/dashboard";
    }

    @GetMapping("/products")
    public String products(@ModelAttribute("currentUser") User user,
                           @RequestParam(required = false) String q,
                           @RequestParam(required = false) Long category,
                           @RequestParam(required = false) Long brand,
                           @RequestParam(defaultValue = "newest") String sort,
                           Model model) {
        Seller seller = requireSeller(user);
        model.addAttribute("seller", seller);
        model.addAttribute("products", productService.sellerSearch(seller.getId(), q, category, brand, sort));
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderBySortOrderAsc());
        model.addAttribute("brands", brandRepository.findByActiveTrueOrderByNameAsc());
        model.addAttribute("q", q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "products");
        model.addAttribute("pageTitle", "My Products — Seller");
        return "seller/products";
    }

    @GetMapping("/categories")
    public String categories(@ModelAttribute("currentUser") User user,
                             @RequestParam(required = false) String q,
                             @RequestParam(defaultValue = "sort") String sort,
                             Model model) {
        Seller seller = requireSeller(user);
        List<Category> categories = categoryRepository.findByActiveTrueOrderBySortOrderAsc();
        if (q != null && !q.isBlank()) {
            String term = q.trim().toLowerCase();
            categories = categories.stream()
                    .filter(c -> c.getName().toLowerCase().contains(term) || c.getSlug().toLowerCase().contains(term))
                    .toList();
        }
        categories = switch (sort == null ? "sort" : sort) {
            case "name_asc", "name-asc" -> categories.stream()
                    .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).toList();
            case "name_desc", "name-desc" -> categories.stream()
                    .sorted((a, b) -> b.getName().compareToIgnoreCase(a.getName())).toList();
            case "products" -> categories.stream()
                    .sorted((a, b) -> Long.compare(
                            productService.countBySellerAndCategory(seller.getId(), b.getId()),
                            productService.countBySellerAndCategory(seller.getId(), a.getId()))).toList();
            default -> categories;
        };
        Map<Long, Long> counts = new HashMap<>();
        for (Category c : categories) {
            counts.put(c.getId(), productService.countBySellerAndCategory(seller.getId(), c.getId()));
        }
        model.addAttribute("seller", seller);
        model.addAttribute("categories", categories);
        model.addAttribute("counts", counts);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "categories");
        model.addAttribute("pageTitle", "Categories — Seller");
        return "seller/categories";
    }

    @GetMapping("/brands")
    public String brands(@ModelAttribute("currentUser") User user,
                         @RequestParam(required = false) String q,
                         @RequestParam(defaultValue = "name_asc") String sort,
                         Model model) {
        Seller seller = requireSeller(user);
        List<Brand> brands = brandRepository.findByActiveTrueOrderByNameAsc();
        if (q != null && !q.isBlank()) {
            String term = q.trim().toLowerCase();
            brands = brands.stream()
                    .filter(b -> b.getName().toLowerCase().contains(term) || b.getSlug().toLowerCase().contains(term))
                    .toList();
        }
        brands = switch (sort == null ? "name_asc" : sort) {
            case "name_desc", "name-desc" -> brands.stream()
                    .sorted((a, b) -> b.getName().compareToIgnoreCase(a.getName())).toList();
            case "products" -> brands.stream()
                    .sorted((a, b) -> Long.compare(
                            productService.countBySellerAndBrand(seller.getId(), b.getId()),
                            productService.countBySellerAndBrand(seller.getId(), a.getId()))).toList();
            default -> brands.stream()
                    .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).toList();
        };
        Map<Long, Long> counts = new HashMap<>();
        for (Brand b : brands) {
            counts.put(b.getId(), productService.countBySellerAndBrand(seller.getId(), b.getId()));
        }
        model.addAttribute("seller", seller);
        model.addAttribute("brands", brands);
        model.addAttribute("counts", counts);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "brands");
        model.addAttribute("pageTitle", "Brands — Seller");
        return "seller/brands";
    }

    @GetMapping("/products/add")
    public String addProductForm(@ModelAttribute("currentUser") User user, Model model) {
        Seller seller = requireSeller(user);
        model.addAttribute("seller", seller);
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderBySortOrderAsc());
        model.addAttribute("brands", brandRepository.findByActiveTrueOrderByNameAsc());
        model.addAttribute("activeNav", "add-product");
        model.addAttribute("pageTitle", "Add Product — Seller");
        return "seller/add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam Long categoryId,
                             @RequestParam Long brandId,
                             @RequestParam String description,
                             @RequestParam(required = false) String specifications,
                             @RequestParam BigDecimal price,
                             @RequestParam(required = false) BigDecimal originalPrice,
                             @RequestParam Integer stock,
                             @RequestParam(required = false) String sku,
                             @RequestParam(required = false) String warranty,
                             @RequestParam(required = false) MultipartFile imageFile,
                             RedirectAttributes ra) {
        try {
            Seller seller = requireSeller();
            if (imageFile == null || imageFile.isEmpty()) {
                throw new RuntimeException("Please choose a product image");
            }
            String imageUrl = fileStorageService.storeImage(imageFile, "products");
            Category cat = categoryRepository.findById(categoryId).orElseThrow();
            Brand brand = brandRepository.findById(brandId).orElseThrow();
            String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-" + System.currentTimeMillis() % 10000;

            int discount = 0;
            if (originalPrice != null && originalPrice.compareTo(price) > 0) {
                discount = originalPrice.subtract(price).multiply(BigDecimal.valueOf(100))
                        .divide(originalPrice, 0, java.math.RoundingMode.HALF_UP).intValue();
            }

            Product product = Product.builder()
                    .name(name).slug(slug).description(description).specifications(specifications)
                    .category(cat).brand(brand).seller(seller)
                    .price(price).originalPrice(originalPrice != null ? originalPrice : price)
                    .discountPercent(discount).stock(stock).sku(sku).warranty(warranty)
                    .conditionType(ProductCondition.NEW)
                    .imageUrl(imageUrl)
                    .galleryUrls(imageUrl)
                    .active(true).rating(0.0).reviewCount(0).soldCount(0).build();
            productService.save(product);
            seller.setTotalProducts(seller.getTotalProducts() + 1);
            ra.addFlashAttribute("message", "Product added successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/seller-dashboard/products/add";
        }
        return "redirect:/seller-dashboard/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@ModelAttribute("currentUser") User user,
                                  @PathVariable Long id,
                                  Model model) {
        Seller seller = requireSeller(user);
        Product product = productService.findById(id);
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("Not your product");
        }
        model.addAttribute("seller", seller);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderBySortOrderAsc());
        model.addAttribute("brands", brandRepository.findByActiveTrueOrderByNameAsc());
        model.addAttribute("activeNav", "products");
        model.addAttribute("pageTitle", "Edit Product — Seller");
        return "seller/edit-product";
    }

    @PostMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam Long categoryId,
                              @RequestParam Long brandId,
                              @RequestParam String description,
                              @RequestParam(required = false) String specifications,
                              @RequestParam BigDecimal price,
                              @RequestParam(required = false) BigDecimal originalPrice,
                              @RequestParam Integer stock,
                              @RequestParam(required = false) String sku,
                              @RequestParam(required = false) String warranty,
                              @RequestParam(required = false) MultipartFile imageFile,
                              @RequestParam(required = false) Boolean active,
                              RedirectAttributes ra) {
        try {
            Seller seller = requireSeller();
            Product product = productService.findById(id);
            if (!product.getSeller().getId().equals(seller.getId())) {
                throw new RuntimeException("Not your product");
            }
            String imageUrl;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = fileStorageService.storeImage(imageFile, "products");
            } else if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
                imageUrl = product.getImageUrl();
            } else {
                throw new RuntimeException("Please choose a product image");
            }
            productService.update(id, name, categoryId, brandId, description, specifications,
                    price, originalPrice, stock, sku, warranty, imageUrl, Boolean.TRUE.equals(active));
            ra.addFlashAttribute("message", "Product updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller-dashboard/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Seller seller = requireSeller();
            Product product = productService.findById(id);
            if (!product.getSeller().getId().equals(seller.getId())) {
                throw new RuntimeException("Not your product");
            }
            productService.delete(id);
            ra.addFlashAttribute("message", "Product deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller-dashboard/products";
    }

    @GetMapping("/orders")
    public String orders(@ModelAttribute("currentUser") User user,
                         @RequestParam(defaultValue = "newest") String sort,
                         Model model) {
        Seller seller = requireSeller(user);
        model.addAttribute("seller", seller);
        model.addAttribute("orders", orderService.getSellerOrders(seller.getId(), sort));
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "orders");
        model.addAttribute("pageTitle", "Orders — Seller");
        return "seller/orders";
    }

    @PostMapping("/orders/{id}/accept")
    public String acceptOrder(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Seller seller = requireSeller();
            orderService.sellerAcceptOrder(id, seller.getId());
            ra.addFlashAttribute("message", "Stock confirmed — order is now Processing. Ship when ready.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller-dashboard/orders";
    }

    @PostMapping("/orders/{id}/decline-stock")
    public String declineOutOfStock(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Seller seller = requireSeller();
            orderService.sellerDeclineOutOfStock(id, seller.getId());
            ra.addFlashAttribute("message", "Order declined (out of stock). Customer will be refunded.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller-dashboard/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    RedirectAttributes ra) {
        try {
            Seller seller = requireSeller();
            OrderStatus next = OrderStatus.valueOf(status);
            orderService.sellerUpdateFulfillment(id, seller.getId(), next);
            String label = switch (next) {
                case SHIPPED -> "Shipped — next: Out for delivery";
                case OUT_FOR_DELIVERY -> "Out for delivery — waiting for admin to confirm Delivered & pay you";
                default -> next.name().replace('_', ' ');
            };
            ra.addFlashAttribute("message", "Status updated: " + label);
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller-dashboard/orders";
    }

    @GetMapping("/reviews")
    public String reviews(@ModelAttribute("currentUser") User user, Model model) {
        Seller seller = requireSeller(user);
        model.addAttribute("seller", seller);
        model.addAttribute("reviews", reviewRepository.findByProductSellerIdOrderByCreatedAtDesc(seller.getId()));
        model.addAttribute("activeNav", "reviews");
        model.addAttribute("pageTitle", "Reviews — Seller");
        return "seller/reviews";
    }

    @GetMapping("/earnings")
    public String earnings(@ModelAttribute("currentUser") User user, Model model) {
        Seller seller = requireSeller(user);
        model.addAttribute("seller", seller);
        model.addAttribute("commissions", commissionRepository.findBySellerIdOrderByCreatedAtDesc(seller.getId()));
        model.addAttribute("totalEarnings", commissionRepository.sumSellerEarnings(seller.getId()));
        model.addAttribute("heldEarnings", commissionRepository.sumSellerHeld(seller.getId()));
        model.addAttribute("feesPaid", commissionRepository.sumSellerFeesPaid(seller.getId()));
        model.addAttribute("feesPending", commissionRepository.sumSellerFeesPending(seller.getId()));
        model.addAttribute("commissionRate", "3%");
        model.addAttribute("activeNav", "earnings");
        model.addAttribute("pageTitle", "Earnings — Seller");
        return "seller/earnings";
    }

    @GetMapping("/settings")
    public String settings(@ModelAttribute("currentUser") User user, Model model) {
        Seller seller = requireSeller(user);
        model.addAttribute("seller", seller);
        model.addAttribute("activeNav", "settings");
        model.addAttribute("pageTitle", "Store Settings — Seller");
        return "seller/settings";
    }

    @PostMapping("/settings")
    public String updateSettings(@RequestParam String storeName,
                                 @RequestParam String ownerName,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam(required = false) String location,
                                 @RequestParam(required = false) String address,
                                 @RequestParam(required = false) String country,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(required = false) String logoUrl,
                                 @RequestParam(required = false) MultipartFile logoFile,
                                 @RequestParam(required = false) String bannerUrl,
                                 @RequestParam(required = false) MultipartFile bannerFile,
                                 RedirectAttributes ra) {
        try {
            Seller seller = requireSeller();
            String resolvedLogo = logoUrl;
            if (logoFile != null && !logoFile.isEmpty()) {
                resolvedLogo = fileStorageService.storeImage(logoFile, "sellers");
            }
            String resolvedBanner = bannerUrl;
            if (bannerFile != null && !bannerFile.isEmpty()) {
                resolvedBanner = fileStorageService.storeImage(bannerFile, "avatars/cover photos");
            }
            sellerService.updateStoreProfile(seller.getId(), storeName, ownerName, phone, location, address,
                    country, description, resolvedLogo, resolvedBanner);
            ra.addFlashAttribute("message", "Store profile updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Could not update store");
        }
        return "redirect:/seller-dashboard/settings";
    }

    @PostMapping("/settings/personal")
    public String updatePersonalProfile(@RequestParam String fullName,
                                        @RequestParam(required = false) String phone,
                                        @RequestParam(required = false) String avatarUrl,
                                        @RequestParam(required = false) MultipartFile avatarFile,
                                        @RequestParam(required = false) String coverUrl,
                                        @RequestParam(required = false) MultipartFile coverFile,
                                        RedirectAttributes ra) {
        try {
            User user = loggedInUser();
            requireSeller(user);
            String resolvedAvatar = avatarUrl;
            if (avatarFile != null && !avatarFile.isEmpty()) {
                resolvedAvatar = fileStorageService.storeImage(avatarFile, "avatars");
            }
            String resolvedCover = coverUrl;
            if (coverFile != null && !coverFile.isEmpty()) {
                resolvedCover = fileStorageService.storeImage(coverFile, "avatars/cover photos");
            }
            userService.updateProfile(user.getId(), fullName, phone, resolvedAvatar, resolvedCover);
            ra.addFlashAttribute("message", "Personal profile updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Could not update profile");
        }
        return "redirect:/seller-dashboard/settings";
    }
}
