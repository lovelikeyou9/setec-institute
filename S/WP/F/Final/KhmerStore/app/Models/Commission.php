<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Commission extends Model
{
    const UPDATED_AT = null;

    protected $fillable = [
        'order_id', 'seller_id', 'sale_amount', 'commission_percent',
        'commission_amount', 'seller_earnings', 'payout_status',
        'held_at', 'protection_started_at', 'released_at', 'refunded_at', 'created_at',
    ];

    protected function casts(): array
    {
        return [
            'sale_amount' => 'decimal:2',
            'commission_percent' => 'decimal:2',
            'commission_amount' => 'decimal:2',
            'seller_earnings' => 'decimal:2',
            'held_at' => 'datetime',
            'protection_started_at' => 'datetime',
            'released_at' => 'datetime',
            'refunded_at' => 'datetime',
            'created_at' => 'datetime',
        ];
    }

    public function order(): BelongsTo
    {
        return $this->belongsTo(Order::class);
    }

    public function seller(): BelongsTo
    {
        return $this->belongsTo(Seller::class);
    }
}
