package com.example.demo.service;

import com.example.demo.model.Address;
import com.example.demo.model.Order;
import com.example.demo.model.Seller;

import java.util.List;
import java.util.Locale;

/**
 * Resolves display country from an explicit country field first, then free-text address/location.
 * Never invents Cambodia for blank or unknown addresses.
 */
public final class CountryResolver {

    public static final List<String> OPTIONS = List.of(
            "Cambodia", "United States", "United Kingdom", "Germany", "Canada",
            "Thailand", "Singapore", "Vietnam", "Japan", "Australia", "China", "Other"
    );

    private CountryResolver() {}

    public static String resolve(String address, String location) {
        return resolve(address, location, null);
    }

    public static String resolve(String address, String location, String explicitCountry) {
        if (explicitCountry != null && !explicitCountry.isBlank()) {
            return normalizeKnown(explicitCountry.trim());
        }
        String blob = ((address != null ? address : "") + " " + (location != null ? location : ""))
                .toLowerCase(Locale.ENGLISH).trim();
        if (blob.isBlank()) return "Unknown";

        if (containsAny(blob, "united states", "usa", "u.s.a", "u.s.", " america")) return "United States";
        if (containsAny(blob, "united kingdom", "england", "london", " scotland", " wales")) return "United Kingdom";
        if (containsAny(blob, "germany", "berlin", "munich")) return "Germany";
        if (containsAny(blob, "canada", "toronto", "vancouver")) return "Canada";
        if (containsAny(blob, "thailand", "bangkok")) return "Thailand";
        if (containsAny(blob, "singapore")) return "Singapore";
        if (containsAny(blob, "vietnam", "ho chi minh", "hanoi")) return "Vietnam";
        if (containsAny(blob, "japan", "tokyo", "osaka")) return "Japan";
        if (containsAny(blob, "australia", "sydney", "melbourne")) return "Australia";
        if (containsAny(blob, "china", "beijing", "shanghai")) return "China";
        if (containsAny(blob, "cambodia", "phnom penh", "siem reap", "battambang", "sihanoukville",
                "kampong", "kandal", "takeo", "kampot", "banteay", "prey veng", "svay rieng")) {
            return "Cambodia";
        }
        return "Other";
    }

    /** Seller store country — explicit field, else address/location text. */
    public static String resolveSeller(Seller seller) {
        if (seller == null) return "Unknown";
        return resolve(seller.getAddress(), seller.getLocation(), seller.getCountry());
    }

    /** Buyer destination — shipping country / address only (never seller location). */
    public static String resolveBuyer(Order order) {
        if (order == null) return "Unknown";
        return resolve(order.getShippingAddress(), null, order.getShippingCountry());
    }

    public static String resolveAddress(Address address) {
        if (address == null) return "Unknown";
        return resolve(address.getFullAddress(), null, address.getCountry());
    }

    public static boolean matches(String countryName, String address, String location) {
        return matches(countryName, address, location, null);
    }

    public static boolean matches(String countryName, String address, String location, String explicitCountry) {
        if (countryName == null || countryName.isBlank()) return false;
        return countryName.equalsIgnoreCase(resolve(address, location, explicitCountry));
    }

    public static boolean matchesSeller(String countryName, Seller seller) {
        return countryName != null && countryName.equalsIgnoreCase(resolveSeller(seller));
    }

    public static boolean matchesBuyer(String countryName, Order order) {
        return countryName != null && countryName.equalsIgnoreCase(resolveBuyer(order));
    }

    private static String normalizeKnown(String country) {
        for (String opt : OPTIONS) {
            if (opt.equalsIgnoreCase(country)) return opt;
        }
        return country;
    }

    private static boolean containsAny(String text, String... needles) {
        for (String n : needles) {
            if (text.contains(n)) return true;
        }
        return false;
    }
}
