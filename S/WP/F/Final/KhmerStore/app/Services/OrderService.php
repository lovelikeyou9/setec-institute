<?php

namespace App\Services;

use App\Models\CartItem;
use App\Models\Commission;
use App\Models\Notification;
use App\Models\Order;
use App\Models\OrderItem;
use App\Models\Product;
use App\Models\User;
use Illuminate\Support\Collection;
use Illuminate\Support\Facades\DB;

class OrderService
{
    private float $commissionPercent = 3.0;

    private int $buyerProtectionDays = 3;

    public function getBuyerProtectionDays(): int
    {
        return $this->buyerProtectionDays;
    }

    public function placeOrders(
        User $user,
        string $recipientName,
        string $recipientPhone,
        string $shippingAddress,
        ?string $shippingCountry,
        string $deliveryType,
        string $paymentMethod
    ): Collection {
        return DB::transaction(function () use ($user, $recipientName, $recipientPhone, $shippingAddress, $shippingCountry, $deliveryType, $paymentMethod) {
            $selected = CartItem::with('product.seller')
                ->where('user_id', $user->id)
                ->where('selected', 1)
                ->get();

            if ($selected->isEmpty()) {
                throw new \RuntimeException('No items selected');
            }

            $bySeller = $selected->groupBy(fn ($ci) => $ci->product->seller_id);
            $shipping = $deliveryType === 'EXPRESS' ? 5.00 : 2.00;
            $orders = collect();
            $onlinePay = $paymentMethod !== 'CASH_ON_DELIVERY';
            $now = now();

            foreach ($bySeller as $sellerId => $items) {
                $seller = $items->first()->product->seller;
                $subtotal = $items->sum(fn ($ci) => (float) $ci->product->price * $ci->quantity);
                $commissionAmt = round($subtotal * $this->commissionPercent / 100, 2);
                $orderNumber = 'KS'.((int) (microtime(true) * 1000)).$sellerId;

                $order = Order::create([
                    'order_number' => $orderNumber,
                    'user_id' => $user->id,
                    'seller_id' => $seller->id,
                    'status' => $onlinePay ? 'PAYMENT_CONFIRMED' : 'PENDING_PAYMENT',
                    'payment_method' => $paymentMethod,
                    'delivery_type' => $deliveryType,
                    'subtotal' => $subtotal,
                    'shipping_fee' => $shipping,
                    'discount' => 0,
                    'platform_fee' => $commissionAmt,
                    'total' => $subtotal + $shipping,
                    'recipient_name' => $recipientName,
                    'recipient_phone' => $recipientPhone,
                    'shipping_address' => $shippingAddress,
                    'shipping_country' => $shippingCountry ? trim($shippingCountry) : null,
                    'placed_at' => $now,
                    'paid_at' => $onlinePay ? $now : null,
                    'estimated_delivery' => $now->copy()->addDays($deliveryType === 'EXPRESS' ? 1 : 3),
                ]);

                foreach ($items as $ci) {
                    $p = $ci->product;
                    if ($p->stock < $ci->quantity) {
                        throw new \RuntimeException('Insufficient stock for '.$p->name);
                    }
                    $p->stock -= $ci->quantity;
                    $p->sold_count = ($p->sold_count ?? 0) + $ci->quantity;
                    $p->save();

                    OrderItem::create([
                        'order_id' => $order->id,
                        'product_id' => $p->id,
                        'product_name' => $p->name,
                        'product_image' => $p->image_url,
                        'quantity' => $ci->quantity,
                        'unit_price' => $p->price,
                        'line_total' => (float) $p->price * $ci->quantity,
                    ]);
                }

                Commission::create([
                    'order_id' => $order->id,
                    'seller_id' => $seller->id,
                    'sale_amount' => $subtotal,
                    'commission_percent' => $this->commissionPercent,
                    'commission_amount' => $commissionAmt,
                    'seller_earnings' => $subtotal - $commissionAmt,
                    'payout_status' => 'HELD',
                    'held_at' => $now,
                    'created_at' => $now,
                ]);

                $seller->total_sales = ($seller->total_sales ?? 0) + $items->sum('quantity');
                $seller->save();

                $payMsg = $onlinePay
                    ? 'Payment successful. Waiting for MMT admin to confirm and hold your funds.'
                    : 'Order placed (Cash on Delivery). Pay when you receive the package.';

                $this->notify($user->id, 'Order Confirmed', "Order {$orderNumber}: {$payMsg}", 'ORDER', '/orders/'.$orderNumber);

                $orders->push($order->load('items', 'seller'));
            }

            CartItem::where('user_id', $user->id)->where('selected', 1)->delete();

            return $orders;
        });
    }

