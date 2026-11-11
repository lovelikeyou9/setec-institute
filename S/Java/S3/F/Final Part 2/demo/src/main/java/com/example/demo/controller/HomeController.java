package com.example.demo.controller;

import com.example.demo.DTO.CategoryShowcase;
import com.example.demo.DTO.PromoSlide;
import com.example.demo.model.*;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final SellerService sellerService;
    private final ReviewService reviewService;
    private final CatalogNavService catalogNavService;
    private final SellerFollowService sellerFollowService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("flashDeals", productService.getFlashDeals());
        model.addAttribute("featured", productService.getFeatured(8));
        model.addAttribute("categories", productService.getCategories());
        model.addAttribute("brands", productService.getBrands());
        model.addAttribute("sellers", sellerService.getApprovedSellers());
        model.addAttribute("showcases", buildShowcases());
        model.addAttribute("promoSlides", buildPromoSlides());
        model.addAttribute("pageTitle", "MME — Modern Multiple Technology");
        return "home";
    }

    private List<PromoSlide> buildPromoSlides() {
        return List.of(
                new PromoSlide(
                        "Computers & Components",
                        "PCS, LAPTOPS, CPUS, GPUS, AND FULL BUILDS.",
                        "Shop Computers",
                        productsHref("computers"),
                        "/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif"),
                new PromoSlide(
                        "Networking & Wi-Fi",
                        "ROUTERS, SWITCHES, MESH, AND NAS STORAGE.",
                        "Shop Networking",
                        productsHref("wifi-routers"),
                        "/uploads/products/BE9300 Tri-Band Wi-Fi 7 Router.jpg"),
                new PromoSlide(
                        "Gaming Consoles",
                        "CONSOLES, CONTROLLERS, AND DIGITAL GAME CARDS.",
                        "Shop Gaming",
                        productsHref("gaming-consoles"),
                        "/uploads/products/Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif"),
                new PromoSlide(
                        "Cameras, Video & Drones",
                        "ACTION CAMS, CAPTURE CARDS, AND AERIAL TECH.",
                        "Shop Capture",
                        productsHref("drones"),
                        "/uploads/products/DJI Mini 4 Pro.jpg"),
                new PromoSlide(
                        "Power & Pro IT",
                        "CHARGERS, UPS, SERVERS, AND WORKSTATIONS.",
                        "Shop Power & IT",
                        productsHref("servers"),
                        "/uploads/products/Dell PowerEdge R760.jpg")
        );
    }

    private String productsHref(String categorySlug) {
        try {
            return "/products?category=" + productService.findCategoryBySlug(categorySlug).getId();
        } catch (Exception e) {
            return "/products";
        }
    }

    private List<CategoryShowcase> buildShowcases() {
        List<CategoryShowcase> panels = new ArrayList<>();
        addShowcase(panels, "wifi-routers", "teal");
        addShowcase(panels, "drones", "blue");
        addShowcase(panels, "gaming-consoles", "purple");
        addShowcase(panels, "earbuds", "orange");
        return panels;
    }

    private void addShowcase(List<CategoryShowcase> panels, String slug, String theme) {
        try {
            Category cat = productService.findCategoryBySlug(slug);
            List<Product> products = productService.getByCategorySlug(slug, 3);
            if (products.isEmpty()) return;
            Product hero = products.get(0);
            BigDecimal priceFrom = products.stream()
                    .map(Product::getPrice)
                    .min(Comparator.naturalOrder())
                    .orElse(hero.getPrice());
            panels.add(new CategoryShowcase(
                    cat,
                    hero.getName(),
                    hero.getImageUrl() != null ? hero.getImageUrl() : cat.getImageUrl(),
                    theme,
                    priceFrom,
                    products
            ));
        } catch (Exception ignored) {
            // skip missing category
        }
    }

    @GetMapping("/deals")
    public String deals(Model model) {
        model.addAttribute("flashDeals", productService.getFlashDeals());
        model.addAttribute("pageTitle", "Flash Deals — MME");
        return "deals";
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Long brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "recommended") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<Product> products = productService.searchProducts(
                q, category, brand, minPrice, maxPrice, condition, rating, verified, inStock, sort, page, 12);
        Category selectedCat = null;
        if (category != null) {
            try {
                selectedCat = productService.findCategoryById(category);
            } catch (Exception ignored) {
                selectedCat = null;
            }
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getCategories());
        model.addAttribute("brands", productService.getBrands());
        model.addAttribute("q", q);
        model.addAttribute("category", selectedCat);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("condition", condition);
        model.addAttribute("rating", rating);
        model.addAttribute("verified", verified);
        model.addAttribute("inStock", inStock);
        model.addAttribute("sort", sort);
        model.addAttribute("pageTitle",
                selectedCat != null ? selectedCat.getName() + " — MME" : "Products — MME");
        model.addAttribute("activeNav", "shop-products");
        return "products/list";
    }

    @GetMapping("/product/{slug}")
    public String productDetail(@PathVariable String slug,
                                @ModelAttribute("currentUser") User currentUser,
                                Model model) {
        Product product = productService.findBySlug(slug);
        List<Review> reviews = reviewService.getProductReviews(product.getId());

        List<String[]> specRows = new ArrayList<>();
        if (product.getSpecifications() != null && !product.getSpecifications().isBlank()) {
            for (String line : product.getSpecifications().split("\\R")) {
                if (line.isBlank()) continue;
                int idx = line.indexOf(':');
                if (idx > 0) {
                    specRows.add(new String[]{line.substring(0, idx).trim(), line.substring(idx + 1).trim()});
                } else {
                    specRows.add(new String[]{"Spec", line.trim()});
                }
            }
        }

        Long sellerId = product.getSeller() != null ? product.getSeller().getId() : null;
        long followerCount = sellerId != null ? sellerFollowService.countFollowers(sellerId) : 0;
        boolean following = currentUser != null && sellerId != null
                && sellerFollowService.isFollowing(currentUser.getId(), sellerId);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("specRows", specRows);
        model.addAttribute("sellerFollowers", followerCount);
        model.addAttribute("followingSeller", following);
        model.addAttribute("related", productService.searchProducts(
                null, product.getCategory().getId(), null, null, null, null, null, null, null,
                "popular", 0, 4).getContent());
        model.addAttribute("pageTitle", product.getName() + " — MME");
        return "products/detail";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", productService.getCategories());
        model.addAttribute("menuGroups", catalogNavService.categoryPageGroups());
        model.addAttribute("pageTitle", "Categories — MME");
        model.addAttribute("activeNav", "shop-categories");
        return "categories";
    }

    @GetMapping("/brands")
    public String brands(Model model) {
        model.addAttribute("brands", productService.getBrands());
        model.addAttribute("pageTitle", "Brands — MME");
        model.addAttribute("activeNav", "shop-brands");
        return "brands";
    }

    @GetMapping("/category/{slug}")
    public String category(@PathVariable String slug,
                           @RequestParam(defaultValue = "recommended") String sort) {
        Category cat = productService.findCategoryBySlug(slug);
        return "redirect:/products?category=" + cat.getId() + "&sort=" + sort;
    }

    @GetMapping("/sellers")
    public String sellers(Model model) {
        model.addAttribute("sellers", sellerService.getApprovedSellers());
        model.addAttribute("pageTitle", "Sellers — MME");
        return "sellers/list";
    }

    @GetMapping("/search/suggest")
    @ResponseBody
    public List<String> suggest(@RequestParam String q) {
        return productService.suggest(q);
    }
}
