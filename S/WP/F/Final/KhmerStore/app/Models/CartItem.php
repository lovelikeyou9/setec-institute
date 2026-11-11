<?php

namespace App\Models;

use App\Casts\MysqlBit;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class CartItem extends Model
{
    const UPDATED_AT = null;

    protected $fillable = [
        'user_id', 'product_id', 'quantity', 'selected', 'created_at',
    ];

    protected function casts(): array
    {
        return [
            'selected' => MysqlBit::class,
            'created_at' => 'datetime',
        ];
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function product(): BelongsTo
    {
        return $this->belongsTo(Product::class);
    }
}
