package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final SellerService sellerService;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final CommissionRepository commissionRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final BrandRepository brandRepository;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final DashboardService dashboardService;
    private final AdminProfileService adminProfileService;
    private final FileStorageService fileStorageService;

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAllAttributes(dashboardService.adminDashboard());
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("pageTitle", "Admin Dashboard — MME");
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String q,
                        @RequestParam(required = false) String role,
                        @RequestParam(defaultValue = "newest") String sort,
                        Model model) {
        Role roleFilter = parseRole(role);
        model.addAttribute("users", userService.search(q, roleFilter, sort));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("roleFilter", roleFilter != null ? roleFilter.name() : "");
        model.addAttribute("customersOnly", false);
        model.addAttribute("roles", Role.values());
        model.addAttribute("activeNav", "users");
        model.addAttribute("pageTitle", "Users Lists — Admin");
        return "admin/users";
    }

    @GetMapping("/customers")
    public String customers(@RequestParam(required = false) String q,
                            @RequestParam(defaultValue = "newest") String sort,
                            Model model) {
        model.addAttribute("users", userService.search(q, Role.CUSTOMER, sort));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("roleFilter", Role.CUSTOMER.name());
        model.addAttribute("customersOnly", true);
        model.addAttribute("roles", Role.values());
        model.addAttribute("activeNav", "customers");
        model.addAttribute("pageTitle", "Customers Lists — Admin");
        return "admin/users";
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) return null;
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAllAttributes(adminProfileService.userProfile(id));
            model.addAttribute("activeNav", "user-detail");
            model.addAttribute("pageTitle", "User Profile — Admin");
            return "admin/user-detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users")
    public String addUser(@RequestParam String fullName,
                          @RequestParam String email,
                          @RequestParam(required = false) String phone,
                          @RequestParam String password,
                          @RequestParam String role,
                          @RequestParam(required = false) Boolean enabled,
                          RedirectAttributes ra) {
        try {
            userService.create(fullName, email, phone, password, Role.valueOf(role), enabled == null || enabled);
            ra.addFlashAttribute("message", "User created successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id,
                           @RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam(required = false) String phone,
                           @RequestParam(required = false) String password,
                           @RequestParam String role,
                           @RequestParam(required = false) Boolean enabled,
                           RedirectAttributes ra) {
        try {
            userService.update(id, fullName, email, phone, password, Role.valueOf(role), enabled != null && enabled);
            ra.addFlashAttribute("message", "User updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.delete(id);
            ra.addFlashAttribute("message", "User deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.setEnabled(id, false);
            ra.addFlashAttribute("message", "User banned successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.setEnabled(id, true);
            ra.addFlashAttribute("message", "User unbanned successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/countries")
    public String countries(@RequestParam(defaultValue = "money_desc") String sort, Model model) {
        model.addAttribute("countries", adminProfileService.countriesList(sort));
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "countries");
        model.addAttribute("pageTitle", "Countries — Admin");
        return "admin/countries";
    }

    @GetMapping("/countries/{name}")
    public String countryDetail(@PathVariable String name,
                                @RequestParam(defaultValue = "revenue_desc") String sellerSort,
                                @RequestParam(defaultValue = "spent_desc") String buyerSort,
                                Model model, RedirectAttributes ra) {
        try {
            Map<String, Object> data = adminProfileService.countryProfile(name);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> sellers = (List<Map<String, Object>>) data.get("sellers");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> buyers = (List<Map<String, Object>>) data.get("buyers");
            data.put("sellers", adminProfileService.sortSellerRows(sellers, sellerSort));
            data.put("buyers", adminProfileService.sortBuyerRows(buyers, buyerSort));
            model.addAllAttributes(data);
            model.addAttribute("sellerSort", sellerSort);
            model.addAttribute("buyerSort", buyerSort);
            model.addAttribute("activeNav", "country-detail");
            model.addAttribute("pageTitle",
                    model.getAttribute("countryName") + " — Admin");
            return "admin/country-detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/countries";
        }
    }

    @GetMapping("/sellers")
    public String sellers(@RequestParam(required = false) String q,
                          @RequestParam(defaultValue = "newest") String sort,
                          Model model) {
        model.addAttribute("sellers", sellerService.search(q, sort));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "sellers");
        model.addAttribute("pageTitle", "Sellers Lists — Admin");
        return "admin/sellers";
    }

    @GetMapping("/sellers/{id}")
    public String sellerDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAllAttributes(adminProfileService.sellerProfile(id));
            model.addAttribute("activeNav", "seller-detail");
            model.addAttribute("pageTitle", "Seller Profile — Admin");
            return "admin/seller-detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/sellers";
        }
    }

    @PostMapping("/sellers")
    public String addSeller(@RequestParam String storeName,
                            @RequestParam String ownerName,
                            @RequestParam String email,
                            @RequestParam(required = false) String phone,
                            @RequestParam String password,
                            @RequestParam(required = false) String location,
                            @RequestParam(required = false) String address,
                            @RequestParam(required = false) String country,
                            @RequestParam(required = false) String level,
                            RedirectAttributes ra) {
        try {
            sellerService.createByAdmin(storeName, ownerName, email, phone, password,
                    location, address, country, level);
            ra.addFlashAttribute("message", "Seller created successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sellers";
    }

    @PostMapping("/sellers/{id}/edit")
    public String editSeller(@PathVariable Long id,
                             @RequestParam String storeName,
                             @RequestParam String ownerName,
                             @RequestParam(required = false) String phone,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) String address,
                             @RequestParam(required = false) String country,
                             @RequestParam String status,
                             @RequestParam String level,
                             RedirectAttributes ra) {
        try {
            sellerService.updateByAdmin(id, storeName, ownerName, phone, location, address, country, status, level);
            ra.addFlashAttribute("message", "Seller updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sellers";
    }

    @PostMapping("/sellers/{id}/delete")
    public String deleteSeller(@PathVariable Long id, RedirectAttributes ra) {
        try {
            sellerService.deleteByAdmin(id);
            ra.addFlashAttribute("message", "Seller deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sellers";
    }

    @PostMapping("/sellers/{id}/ban")
    public String banSeller(@PathVariable Long id, RedirectAttributes ra) {
        try {
            sellerService.ban(id);
            ra.addFlashAttribute("message", "Seller banned successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sellers";
    }

    @PostMapping("/sellers/{id}/unban")
    public String unbanSeller(@PathVariable Long id, RedirectAttributes ra) {
        try {
            sellerService.unban(id);
            ra.addFlashAttribute("message", "Seller unbanned successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sellers";
    }

    @GetMapping("/seller-applications")
    public String applications(Model model) {
        model.addAttribute("applications", sellerService.getPendingApplications());
        model.addAttribute("activeNav", "applications");
        model.addAttribute("pageTitle", "Seller Applications — Admin");
        return "admin/applications";
    }

    @PostMapping("/sellers/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        sellerService.approve(id);
        ra.addFlashAttribute("message", "Seller approved");
        return "redirect:/admin/seller-applications";
    }

    @PostMapping("/sellers/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam String reason, RedirectAttributes ra) {
        sellerService.reject(id, reason);
        ra.addFlashAttribute("message", "Seller rejected");
        return "redirect:/admin/seller-applications";
    }

    @PostMapping("/sellers/{id}/level")
    public String updateLevel(@PathVariable Long id, @RequestParam String level) {
        sellerService.updateLevel(id, SellerLevel.valueOf(level));
        return "redirect:/admin/sellers";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String q,
                           @RequestParam(required = false) Long category,
                           @RequestParam(required = false) Long brand,
                           @RequestParam(defaultValue = "newest") String sort,
                           Model model) {
        model.addAttribute("products", productService.adminSearch(q, category, brand, sort));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("q", q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "products");
        model.addAttribute("pageTitle", "Products — Admin");
        return "admin/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("activeNav", "products");
        model.addAttribute("pageTitle", "Edit Product — Admin");
        return "admin/product-form";
    }

    @PostMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam Long categoryId,
                              @RequestParam Long brandId,
                              @RequestParam String description,
                              @RequestParam(required = false) String specifications,
                              @RequestParam java.math.BigDecimal price,
                              @RequestParam(required = false) java.math.BigDecimal originalPrice,
                              @RequestParam Integer stock,
                              @RequestParam(required = false) String sku,
                              @RequestParam(required = false) String warranty,
                              @RequestParam(required = false) MultipartFile imageFile,
                              @RequestParam(required = false) Boolean active,
                              RedirectAttributes ra) {
        try {
            Product existing = productService.findById(id);
            String imageUrl = resolveProductImage(imageFile, existing.getImageUrl(), false);
            productService.update(id, name, categoryId, brandId, description, specifications,
                    price, originalPrice, stock, sku, warranty, imageUrl, active != null && active);
            ra.addFlashAttribute("message", "Product updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/products";
    }

    /** New image required when product has no image yet; otherwise keep existing if no file chosen. */
    private String resolveProductImage(MultipartFile imageFile, String existingUrl, boolean requireNew) {
        if (imageFile != null && !imageFile.isEmpty()) {
            return fileStorageService.storeImage(imageFile, "products");
        }
        if (!requireNew && existingUrl != null && !existingUrl.isBlank()) {
            return existingUrl.trim();
        }
        throw new RuntimeException("Please choose a product image");
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        productService.delete(id);
        ra.addFlashAttribute("message", "Product deleted successfully");
        return "redirect:/admin/products";
    }

    @GetMapping("/categories")
    public String categories(@RequestParam(required = false) String q,
                             @RequestParam(defaultValue = "sort") String sort,
                             Model model) {
        model.addAttribute("categories", categoryService.search(q, sort));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "categories");
        model.addAttribute("pageTitle", "Categories — Admin");
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String addCategory(@RequestParam String name,
                              @RequestParam String icon,
                              RedirectAttributes ra) {
        try {
            Category c = categoryService.create(name, icon);
            ra.addFlashAttribute("message", "Category \"" + c.getName() + "\" added");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Could not add category");
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/edit")
    public String editCategory(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String icon,
                               @RequestParam(required = false) Boolean active,
                               RedirectAttributes ra) {
        try {
            categoryService.update(id, name, icon, active != null && active);
            ra.addFlashAttribute("message", "Category updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Could not update category");
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.delete(id);
            ra.addFlashAttribute("message", "Category deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Could not delete category");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/brands")
    public String brands(@RequestParam(required = false) String q,
                         @RequestParam(defaultValue = "name_asc") String sort,
                         Model model) {
        model.addAttribute("brands", brandService.search(q, sort));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "brands");
        model.addAttribute("pageTitle", "Brands — Admin");
        return "admin/brands";
    }

    @PostMapping("/brands")
    public String addBrand(@RequestParam String name,
                           @RequestParam(required = false) String logoUrl,
                           @RequestParam(required = false) MultipartFile logoFile,
                           RedirectAttributes ra) {
        try {
            String resolvedLogo = resolveBrandLogo(logoUrl, logoFile);
            Brand brand = brandService.createFromName(name, resolvedLogo);
            ra.addFlashAttribute("message", "Brand \"" + brand.getName() + "\" added");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Could not add brand");
        }
        return "redirect:/admin/brands";
    }

    @PostMapping("/brands/{id}/edit")
    public String editBrand(@PathVariable Long id,
                            @RequestParam String name,
                            @RequestParam(required = false) String logoUrl,
                            @RequestParam(required = false) MultipartFile logoFile,
                            @RequestParam(required = false) Boolean active,
                            RedirectAttributes ra) {
        try {
            String resolvedLogo = resolveBrandLogo(logoUrl, logoFile);
            brandService.update(id, new com.example.demo.DTO.BrandRequest(name, resolvedLogo, active != null && active));
            ra.addFlashAttribute("message", "Brand updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Could not update brand");
        }
        return "redirect:/admin/brands";
    }

    private String resolveBrandLogo(String logoUrl, MultipartFile logoFile) {
        if (logoFile != null && !logoFile.isEmpty()) {
            return fileStorageService.storeImage(logoFile, "logo");
        }
        return logoUrl;
    }

    @PostMapping("/brands/{id}/delete")
    public String deleteBrand(@PathVariable Long id, RedirectAttributes ra) {
        try {
            brandService.delete(id);
            ra.addFlashAttribute("message", "Brand deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Could not delete brand");
        }
        return "redirect:/admin/brands";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("activeNav", "orders");
        model.addAttribute("pageTitle", "Orders — Admin");
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/confirm-hold")
    public String confirmHold(@PathVariable Long id, RedirectAttributes ra) {
        try {
            orderService.adminConfirmHold(id);
            ra.addFlashAttribute("message", "Payment confirmed — funds held by MME. Seller can now confirm stock.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{id}/confirm-delivered")
    public String confirmDelivered(@PathVariable Long id, RedirectAttributes ra) {
        try {
            orderService.adminConfirmDelivered(id);
            ra.addFlashAttribute("message", "Delivery confirmed. Buyer protection started — then deduct 3% and pay seller.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{id}/release-payout")
    public String releasePayout(@PathVariable Long id, RedirectAttributes ra) {
        try {
            orderService.forceReleasePayout(id);
            ra.addFlashAttribute("message", "3% commission (+ fees) deducted — seller has been paid.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{id}/refund")
    public String refundOrder(@PathVariable Long id, RedirectAttributes ra) {
        try {
            orderService.refundOrder(id);
            ra.addFlashAttribute("message", "Order refunded — held funds returned to customer.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/commissions")
    public String commissions(Model model) {
        model.addAttribute("commissions", commissionRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("totalPlatform", commissionRepository.sumPlatformEarnings());
        model.addAttribute("feesInEscrow", commissionRepository.sumPlatformFeesInEscrow());
        model.addAttribute("escrowSales", commissionRepository.sumSaleAmountInEscrow());
        model.addAttribute("heldCount", commissionRepository.countByPayoutStatus(PayoutStatus.HELD)
                + commissionRepository.countByPayoutStatus(PayoutStatus.IN_PROTECTION));
        model.addAttribute("commissionRate", "3%");
        model.addAttribute("activeNav", "commissions");
        model.addAttribute("pageTitle", "Commissions — Admin");
        return "admin/commissions";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll());
        model.addAttribute("activeNav", "reviews");
        model.addAttribute("pageTitle", "Reviews — Admin");
        return "admin/reviews";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("activeNav", "settings");
        model.addAttribute("pageTitle", "Settings — Admin");
        return "admin/settings";
    }
}
