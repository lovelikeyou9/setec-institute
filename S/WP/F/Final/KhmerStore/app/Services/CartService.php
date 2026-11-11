<?php

namespace App\Services;

use App\Models\CartItem;
use App\Models\Product;
use Illuminate\Support\Collection;

class CartService
{
    public function getCart(int $userId): Collection
    {
        return CartItem::with(['product.seller', 'product.brand'])
            ->where('user_id', $userId)
            ->orderByDesc('id')
            ->get();
    }

    public function getCartGroupedBySeller(int $userId): Collection
    {
        return $this->getCart($userId)->groupBy(fn ($ci) => $ci->product->seller_id);
    }

    public function getSelected(int $userId): Collection
    {
        return CartItem::with('product.seller')
            ->where('user_id', $userId)
            ->where('selected', 1)
            ->get();
    }

    public function selectedSubtotal(int $userId): float
    {
        return $this->getSelected($userId)->sum(fn ($ci) => (float) $ci->product->price * $ci->quantity);
    }

    public function count(int $userId): int
    {
        return (int) CartItem::where('user_id', $userId)->sum('quantity');
    }

    public function addToCart(int $userId, int $productId, int $quantity = 1): CartItem
    {
        $product = Product::findOrFail($productId);
        $item = CartItem::where('user_id', $userId)->where('product_id', $productId)->first();
        if ($item) {
            $item->quantity += max(1, $quantity);
            $item->selected = true;
            $item->save();

            return $item;
        }

        return CartItem::create([
            'user_id' => $userId,
            'product_id' => $product->id,
            'quantity' => max(1, $quantity),
            'selected' => 1,
            'created_at' => now(),
        ]);
    }

    public function updateQuantity(int $itemId, int $quantity): void
    {
        $item = CartItem::findOrFail($itemId);
        if ($quantity <= 0) {
            $item->delete();

            return;
        }
        $item->quantity = $quantity;
        $item->save();
    }

    public function toggleSelect(int $itemId): void
    {
        $item = CartItem::findOrFail($itemId);
        $item->selected = ! $item->selected;
        $item->save();
    }

    public function selectAll(int $userId, bool $selected): void
    {
        CartItem::where('user_id', $userId)->update(['selected' => $selected ? 1 : 0]);
    }

    public function remove(int $itemId): void
    {
        CartItem::where('id', $itemId)->delete();
    }
}
