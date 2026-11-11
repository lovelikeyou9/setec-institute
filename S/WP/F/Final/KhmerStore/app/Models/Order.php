<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Relations\HasOne;

class Order extends Model
{
    public $timestamps = false;

    protected $fillable = [
        'order_number', 'user_id', 'seller_id', 'status', 'payment_method', 'delivery_type',
        'subtotal', 'shipping_fee', 'discount', 'platform_fee', 'total',
        'recipient_name', 'recipient_phone', 'shipping_address', 'shipping_country',
        'tracking_number', 'dispute_note', 'dispute_reason', 'cancel_reason',
        'placed_at', 'paid_at', 'admin_held_at', 'seller_confirmed_at',
        'shipped_at', 'out_for_delivery_at', 'delivered_at', 'cancelled_at',
        'dispute_opened_at', 'protection_ends_at', 'payout_released_at', 'estimated_delivery',
    ];

    protected function casts(): array
    {
        return [
            'subtotal' => 'decimal:2',
            'shipping_fee' => 'decimal:2',
            'discount' => 'decimal:2',
            'platform_fee' => 'decimal:2',
            'total' => 'decimal:2',
            'placed_at' => 'datetime',
            'paid_at' => 'datetime',
            'admin_held_at' => 'datetime',
            'seller_confirmed_at' => 'datetime',
            'shipped_at' => 'datetime',
            'out_for_delivery_at' => 'datetime',
            'delivered_at' => 'datetime',
            'cancelled_at' => 'datetime',
            'dispute_opened_at' => 'datetime',
            'protection_ends_at' => 'datetime',
            'payout_released_at' => 'datetime',
            'estimated_delivery' => 'datetime',
        ];
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function seller(): BelongsTo
    {
        return $this->belongsTo(Seller::class);
    }

    public function items(): HasMany
    {
        return $this->hasMany(OrderItem::class);
    }

    public function commission(): HasOne
    {
        return $this->hasOne(Commission::class);
    }

    public function statusLabel(): string
    {
        return str_replace('_', ' ', $this->status ?? '');
    }
}
