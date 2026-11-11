<header class="site-header">
  <div class="container header-top">
    <a href="{{ url('/') }}" class="logo" aria-label="MMT - Modern Multiple Technology">
      <img class="logo-icon" src="{{ asset('images/mme-icon.png') }}" alt="MMT" width="40" height="40"/>
      <span class="logo-copy">
        <span class="logo-text">MMT</span>
        <span class="logo-tagline">Modern Multiple Technology</span>
      </span>
    </a>

    <div class="search-wrap">
      <form class="search-form" action="{{ url('/products') }}" method="get" autocomplete="off">
        <input type="search" name="q" id="searchInput" placeholder="Search routers, drones, consoles, GPUs, chargers..."
               value="{{ request('q') }}" aria-label="Search"/>
        <button type="submit" aria-label="Search">Search</button>
      </form>
      <div class="search-dropdown" id="searchDropdown">
        <h4>Suggestions</h4>
        <div id="suggestList"></div>
        <h4>Popular Searches</h4>
        @foreach($popularSearches ?? ['RTX 5070', 'WiFi 7', 'drone', 'mechanical keyboard'] as $s)
          <a href="{{ url('/products?q='.urlencode($s)) }}">{{ $s }}</a>
        @endforeach
      </div>
    </div>

    <div class="header-actions">
      @auth
        @if(Auth::user()->role === 'ADMIN')
          <a href="{{ url('/admin') }}" class="btn btn-primary btn-sm nav-hide-mobile" style="margin-right:.15rem">Admin</a>
        @endif
        @if(Auth::user()->role === 'SELLER' && Auth::user()->seller?->status === 'APPROVED')
          <a href="{{ url('/seller-dashboard') }}" class="btn btn-outline btn-sm nav-hide-mobile" style="margin-right:.15rem">Seller</a>
        @endif

        <a href="{{ url('/account/wishlist') }}" class="btn-icon {{ ($wishlistCount ?? 0) > 0 ? 'has-wish' : '' }}" title="Wishlist" aria-label="Wishlist">
          @if(($wishlistCount ?? 0) > 0)
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="1.5"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
          @else
            <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
          @endif
          @if(($wishlistCount ?? 0) > 0)<span class="badge-count">{{ $wishlistCount }}</span>@endif
        </a>
        <a href="{{ url('/account/cart') }}" class="btn-icon" title="Cart" aria-label="Cart">
          <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
          @if(($cartCount ?? 0) > 0)<span class="badge-count">{{ $cartCount }}</span>@endif
        </a>
        <a href="{{ url('/account/notifications') }}" class="btn-icon" title="Notifications" aria-label="Notifications">
          <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
          @if(($unreadNotifications ?? 0) > 0)<span class="badge-count">{{ $unreadNotifications }}</span>@endif
        </a>

        <div class="user-menu">
          <button type="button" class="user-btn" id="userMenuBtn">
            @if(Auth::user()->avatar_url)
              <img class="user-avatar-img" src="{{ Auth::user()->avatar_url }}" alt="{{ Auth::user()->full_name }}"/>
            @else
              <span class="user-avatar">{{ mb_substr(Auth::user()->full_name ?? 'U', 0, 1) }}</span>
            @endif
            <span class="nav-hide-mobile">{{ Auth::user()->full_name }}</span>
          </button>
          <div class="dropdown-menu" id="userDropdown">
            @if(Auth::user()->role === 'ADMIN')
              <a href="{{ url('/admin') }}">Admin Dashboard</a>
              <div class="divider"></div>
            @endif
            @if(Auth::user()->role === 'SELLER' && Auth::user()->seller?->status === 'APPROVED')
              <a href="{{ url('/seller-dashboard') }}">Seller Dashboard</a>
              <a href="{{ url('/seller-dashboard/products') }}">My Products</a>
              <a href="{{ url('/seller-dashboard/orders') }}">Seller Orders</a>
              <div class="divider"></div>
            @endif
            <a href="{{ url('/account') }}">My Account</a>
            <a href="{{ url('/account/orders') }}">My Orders</a>
            <a href="{{ url('/account/wishlist') }}">Wishlist</a>
            <a href="{{ url('/account/cart') }}">Cart</a>
            @if(Auth::user()->role === 'CUSTOMER' || (Auth::user()->seller && Auth::user()->seller->status !== 'APPROVED'))
              <a href="{{ url('/become-seller') }}">{{ Auth::user()->seller ? 'Seller Application' : 'Become a Seller' }}</a>
            @endif
            <div class="divider"></div>
            <form action="{{ url('/logout') }}" method="post">
              @csrf
              <button type="submit">Logout</button>
            </form>
          </div>
        </div>
      @else
        <a href="{{ url('/login') }}" class="btn-icon" title="Wishlist" aria-label="Wishlist">
          <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
        </a>
        <a href="{{ url('/login') }}" class="btn-icon" title="Cart" aria-label="Cart">
          <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
        </a>
        <div class="header-auth">
          <a href="{{ url('/login') }}" class="btn btn-outline btn-sm">Login</a>
          <a href="{{ url('/register') }}" class="btn btn-primary btn-sm">Register</a>
        </div>
      @endauth
    </div>
  </div>

  <div class="nav-bar">
    <div class="container nav-links">
      <a href="{{ url('/') }}" class="{{ request()->is('/') || request()->is('home') ? 'active' : '' }}">Home</a>
      <a href="{{ url('/categories') }}" class="{{ request()->is('categories*') ? 'active' : '' }}">Categories</a>
      <a href="{{ url('/products') }}" class="{{ request()->is('products*') ? 'active' : '' }}">Products</a>
      <a href="{{ url('/deals') }}" class="{{ request()->is('deals') ? 'active' : '' }}">Deals</a>
      <a href="{{ url('/sellers') }}" class="{{ request()->is('sellers*') ? 'active' : '' }}">Sellers</a>

      @if(!empty($navMenuGroups) && count($navMenuGroups))
        <span class="nav-divider" aria-hidden="true"></span>
        <div class="mega-nav mega-nav-browse">
          <a href="{{ url('/categories') }}" class="mega-nav-btn" id="browseTechBtn" aria-expanded="false" aria-haspopup="true" role="button">
            Browse Technology ▾
          </a>
          <div class="mega-panel mega-panel-browse" role="menu" aria-label="Browse technology categories">
            <div class="mega-browse-columns">
              @foreach($navMenuGroups as $g)
                <div class="mega-browse-col">
                  <div class="mega-panel-head">
                    <span class="mega-panel-icon">{{ $g['icon'] }}</span>
                    <strong>{{ $g['title'] }}</strong>
                  </div>
                  <div class="mega-panel-list">
                    @foreach($g['items'] as $item)
                      <a class="mega-panel-item" href="{{ url('/products?category='.$item['id']) }}">
                        @if(!empty($item['image_url']))
                          <span class="mega-panel-thumb"><img src="{{ $item['image_url'] }}" alt="" loading="lazy"/></span>
                        @else
                          <span class="mega-panel-fallback">{{ $item['icon'] ?? $g['icon'] }}</span>
                        @endif
                        {{ $item['name'] }}
                      </a>
                    @endforeach
                  </div>
                </div>
              @endforeach
            </div>
            <div class="mega-browse-foot">
              <a href="{{ url('/categories') }}">All categories →</a>
              <a href="{{ url('/products') }}">All products →</a>
            </div>
          </div>
        </div>
      @endif
    </div>
  </div>
</header>
