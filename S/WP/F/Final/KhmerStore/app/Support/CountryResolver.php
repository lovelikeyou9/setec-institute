<?php

namespace App\Support;

use App\Models\Address;
use App\Models\Order;
use App\Models\Seller;

/**
 * Resolves display country from an explicit country field first, then free-text address/location.
 * Never invents Cambodia for blank or unknown addresses.
 */
final class CountryResolver
{
    public const OPTIONS = [
        'Cambodia', 'United States', 'United Kingdom', 'Germany', 'Canada',
        'Thailand', 'Singapore', 'Vietnam', 'Japan', 'Australia', 'China', 'Other',
    ];

    public static function resolve(?string $address, ?string $location = null, ?string $explicitCountry = null): string
    {
        if ($explicitCountry !== null && trim($explicitCountry) !== '') {
            return self::normalizeKnown(trim($explicitCountry));
        }

        $blob = strtolower(trim(($address ?? '').' '.($location ?? '')));
        if ($blob === '') {
            return 'Unknown';
        }

        if (self::containsAny($blob, ['united states', 'usa', 'u.s.a', 'u.s.', ' america'])) {
            return 'United States';
        }
        if (self::containsAny($blob, ['united kingdom', 'england', 'london', ' scotland', ' wales'])) {
            return 'United Kingdom';
        }
        if (self::containsAny($blob, ['germany', 'berlin', 'munich'])) {
            return 'Germany';
        }
        if (self::containsAny($blob, ['canada', 'toronto', 'vancouver'])) {
            return 'Canada';
        }
        if (self::containsAny($blob, ['thailand', 'bangkok'])) {
            return 'Thailand';
        }
        if (self::containsAny($blob, ['singapore'])) {
            return 'Singapore';
        }
        if (self::containsAny($blob, ['vietnam', 'ho chi minh', 'hanoi'])) {
            return 'Vietnam';
        }
        if (self::containsAny($blob, ['japan', 'tokyo', 'osaka'])) {
            return 'Japan';
        }
        if (self::containsAny($blob, ['australia', 'sydney', 'melbourne'])) {
            return 'Australia';
        }
        if (self::containsAny($blob, ['china', 'beijing', 'shanghai'])) {
            return 'China';
        }
        if (self::containsAny($blob, [
            'cambodia', 'phnom penh', 'siem reap', 'battambang', 'sihanoukville',
            'kampong', 'kandal', 'takeo', 'kampot', 'banteay', 'prey veng', 'svay rieng',
        ])) {
            return 'Cambodia';
        }

        return 'Other';
    }

    public static function resolveSeller(?Seller $seller): string
    {
        if (! $seller) {
            return 'Unknown';
        }

        return self::resolve($seller->address, $seller->location, $seller->country);
    }

    public static function resolveBuyer(?Order $order): string
    {
        if (! $order) {
            return 'Unknown';
        }

        return self::resolve($order->shipping_address, null, $order->shipping_country);
    }

    public static function resolveAddress(?Address $address): string
    {
        if (! $address) {
            return 'Unknown';
        }

        return self::resolve($address->formatted(), null, $address->country);
    }

    public static function matchesSeller(string $countryName, ?Seller $seller): bool
    {
        return $countryName !== '' && strcasecmp($countryName, self::resolveSeller($seller)) === 0;
    }

    public static function matchesBuyer(string $countryName, ?Order $order): bool
    {
        return $countryName !== '' && strcasecmp($countryName, self::resolveBuyer($order)) === 0;
    }

    public static function normalizeName(string $name): string
    {
        $decoded = trim(rawurldecode($name));
        foreach (self::OPTIONS as $known) {
            if (strcasecmp($known, $decoded) === 0) {
                return $known;
            }
        }

        return $decoded !== '' ? $decoded : 'Unknown';
    }

    private static function normalizeKnown(string $country): string
    {
        foreach (self::OPTIONS as $opt) {
            if (strcasecmp($opt, $country) === 0) {
                return $opt;
            }
        }

        return $country;
    }

    /** @param list<string> $needles */
    private static function containsAny(string $text, array $needles): bool
    {
        foreach ($needles as $n) {
            if (str_contains($text, $n)) {
                return true;
            }
        }

        return false;
    }
}
