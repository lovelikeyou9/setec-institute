<?php

namespace App\Services;

use App\Models\Address;
use App\Models\Commission;
use App\Models\Order;
use App\Models\Seller;
use App\Models\User;
use App\Models\WishlistItem;
use App\Support\CountryResolver;
use Illuminate\Support\Collection;

class AdminCountryService
{
    public function countriesList(string $sort = 'money_desc'): Collection
    {
        $orders = $this->validOrders(Order::with(['user', 'seller', 'items'])->get());
        $sellers = Seller::all();
        $addresses = Address::all();
        $wishlists = WishlistItem::with(['user', 'product.seller'])->get();
        $userCountry = $this->resolveUserCountries($addresses, $orders);

        $map = [];

        foreach ($sellers as $s) {
            if ($s->status === 'PENDING') {
                continue;
            }
            $c = CountryResolver::resolveSeller($s);
            if ($c === 'Unknown') {
                continue;
            }
            $this->ensureAgg($map, $c);
            $map[$c]['sellerIds'][$s->id] = true;
        }

        foreach ($userCountry as $uid => $c) {
            if ($c === 'Unknown') {
                continue;
            }
            $this->ensureAgg($map, $c);
            $map[$c]['buyerIds'][$uid] = true;
        }

        foreach ($orders as $o) {
            $buyCountry = CountryResolver::resolveBuyer($o);
            if ($buyCountry !== 'Unknown') {
                $this->ensureAgg($map, $buyCountry);
                $map[$buyCountry]['buyOrders']++;
                $map[$buyCountry]['buyMoney'] += (float) $o->total;
                $map[$buyCountry]['buyUnits'] += (int) $o->items->sum('quantity');
                if ($o->user_id) {
                    $map[$buyCountry]['buyerIds'][$o->user_id] = true;
                }
            }

            if ($o->seller) {
                $sellCountry = CountryResolver::resolveSeller($o->seller);
                if ($sellCountry === 'Unknown') {
                    continue;
                }
                $this->ensureAgg($map, $sellCountry);
                $map[$sellCountry]['sellOrders']++;
                $map[$sellCountry]['sellMoney'] += (float) $o->total;
                $map[$sellCountry]['sellUnits'] += (int) $o->items->sum('quantity');
                $map[$sellCountry]['sellerIds'][$o->seller_id] = true;
            }
        }

        foreach ($wishlists as $w) {
            if ($w->user_id && isset($userCountry[$w->user_id]) && $userCountry[$w->user_id] !== 'Unknown') {
                $c = $userCountry[$w->user_id];
                $this->ensureAgg($map, $c);
                $map[$c]['wishlistViews']++;
            }
            $seller = $w->product?->seller;
            if ($seller) {
                $c = CountryResolver::resolveSeller($seller);
                if ($c !== 'Unknown') {
                    $this->ensureAgg($map, $c);
                    $map[$c]['wishlistOnSellers']++;
                }
            }
        }

        $rows = collect($map)->map(function ($a, $name) {
            return [
                'name' => $name,
                'sellerCount' => count($a['sellerIds']),
                'buyerCount' => count($a['buyerIds']),
                'buyOrders' => $a['buyOrders'],
                'sellOrders' => $a['sellOrders'],
                'buyMoney' => round($a['buyMoney'], 2),
                'sellMoney' => round($a['sellMoney'], 2),
                'buyUnits' => $a['buyUnits'],
                'sellUnits' => $a['sellUnits'],
                'wishlistViews' => $a['wishlistViews'],
            ];
        })->values();

        return $this->sortCountryRows($rows, $sort);
    }

