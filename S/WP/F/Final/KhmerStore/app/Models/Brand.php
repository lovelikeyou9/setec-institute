<?php

namespace App\Models;

use App\Casts\MysqlBit;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Brand extends Model
{
    public $timestamps = false;

    protected $fillable = [
        'name', 'slug', 'logo_url', 'active',
    ];

    protected function casts(): array
    {
        return [
            'active' => MysqlBit::class,
        ];
    }

    public function products(): HasMany
    {
        return $this->hasMany(Product::class);
    }
}
