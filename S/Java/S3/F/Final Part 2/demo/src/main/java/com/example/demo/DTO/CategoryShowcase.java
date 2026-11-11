package com.example.demo.DTO;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import java.math.BigDecimal;
import java.util.List;

public record CategoryShowcase(
        Category category,
        String bannerTitle,
        String bannerImage,
        String theme,
        BigDecimal priceFrom,
        List<Product> products
) {}
