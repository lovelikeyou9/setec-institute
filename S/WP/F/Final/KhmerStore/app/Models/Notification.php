<?php

namespace App\Models;

use App\Casts\MysqlBit;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Notification extends Model
{
    const UPDATED_AT = null;

    protected $fillable = [
        'user_id', 'title', 'message', 'type', 'link', 'read_flag', 'created_at',
    ];

    protected function casts(): array
    {
        return [
            'read_flag' => MysqlBit::class,
            'created_at' => 'datetime',
        ];
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }
}