    public function countryProfile(string $countryName, string $sellerSort = 'revenue_desc', string $buyerSort = 'spent_desc'): array
    {
        $country = CountryResolver::normalizeName($countryName);
        $allOrders = $this->validOrders(Order::with(['user', 'seller', 'items.product.category', 'commission'])->get());
        $allSellers = Seller::all();
        $addresses = Address::all();
        $wishlists = WishlistItem::with(['user', 'product.seller'])->get();
        $userCountry = $this->resolveUserCountries($addresses, $allOrders);

        $sellers = $allSellers->filter(fn ($s) => CountryResolver::matchesSeller($country, $s))->values();
        $sellerIds = $sellers->pluck('id')->all();

        $buyOrders = $allOrders->filter(fn ($o) => CountryResolver::matchesBuyer($country, $o))->values();
        $sellOrders = $allOrders->filter(fn ($o) => $o->seller_id && in_array($o->seller_id, $sellerIds, true))->values();

        $buyerIds = [];
        foreach ($userCountry as $uid => $c) {
            if (strcasecmp($c, $country) === 0) {
                $buyerIds[$uid] = true;
            }
        }
        foreach ($buyOrders as $o) {
            if ($o->user_id) {
                $buyerIds[$o->user_id] = true;
            }
        }
        $buyerIdList = array_keys($buyerIds);

        $buyMoney = round((float) $buyOrders->sum('total'), 2);
        $sellMoney = round((float) $sellOrders->sum('total'), 2);
        $buyUnits = (int) $buyOrders->sum(fn ($o) => $o->items->sum('quantity'));
        $sellUnits = (int) $sellOrders->sum(fn ($o) => $o->items->sum('quantity'));

        $wishlistViews = $wishlists->filter(fn ($w) => $w->user_id && isset($buyerIds[$w->user_id]))->count();
        $wishlistOnSellers = $wishlists->filter(
            fn ($w) => $w->product?->seller_id && in_array($w->product->seller_id, $sellerIds, true)
        )->count();

        $year = (int) date('Y');
        $monthStart = now()->startOfMonth();
        $yearStart = now()->startOfYear();

        $sellerRows = $sellers->map(function ($s) use ($sellOrders) {
            $sOrders = $sellOrders->where('seller_id', $s->id);

            return [
                'id' => $s->id,
                'storeName' => $s->store_name,
                'location' => $s->location ?: '—',
                'units' => (int) $sOrders->sum(fn ($o) => $o->items->sum('quantity')),
                'revenue' => round((float) $sOrders->sum('total'), 2),
            ];
        });
        $sellerRows = $this->sortSellerRows($sellerRows, $sellerSort);

        $users = User::whereIn('id', $buyerIdList)->get()->keyBy('id');
        $buyerRows = collect($buyerIdList)->map(function ($uid) use ($users, $buyOrders, $wishlists) {
            $u = $users->get($uid);
            if (! $u) {
                return null;
            }
            $uOrders = $buyOrders->where('user_id', $uid);

            return [
                'id' => $u->id,
                'fullName' => $u->full_name,
                'email' => $u->email,
                'orders' => $uOrders->count(),
                'spent' => round((float) $uOrders->sum('total'), 2),
                'wishlist' => $wishlists->where('user_id', $uid)->count(),
            ];
        })->filter()->values();
        $buyerRows = $this->sortBuyerRows($buyerRows, $buyerSort)->take(50)->values();

        $sellOrderIds = $sellOrders->pluck('id')->all();
        $countryCommissions = Commission::whereIn('order_id', $sellOrderIds)->get();
        $feesEarned = round((float) $countryCommissions->where('payout_status', 'RELEASED')->sum('commission_amount'), 2);
        $feesInEscrow = round((float) $countryCommissions->whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->sum('commission_amount'), 2);

        $months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        $buyTrend = [];
        $sellTrend = [];
        $ordersTrend = [];
        for ($m = 1; $m <= 12; $m++) {
            $buyTrend[] = round((float) $buyOrders->filter(
                fn ($o) => optional($o->placed_at)->year === $year && (int) optional($o->placed_at)->month === $m
            )->sum('total'), 2);
            $sellTrend[] = round((float) $sellOrders->filter(
                fn ($o) => optional($o->placed_at)->year === $year && (int) optional($o->placed_at)->month === $m
            )->sum('total'), 2);
            $ordersTrend[] = $buyOrders->filter(
                fn ($o) => optional($o->placed_at)->year === $year && (int) optional($o->placed_at)->month === $m
            )->count();
        }

        return [
            'countryName' => $country,
            'sellerCount' => $sellers->count(),
            'buyerCount' => count($buyerIdList),
            'buyOrders' => $buyOrders->count(),
            'sellOrders' => $sellOrders->count(),
            'buyMoney' => $buyMoney,
            'sellMoney' => $sellMoney,
            'buyUnits' => $buyUnits,
            'sellUnits' => $sellUnits,
            'wishlistViews' => $wishlistViews,
            'wishlistOnSellers' => $wishlistOnSellers,
            'feesEarned' => $feesEarned,
            'feesInEscrow' => $feesInEscrow,
            'feesTotal' => round($feesEarned + $feesInEscrow, 2),
            'commissionRate' => '3%',
            'feeOrders' => $countryCommissions->count(),
            'buyMonth' => round((float) $buyOrders->filter(fn ($o) => $o->placed_at && $o->placed_at->gte($monthStart))->sum('total'), 2),
            'sellMonth' => round((float) $sellOrders->filter(fn ($o) => $o->placed_at && $o->placed_at->gte($monthStart))->sum('total'), 2),
            'buyYear' => round((float) $buyOrders->filter(fn ($o) => $o->placed_at && $o->placed_at->gte($yearStart))->sum('total'), 2),
            'sellYear' => round((float) $sellOrders->filter(fn ($o) => $o->placed_at && $o->placed_at->gte($yearStart))->sum('total'), 2),
            'ordersMonth' => $buyOrders->filter(fn ($o) => $o->placed_at && $o->placed_at->gte($monthStart))->count(),
            'ordersYear' => $buyOrders->filter(fn ($o) => $o->placed_at && $o->placed_at->gte($yearStart))->count(),
            'topBuyCategories' => $this->topCategories($buyOrders, 5),
            'topSellCategories' => $this->topCategories($sellOrders, 5),
            'topBuyProducts' => $this->topProducts($buyOrders, 5),
            'topSellProducts' => $this->topProducts($sellOrders, 5),
            'sellers' => $sellerRows,
            'buyers' => $buyerRows,
            'recentOrders' => $buyOrders->sortByDesc('placed_at')->take(12)->values(),
            'months' => $months,
            'buyTrend' => $buyTrend,
            'sellTrend' => $sellTrend,
            'ordersTrend' => $ordersTrend,
            'year' => $year,
            'sellerSort' => $sellerSort,
            'buyerSort' => $buyerSort,
        ];
    }

