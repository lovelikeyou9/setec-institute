@php
  $nav = $activeNav ?? '';
  if (!$nav) {
    if (request()->is('account') || request()->is('dashboard')) $nav = 'overview';
    elseif (request()->is('account/orders') || request()->is('orders/*')) $nav = 'orders';
    elseif (request()->is('account/wishlist')) $nav = 'wishlist';
    elseif (request()->is('account/cart')) $nav = 'cart';
    elseif (request()->is('account/notifications')) $nav = 'notifications';
    elseif (request()->is('account/addresses')) $nav = 'addresses';
    elseif (request()->is('account/settings')) $nav = 'settings';
  }
@endphp
<aside class="dash-sidebar">
  <div class="brand-block">
    <strong>My Account</strong>
    <span>{{ Auth::user()->full_name ?? 'Customer' }}</span>
  </div>
  <nav class="dash-nav">
    <a href="{{ url('/account') }}" class="{{ $nav === 'overview' ? 'active' : '' }}">Overview</a>
    <a href="{{ url('/account/orders') }}" class="{{ in_array($nav, ['orders', 'my-orders'], true) ? 'active' : '' }}">Orders</a>
    <a href="{{ url('/account/wishlist') }}" class="{{ $nav === 'wishlist' ? 'active' : '' }}">Wishlist</a>
    <a href="{{ url('/account/cart') }}" class="{{ $nav === 'cart' ? 'active' : '' }}">Cart</a>
    <a href="{{ url('/account/notifications') }}" class="{{ $nav === 'notifications' ? 'active' : '' }}">Notifications</a>
    <a href="{{ url('/account/addresses') }}" class="{{ $nav === 'addresses' ? 'active' : '' }}">Addresses</a>
    <a href="{{ url('/account/settings') }}" class="{{ in_array($nav, ['settings', 'account-settings'], true) ? 'active' : '' }}">Settings</a>

    <span class="dash-nav-label">Shop</span>
    <div class="dash-nav-group">
      <button type="button" class="dash-nav-toggle" aria-expanded="false">
        <span>Browse</span>
        <svg class="chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
          <polyline points="6 9 12 15 18 9"/>
        </svg>
      </button>
      <div class="dash-nav-sub">
        <a href="{{ url('/products') }}">Products List</a>
        <a href="{{ url('/categories') }}">Category</a>
        <a href="{{ url('/brands') }}">Brand</a>
      </div>
    </div>
  </nav>
</aside>
