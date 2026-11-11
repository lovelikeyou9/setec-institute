<?php

namespace App\Services;

use App\Models\Category;

class ProductVariantOptions
{
    /** @return array{color:?string,storage:?string,ram:?string} */
    public static function forCategory(?Category $category): array
    {
        $key = self::categoryKey($category);

        return match (true) {
            in_array($key, ['laptop', 'gaming-pc', 'computers', 'workstations'], true) => [
                'color' => 'Black,Silver', 'storage' => '512GB,1TB,2TB', 'ram' => '16GB,32GB,64GB',
            ],
            in_array($key, ['ssd', 'hdd', 'storage'], true) => [
                'color' => null, 'storage' => '512GB,1TB,2TB,4TB', 'ram' => null,
            ],
            $key === 'ram' => [
                'color' => 'Black', 'storage' => null, 'ram' => '8GB,16GB,32GB,64GB',
            ],
            in_array($key, ['monitor', 'smart-tv'], true) => [
                'color' => 'Black', 'storage' => null, 'ram' => null,
            ],
            in_array($key, ['mouse', 'keyboard', 'headset', 'cooling', 'pc-case', 'case'], true) => [
                'color' => 'Black,White', 'storage' => null, 'ram' => null,
            ],
            default => [
                'color' => 'Black', 'storage' => null, 'ram' => null,
            ],
        };
    }

    /**
     * Default named groups for a category (seller can rename labels).
     *
     * @return list<array{label:string,choices:list<string>}>
     */
    public static function defaultGroups(?Category $category): array
    {
        $d = self::forCategory($category);
        $groups = [];
        if (! empty($d['color'])) {
            $groups[] = ['label' => 'Color', 'choices' => self::split($d['color'])];
        }
        if (! empty($d['storage'])) {
            $groups[] = ['label' => 'Storage', 'choices' => self::split($d['storage'])];
        }
        if (! empty($d['ram'])) {
            $groups[] = ['label' => 'RAM', 'choices' => self::split($d['ram'])];
        }

        return $groups ?: [['label' => 'Color', 'choices' => ['Black']]];
    }

    /**
     * Normalize posted JSON / legacy fields into option_groups + color/storage/ram columns.
     *
     * @return array{option_groups:?string,color_options:?string,storage_options:?string,ram_options:?string}
     */
    public static function normalizePosted(?Category $category, ?string $optionGroupsJson, ?string $color, ?string $storage, ?string $ram): array
    {
        $groups = self::decodeGroups($optionGroupsJson);

        if ($groups === []) {
            // Fall back to legacy comma fields, then category defaults
            $legacy = self::applyDefaults($category, $color, $storage, $ram);
            $groups = [];
            if (! empty($legacy['color_options'])) {
                $groups[] = ['label' => 'Color', 'choices' => self::split($legacy['color_options'])];
            }
            if (! empty($legacy['storage_options'])) {
                $groups[] = ['label' => 'Storage', 'choices' => self::split($legacy['storage_options'])];
            }
            if (! empty($legacy['ram_options'])) {
                $groups[] = ['label' => 'RAM', 'choices' => self::split($legacy['ram_options'])];
            }
        }

        $groups = array_values(array_filter($groups, static function ($g) {
            return ! empty($g['label']) && ! empty($g['choices']);
        }));

        return [
            'option_groups' => $groups === [] ? null : json_encode($groups, JSON_UNESCAPED_UNICODE),
            // Legacy columns are short varchar — keep them safe; full data lives in option_groups.
            'color_options' => self::fitLegacy(self::choicesForLabel($groups, ['color', 'colour'])),
            'storage_options' => self::fitLegacy(self::choicesForLabel($groups, ['storage', 'ssd', 'hdd', 'tb', 'capacity'])),
            'ram_options' => self::fitLegacy(self::choicesForLabel($groups, ['ram', 'memory', 'gb'])),
        ];
    }

    /** Fit comma-separated legacy option string into varchar(500). */
    private static function fitLegacy(?string $value, int $max = 500): ?string
    {
        $value = self::normalize($value);
        if ($value === null || strlen($value) <= $max) {
            return $value;
        }
        $parts = [];
        $len = 0;
        foreach (self::split($value) as $part) {
            $add = ($parts === [] ? 0 : 1) + strlen($part);
            if ($len + $add > $max) {
                break;
            }
            $parts[] = $part;
            $len += $add;
        }

        return $parts === [] ? substr($value, 0, $max) : implode(',', $parts);
    }

    public static function applyDefaults(?Category $category, ?string $color, ?string $storage, ?string $ram): array
    {
        $defaults = self::forCategory($category);

        return [
            'color_options' => self::normalize($color) ?? $defaults['color'],
            'storage_options' => self::normalize($storage) ?? $defaults['storage'],
            'ram_options' => self::normalize($ram) ?? $defaults['ram'],
        ];
    }

    /**
     * Resolve display groups for storefront / view modal.
     *
     * @return list<array{label:string,choices:list<string>}>
     */
    public static function groupsFromProduct(object $product): array
    {
        $groups = self::decodeGroups($product->option_groups ?? null);
        if ($groups !== []) {
            return array_values(array_filter($groups, static fn ($g) => ! empty($g['choices'])));
        }

        $out = [];
        if (! empty($product->color_options)) {
            $out[] = ['label' => 'Color', 'choices' => self::split($product->color_options)];
        }
        if (! empty($product->storage_options)) {
            $out[] = ['label' => 'Storage', 'choices' => self::split($product->storage_options)];
        }
        if (! empty($product->ram_options)) {
            $out[] = ['label' => 'RAM', 'choices' => self::split($product->ram_options)];
        }

        return $out;
    }

    /** @return list<array{label:string,choices:list<string>}> */
    public static function decodeGroups(?string $json): array
    {
        if ($json === null || trim($json) === '') {
            return [];
        }
        $data = json_decode($json, true);
        if (! is_array($data)) {
            return [];
        }
        $out = [];
        foreach ($data as $row) {
            if (! is_array($row)) {
                continue;
            }
            $label = trim((string) ($row['label'] ?? ''));
            $choices = $row['choices'] ?? [];
            if (is_string($choices)) {
                $choices = self::split($choices);
            }
            if (! is_array($choices)) {
                $choices = [];
            }
            $choices = array_values(array_filter(array_map(static fn ($c) => trim((string) $c), $choices)));
            if ($label === '' || $choices === []) {
                continue;
            }
            $out[] = ['label' => $label, 'choices' => $choices];
        }

        return $out;
    }

    /** @param list<array{label:string,choices:list<string>}> $groups */
    private static function choicesForLabel(array $groups, array $aliases): ?string
    {
        foreach ($groups as $g) {
            $key = strtolower(trim($g['label']));
            if (in_array($key, $aliases, true)) {
                return implode(',', $g['choices']);
            }
        }

        return null;
    }

    /** @return list<string> */
    private static function split(?string $value): array
    {
        if ($value === null || trim($value) === '') {
            return [];
        }

        return array_values(array_filter(array_map('trim', explode(',', $value))));
    }

    private static function normalize(?string $value): ?string
    {
        if ($value === null) {
            return null;
        }
        $value = trim($value);

        return $value === '' ? null : $value;
    }

    private static function categoryKey(?Category $category): string
    {
        if (! $category) {
            return '';
        }
        if (! empty($category->slug)) {
            return strtolower(trim($category->slug));
        }
        if (! empty($category->name)) {
            return strtolower(str_replace(' ', '-', trim($category->name)));
        }

        return '';
    }
}
