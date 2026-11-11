package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.Purchase;
import com.example.demo.model.Staff;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseService;
import com.example.demo.service.StaffService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class PageController {

    private static final Map<Long, List<CartItem>> carts = new ConcurrentHashMap<>();

    @Autowired
    private StaffService staffService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PurchaseService purchaseService;

    private static String defaultRole(String userName) {
        if (userName == null) return "User";
        String name = userName.toLowerCase();
        if (name.contains("admin")) return "Admin";
        if (name.contains("stock")) return "Stock";
        return "User";
    }

    private StaffView toView(Staff staff) {
        if (staff == null) return null;
        StaffView view = new StaffView();
        view.setSId(staff.getSId());
        view.setUserName(staff.getUserName());
        view.setPassword(staff.getPassword());
        String role = staff.getRole();
        if (role == null || role.isBlank()) {
            role = defaultRole(staff.getUserName());
        }
        view.setRole(role);
        return view;
    }

    private List<StaffView> toViewList(List<Staff> staffList) {
        List<StaffView> views = new ArrayList<>();
        for (Staff staff : staffList) {
            views.add(toView(staff));
        }
        return views;
    }

    private List<CartItem> cartOf(Long staffId) {
        return carts.computeIfAbsent(staffId, id -> new ArrayList<>());
    }

    private List<CartItem> getCartItems(Long staffId) {
        return new ArrayList<>(cartOf(staffId));
    }

    private int getCartQty(Long staffId) {
        int total = 0;
        for (CartItem item : cartOf(staffId)) {
            total += item.getQty();
        }
        return total;
    }

    private double getCartTotal(Long staffId) {
        double total = 0;
        for (CartItem item : cartOf(staffId)) {
            total += item.getLineTotal();
        }
        return total;
    }

    private void addItem(Long staffId, Long productId, String productName, double price, int qty) {
        List<CartItem> cart = cartOf(staffId);
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                item.setQty(item.getQty() + qty);
                item.setPrice(price);
                item.setProductName(productName);
                return;
            }
        }
        cart.add(new CartItem(productId, productName, price, qty));
    }

    private void updateQty(Long staffId, Long productId, int qty) {
        List<CartItem> cart = cartOf(staffId);
        if (qty <= 0) {
            cart.removeIf(item -> item.getProductId().equals(productId));
            return;
        }
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                item.setQty(qty);
                return;
            }
        }
    }

    private void removeItem(Long staffId, Long productId) {
        cartOf(staffId).removeIf(item -> item.getProductId().equals(productId));
    }

    private void clearCart(Long staffId) {
        cartOf(staffId).clear();
    }

    private void savePurchase(Long staffId, String staffUserName, Long productId,
                              String productName, int qty, double price, double total) {
        purchaseService.savePurchase(new Purchase(
                staffId,
                staffUserName,
                productId,
                productName,
                qty,
                price,
                total,
                LocalDateTime.now()
        ));
    }

    private double getTotalRevenue() {
        return purchaseService.getTotalRevenue();
    }

    private int getTotalQtySold() {
        return purchaseService.getTotalQtySold();
    }

    private long getOrderCount() {
        return purchaseService.getOrderCount();
    }

    private StaffView requireLogin(HttpSession session) {
        return (StaffView) session.getAttribute("loggedInStaff");
    }

    private boolean isRole(StaffView staff, String role) {
        return staff != null && role.equalsIgnoreCase(staff.getRole());
    }

    private boolean isAdminOrStock(StaffView staff) {
        return isRole(staff, "Admin") || isRole(staff, "Stock");
    }

    private void addCartAttrs(Model model, HttpSession session) {
        StaffView staff = requireLogin(session);
        if (staff != null && isRole(staff, "User")) {
            model.addAttribute("cartCount", getCartQty(staff.getSId()));
            model.addAttribute("cartTotal", getCartTotal(staff.getSId()));
        } else {
            model.addAttribute("cartCount", 0);
            model.addAttribute("cartTotal", 0.0);
        }
    }

    private void addDashCommon(Model model) {
        model.addAttribute("expiredCount", getExpiredProducts().size());
    }

    private Map<Long, String> categoryMap() {
        Map<Long, String> catNames = new HashMap<>();
        for (Category c : categoryService.getAllCategories()) {
            catNames.put(c.getCatID(), c.getCatName());
        }
        return catNames;
    }

    private String afterLoginRedirect(StaffView staff) {
        if (isRole(staff, "User")) return "redirect:/products";
        if (isRole(staff, "Stock")) return "redirect:/stock/products";
        return "redirect:/dashboard";
    }

    private StaffView login(String userName, String password) {
        for (Staff staff : staffService.getAllStaff()) {
            if (userName.equals(staff.getUserName()) && password.equals(staff.getPassword())) {
                return toView(staff);
            }
        }
        return null;
    }

    private boolean userNameExists(String userName) {
        for (Staff staff : staffService.getAllStaff()) {
            if (staff.getUserName() != null && staff.getUserName().equalsIgnoreCase(userName)) {
                return true;
            }
        }
        return false;
    }

    private List<Product> getExpiredProducts() {
        LocalDate today = LocalDate.now();
        return productService.getAllProducts().stream()
                .filter(p -> p.getExpiredDate() != null && p.getExpiredDate().isBefore(today))
                .toList();
    }

    private List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return productService.getAllProducts();
        }
        String q = keyword.toLowerCase().trim();
        return productService.getAllProducts().stream()
                .filter(p -> p.getPName() != null && p.getPName().toLowerCase().contains(q))
                .toList();
    }

    private Map<Long, Integer> getSoldByProduct() {
        Map<Long, Integer> soldByProduct = new HashMap<>();
        for (Purchase purchase : purchaseService.getAllPurchases()) {
            if (purchase.getProductId() == null) continue;
            soldByProduct.merge(purchase.getProductId(), purchase.getQty(), Integer::sum);
        }
        return soldByProduct;
    }

    private Map<Long, Integer> getSoldByCategory() {
        Map<Long, Integer> soldByCat = new HashMap<>();
        Map<Long, Integer> soldByProduct = getSoldByProduct();
        for (Product product : productService.getAllProducts()) {
            if (product.getCatID() == null) continue;
            int sold = soldByProduct.getOrDefault(product.getPId(), 0);
            if (sold > 0) {
                soldByCat.merge(product.getCatID(), sold, Integer::sum);
            }
        }
        return soldByCat;
    }

    private Long getTopSaleCategoryId() {
        return getSoldByCategory().entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Product buyProduct(Long pId, int qty) {
        Product product = productService.getProductByPId(pId);
        if (product == null || qty <= 0 || product.getSQty() < qty) {
            return null;
        }
        product.setSQty(product.getSQty() - qty);
        product.setSoldQty(product.getSoldQty() + qty);
        return productService.saveProduct(product);
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("staff", requireLogin(session));
        addCartAttrs(model, session);
        return "index";
    }

    @GetMapping({"/shop", "/product-listing"})
    public String productListing(@RequestParam(required = false) String q,
                                 @RequestParam(required = false) Long cat,
                                 @RequestParam(required = false) Double minPrice,
                                 @RequestParam(required = false) Double maxPrice,
                                 @RequestParam(required = false) Boolean inStock,
                                 HttpSession session,
                                 Model model) {
        List<Product> all = productService.getAllProducts();
        List<Product> list = searchProducts(q);

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
        StaffView staff = requireLogin(session);
        if (staff != null) return afterLoginRedirect(staff);
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String userName,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes ra) {
        StaffView staff = login(userName, password);
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
        StaffView staff = requireLogin(session);
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
        if (userNameExists(userName.trim())) {
            ra.addFlashAttribute("error", "Username already exists");
            return "redirect:/register";
        }

        staffService.saveStaff(new Staff(userName.trim(), password, "User"));
        ra.addFlashAttribute("success", "Account created. Please login.");
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
        if (staff == null) return "redirect:/login";
        if (isRole(staff, "User")) return "redirect:/products";
        if (isRole(staff, "Stock")) return "redirect:/stock/products";

        List<Product> allProducts = productService.getAllProducts();
        Map<Long, Integer> soldByCat = getSoldByCategory();
        Map<Long, String> catNames = categoryMap();

        model.addAttribute("staff", staff);
        model.addAttribute("productCount", allProducts.size());
        model.addAttribute("categoryCount", categoryService.getAllCategories().size());
        model.addAttribute("staffCount", staffService.getAllStaff().size());
        model.addAttribute("expiredCount", getExpiredProducts().size());
        model.addAttribute("totalSales", getTotalRevenue());
        model.addAttribute("totalOrders", getOrderCount());
        model.addAttribute("totalSoldQty", getTotalQtySold());
        model.addAttribute("stockQty", allProducts.stream().mapToInt(Product::getSQty).sum());

        Map<Long, Integer> soldByProduct = getSoldByProduct();
        List<Product> ranking = allProducts.stream()
                .sorted((a, b) -> Integer.compare(
                        soldByProduct.getOrDefault(b.getPId(), 0),
                        soldByProduct.getOrDefault(a.getPId(), 0)))
                .limit(7)
                .toList();
        model.addAttribute("soldByProduct", soldByProduct);
        model.addAttribute("salesRanking", ranking);
        model.addAttribute("catNames", catNames);
        model.addAttribute("soldByCat", soldByCat);

        int effect = allProducts.isEmpty() ? 0 :
                (int) Math.min(100, Math.round(
                        (allProducts.stream().filter(p -> p.getSQty() > 0).count() * 100.0) / allProducts.size()
                ));
        model.addAttribute("operationEffect", effect);

        Long topId = getTopSaleCategoryId();
        if (topId != null) {
            Category topCat = categoryService.getCategoryByCatID(topId);
            model.addAttribute("topCategory", topCat);
            model.addAttribute("topSold", soldByCat.get(topId));
        }
        return "dashboard";
    }

    @GetMapping("/products")
    public String viewProducts(@RequestParam(required = false) String q,
                               HttpSession session,
                               Model model) {
        StaffView staff = requireLogin(session);
        if (staff == null) return "redirect:/login";

        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("products", searchProducts(q));
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("catNames", categoryMap());
        addCartAttrs(model, session);
        return "products";
    }

    @GetMapping("/admin/staff")
    public String staffPage(HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "Admin")) return "redirect:/login";
        addStaffForm(model, staff, new StaffView(), false);
        return "admin-staff";
    }

    @PostMapping("/admin/staff/save")
    public String saveStaff(@ModelAttribute StaffView formStaff, HttpSession session) {
        if (!isRole(requireLogin(session), "Admin")) return "redirect:/login";
        persistStaff(formStaff, formStaff.getRole() == null ? "User" : formStaff.getRole());
        return "redirect:/admin/staff";
    }

    @GetMapping("/admin/staff/edit/{id}")
    public String editStaff(@PathVariable Long id, HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "Admin")) return "redirect:/login";
        addStaffForm(model, staff, toView(staffService.getStaffBySId(id)), false);
        return "admin-staff";
    }

    @GetMapping("/admin/staff/delete/{id}")
    public String deleteStaff(@PathVariable Long id, HttpSession session) {
        if (!isRole(requireLogin(session), "Admin")) return "redirect:/login";
        staffService.deleteStaff(id);
        return "redirect:/admin/staff";
    }

    @GetMapping("/admin/users")
    public String userPage(HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "Admin")) return "redirect:/login";
        StaffView form = new StaffView();
        form.setRole("User");
        addStaffForm(model, staff, form, true);
        return "admin-staff";
    }

    @PostMapping("/admin/users/save")
    public String saveUser(@ModelAttribute StaffView formStaff, HttpSession session) {
        if (!isRole(requireLogin(session), "Admin")) return "redirect:/login";
        persistStaff(formStaff, "User");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUser(@PathVariable Long id, HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "Admin")) return "redirect:/login";
        StaffView form = toView(staffService.getStaffBySId(id));
        if (form != null) form.setRole("User");
        addStaffForm(model, staff, form, true);
        return "admin-staff";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        if (!isRole(requireLogin(session), "Admin")) return "redirect:/login";
        staffService.deleteStaff(id);
        return "redirect:/admin/users";
    }

    private void addStaffForm(Model model, StaffView staff, StaffView formStaff, boolean usersOnly) {
        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("formStaff", formStaff == null ? new StaffView() : formStaff);
        model.addAttribute("usersOnly", usersOnly);
        if (usersOnly) {
            model.addAttribute("staffList", toViewList(staffService.getAllStaff()).stream()
                    .filter(s -> "User".equalsIgnoreCase(s.getRole()))
                    .toList());
            model.addAttribute("pageTitle", "CRUD User");
            model.addAttribute("listTitle", "User list");
            model.addAttribute("activeMenu", "users");
            model.addAttribute("saveUrl", "/admin/users/save");
            model.addAttribute("editBase", "/admin/users/edit/");
            model.addAttribute("deleteBase", "/admin/users/delete/");
        } else {
            model.addAttribute("staffList", toViewList(staffService.getAllStaff()));
            model.addAttribute("pageTitle", "CRUD Staff");
            model.addAttribute("listTitle", "Staff list");
            model.addAttribute("activeMenu", "staff");
            model.addAttribute("saveUrl", "/admin/staff/save");
            model.addAttribute("editBase", "/admin/staff/edit/");
            model.addAttribute("deleteBase", "/admin/staff/delete/");
        }
    }

    private void persistStaff(StaffView formStaff, String role) {
        Staff entity;
        if (formStaff.getSId() != null) {
            entity = staffService.getStaffBySId(formStaff.getSId());
            if (entity == null) {
                entity = new Staff();
            }
            entity.setUserName(formStaff.getUserName());
            entity.setPassword(formStaff.getPassword());
        } else {
            entity = new Staff(formStaff.getUserName(), formStaff.getPassword());
        }
        entity.setRole(role);
        staffService.saveStaff(entity);
    }

    @GetMapping("/admin/expired")
    public String expiredProducts(HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "Admin")) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);
        model.addAttribute("products", getExpiredProducts());
        model.addAttribute("catNames", categoryMap());
        return "admin-expired";
    }

    @GetMapping("/admin/top-sale")
    public String topSale(HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "Admin")) return "redirect:/login";
        addDashCommon(model);
        model.addAttribute("staff", staff);

        Map<Long, Integer> soldByCat = getSoldByCategory();
        model.addAttribute("soldByCat", soldByCat);
        model.addAttribute("catNames", categoryMap());

        Long topId = getTopSaleCategoryId();
        if (topId != null) {
            model.addAttribute("topCatName", categoryMap().get(topId));
            model.addAttribute("topSold", soldByCat.get(topId));
        }
        return "admin-top-sale";
    }

    @GetMapping("/history")
    public String purchaseHistory(HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
        if (staff == null) return "redirect:/login";
        if (isRole(staff, "Stock")) return "redirect:/stock/products";

        addDashCommon(model);
        model.addAttribute("staff", staff);
        if (isRole(staff, "Admin")) {
            model.addAttribute("purchases", purchaseService.getAllPurchases());
            model.addAttribute("allUsers", true);
        } else {
            model.addAttribute("purchases", purchaseService.getPurchasesByStaffId(staff.getSId()));
            model.addAttribute("allUsers", false);
        }
        addCartAttrs(model, session);
        return "history";
    }

    @GetMapping("/stock/products")
    public String stockProducts(HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
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
        StaffView staff = requireLogin(session);
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
        StaffView staff = requireLogin(session);
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
        StaffView staff = requireLogin(session);
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

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") int qty,
                            @RequestParam(required = false) String redirect,
                            HttpSession session,
                            RedirectAttributes ra) {
        StaffView staff = requireLogin(session);
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

addItem(staff.getSId(), product.getPId(), product.getPName(), product.getPrice(), qty);
        ra.addFlashAttribute("success", "Added to cart: " + product.getPName());
        return "redirect:" + (redirect != null && !redirect.isBlank() ? redirect : "/products");
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";

        addDashCommon(model);
        List<CartItem> cart = getCartItems(staff.getSId());
        model.addAttribute("staff", staff);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", getCartTotal(staff.getSId()));
        model.addAttribute("cartCount", getCartQty(staff.getSId()));
        return "cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCartItem(@PathVariable Long id,
                                 @RequestParam int qty,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";
updateQty(staff.getSId(), id, qty);
        ra.addFlashAttribute("success", "Cart updated");
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeCartItem(@PathVariable Long id, HttpSession session) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";
removeItem(staff.getSId(), id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model, RedirectAttributes ra) {
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";

        List<CartItem> cart = getCartItems(staff.getSId());
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Cart is empty");
            return "redirect:/cart";
        }

        model.addAttribute("staff", staff);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", getCartTotal(staff.getSId()));
        model.addAttribute("cartCount", getCartQty(staff.getSId()));
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
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";

        List<CartItem> cart = getCartItems(staff.getSId());
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
        double orderTotal = getCartTotal(staff.getSId()) + shippingFee;

        for (CartItem item : cart) {
            Product bought = buyProduct(item.getProductId(), item.getQty());
            if (bought == null) {
                ra.addFlashAttribute("error", "Checkout failed for: " + item.getProductName() + " (stock issue)");
                return "redirect:/checkout";
            }
            savePurchase(
                    staff.getSId(),
                    staff.getUserName(),
                    bought.getPId(),
                    bought.getPName(),
                    item.getQty(),
                    bought.getPrice(),
                    item.getLineTotal()
            );
        }

        clearCart(staff.getSId());
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
        StaffView staff = requireLogin(session);
        if (!isRole(staff, "User")) return "redirect:/login";
        model.addAttribute("staff", staff);
        return "checkout-success";
    }

    @PostMapping("/cart/checkout")
    public String checkoutOld() {
        return "redirect:/checkout";
    }

    public static class StaffView {
        private Long SId;
        private String userName;
        private String password;
        private String role;

        public StaffView() {}

        public Long getSId() { return SId; }
        public void setSId(Long SId) { this.SId = SId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class CartItem {
        private Long productId;
        private String productName;
        private double price;
        private int qty;

        public CartItem() {}

        public CartItem(Long productId, String productName, double price, int qty) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.qty = qty;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public int getQty() { return qty; }
        public void setQty(int qty) { this.qty = qty; }

        public double getLineTotal() { return price * qty; }
    }
}