    public function adminConfirmHold(int $orderId): Order
    {
        $order = Order::with(['user', 'seller.user'])->findOrFail($orderId);
        if (! in_array($order->status, ['PAYMENT_CONFIRMED', 'PENDING_PAYMENT'], true)) {
            throw new \RuntimeException('Only paid (or pending COD) orders can be confirmed for escrow hold');
        }
        $now = now();
        if (! $order->paid_at) {
            $order->paid_at = $now;
        }
        $order->admin_held_at = $now;
        $order->status = 'FUNDS_HELD';
        $order->save();
        $this->ensureCommissionHeld($order);

        $this->notify($order->user_id, 'Payment Held by MMT',
            "Admin confirmed order {$order->order_number}. Your payment is held until delivery and buyer protection finish.",
            'ORDER', '/orders/'.$order->order_number);

        if ($order->seller?->user_id) {
            $this->notify($order->seller->user_id, 'Confirm Stock',
                "Order {$order->order_number} — payment is held. Please confirm you have stock, or decline if out of stock.",
                'ORDER', '/seller-dashboard/orders');
        }

        return $order;
    }

    public function sellerAcceptOrder(int $orderId, int $sellerId): Order
    {
        $order = Order::findOrFail($orderId);
        $this->assertSellerOwns($order, $sellerId);
        if ($order->status !== 'FUNDS_HELD') {
            throw new \RuntimeException('You can only accept after admin has confirmed and held payment');
        }
        $order->seller_confirmed_at = now();
        $order->status = 'SELLER_PROCESSING';
        $order->dispute_reason = null;
        $order->dispute_note = null;
        $order->dispute_opened_at = null;
        $order->save();

        $this->notify($order->user_id, 'Seller Confirmed',
            "Seller confirmed stock for order {$order->order_number} and is preparing your package.",
            'ORDER', '/orders/'.$order->order_number);

        return $order;
    }

    public function sellerDeclineOutOfStock(int $orderId, int $sellerId): Order
    {
        $order = Order::with('items.product')->findOrFail($orderId);
        $this->assertSellerOwns($order, $sellerId);
        if (! in_array($order->status, ['FUNDS_HELD', 'PAYMENT_CONFIRMED'], true)) {
            throw new \RuntimeException('This order can no longer be declined for stock');
        }

        $order->cancel_reason = 'OUT_OF_STOCK';
        $order->cancelled_at = now();
        $order->status = 'CANCELLED';
        $order->save();

        $commission = Commission::where('order_id', $order->id)->first();
        if ($commission && $commission->payout_status !== 'RELEASED') {
            $commission->payout_status = 'REFUNDED';
            $commission->refunded_at = now();
            $commission->save();
        }

        $order->status = 'REFUNDED';
        $order->save();

        foreach ($order->items as $item) {
            if ($item->product) {
                $item->product->stock += $item->quantity;
                $item->product->sold_count = max(0, ($item->product->sold_count ?? 0) - $item->quantity);
                $item->product->save();
            }
        }

        $this->notify($order->user_id, 'Order Cancelled — Out of Stock',
            "Seller could not fulfill order {$order->order_number} (out of stock). Your payment has been refunded.",
            'ORDER', '/orders/'.$order->order_number);

        return $order;
    }

    public function sellerUpdateFulfillment(int $orderId, int $sellerId, string $status): Order
    {
        $order = Order::findOrFail($orderId);
        $this->assertSellerOwns($order, $sellerId);
        $allowed = match ($order->status) {
            'SELLER_PROCESSING' => ['SHIPPED'],
            'SHIPPED' => ['OUT_FOR_DELIVERY'],
            default => [],
        };
        if (! in_array($status, $allowed, true)) {
            throw new \RuntimeException('Seller can only: Processing → Shipped → Out for delivery. Admin confirms Delivered.');
        }

        return $this->updateStatus($orderId, $status);
    }

    public function adminConfirmDelivered(int $orderId): Order
    {
        $order = Order::findOrFail($orderId);
        if (! in_array($order->status, ['OUT_FOR_DELIVERY', 'SHIPPED'], true)) {
            throw new \RuntimeException('Confirm delivered only after seller marks Out for delivery (or Shipped).');
        }

        return $this->updateStatus($orderId, 'DELIVERED');
    }

