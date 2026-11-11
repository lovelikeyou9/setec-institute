<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Relations\HasOne;

class Product extends Model
{
    protected $table = "products";
    protected $primaryKey = 'id';
    protected $fillable = [
        "category_id",
        "brand_id",
        "user_id",
        "edited_by",
        "model",
        "price",
        "quantity",
        "warranty",
        "photo",
        "description",
        "status"
    ];

    public function brand(): BelongsTo
    {
        return $this->belongsTo(Brand::class);
    }

    public function category(): BelongsTo
    {
        return $this->belongsTo(Category::class);
    }

    // Created By
    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    // Edited By
    public function editor(): BelongsTo
    {
        return $this->belongsTo(User::class, 'edited_by');
    }

    public function photos(): HasMany
    {
        return $this->hasMany(ProductPhoto::class)->orderBy('sort_order')->orderBy('id');
    }

    public function displayPhoto(): HasOne
    {
        return $this->hasOne(ProductPhoto::class)->where('is_display', true);
    }

    public function displayPhotoUrl(): string
    {
        $display = $this->displayPhoto ?? $this->photos->first();

        if ($display) {
            return $display->url();
        }

        if ($this->photo) {
            return asset('storage/' . $this->photo);
        }

        return asset('images/no_image.jpg');
    }
}
