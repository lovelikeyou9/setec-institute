<?php

namespace App\Models;

use App\Casts\MysqlBit;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Relations\HasOne;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;

class User extends Authenticatable
{
    use Notifiable;

    protected $fillable = [
        'full_name',
        'email',
        'password',
        'phone',
        'avatar_url',
        'cover_url',
        'role',
        'enabled',
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    protected function casts(): array
    {
        return [
            'enabled' => MysqlBit::class,
            'created_at' => 'datetime',
            'updated_at' => 'datetime',
        ];
    }

    public function getNameAttribute(): string
    {
        return $this->full_name ?? '';
    }

    public function setPasswordAttribute(?string $value): void
    {
        if ($value === null || $value === '') {
            return;
        }
        // Already bcrypt (Spring or Laravel)
        if (preg_match('/^\$2[ayb]\$/', $value)) {
            $this->attributes['password'] = $value;

            return;
        }
        $this->attributes['password'] = bcrypt($value);
    }

    public function isAdmin(): bool
    {
        return $this->role === 'ADMIN';
    }

    public function isSeller(): bool
    {
        return $this->role === 'SELLER';
    }

    /** Approved store owner who may use Seller Center. */
    public function isApprovedSeller(): bool
    {
        return $this->isSeller()
            && $this->seller
            && $this->seller->status === 'APPROVED';
    }

    public function isCustomer(): bool
    {
        return $this->role === 'CUSTOMER';
    }

    public function seller(): HasOne
    {
        return $this->hasOne(Seller::class);
    }

    public function addresses(): HasMany
    {
        return $this->hasMany(Address::class);
    }

    public function cartItems(): HasMany
    {
        return $this->hasMany(CartItem::class);
    }

    public function wishlistItems(): HasMany
    {
        return $this->hasMany(WishlistItem::class);
    }

    public function orders(): HasMany
    {
        return $this->hasMany(Order::class);
    }

    public function notifications(): HasMany
    {
        return $this->hasMany(Notification::class);
    }

    public function reviews(): HasMany
    {
        return $this->hasMany(Review::class);
    }

    public function follows(): HasMany
    {
        return $this->hasMany(SellerFollow::class);
    }
}
