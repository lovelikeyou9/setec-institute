<?php

namespace App\Models;

use App\Casts\MysqlBit;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Review extends Model
{
    const UPDATED_AT = null;

    protected $fillable = [
        'product_id', 'user_id', 'rating', 'comment', 'image_urls',
        'hidden', 'verified_purchase', 'seller_response', 'seller_responded_at', 'created_at',
    ];

    protected function casts(): array
    {
        return [
            'hidden' => MysqlBit::class,
            'verified_purchase' => MysqlBit::class,
            'created_at' => 'datetime',
            'seller_responded_at' => 'datetime',
        ];
    }

    public function product(): BelongsTo
    {
        return $this->belongsTo(Product::class);
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }
}
