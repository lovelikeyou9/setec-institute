<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class ProductPhoto extends Model
{
    protected $table = 'product_photos';

    protected $fillable = [
        'product_id',
        'path',
        'is_display',
        'sort_order',
    ];

    protected $casts = [
        'is_display' => 'boolean',
    ];

    public function product(): BelongsTo
    {
        return $this->belongsTo(Product::class);
    }

    public function url(): string
    {
        return asset('storage/' . $this->path);
    }
}
