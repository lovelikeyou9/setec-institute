@php
  $nav = $activeNav ?? '';
  if (!$nav) {
    if (request()->is('admin') && !request()->is('admin/*')) $nav = 'dashboard';
    elseif (request()->is('admin/users*')) $nav = 'users';
    elseif (request()->is('admin/sellers*') && !request()->is('admin/seller-applications*')) $nav = 'sellers';
    elseif (request()->is('admin/seller-applications*')) $nav = 'applications';
    elseif (request()->is('admin/products*')) $nav = 'products';
    elseif (request()->is('admin/categories*')) $nav = 'categories';
    elseif (request()->is('admin/brands*')) $nav = 'brands';
    elseif (request()->is('admin/orders*')) $nav = 'orders';
    elseif (request()->is('admin/commissions*')) $nav = 'commissions';
    elseif (request()->is('admin/countries*')) $nav = 'countries';
    elseif (request()->is('admin/reviews*')) $nav = 'reviews';
    elseif (request()->is('admin/settings*')) $nav = 'settings';
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
    <strong>Admin Panel</strong>
    <span>MMT</span>
  </div>
  <nav class="dash-nav">
    <span class="dash-nav-label">Admin</span>
    <a href="{{ url('/admin') }}" class="{{ $nav === 'dashboard' ? 'active' : '' }}">Dashboard</a>

    <div class="dash-nav-group {{ in_array($nav, ['users', 'customers', 'sellers', 'user-detail', 'seller-detail'], true) ? 'open' : '' }}">
      <button type="button" class="dash-nav-toggle {{ in_array($nav, ['users', 'customers', 'sellers', 'user-detail', 'seller-detail'], true) ? 'active' : '' }}"
              aria-expanded="{{ in_array($nav, ['users', 'customers', 'sellers', 'user-detail', 'seller-detail'], true) ? 'true' : 'false' }}">
        <span>Users</span>
        <svg class="chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
          <polyline points="6 9 12 15 18 9"/>
        </svg>
      </button>
      <div class="dash-nav-sub">
        <a href="{{ url('/admin/users') }}" class="{{ in_array($nav, ['users', 'user-detail'], true) ? 'active' : '' }}">Users Lists</a>
        <a href="{{ url('/admin/users?role=CUSTOMER') }}" class="{{ request('role') === 'CUSTOMER' ? 'active' : '' }}">Customers Lists</a>
        <a href="{{ url('/admin/sellers') }}" class="{{ in_array($nav, ['sellers', 'seller-detail'], true) ? 'active' : '' }}">Sellers Lists</a>
      </div>
    </div>

    <a href="{{ url('/admin/seller-applications') }}" class="{{ $nav === 'applications' ? 'active' : '' }}">Applications</a>

    <div class="dash-nav-group {{ in_array($nav, ['products', 'categories', 'brands'], true) ? 'open' : '' }}">
      <button type="button" class="dash-nav-toggle {{ in_array($nav, ['products', 'categories', 'brands'], true) ? 'active' : '' }}"
              aria-expanded="{{ in_array($nav, ['products', 'categories', 'brands'], true) ? 'true' : 'false' }}">
        <span>Products</span>
        <svg class="chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
          <polyline points="6 9 12 15 18 9"/>
        </svg>
      </button>
      <div class="dash-nav-sub">
        <a href="{{ url('/admin/products') }}" class="{{ $nav === 'products' ? 'active' : '' }}">Products List</a>
        <a href="{{ url('/admin/categories') }}" class="{{ $nav === 'categories' ? 'active' : '' }}">Category</a>
        <a href="{{ url('/admin/brands') }}" class="{{ $nav === 'brands' ? 'active' : '' }}">Brand</a>
      </div>
    </div>

    <a href="{{ url('/admin/orders') }}" class="{{ $nav === 'orders' ? 'active' : '' }}">Orders</a>
    <a href="{{ url('/admin/commissions') }}" class="{{ $nav === 'commissions' ? 'active' : '' }}">Commissions</a>
    <a href="{{ url('/admin/countries') }}" class="{{ $nav === 'countries' ? 'active' : '' }}">Countries</a>
    <a href="{{ url('/admin/reviews') }}" class="{{ $nav === 'reviews' ? 'active' : '' }}">Reviews</a>
    <a href="{{ url('/admin/settings') }}" class="{{ $nav === 'settings' ? 'active' : '' }}">Settings</a>

    <span class="dash-nav-label">Account</span>
    <a href="{{ url('/account/orders') }}" class="{{ $nav === 'my-orders' ? 'active' : '' }}">My Orders</a>
    <a href="{{ url('/account/wishlist') }}" class="{{ $nav === 'wishlist' ? 'active' : '' }}">Wishlist</a>
    <a href="{{ url('/account/cart') }}" class="{{ $nav === 'cart' ? 'active' : '' }}">Cart</a>
    <a href="{{ url('/account/notifications') }}" class="{{ $nav === 'notifications' ? 'active' : '' }}">Notifications</a>
    <a href="{{ url('/account/addresses') }}" class="{{ $nav === 'addresses' ? 'active' : '' }}">Addresses</a>
    <a href="{{ url('/account/settings') }}" class="{{ $nav === 'account-settings' ? 'active' : '' }}">Account Settings</a>
  </nav>
</aside>
