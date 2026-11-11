@php
  $nav = $activeNav ?? '';
  if (!$nav) {
    if (request()->is('seller-dashboard') && !request()->is('seller-dashboard/*')) $nav = 'dashboard';
    elseif (request()->is('seller-dashboard/products*')) $nav = 'products';
    elseif (request()->is('seller-dashboard/categories')) $nav = 'categories';
    elseif (request()->is('seller-dashboard/brands')) $nav = 'brands';
    elseif (request()->is('seller-dashboard/orders*')) $nav = 'orders';
    elseif (request()->is('seller-dashboard/reviews')) $nav = 'reviews';
    elseif (request()->is('seller-dashboard/earnings')) $nav = 'earnings';
    elseif (request()->is('seller-dashboard/settings')) $nav = 'settings';
    elseif (request()->is('account/orders') || request()->is('orders/*')) $nav = 'my-orders';
    elseif (request()->is('account/wishlist')) $nav = 'wishlist';
    elseif (request()->is('account/cart')) $nav = 'cart';
    elseif (request()->is('account/notifications')) $nav = 'notifications';
    elseif (request()->is('account/addresses')) $nav = 'addresses';
    elseif (request()->is('account/settings')) $nav = 'account-settings';
  }
@endphp
<aside class="dash-sidebar">
  <div class="brand-block">
    <strong>Seller Center</strong>
    <span>{{ ($seller->store_name ?? null) ?: (Auth::user()->seller->store_name ?? 'Store') }}</span>
  </div>
  <nav class="dash-nav">
    <span class="dash-nav-label">Seller</span>
    <a href="{{ url('/seller-dashboard') }}" class="{{ $nav === 'dashboard' ? 'active' : '' }}">Dashboard</a>

    <div class="dash-nav-group {{ in_array($nav, ['products', 'categories', 'brands', 'add-product'], true) ? 'open' : '' }}">
      <button type="button" class="dash-nav-toggle {{ in_array($nav, ['products', 'categories', 'brands', 'add-product'], true) ? 'active' : '' }}"
              aria-expanded="{{ in_array($nav, ['products', 'categories', 'brands', 'add-product'], true) ? 'true' : 'false' }}">
        <span>Products</span>
        <svg class="chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
          <polyline points="6 9 12 15 18 9"/>
        </svg>
      </button>
      <div class="dash-nav-sub">
        <a href="{{ url('/seller-dashboard/products') }}" class="{{ in_array($nav, ['products', 'add-product'], true) ? 'active' : '' }}">Products List</a>
        <a href="{{ url('/seller-dashboard/categories') }}" class="{{ $nav === 'categories' ? 'active' : '' }}">Category</a>
        <a href="{{ url('/seller-dashboard/brands') }}" class="{{ $nav === 'brands' ? 'active' : '' }}">Brand</a>
      </div>
    </div>

    <a href="{{ url('/seller-dashboard/orders') }}" class="{{ $nav === 'orders' ? 'active' : '' }}">Orders</a>
    <a href="{{ url('/seller-dashboard/reviews') }}" class="{{ $nav === 'reviews' ? 'active' : '' }}">Reviews</a>
    <a href="{{ url('/seller-dashboard/earnings') }}" class="{{ $nav === 'earnings' ? 'active' : '' }}">Earnings</a>
    <a href="{{ url('/seller-dashboard/settings') }}" class="{{ $nav === 'settings' ? 'active' : '' }}">Settings</a>

    <span class="dash-nav-label">Account</span>
    <a href="{{ url('/account/orders') }}" class="{{ $nav === 'my-orders' ? 'active' : '' }}">My Orders</a>
    <a href="{{ url('/account/wishlist') }}" class="{{ $nav === 'wishlist' ? 'active' : '' }}">Wishlist</a>
    <a href="{{ url('/account/cart') }}" class="{{ $nav === 'cart' ? 'active' : '' }}">Cart</a>
    <a href="{{ url('/account/notifications') }}" class="{{ $nav === 'notifications' ? 'active' : '' }}">Notifications</a>
    <a href="{{ url('/account/addresses') }}" class="{{ $nav === 'addresses' ? 'active' : '' }}">Addresses</a>
    <a href="{{ url('/account/settings') }}" class="{{ $nav === 'account-settings' ? 'active' : '' }}">Account Settings</a>
  </nav>
</aside>
