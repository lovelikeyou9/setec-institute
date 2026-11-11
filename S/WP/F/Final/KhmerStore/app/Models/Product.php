<?php

namespace App\Models;

use App\Casts\MysqlBit;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Product extends Model
{
    protected $fillable = [
        'name', 'slug', 'description', 'specifications', 'sku', 'warranty',
        'price', 'original_price', 'discount_percent', 'stock', 'sold_count',
        'rating', 'review_count', 'image_url', 'gallery_urls',
        'color_options', 'ram_options', 'storage_options', 'option_groups',
        'condition_type', 'featured', 'flash_deal', 'flash_deal_ends_at',
        'active', 'brand_id', 'category_id', 'seller_id',
    ];

    protected function casts(): array
    {
        return [
            'active' => MysqlBit::class,
            'featured' => MysqlBit::class,
            'flash_deal' => MysqlBit::class,
            'price' => 'decimal:2',
            'original_price' => 'decimal:2',
            'rating' => 'float',
            'flash_deal_ends_at' => 'datetime',
            'created_at' => 'datetime',
            'updated_at' => 'datetime',
        ];
    }

    public function brand(): BelongsTo
    {
        return $this->belongsTo(Brand::class);
    }

    public function category(): BelongsTo
    {
        return $this->belongsTo(Category::class);
    }

    public function seller(): BelongsTo
    {
        return $this->belongsTo(Seller::class);
    }

    public function reviews(): HasMany
    {
        return $this->hasMany(Review::class);
    }

    public function cartItems(): HasMany
    {
        return $this->hasMany(CartItem::class);
    }

    public function getEffectivePriceAttribute()
    {
        return $this->price;
    }

    /** @return list<array{label:string,choices:list<string>}> */
    public function selectableOptionGroups(): array
    {
        return \App\Services\ProductVariantOptions::groupsFromProduct($this);
    }

    /** @return list<string> */
    public function galleryList(): array
    {
        return product_gallery_urls($this->image_url, $this->gallery_urls);
    }

    public function primaryImageUrl(): string
    {
        $list = $this->galleryList();

        return $list[0] ?? default_product_image();
    }
}
