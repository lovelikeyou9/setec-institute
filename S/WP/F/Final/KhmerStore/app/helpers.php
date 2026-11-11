<?php

if (! function_exists('default_product_image')) {
    /** Placeholder used when a product has no uploaded image. */
    function default_product_image(): string
    {
        return '/uploads/No_Image_Available.jpg';
    }
}

if (! function_exists('media_url')) {
    /**
     * Encode a public media path so spaces, +, & etc. work in <img src>.
     * Keeps leading slash; leaves full http(s) URLs unchanged.
     * Empty paths fall back to the default product placeholder.
     */
    function media_url(?string $path, bool $fallback = true): string
    {
        if ($path === null || trim($path) === '') {
            return $fallback ? default_product_image() : '';
        }

        $path = str_replace('\\', '/', trim($path));

        if (preg_match('#^https?://#i', $path)) {
            return $path;
        }

        $absolute = str_starts_with($path, '/');
        $segments = array_values(array_filter(explode('/', $path), static fn ($s) => $s !== ''));
        $encoded = implode('/', array_map('rawurlencode', $segments));

        return ($absolute ? '/' : '').$encoded;
    }
}

if (! function_exists('product_gallery_urls')) {
    /**
     * Unique gallery paths for a product (primary + gallery_urls), no empties.
     *
     * @return list<string>
     */
    function product_gallery_urls(?string $imageUrl, ?string $galleryCsv): array
    {
        $items = [];
        foreach (array_merge(
            [$imageUrl ?? ''],
            array_map('trim', explode(',', (string) ($galleryCsv ?? '')))
        ) as $url) {
            $url = trim((string) $url);
            if ($url === '' || in_array($url, $items, true)) {
                continue;
            }
            $items[] = $url;
        }

        return $items;
    }
}