    public function updateStatus(int $orderId, string $status): Order
    {
        $order = Order::findOrFail($orderId);
        $previous = $order->status;
        $order->status = $status;
        $now = now();

        switch ($status) {
            case 'PAYMENT_CONFIRMED':
                $order->paid_at = $now;
                $this->ensureCommissionHeld($order);
                break;
            case 'FUNDS_HELD':
                if (! $order->paid_at) {
                    $order->paid_at = $now;
                }
                $order->admin_held_at = $now;
                $this->ensureCommissionHeld($order);
                break;
            case 'SELLER_PROCESSING':
                if (! $order->seller_confirmed_at) {
                    $order->seller_confirmed_at = $now;
                }
                break;
            case 'SHIPPED':
                $order->shipped_at = $now;
                if (! $order->tracking_number) {
                    $order->tracking_number = 'TRK'.((int) (microtime(true) * 1000));
                }
                break;
            case 'OUT_FOR_DELIVERY':
                if (! $order->shipped_at) {
                    $order->shipped_at = $now;
                }
                $order->out_for_delivery_at = $now;
                break;
            case 'DELIVERED':
                $order->delivered_at = $now;
                if ($order->payment_method === 'CASH_ON_DELIVERY' && ! $order->paid_at) {
                    $order->paid_at = $now;
                }
                $this->startBuyerProtection($order, $now);
                break;
            case 'CANCELLED':
                $order->cancelled_at = $now;
                break;
        }

        $order->save();
        $this->notify($order->user_id, 'Order Update', $this->statusMessage($order, $status), 'ORDER', '/orders/'.$order->order_number);

        return $order;
    }

    public function openDispute(int $orderId, User $buyer, string $reason, ?string $note): Order
    {
        $order = Order::with('seller')->findOrFail($orderId);
        $this->assertBuyer($order, $buyer);
        if ($order->status !== 'DELIVERED') {
            throw new \RuntimeException('You can only open a dispute after the order is delivered');
        }
        if ($order->protection_ends_at && now()->isAfter($order->protection_ends_at)) {
            throw new \RuntimeException('Buyer protection period has ended');
        }

        $order->status = 'DISPUTED';
        $order->dispute_reason = $reason;
        $order->dispute_note = $note;
        $order->dispute_opened_at = now();
        $order->save();

        Commission::where('order_id', $order->id)->update(['payout_status' => 'DISPUTED']);

        $this->notify($order->user_id, 'Dispute Opened',
            "Your report ({$reason}) for order {$order->order_number} was submitted. Payment remains held.",
            'ORDER', '/orders/'.$order->order_number);

        if ($order->seller?->user_id) {
            $this->notify($order->seller->user_id, 'Order Disputed',
                "Buyer reported {$reason} on order {$order->order_number}. Payout is paused.",
                'ORDER', '/seller-dashboard/orders');
        }

        return $order;
    }

    public function releasePayout(int $orderId, bool $force = false): Order
    {
        $order = Order::with('seller')->findOrFail($orderId);
        $commission = Commission::where('order_id', $order->id)->firstOrFail();

        if ($commission->payout_status === 'RELEASED') {
            return $order;
        }
        if ($commission->payout_status === 'REFUNDED') {
            throw new \RuntimeException('This order was refunded — cannot release to seller');
        }
        if ($commission->payout_status === 'DISPUTED' && ! $force) {
            throw new \RuntimeException('Resolve the dispute before releasing payout');
        }
        if (! in_array($order->status, ['DELIVERED', 'COMPLETED'], true)
            && ! ($force && $order->status === 'DISPUTED')) {
            throw new \RuntimeException('Payout can only be released after delivery');
        }
        if (! $force && $order->protection_ends_at && now()->isBefore($order->protection_ends_at)) {
            throw new \RuntimeException('Buyer protection is still active until '.$order->protection_ends_at);
        }

        $now = now();
        $commission->payout_status = 'RELEASED';
        $commission->released_at = $now;
        $commission->save();

        $order->payout_released_at = $now;
        $order->status = 'COMPLETED';
        $order->save();

        if ($order->seller?->user_id) {
            $this->notify($order->seller->user_id, 'Payout Released',
                "MMT released \${$commission->seller_earnings} for order {$order->order_number} (commission {$commission->commission_percent}% deducted).",
                'PAYOUT', '/seller-dashboard/earnings');
        }
        $this->notify($order->user_id, 'Order Complete',
            "Order {$order->order_number} is complete. Thank you for shopping on MMT.",
            'ORDER', '/orders/'.$order->order_number);

        return $order;
    }