    private function ensureAgg(array &$map, string $name): void
    {
        if (! isset($map[$name])) {
            $map[$name] = [
                'sellerIds' => [],
                'buyerIds' => [],
                'buyOrders' => 0,
                'sellOrders' => 0,
                'buyMoney' => 0.0,
                'sellMoney' => 0.0,
                'buyUnits' => 0,
                'sellUnits' => 0,
                'wishlistViews' => 0,
                'wishlistOnSellers' => 0,
            ];
        }
    }

    private function validOrders(Collection $orders): Collection
    {
        return $orders->whereNotIn('status', ['CANCELLED', 'PENDING_PAYMENT'])->values();
    }

    private function resolveUserCountries(Collection $addresses, Collection $orders): array
    {
        $map = [];
        foreach ($addresses as $a) {
            if (! $a->user_id) {
                continue;
            }
            if (isset($map[$a->user_id]) && ! $a->default_address) {
                continue;
            }
            $map[$a->user_id] = CountryResolver::resolveAddress($a);
        }
        foreach ($orders as $o) {
            if (! $o->user_id || isset($map[$o->user_id])) {
                continue;
            }
            $map[$o->user_id] = CountryResolver::resolveBuyer($o);
        }

        return $map;
    }

    private function sortCountryRows(Collection $rows, string $sort): Collection
    {
        return match ($sort) {
            'name_asc' => $rows->sortBy('name', SORT_NATURAL | SORT_FLAG_CASE)->values(),
            'name_desc' => $rows->sortByDesc('name', SORT_NATURAL | SORT_FLAG_CASE)->values(),
            'sellers_asc' => $rows->sortBy('sellerCount')->values(),
            'sellers_desc' => $rows->sortByDesc('sellerCount')->values(),
            'buyers_asc' => $rows->sortBy('buyerCount')->values(),
            'buyers_desc' => $rows->sortByDesc('buyerCount')->values(),
            'buy_money_desc' => $rows->sortByDesc('buyMoney')->values(),
            'sell_money_desc' => $rows->sortByDesc('sellMoney')->values(),
            default => $rows->sortByDesc(fn ($r) => $r['buyMoney'] + $r['sellMoney'])->values(),
        };
    }

    private function sortSellerRows(Collection $rows, string $sort): Collection
    {
        return match ($sort) {
            'name_asc' => $rows->sortBy('storeName', SORT_NATURAL | SORT_FLAG_CASE)->values(),
            'name_desc' => $rows->sortByDesc('storeName', SORT_NATURAL | SORT_FLAG_CASE)->values(),
            'units_desc' => $rows->sortByDesc('units')->values(),
            'units_asc' => $rows->sortBy('units')->values(),
            'revenue_asc' => $rows->sortBy('revenue')->values(),
            default => $rows->sortByDesc('revenue')->values(),
        };
    }

    private function sortBuyerRows(Collection $rows, string $sort): Collection
    {
        return match ($sort) {
            'name_asc' => $rows->sortBy('fullName', SORT_NATURAL | SORT_FLAG_CASE)->values(),
            'name_desc' => $rows->sortByDesc('fullName', SORT_NATURAL | SORT_FLAG_CASE)->values(),
            'orders_desc' => $rows->sortByDesc('orders')->values(),
            'orders_asc' => $rows->sortBy('orders')->values(),
            'spent_asc' => $rows->sortBy('spent')->values(),
            default => $rows->sortByDesc('spent')->values(),
        };
    }

    private function topCategories(Collection $orders, int $limit): array
    {
        $map = [];
        foreach ($orders as $o) {
            foreach ($o->items as $item) {
                $label = $item->product?->category?->name ?? 'Other';
                if (! isset($map[$label])) {
                    $map[$label] = ['label' => $label, 'qty' => 0, 'amount' => 0.0];
                }
                $map[$label]['qty'] += (int) $item->quantity;
                $map[$label]['amount'] += (float) $item->line_total;
            }
        }

        return collect($map)->sortByDesc('amount')->take($limit)->map(fn ($r) => [
            'label' => $r['label'],
            'qty' => $r['qty'],
            'amount' => round($r['amount'], 2),
        ])->values()->all();
    }

    private function topProducts(Collection $orders, int $limit): array
    {
        $map = [];
        foreach ($orders as $o) {
            foreach ($o->items as $item) {
                $label = $item->product_name ?: 'Product';
                if (! isset($map[$label])) {
                    $map[$label] = ['label' => $label, 'qty' => 0, 'amount' => 0.0];
                }
                $map[$label]['qty'] += (int) $item->quantity;
                $map[$label]['amount'] += (float) $item->line_total;
            }
        }

        return collect($map)->sortByDesc('amount')->take($limit)->map(fn ($r) => [
            'label' => $r['label'],
            'qty' => $r['qty'],
            'amount' => round($r['amount'], 2),
        ])->values()->all();
    }
}
