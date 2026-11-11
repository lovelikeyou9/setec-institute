package com.example.demo.DTO;

import com.example.demo.model.Brand;

public record BrandResponse(Long id, String name, String slug, String logoUrl, boolean active) {
    public static BrandResponse from(Brand b) {
        return new BrandResponse(b.getId(), b.getName(), b.getSlug(), b.getLogoUrl(), b.isActive());
    }
}