    public function forceReleasePayout(int $orderId): Order
    {
        $order = Order::findOrFail($orderId);
        if ($order->status === 'DISPUTED') {
            $order->status = 'DELIVERED';
            $order->save();
            Commission::where('order_id', $order->id)->update(['payout_status' => 'IN_PROTECTION']);
        }

        return $this->releasePayout($orderId, true);
    }

    public function refundOrder(int $orderId): Order
    {
        $order = Order::findOrFail($orderId);
        $commission = Commission::where('order_id', $order->id)->first();
        $now = now();
        if ($commission) {
            if ($commission->payout_status === 'RELEASED') {
                throw new \RuntimeException('Payout already released to seller — cannot refund automatically');
            }
            $commission->payout_status = 'REFUNDED';
            $commission->refunded_at = $now;
            $commission->save();
        }
        $order->status = 'REFUNDED';
        $order->cancelled_at = $now;
        $order->save();

        $this->notify($order->user_id, 'Refund Issued',
            "Order {$order->order_number} was refunded. Held funds were returned.",
            'ORDER', '/orders/'.$order->order_number);

        return $order;
    }

    public function canOpenDispute(Order $order): bool
    {
        if ($order->status !== 'DELIVERED') {
            return false;
        }
        if (! $order->protection_ends_at) {
            return false;
        }

        return ! now()->isAfter($order->protection_ends_at);
    }

    public function autoReleaseEligibleOrders(): int
    {
        $due = Order::where('status', 'DELIVERED')
            ->whereNotNull('protection_ends_at')
            ->where('protection_ends_at', '<=', now())
            ->get();
        $count = 0;
        foreach ($due as $o) {
            try {
                $this->releasePayout($o->id);
                $count++;
            } catch (\Throwable) {
            }
        }

        return $count;
    }

    private function startBuyerProtection(Order $order, $now): void
    {
        $order->protection_ends_at = $now->copy()->addDays(max(1, $this->buyerProtectionDays));
        $c = Commission::where('order_id', $order->id)->first();
        if ($c && in_array($c->payout_status, ['HELD', null], true)) {
            $c->payout_status = 'IN_PROTECTION';
            $c->protection_started_at = $now;
            $c->save();
        }
    }

    private function ensureCommissionHeld(Order $order): void
    {
        $c = Commission::where('order_id', $order->id)->first();
        if ($c && in_array($c->payout_status, [null, 'DISPUTED', 'HELD'], true)) {
            $c->payout_status = 'HELD';
            if (! $c->held_at) {
                $c->held_at = now();
            }
            $c->save();
        }
    }

    private function assertSellerOwns(Order $order, int $sellerId): void
    {
        if ((int) $order->seller_id !== (int) $sellerId) {
            throw new \RuntimeException('You can only manage orders for your own store');
        }
    }

    private function assertBuyer(Order $order, User $buyer): void
    {
        if ((int) $order->user_id !== (int) $buyer->id) {
            throw new \RuntimeException('You can only manage your own orders');
        }
    }

    private function statusMessage(Order $order, string $status): string
    {
        return match ($status) {
            'FUNDS_HELD' => "MMT admin confirmed payment for order {$order->order_number}. Funds are held until you receive the product.",
            'SELLER_PROCESSING' => "Seller confirmed stock and is processing order {$order->order_number}.",
            'SHIPPED' => 'Order '.$order->order_number.' has been shipped'.($order->tracking_number ? ' · Tracking: '.$order->tracking_number : ''),
            'OUT_FOR_DELIVERY' => "Order {$order->order_number} is out for delivery.",
            'DELIVERED' => "Order {$order->order_number} delivered. Buyer protection is open for {$this->buyerProtectionDays} day(s).",
            'DISPUTED' => "A dispute was opened on order {$order->order_number}. Funds remain held.",
            'COMPLETED' => "Order {$order->order_number} completed. Admin deducted commission and paid the seller.",
            default => 'Order '.$order->order_number.' is now: '.str_replace('_', ' ', $status),
        };
    }

    private function notify(int $userId, string $title, string $message, string $type, ?string $link = null): void
    {
        Notification::create([
            'user_id' => $userId,
            'title' => $title,
            'message' => $message,
            'type' => $type,
            'link' => $link,
            'read_flag' => 0,
            'created_at' => now(),
        ]);
    }
}
