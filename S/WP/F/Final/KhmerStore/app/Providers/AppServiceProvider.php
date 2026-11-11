<?php

namespace App\Providers;

use App\Models\CartItem;
use App\Models\Category;
use App\Models\Notification;
use App\Models\WishlistItem;
use Illuminate\Pagination\Paginator;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\View;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        //
    }

    public function boot(): void
    {
        Paginator::defaultView('vendor.pagination.mmt');
        Paginator::defaultSimpleView('vendor.pagination.mmt');

        // Share cart/wishlist before child views render (@extends renders content first).
        View::composer([
            'layouts.*',
            'partials.*',
            'store.*',
            'account.*',
            'auth.*',
            'components.*',
        ], function ($view) {
            static $shared = null;
            if ($shared === null) {
                $navCategories = Category::where('active', 1)->orderBy('sort_order')->limit(12)->get();
                $navMenuGroups = $this->buildNavMenuGroups();
                $cartCount = 0;
                $wishlistCount = 0;
                $unreadNotifications = 0;
                $wishlistProductIds = [];
                if (Auth::check()) {
                    // Count distinct cart lines (products), not total quantity units.
                    $cartCount = (int) CartItem::where('user_id', Auth::id())->count();
                    $wishlistProductIds = WishlistItem::where('user_id', Auth::id())
                        ->pluck('product_id')
                        ->map(fn ($id) => (int) $id)
                        ->all();
                    $wishlistCount = count($wishlistProductIds);
                    $unreadNotifications = Notification::where('user_id', Auth::id())->where('read_flag', 0)->count();
                }
                $shared = compact(
                    'navCategories',
                    'navMenuGroups',
                    'cartCount',
                    'wishlistCount',
                    'wishlistProductIds',
                    'unreadNotifications'
                );
                View::share('wishlistProductIds', $wishlistProductIds);
                View::share('cartCount', $cartCount);
                View::share('wishlistCount', $wishlistCount);
            }
            $view->with($shared);
        });
    }

    private function buildNavMenuGroups(): array
    {
        $groups = [
            ['Computers & Components', '💻', [
                'computers', 'gaming-pc', 'laptop', 'cpu', 'gpu', 'motherboard', 'ram',
                'ssd', 'hdd', 'power-supply', 'pc-case', 'cooling', 'monitor',
                'keyboard', 'mouse', 'headset', 'accessories',
            ]],
            ['Networking & Wi-Fi', '📡', ['networking', 'wifi-routers', 'network-switches', 'nas-storage']],
            ['Gaming Consoles', '🎮', ['gaming-consoles', 'controllers', 'game-cards']],
            ['Cameras, Video & Drones', '📷', ['cameras', 'action-cams', 'drones', 'capture-cards', 'security-cameras']],
            ['Power & Charging', '⚡', ['chargers', 'ups', 'power-banks', 'cables']],
            ['Electronics, Robotics & DIY', '🤖', ['microcontrollers', 'audio', 'earbuds', 'smart-tv']],
            ['Servers & Professional IT', '🖥️', ['servers', 'workstations', 'enterprise-networking']],
        ];

        $bySlug = Category::where('active', 1)->orderBy('sort_order')->get()->keyBy('slug');
        $out = [];
        foreach ($groups as [$title, $icon, $slugs]) {
            $items = [];
            foreach ($slugs as $slug) {
                $c = $bySlug->get($slug);
                if ($c) {
                    $items[] = [
                        'id' => $c->id,
                        'name' => $c->name,
                        'slug' => $c->slug,
                        'icon' => $c->icon ?: $icon,
                        'image_url' => $c->image_url,
                    ];
                }
            }
            if ($items) {
                $out[] = compact('title', 'icon', 'items');
            }
        }

        return $out;
    }
}
