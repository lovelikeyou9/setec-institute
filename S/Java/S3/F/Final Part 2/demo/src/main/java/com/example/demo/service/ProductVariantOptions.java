package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.Product;

/**
 * Category-aware product detail options (Color / Storage / RAM).
 * Avoids showing laptop-style Storage/RAM pickers on GPUs, CPUs, etc.
 */
public final class ProductVariantOptions {

    private ProductVariantOptions() {}

    public record Options(String color, String storage, String ram) {}

    public static Options forCategory(Category category) {
        String key = categoryKey(category);
        return switch (key) {
            case "laptop", "gaming-pc", "computers", "workstations" ->
                    new Options("Black,Silver", "512GB,1TB,2TB", "16GB,32GB,64GB");
            case "ssd", "hdd", "storage" ->
                    new Options(null, "512GB,1TB,2TB,4TB", null);
            case "ram" ->
                    new Options("Black", null, "8GB,16GB,32GB,64GB");
            case "monitor", "smart-tv" ->
                    new Options("Black", null, null);
            case "mouse", "keyboard", "headset", "cooling", "pc-case", "case" ->
                    new Options("Black,White", null, null);
            case "gpu", "cpu", "motherboard", "power-supply", "psu", "networking", "router",
                 "servers", "camera", "drone", "console", "chargers", "diy" ->
                    new Options("Black", null, null);
            default -> new Options("Black", null, null);
        };
    }

    public static void apply(Product product, Category category) {
        Options o = forCategory(category != null ? category : product.getCategory());
        product.setColorOptions(o.color());
        product.setStorageOptions(o.storage());
        product.setRamOptions(o.ram());
    }

    private static String categoryKey(Category category) {
        if (category == null) return "";
        if (category.getSlug() != null && !category.getSlug().isBlank()) {
            return category.getSlug().toLowerCase().trim();
        }
        if (category.getName() != null) {
            return category.getName().toLowerCase().trim().replace(' ', '-');
        }
        return "";
    }
}
