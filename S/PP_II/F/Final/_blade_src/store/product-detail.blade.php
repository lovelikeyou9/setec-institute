@extends('layouts.store')
@section('title', $product->name.' — MMT')
@section('content')
@php
  $gallery = $product->galleryList();
  if ($gallery === []) {
      $gallery = [default_product_image()];
  }
  $optionGroups = $product->selectableOptionGroups();
  $verified = in_array($product->seller->level ?? '', ['VERIFIED', 'TRUSTED', 'TOP'], true);
  $specRows = [];
  foreach (preg_split("/\r\n|\n|\r/", (string) ($product->specifications ?? '')) as $line) {
      $line = trim($line);
      if ($line === '') {
          continue;
      }
      $parts = array_map('trim', explode(':', $line, 2));
      if (count($parts) === 2 && $parts[0] !== '') {
          $specRows[] = ['key' => $parts[0], 'value' => $parts[1]];
      } elseif ($line !== '') {
          $specRows[] = ['key' => 'Spec', 'value' => $line];
      }
  }
@endphp
<div class="container">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <a href="{{ url('/products') }}">Products</a>
    @if($product->category)
      <span class="sep">/</span>
      <a href="{{ url('/products?category='.$product->category_id) }}">{{ $product->category->name }}</a>
    @endif
    <span class="sep">/</span>
    <span>{{ $product->name }}</span>
  </nav>

  <div class="detail-layout">
    <div class="gallery">
      <div class="gallery-main">
        <img src="{{ media_url($gallery[0]) }}" alt="{{ $product->name }}" id="mainImage"/>
      </div>
      @if(count($gallery) > 1)
        <div class="gallery-thumbs">
          @foreach($gallery as $i => $url)
            <button type="button" class="{{ $i === 0 ? 'active' : '' }}" data-src="{{ media_url($url) }}">
              <img src="{{ media_url($url) }}" alt="{{ $product->name }}"/>
            </button>
          @endforeach
        </div>
      @endif
    </div>

    <div class="detail-info">
      <div class="product-card-brand">{{ $product->brand->name ?? '' }}</div>
      <h1>{{ $product->name }}</h1>

      <div class="detail-stats">
        <div class="rating-row">
          <x-stars :rating="$product->rating ?? 0" />
          <span>{{ number_format($product->rating ?? 0, 1) }}</span>
          <span>({{ $product->review_count ?? 0 }} reviews)</span>
        </div>
        <span>Sold {{ $product->sold_count ?? 0 }}</span>
        @if(($product->stock ?? 0) > 0)
          <span class="status-pill green">In Stock</span>
        @else
          <span class="status-pill red">Out of Stock</span>
        @endif
        @if($product->condition_type)
          <span class="status-pill blue">{{ $product->condition_type }}</span>
        @endif
      </div>

      <div class="price-row" style="margin-bottom:1.25rem">
        <span class="price price-lg">${{ number_format($product->price, 2) }}</span>
        @if($product->original_price && $product->original_price > $product->price)
          <span class="price-old">${{ number_format($product->original_price, 2) }}</span>
        @endif
        @if(($product->discount_percent ?? 0) > 0)
          <span class="badge badge-sale">{{ $product->discount_percent }}% OFF</span>
        @endif
      </div>

      @foreach($optionGroups as $group)
        <div class="option-group">
          <label>{{ $group['label'] }}</label>
          <div class="option-pills">
            @foreach($group['choices'] as $i => $opt)
              <button type="button" class="{{ $i === 0 ? 'active' : '' }}">{{ $opt }}</button>
            @endforeach
          </div>
        </div>
      @endforeach

      <div class="option-group">
        <label>Quantity</label>
        <div class="qty-selector">
          <button type="button" data-qty="-">−</button>
          <input type="number" id="qty" name="quantity" value="1" min="1" max="{{ max(1, (int) $product->stock) }}"/>
          <button type="button" data-qty="+">+</button>
        </div>
        <span style="font-size:.85rem;color:var(--text-muted);margin-left:.75rem">{{ $product->stock }} available</span>
      </div>

      <div class="detail-actions">
        @auth
          <form action="{{ url('/cart/add') }}" method="post" id="addCartForm">
            @csrf
            <input type="hidden" name="product_id" value="{{ $product->id }}"/>
            <input type="hidden" name="quantity" id="cartQty" value="1"/>
            <input type="hidden" name="redirect" value="{{ url('/products/'.$product->slug) }}"/>
            <button type="submit" class="btn btn-accent" onclick="document.getElementById('cartQty').value=document.getElementById('qty').value">Add to Cart</button>
          </form>
          <form action="{{ url('/cart/add') }}" method="post">
            @csrf
            <input type="hidden" name="product_id" value="{{ $product->id }}"/>
            <input type="hidden" name="quantity" id="buyQty" value="1"/>
            <input type="hidden" name="redirect" value="{{ url('/checkout') }}"/>
            <button type="submit" class="btn btn-primary" onclick="document.getElementById('buyQty').value=document.getElementById('qty').value">Buy Now</button>
          </form>
          @php $inWishlist = in_array((int) $product->id, $wishlistProductIds ?? [], true); @endphp
          <form action="{{ url('/wishlist/toggle') }}" method="post">
            @csrf
            <input type="hidden" name="product_id" value="{{ $product->id }}"/>
            <input type="hidden" name="redirect" value="{{ url('/products/'.$product->slug) }}"/>
            <button type="submit" class="btn {{ $inWishlist ? 'btn-wish-active' : 'btn-outline' }}">
              {{ $inWishlist ? '♥ In Wishlist' : '♡ Wishlist' }}
            </button>
          </form>
        @else
          <a href="{{ url('/login') }}" class="btn btn-accent">Login to Buy</a>
        @endauth
      </div>

      @if($product->seller)
        <div class="seller-box">
          <img src="{{ $product->seller->logo_url ?: asset('images/mme-icon.png') }}" alt="{{ $product->seller->store_name }}"/>
          <div>
            <strong>{{ $product->seller->store_name }}</strong>
            @if($verified)<span class="badge badge-verified">✓ Verified</span>@endif
            <div class="rating-row" style="margin-top:.25rem">
              <x-stars :rating="$product->seller->rating ?? 0" />
              <span>{{ number_format($product->seller->rating ?? 0, 1) }}</span>
              <span>· {{ $product->seller->location }}</span>
            </div>
          </div>
          <div class="seller-box-actions">
            <a href="{{ url('/sellers/'.$product->seller_id) }}" class="btn btn-outline btn-sm">View Profile</a>
            @auth
              <form action="{{ url('/sellers/'.$product->seller_id.'/follow') }}" method="post">
                @csrf
                <button type="submit" class="btn btn-ghost btn-sm">{{ $following ? 'Unfollow' : 'Follow' }}</button>
              </form>
            @endauth
          </div>
        </div>
      @endif
    </div>
  </div>

  <div class="product-detail-blocks">
    <section class="detail-block" id="product-description">
      <h2 class="detail-block-title">Description</h2>
      <p class="detail-block-text">{{ $product->description ?: 'No description provided.' }}</p>
    </section>

    <section class="detail-block" id="product-specifications">
      <h2 class="detail-block-title">Specifications</h2>
      @if(count($specRows))
        <table class="spec-table">
          @foreach($specRows as $row)
            <tr>
              <td>{{ $row['key'] }}</td>
              <td>{{ $row['value'] }}</td>
            </tr>
          @endforeach
        </table>
      @else
        <p class="detail-block-text">No specifications listed.</p>
      @endif
    </section>
  </div>

  <div class="tabs">
    <div class="tab-nav">
      <button type="button" class="active" data-tab="tab-reviews">Reviews</button>
      <button type="button" data-tab="tab-seller">Seller Info</button>
      <button type="button" data-tab="tab-shipping">Shipping</button>
      <button type="button" data-tab="tab-warranty">Warranty</button>
    </div>

    <div class="tab-panel active" id="tab-reviews">
      @forelse($reviews as $r)
        <div class="review-card">
          <div class="review-header">
            <span class="review-avatar">{{ mb_substr($r->user->full_name ?? 'U', 0, 1) }}</span>
            <div>
              <strong>{{ $r->user->full_name ?? 'Buyer' }}</strong>
              @if($r->verified_purchase)<span class="badge badge-verified" style="margin-left:.35rem">Verified Purchase ✓</span>@endif
              <div class="rating-row">
                <x-stars :rating="$r->rating" />
                <span>{{ $r->rating }}</span>
              </div>
            </div>
          </div>
          <p style="color:var(--text-secondary)">{{ $r->comment }}</p>
          @if($r->seller_response)
            <div class="seller-reply">
              <strong>Seller response:</strong>
              <p style="margin-top:.35rem">{{ $r->seller_response }}</p>
            </div>
          @endif
        </div>
      @empty
        <div class="empty-state">
          <div class="icon">⭐</div>
          <h3>No reviews yet</h3>
          <p>Be the first to review this product after purchase.</p>
        </div>
      @endforelse

      @auth
        <form method="post" action="{{ url('/products/'.$product->slug.'/review') }}" class="form-card" style="margin-top:1rem">
          @csrf
          <h3 style="margin-bottom:1rem">Write a review</h3>
          <div class="form-group">
            <label>Rating</label>
            <select name="rating">
              @for($i=5;$i>=1;$i--)<option value="{{ $i }}">{{ $i }} stars</option>@endfor
            </select>
          </div>
          <div class="form-group">
            <label>Comment</label>
            <textarea name="comment" rows="3"></textarea>
          </div>
          <button class="btn btn-primary" type="submit">Submit review</button>
        </form>
      @endauth
    </div>

    <div class="tab-panel" id="tab-seller">
      @if($product->seller)
        <div class="seller-box" style="background:transparent;border:none;padding:0">
          <img src="{{ $product->seller->logo_url ?: asset('images/mme-icon.png') }}" alt="{{ $product->seller->store_name }}" style="width:64px;height:64px;border-radius:12px"/>
          <div>
            <h3>{{ $product->seller->store_name }}</h3>
            <p style="color:var(--text-secondary);margin:.5rem 0;max-width:520px">{{ $product->seller->description }}</p>
            <div class="seller-stats">
              <span>Rating {{ number_format($product->seller->rating ?? 0, 1) }}</span> ·
              <span>{{ $product->seller->total_products }} products</span> ·
              <span>{{ $product->seller->followers ?? 0 }} followers</span>
            </div>
            <a href="{{ url('/sellers/'.$product->seller_id) }}" class="btn btn-primary btn-sm" style="margin-top:.75rem">View Seller Profile</a>
          </div>
        </div>
      @endif
    </div>

    <div class="tab-panel" id="tab-shipping">
      <p style="line-height:1.7;color:var(--text-secondary)">
        Standard delivery across Cambodia in 2–4 business days ($2.00).<br/>
        Express delivery available in Phnom Penh and major cities (1 business day, $5.00).<br/>
        Seller ships from <strong>{{ $product->seller->location ?? 'Cambodia' }}</strong>.
      </p>
    </div>

    <div class="tab-panel" id="tab-warranty">
      <p style="line-height:1.7;color:var(--text-secondary)">
        This product includes {{ $product->warranty ?: 'manufacturer' }} warranty covered by {{ $product->seller->store_name ?? 'the seller' }}.
      </p>
    </div>
  </div>

  @if($related->count())
    <section class="section">
      <div class="section-head">
        <h2 class="section-title">Related Products</h2>
      </div>
      <div class="product-grid">
        @foreach($related as $p)
          <x-product-card :product="$p"/>
        @endforeach
      </div>
    </section>
  @endif
</div>
@endsection
