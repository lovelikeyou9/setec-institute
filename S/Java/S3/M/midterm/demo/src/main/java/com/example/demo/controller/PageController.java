package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.Purchase;
import com.example.demo.model.Staff;
import com.example.demo.service.CartService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseService;
import com.example.demo.service.StaffService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private CartService cartService;

    private Staff requireLogin(HttpSession session) {
        return (Staff) session.getAttribute("loggedInStaff");
    }

    private boolean isRole(Staff staff, String role) {
        return staff != null && role.equalsIgnoreCase(staff.getRole());
    }

    private boolean isAdminOrStock(Staff staff) {
        return isRole(staff, "Admin") || isRole(staff, "Stock");
    }

    private void addCartAttrs(Model model, HttpSession session) {
        Staff staff = requireLogin(session);
        if (staff != null && isRole(staff, "User")) {
            model.addAttribute("cartCount", cartService.getCartQty(staff.getSId()));
            model.addAttribute("cartTotal", cartService.getCartTotal(staff.getSId()));
        } else {
            model.addAttribute("cartCount", 0);
            model.addAttribute("cartTotal", 0.0);
        }
    }

    private void addDashCommon(Model model) {
        model.addAttribute("expiredCount", productService.getExpiredProducts().size());
    }

    private Map<Long, String> categoryMap() {
        Map<Long, String> catNames = new HashMap<>();
        for (Category c : categoryService.getAllCategories()) {
            catNames.put(c.getCatID(), c.getCatName());
        }
        return catNames;
    }

    private String afterLoginRedirect(Staff staff) {
        if (isRole(staff, "User")) return "redirect:/products";
        if (isRole(staff, "Stock")) return "redirect:/stock/products";
        return "redirect:/dashboard";
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("staff", requireLogin(session));
        addCartAttrs(model, session);
        return "index";
    }

    // Public Product Listing (storefront) - all products for sale
    @GetMapping({"/shop", "/product-listing"})
    public String productListing(@RequestParam(required = false) String q,
                                 @RequestParam(required = false) Long cat,
                                 @RequestParam(required = false) Double minPrice,
                                 @RequestParam(required = false) Double maxPrice,
                                 @RequestParam(required = false) Boolean inStock,
                                 HttpSession session,
                                 Model model) {
        List<Product> all = productService.getAllProducts();
        List<Product> list = productService.searchProducts(q);

        if (cat != null) {
            list = list.stream().filter(p -> cat.equals(p.getCatID())).toList();
        }
        if (minPrice != null) {
            list = list.stream().filter(p -> p.getPrice() >= minPrice).toList();
        }
        if (maxPrice != null) {
            list = list.stream().filter(p -> p.getPrice() <= maxPrice).toList();
        }
        if (Boolean.TRUE.equals(inStock)) {
            list = list.stream().filter(p -> p.getSQty() > 0).toList();
        }

        Map<Long, Integer> catCounts = new HashMap<>();
        for (Product p : all) {
            if (p.getCatID() != null) {
                catCounts.merge(p.getCatID(), 1, Integer::sum);
            }
        }

        double priceMin = all.stream().mapToDouble(Product::getPrice).min().orElse(0);
        double priceMax = all.stream().mapToDouble(Product::getPrice).max().orElse(1000);

        model.addAttribute("products", list);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("catNames", categoryMap());
        model.addAttribute("catCounts", catCounts);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("selectedCat", cat);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", Boolean.TRUE.equals(inStock));
        model.addAttribute("priceMinBound", priceMin);
        model.addAttribute("priceMaxBound", priceMax);
        model.addAttribute("staff", requireLogin(session));
        addCartAttrs(model, session);
        return "product-listing";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        Staff staff = requireLogin(session);
        if (staff != null) return afterLoginRedirect(staff);
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String userName,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes ra) {
        Staff staff = staffService.login(userName, password);
        if (staff == null) {
            ra.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
        session.setAttribute("loggedInStaff", staff);
        return afterLoginRedirect(staff);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        Staff staff = requireLogin(session);
        if (staff != null) return afterLoginRedirect(staff);
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String userName,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             RedirectAttributes ra) {
        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            ra.addFlashAttribute("error", "Username and password are required");
            return "redirect:/register";
        }
        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/register";
        }
        if (password.length() < 4) {
            ra.addFlashAttribute("error", "Password must be at least 4 characters");
            return "redirect:/register";
        }
        if (staffService.userNameExists(userName.trim())) {
            ra.addFlashAttribute("error", "Username already exists");
            return "redirect:/register";
        }

        staffService.registerUser(userName.trim(), password);
        ra.addFlashAttribute("success", "Account created. Please login.");
        return "redirect:/login";
    }

    // Dashboard: Admin + Stock only (User = ❌)
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (staff == null) return "redirect:/login";
        if (isRole(staff, "User")) return "redirect:/products";
        if (isRole(staff, "Stock")) return "redirect:/stock/products";

        List<Product> allProducts = productService.getAllProducts();
        Map<Long, Integer> soldByCat = productService.getSoldByCategory();
        Map<Long, String> catNames = categoryMap();

        model.addAttribute("staff", staff);
        model.addAttribute("productCount", allProducts.size());
        model.addAttribute("categoryCount", categoryService.getAllCategories().size());
        model.addAttribute("staffCount", staffService.getAllStaff().size());
        model.addAttribute("expiredCount", productService.getExpiredProducts().size());
        model.addAttribute("totalSales", purchaseService.getTotalRevenue());
        model.addAttribute("totalOrders", purchaseService.getOrderCount());
        model.addAttribute("totalSoldQty", purchaseService.getTotalQtySold());
        model.addAttribute("stockQty", allProducts.stream().mapToInt(Product::getSQty).sum());

        // sales ranking by product soldQty
        List<Product> ranking = allProducts.stream()
                .sorted((a, b) -> Integer.compare(b.getSoldQty(), a.getSoldQty()))
                .limit(7)
                .toList();
        model.addAttribute("salesRanking", ranking);
        model.addAttribute("catNames", catNames);

        // chart data: sold by category
        model.addAttribute("soldByCat", soldByCat);

        int effect = allProducts.isEmpty() ? 0 :
                (int) Math.min(100, Math.round(
                        (allProducts.stream().filter(p -> p.getSQty() > 0).count() * 100.0) / allProducts.size()
                ));
        model.addAttribute("operationEffect", effect);

        Long topId = productService.getTopSaleCategoryId();
        if (topId != null) {
            Category topCat = categoryService.getCategoryByCatID(topId);
            model.addAttribute("topCategory", topCat);
            model.addAttribute("topSold", soldByCat.get(topId));
        }
        return "dashboard";
    }

    // View + Search Products: Admin, Stock, User
    @GetMapping("/products")
    public String viewProducts(@RequestParam(required = false) String q,
                               HttpSession session,
                               Model model) {
        Staff staff = requireLogin(session);
        if (staff == null) return "redirect:/login";

        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("products", productService.searchProducts(q));
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("catNames", categoryMap());
        addCartAttrs(model, session);
        return "products";
    }

    // ===== Admin: Staff CRUD =====
    @GetMapping("/admin/staff")
    public String staffPage(HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "Admin")) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("staffList", staffService.getAllStaff());
        model.addAttribute("formStaff", new Staff());
        return "admin-staff";
    }

    @PostMapping("/admin/staff/save")
    public String saveStaff(@ModelAttribute Staff formStaff, HttpSession session) {
        if (!isRole(requireLogin(session), "Admin")) return "redirect:/login";
        if (formStaff.getSId() != null) {
            Staff existing = staffService.getStaffBySId(formStaff.getSId());
            if (existing != null) {
                existing.setUserName(formStaff.getUserName());
                existing.setPassword(formStaff.getPassword());
                existing.setRole(formStaff.getRole());
                staffService.saveStaff(existing);
            }
        } else {
            staffService.saveStaff(formStaff);
        }
        return "redirect:/admin/staff";
    }

    @GetMapping("/admin/staff/edit/{id}")
    public String editStaff(@PathVariable Long id, HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "Admin")) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("staffList", staffService.getAllStaff());
        model.addAttribute("formStaff", staffService.getStaffBySId(id));
        return "admin-staff";
    }

    @GetMapping("/admin/staff/delete/{id}")
    public String deleteStaff(@PathVariable Long id, HttpSession session) {
        if (!isRole(requireLogin(session), "Admin")) return "redirect:/login";
        staffService.deleteStaff(id);
        return "redirect:/admin/staff";
    }

    // Expired: Admin + Stock
    @GetMapping("/admin/expired")
    public String expiredProducts(HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isAdminOrStock(staff)) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("products", productService.getExpiredProducts());
        model.addAttribute("catNames", categoryMap());
        return "admin-expired";
    }

    // Top sale: Admin only
    @GetMapping("/admin/top-sale")
    public String topSale(HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "Admin")) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);

        Map<Long, Integer> soldByCat = productService.getSoldByCategory();
        model.addAttribute("soldByCat", soldByCat);
        model.addAttribute("catNames", categoryMap());

        Long topId = productService.getTopSaleCategoryId();
        if (topId != null) {
            model.addAttribute("topCatName", categoryMap().get(topId));
            model.addAttribute("topSold", soldByCat.get(topId));
        }
        return "admin-top-sale";
    }

    // Purchase history: Admin (all) + User (own)
    @GetMapping("/history")
    public String purchaseHistory(HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (staff == null) return "redirect:/login";
        if (isRole(staff, "Stock")) return "redirect:/stock/products";

        addDashCommon(model);
        model.addAttribute("staff", staff);
        if (isRole(staff, "Admin")) {
            model.addAttribute("purchases", purchaseService.getAll());
            model.addAttribute("allUsers", true);
        } else {
            model.addAttribute("purchases", purchaseService.getByStaff(staff.getSId()));
            model.addAttribute("allUsers", false);
        }
        addCartAttrs(model, session);
        return "history";
    }

    // ===== Stock / Admin: Product & Category CRUD =====
    @GetMapping("/stock/products")
    public String stockProducts(HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isAdminOrStock(staff)) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formProduct", new Product());
        model.addAttribute("catNames", categoryMap());
        return "stock-products";
    }

    @PostMapping("/stock/products/save")
    public String saveProduct(@RequestParam(required = false) String PId,
                              @RequestParam String PName,
                              @RequestParam int SQty,
                              @RequestParam double Price,
                              @RequestParam String ExpiredDate,
                              @RequestParam Long CatID,
                              HttpSession session) {
        if (!isAdminOrStock(requireLogin(session))) return "redirect:/login";
        Long id = (PId == null || PId.isBlank()) ? null : Long.valueOf(PId);
        Product product = (id != null) ? productService.getProductByPId(id) : new Product();
        if (product == null) product = new Product();
        product.setPName(PName);
        product.setSQty(SQty);
        product.setPrice(Price);
        product.setExpiredDate(LocalDate.parse(ExpiredDate));
        product.setCatID(CatID);
        productService.saveProduct(product);
        return "redirect:/stock/products";
    }

    @GetMapping("/stock/products/edit/{id}")
    public String editProduct(@PathVariable Long id, HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isAdminOrStock(staff)) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formProduct", productService.getProductByPId(id));
        model.addAttribute("catNames", categoryMap());
        return "stock-products";
    }

    @GetMapping("/stock/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        if (!isAdminOrStock(requireLogin(session))) return "redirect:/login";
        productService.deleteProduct(id);
        return "redirect:/stock/products";
    }

    @GetMapping("/stock/categories")
    public String stockCategories(HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isAdminOrStock(staff)) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formCategory", new Category());
        return "stock-categories";
    }

    @PostMapping("/stock/categories/save")
    public String saveCategory(@RequestParam(required = false) String CatID,
                               @RequestParam String CatName,
                               HttpSession session) {
        if (!isAdminOrStock(requireLogin(session))) return "redirect:/login";
        Long id = (CatID == null || CatID.isBlank()) ? null : Long.valueOf(CatID);
        Category category = (id != null) ? categoryService.getCategoryByCatID(id) : new Category();
        if (category == null) category = new Category();
        category.setCatName(CatName);
        categoryService.saveCategory(category);
        return "redirect:/stock/categories";
    }

    @GetMapping("/stock/categories/edit/{id}")
    public String editCategory(@PathVariable Long id, HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isAdminOrStock(staff)) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formCategory", categoryService.getCategoryByCatID(id));
        return "stock-categories";
    }

    @GetMapping("/stock/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, HttpSession session) {
        if (!isAdminOrStock(requireLogin(session))) return "redirect:/login";
        categoryService.deleteCategory(id);
        return "redirect:/stock/categories";
    }

    // ===== User only: Cart + Buy (saved in database, keeps after logout/login) =====
    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") int qty,
                            @RequestParam(required = false) String redirect,
                            HttpSession session,
                            RedirectAttributes ra) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";

        Product product = productService.getProductByPId(id);
        if (product == null || qty <= 0) {
            ra.addFlashAttribute("error", "Invalid product or quantity");
            return "redirect:" + (redirect != null && !redirect.isBlank() ? redirect : "/products");
        }
        if (product.getSQty() < qty) {
            ra.addFlashAttribute("error", "Not enough stock");
            return "redirect:" + (redirect != null && !redirect.isBlank() ? redirect : "/products");
        }

        cartService.addItem(staff.getSId(), product.getPId(), product.getPName(), product.getPrice(), qty);
        ra.addFlashAttribute("success", "Added to cart: " + product.getPName());
        return "redirect:" + (redirect != null && !redirect.isBlank() ? redirect : "/products");
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";

        addDashCommon(model);
        List<CartItem> cart = cartService.getCartItems(staff.getSId());
        model.addAttribute("staff", staff);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(staff.getSId()));
        model.addAttribute("cartCount", cartService.getCartQty(staff.getSId()));
        return "cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCartItem(@PathVariable Long id,
                                 @RequestParam int qty,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";
        cartService.updateQty(staff.getSId(), id, qty);
        ra.addFlashAttribute("success", "Cart updated");
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeCartItem(@PathVariable Long id, HttpSession session) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";
        cartService.removeItem(staff.getSId(), id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model, RedirectAttributes ra) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";

        List<CartItem> cart = cartService.getCartItems(staff.getSId());
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Cart is empty");
            return "redirect:/cart";
        }

        model.addAttribute("staff", staff);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(staff.getSId()));
        model.addAttribute("cartCount", cartService.getCartQty(staff.getSId()));
        return "checkout";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(@RequestParam String fullName,
                             @RequestParam(required = false) String phone,
                             @RequestParam(required = false) String mobile,
                             @RequestParam String address,
                             @RequestParam(required = false) String city,
                             @RequestParam(required = false) String province,
                             @RequestParam(required = false) String country,
                             @RequestParam String paymentMethod,
                             @RequestParam(required = false, defaultValue = "standard") String shippingMethod,
                             @RequestParam(required = false) String note,
                             HttpSession session,
                             RedirectAttributes ra) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";

        List<CartItem> cart = cartService.getCartItems(staff.getSId());
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Cart is empty");
            return "redirect:/cart";
        }

        String contactPhone = (mobile != null && !mobile.isBlank()) ? mobile : phone;
        String fullAddress = address;
        if (city != null && !city.isBlank()) fullAddress += ", " + city;
        if (province != null && !province.isBlank()) fullAddress += ", " + province;
        if (country != null && !country.isBlank()) fullAddress += ", " + country;

        double shippingFee = "express".equalsIgnoreCase(shippingMethod) ? 4.99 : 0.0;
        double orderTotal = cartService.getCartTotal(staff.getSId()) + shippingFee;

        for (CartItem item : cart) {
            Product bought = productService.buyProduct(item.getProductId(), item.getQty());
            if (bought == null) {
                ra.addFlashAttribute("error", "Checkout failed for: " + item.getProductName() + " (stock issue)");
                return "redirect:/checkout";
            }
            purchaseService.save(new Purchase(
                    staff.getSId(),
                    staff.getUserName(),
                    bought.getPId(),
                    bought.getPName(),
                    item.getQty(),
                    bought.getPrice(),
                    item.getLineTotal(),
                    LocalDateTime.now()
            ));
        }

        cartService.clearCart(staff.getSId());
        ra.addFlashAttribute("fullName", fullName);
        ra.addFlashAttribute("paymentMethod", paymentMethod + ("express".equalsIgnoreCase(shippingMethod) ? " · Express Shipping" : " · Standard Shipping"));
        ra.addFlashAttribute("orderTotal", orderTotal);
        ra.addFlashAttribute("phone", contactPhone);
        ra.addFlashAttribute("address", fullAddress);
        ra.addFlashAttribute("note", note);
        return "redirect:/checkout/success";
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess(HttpSession session, Model model) {
        Staff staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";
        model.addAttribute("staff", staff);
        return "checkout-success";
    }

    // keep old URL working -> go to checkout page
    @PostMapping("/cart/checkout")
    public String checkoutOld() {
        return "redirect:/checkout";
    }
}
