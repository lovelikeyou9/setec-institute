@props(['product', 'wishlisted' => null])
@php
  $verified = in_array($product->seller->level ?? '', ['VERIFIED', 'TRUSTED', 'TOP'], true);
  $sold = (int) ($product->sold_count ?? 0);
  $soldLabel = $sold >= 1000 ? 'Sold '.number_format($sold / 1000, 1).'K' : 'Sold '.$sold;
  $ids = $wishlistProductIds ?? [];
  $inWishlist = $wishlisted === null
      ? in_array((int) $product->id, $ids, true)
      : (bool) $wishlisted;
@endphp
<article class="product-card">
  <div class="product-card-img">
    <a href="{{ url('/products/'.$product->slug) }}">
      <img src="{{ media_url($product->primaryImageUrl()) }}" alt="{{ $product->name }}" loading="lazy"/>
    </a>
    <div class="product-card-badges">
      @if(($product->discount_percent ?? 0) > 0)
        <span class="badge badge-sale">{{ $product->discount_percent }}% OFF</span>
      @endif
    </div>
    @auth
      <form action="{{ url('/wishlist/toggle') }}" method="post">
        @csrf
        <input type="hidden" name="product_id" value="{{ $product->id }}"/>
        <input type="hidden" name="redirect" value="{{ url()->full() }}"/>
        <button type="submit"
                class="fav-btn {{ $inWishlist ? 'active' : '' }}"
                title="{{ $inWishlist ? 'Remove from wishlist' : 'Add to wishlist' }}"
                aria-label="{{ $inWishlist ? 'Remove from wishlist' : 'Add to wishlist' }}"
                aria-pressed="{{ $inWishlist ? 'true' : 'false' }}">{{ $inWishlist ? '♥' : '♡' }}</button>
      </form>
    @else
      <a href="{{ url('/login') }}" class="fav-btn" title="Wishlist" aria-label="Wishlist">♡</a>
    @endauth
  </div>
  <div class="product-card-body">
    <div class="product-card-brand">{{ $product->brand->name ?? '' }}</div>
    <a href="{{ url('/products/'.$product->slug) }}" class="product-card-name">{{ $product->name }}</a>
    <div class="rating-row">
      <x-stars :rating="$product->rating ?? 0" />
      <span>{{ number_format($product->rating ?? 0, 1) }}</span>
      <span>({{ $product->review_count ?? 0 }})</span>
    </div>
    <div class="price-row">
      <span class="price">${{ number_format($product->price, 2) }}</span>
      @if(($product->discount_percent ?? 0) > 0 && $product->original_price)
        <span class="price-old">${{ number_format($product->original_price, 2) }}</span>
      @endif
    </div>
    <div class="product-card-meta">{{ $soldLabel }}</div>
    <div class="product-card-seller">
      <span>{{ $product->seller->store_name ?? 'Seller' }}</span>
      @if($verified)<span class="verified-mark" title="Verified Seller">✓</span>@endif
    </div>
    <div class="product-card-meta">{{ $product->seller->location ?? '' }}</div>
    <div class="product-card-actions">
      <a href="{{ url('/products/'.$product->slug) }}" class="btn btn-outline btn-sm">View</a>
      @auth
        <form action="{{ url('/cart/add') }}" method="post" style="flex:1;min-width:0">
          @csrf
          <input type="hidden" name="product_id" value="{{ $product->id }}"/>
          <input type="hidden" name="redirect" value="{{ url()->current() }}"/>
          <button type="submit" class="btn btn-accent btn-sm btn-block">Add to Cart</button>
        </form>
      @else
        <a href="{{ url('/login') }}" class="btn btn-accent btn-sm" style="flex:1;text-align:center">Add to Cart</a>
      @endauth
    </div>
  </div>
</article>
