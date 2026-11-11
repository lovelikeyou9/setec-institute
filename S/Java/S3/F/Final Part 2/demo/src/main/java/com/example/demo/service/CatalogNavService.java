package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogNavService {

    private final CategoryRepository categoryRepository;

    public record NavItem(Long id, String name, String slug, String icon, String imageUrl) {}
    public record NavGroup(String title, String icon, List<NavItem> items) {}

    private static final List<GroupDef> GROUPS = List.of(
            new GroupDef("Computers & Components", "💻", List.of(
                    "computers", "gaming-pc", "laptop", "cpu", "gpu", "motherboard", "ram",
                    "ssd", "hdd", "power-supply", "pc-case", "cooling", "monitor",
                    "keyboard", "mouse", "headset", "accessories")),
            new GroupDef("Networking & Wi-Fi", "📡", List.of(
                    "networking", "wifi-routers", "network-switches", "nas-storage")),
            new GroupDef("Gaming Consoles", "🎮", List.of(
                    "gaming-consoles", "controllers", "game-cards")),
            new GroupDef("Cameras, Video & Drones", "📷", List.of(
                    "cameras", "action-cams", "drones", "capture-cards", "security-cameras")),
            new GroupDef("Power & Charging", "⚡", List.of(
                    "chargers", "ups", "power-banks", "cables")),
            new GroupDef("Electronics, Robotics & DIY", "🤖", List.of(
                    "microcontrollers", "audio", "earbuds", "smart-tv")),
            new GroupDef("Servers & Professional IT", "🖥️", List.of(
                    "servers", "workstations", "enterprise-networking"))
    );

    public List<NavGroup> megaMenu() {
        Map<String, Category> bySlug = categoryRepository.findByActiveTrueOrderBySortOrderAsc()
                .stream()
                .collect(Collectors.toMap(Category::getSlug, c -> c, (a, b) -> a, LinkedHashMap::new));

        List<NavGroup> out = new ArrayList<>();
        for (GroupDef g : GROUPS) {
            List<NavItem> items = new ArrayList<>();
            for (String slug : g.slugs()) {
                Category c = bySlug.get(slug);
                if (c != null) {
                    items.add(new NavItem(
                            c.getId(),
                            c.getName(),
                            c.getSlug(),
                            c.getIcon() != null ? c.getIcon() : g.icon(),
                            c.getImageUrl()));
                }
            }
            if (!items.isEmpty()) {
                out.add(new NavGroup(g.title(), g.icon(), items));
            }
        }
        return out;
    }

    public List<NavGroup> categoryPageGroups() {
        return megaMenu();
    }

    private record GroupDef(String title, String icon, List<String> slugs) {}
}
