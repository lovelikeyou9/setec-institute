<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Seller extends Model
{
    public $timestamps = false;

    protected $fillable = [
        'user_id', 'store_name', 'owner_name', 'phone', 'location', 'address', 'country',
        'description', 'business_info', 'id_document', 'logo_url', 'banner_url',
        'status', 'level', 'rating', 'followers', 'response_rate',
        'total_products', 'total_sales', 'rejection_reason',
        'created_at', 'joined_at',
    ];

    protected function casts(): array
    {
        return [
            'created_at' => 'datetime',
            'joined_at' => 'datetime',
            'rating' => 'float',
            'response_rate' => 'float',
        ];
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function products(): HasMany
    {
        return $this->hasMany(Product::class);
    }

    public function orders(): HasMany
    {
        return $this->hasMany(Order::class);
    }

    public function commissions(): HasMany
    {
        return $this->hasMany(Commission::class);
    }

    public function followers(): HasMany
    {
        return $this->hasMany(SellerFollow::class);
    }

    public function isApproved(): bool
    {
        return $this->status === 'APPROVED';
    }
}
