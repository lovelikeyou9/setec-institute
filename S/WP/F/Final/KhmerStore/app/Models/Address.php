<?php

namespace App\Models;

use App\Casts\MysqlBit;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Address extends Model
{
    const UPDATED_AT = null;

    protected $fillable = [
        'user_id', 'full_name', 'phone', 'province', 'district', 'commune',
        'street', 'country', 'default_address', 'created_at',
    ];

    protected function casts(): array
    {
        return [
            'default_address' => MysqlBit::class,
            'created_at' => 'datetime',
        ];
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function formatted(): string
    {
        $parts = array_filter([
            $this->street,
            $this->commune,
            $this->district,
            $this->province,
            $this->country,
        ]);

        return implode(', ', $parts);
    }
}
