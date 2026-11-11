@extends('layouts.store')
@section('title', 'MMT - Modern Multiple Technology')

@section('content')
  {{-- Hero --}}
  <section class="hero">
    <div class="hero-inner">
      <div class="hero-brand">
        <img class="hero-logo-icon" src="{{ asset('images/mme-icon.png') }}" alt="" width="56" height="56"/>
        <span class="hero-brand-text">MMT</span>
      </div>
      <p class="hero-tagline">Modern Multiple Technology</p>
      <h1>One marketplace. Every kind of tech.</h1>
      <p>Computers, networking, gaming, cameras, drones, power, DIY electronics, and pro IT - from verified sellers.</p>
      <div class="hero-ctas">
        <a href="{{ url('/products') }}" class="btn btn-accent btn-lg">Shop Technology</a>
        <a href="{{ url('/categories') }}" class="btn btn-outline btn-lg">Browse Categories</a>
      </div>
    </div>
  </section>

  {{-- Technology pillars --}}
  @if(!empty($navMenuGroups) && count($navMenuGroups))
  <section class="tech-pillars" aria-label="Technology categories">
    <div class="container tech-pillars-track">
      @foreach($navMenuGroups as $g)
        <a href="{{ url('/products?category='.$g['items'][0]['id']) }}" class="tech-pillar">
          <span class="tech-pillar-icon">{{ $g['icon'] }}</span>
          <span class="tech-pillar-copy">
            <strong>{{ $g['title'] }}</strong>
            <small>{{ count($g['items']) }} categories</small>
          </span>
        </a>
      @endforeach
    </div>
  </section>
  @endif

  {{-- Promo peek carousel --}}
  @if(!empty($promoSlides) && count($promoSlides))
  <section class="promo-carousel section" aria-label="Featured categories">
    <div class="promo-viewport" id="promoCarousel">
      <div class="promo-track" id="promoTrack">
        @foreach($promoSlides as $i => $slide)
          <article class="promo-slide {{ $i === 0 ? 'active' : '' }}"
                   style="background-image:linear-gradient(105deg,rgba(10,12,16,.72) 0%,rgba(10,12,16,.28) 55%,rgba(10,12,16,.15) 100%),url('{{ $slide['image'] }}')">
            <div class="promo-slide-copy">
              <h2>{{ $slide['title'] }}</h2>
              <p>{{ $slide['subtitle'] }}</p>
              <a class="promo-cta" href="{{ $slide['href'] }}">{{ $slide['cta'] }}</a>
            </div>
          </article>
        @endforeach
      </div>
      <button type="button" class="promo-nav prev" id="promoPrev" aria-label="Previous slide">&lsaquo;</button>
      <button type="button" class="promo-nav next" id="promoNext" aria-label="Next slide">&rsaquo;</button>
    </div>
  </section>
  @endif

  {{-- Brands marquee --}}
  @if($brands->count())
  <section class="brand-marquee" aria-label="Shop by brand">
    <div class="brand-track">
      @foreach([1, 2] as $pass)
        @foreach($brands as $b)
          <a href="{{ url('/products?brand='.$b->id) }}" class="brand-logo" data-brand="{{ $b->slug }}" title="{{ $b->name }}">
            @if($b->logo_url)
              <img src="{{ $b->logo_url }}" alt="{{ $b->name }}"/>
            @else
              <span>{{ $b->name }}</span>
            @endif
          </a>
        @endforeach
      @endforeach
    </div>
  </section>
  @endif

  {{-- Categories --}}
  <section class="section">
    <div class="container">
      <div class="section-head">
        <div>
          <h2 class="section-title">Shop by Category</h2>
          <p class="section-sub">Modern Multiple Technology - explore every product line</p>
        </div>
        <a href="{{ url('/categories') }}">View all &rarr;</a>
      </div>
      <div class="cat-grid cat-grid-media">
        @foreach($categories->take(12) as $c)
          <a href="{{ url('/products?category='.$c->id) }}" class="cat-card cat-card-media">
            @if($c->image_url)
              <div class="cat-thumb"><img src="{{ media_url($c->image_url) }}" alt="{{ $c->name }}" loading="lazy"/></div>
            @else
              <div class="cat-icon">{{ $c->icon ?: '??' }}</div>
            @endif
            <span>{{ $c->name }}</span>
          </a>
        @endforeach
      </div>
    </div>
  </section>

  {{-- Category showcases --}}
  @if(!empty($showcases) && count($showcases))
  <section class="section" style="padding-top:0">
    <div class="container">
      <div class="showcase-grid">
        @foreach($showcases as $sc)
          <div class="showcase-panel">
            <a class="showcase-banner theme-{{ $sc['theme'] }}" href="{{ url('/products?category='.$sc['category']->id) }}">
              <div class="showcase-banner-copy">
                <h3>{{ $sc['banner_title'] }}</h3>
                <p>PRICE FROM: ${{ number_format($sc['price_from'], 0) }}</p>
                <span class="showcase-shop">Shop Now</span>
              </div>
              <img class="showcase-banner-img" src="{{ $sc['banner_image'] }}" alt="{{ $sc['banner_title'] }}" loading="lazy"/>
            </a>
            <div class="showcase-products">
              @foreach($sc['products'] as $product)
                <a class="showcase-item" href="{{ url('/products/'.$product->slug) }}">
                  @if(($product->discount_percent ?? 0) > 0)
                    <span class="showcase-sale">-{{ $product->discount_percent }}%</span>
                  @endif
                  <div class="showcase-item-img">
                    <img src="{{ media_url($product->image_url) }}" alt="{{ $product->name }}" loading="lazy"/>
                  </div>
                  <h4>{{ $product->name }}</h4>
                  <div class="showcase-stars" aria-label="Rating {{ $product->rating }}">
                    <x-stars :rating="$product->rating ?? 0" />
                  </div>
                  <div class="showcase-price">
                    <span class="now {{ ($product->discount_percent ?? 0) > 0 ? 'sale' : '' }}">${{ number_format($product->price, 2) }}</span>
                    @if(($product->discount_percent ?? 0) > 0 && $product->original_price)
                      <span class="was">${{ number_format($product->original_price, 2) }}</span>
                    @endif
                  </div>
                </a>
              @endforeach
            </div>
          </div>
        @endforeach
      </div>
    </div>
  </section>
  @endif

  {{-- Sale Off + Flash Deals --}}
  @if($flashDeals->count())
  @php
    $saleItems = $flashDeals->take(5);
    $first = $saleItems->first();
  @endphp
  <section class="section" style="padding-top:0">
    <div class="container">
      <div class="sale-off" id="saleOff" aria-label="Sale off deals">
        <div class="sale-off-thumbs" role="tablist">
          @foreach($saleItems as $st => $product)
            <button type="button" class="sale-off-thumb {{ $st === 0 ? 'active' : '' }}"
                    data-index="{{ $st }}"
                    data-id="{{ $product->id }}"
                    data-name="{{ $product->name }}"
                    data-slug="{{ $product->slug }}"
                    data-image="{{ media_url($product->image_url) }}"
                    data-price="{{ number_format($product->price, 2, '.', '') }}"
                    data-original="{{ $product->original_price ? number_format($product->original_price, 2, '.', '') : '' }}"
                    data-discount="{{ $product->discount_percent ?? 0 }}"
                    data-rating="{{ $product->rating ?? 0 }}"
                    data-ends="{{ optional($product->flash_deal_ends_at)->format('Y-m-d\TH:i:s') ?: '2026-12-31T23:59:59' }}"
                    role="tab"
                    aria-selected="{{ $st === 0 ? 'true' : 'false' }}"
                    aria-label="{{ $product->name }}">
              <img src="{{ media_url($product->image_url) }}" alt="{{ $product->name }}" loading="lazy"/>
            </button>
          @endforeach
        </div>

        <div class="sale-off-info">
          <div class="sale-off-watermark" aria-hidden="true">SALE OFF</div>
          <h3 class="sale-off-title" id="saleOffTitle">{{ $first->name }}</h3>
          <div class="sale-off-stars" id="saleOffStars">
            <x-stars :rating="$first->rating ?? 0" />
          </div>
          <div class="sale-off-prices">
            <span class="sale-off-now" id="saleOffPrice">${{ number_format($first->price, 2) }}</span>
            <span class="sale-off-was" id="saleOffOriginal"
                  style="{{ ($first->original_price && ($first->discount_percent ?? 0) > 0) ? '' : 'display:none' }}">
              ${{ $first->original_price ? number_format($first->original_price, 2) : '' }}
            </span>
          </div>
          <div class="sale-off-countdown" id="saleOffCountdown"
               data-countdown="{{ optional($first->flash_deal_ends_at)->format('Y-m-d\TH:i:s') ?: '2026-12-31T23:59:59' }}">
            <div class="sale-off-unit"><strong data-d>00</strong><span>Days</span></div>
            <div class="sale-off-unit"><strong data-h>00</strong><span>Hours</span></div>
            <div class="sale-off-unit"><strong data-m>00</strong><span>Mins</span></div>
            <div class="sale-off-unit"><strong data-s>00</strong><span>Secs</span></div>
          </div>
          <div class="sale-off-actions">
            @auth
              <form action="{{ url('/wishlist/toggle') }}" method="post" id="saleOffWishForm">
                @csrf
                <input type="hidden" name="product_id" id="saleOffWishId" value="{{ $first->id }}"/>
                <input type="hidden" name="redirect" value="/"/>
                <button type="submit" class="sale-off-icon-btn" title="Wishlist" aria-label="Add to wishlist">&hearts;</button>
              </form>
              <form action="{{ url('/cart/add') }}" method="post" id="saleOffCartForm" class="sale-off-quick-form">
                @csrf
                <input type="hidden" name="product_id" id="saleOffCartId" value="{{ $first->id }}"/>
                <input type="hidden" name="redirect" value="/"/>
                <button type="submit" class="sale-off-quick">Quick Add</button>
              </form>
            @else
              <a href="{{ url('/login') }}" class="sale-off-icon-btn" title="Wishlist" aria-label="Login to wishlist">&hearts;</a>
              <a href="{{ url('/login') }}" class="sale-off-quick">Quick Add</a>
            @endauth
            <a class="sale-off-icon-btn" id="saleOffView" href="{{ url('/products/'.$first->slug) }}" title="View product" aria-label="View product">&#8981;</a>
          </div>
        </div>

        <div class="sale-off-visual">
          <span class="sale-off-badge" id="saleOffBadge"
                style="{{ ($first->discount_percent ?? 0) > 0 ? '' : 'display:none' }}">
            -{{ $first->discount_percent }}%
          </span>
          <button type="button" class="sale-off-nav prev" id="saleOffPrev" aria-label="Previous deal">&lsaquo;</button>
          <img id="saleOffImage" src="{{ media_url($first->image_url) }}" alt="{{ $first->name }}"/>
          <button type="button" class="sale-off-nav next" id="saleOffNext" aria-label="Next deal">&rsaquo;</button>
        </div>
      </div>

      <div class="section-head" style="margin-top:1.75rem">
        <div>
          <h2 class="section-title">Flash Deals</h2>
          <p class="section-sub">Limited-time discounts from verified sellers</p>
        </div>
        <a href="{{ url('/deals') }}">See all &rarr;</a>
      </div>
      <div class="product-grid">
        @foreach($flashDeals as $product)
          <x-product-card :product="$product" />
        @endforeach
      </div>
    </div>
  </section>
  @endif

  {{-- Featured --}}
  <section class="section">
    <div class="container">
      <div class="section-head">
        <div>
          <h2 class="section-title">Featured Technology</h2>
          <p class="section-sub">Standout picks across computers, gaming, networking &amp; more</p>
        </div>
        <a href="{{ url('/products') }}">See all &rarr;</a>
      </div>
      <div class="product-grid">
        @foreach($featured as $product)
          <x-product-card :product="$product" />
        @endforeach
      </div>
    </div>
  </section>

  {{-- Sellers --}}
  <section class="section" style="padding-top:0">
    <div class="container">
      <div class="section-head">
        <div>
          <h2 class="section-title">Trusted Sellers</h2>
          <p class="section-sub">Verified stores for every kind of modern technology</p>
        </div>
        <a href="{{ url('/sellers') }}">All sellers &rarr;</a>
      </div>
      <div class="seller-grid">
        @foreach($sellers as $s)
          @php $verified = in_array($s->level, ['VERIFIED', 'TRUSTED', 'TOP'], true); @endphp
          <a href="{{ url('/sellers/'.$s->id) }}" class="seller-card">
            <img src="{{ $s->logo_url ?: asset('images/mme-icon.png') }}" alt="{{ $s->store_name }}" class="seller-logo"/>
            <div class="seller-info">
              <h3>
                <span>{{ $s->store_name }}</span>
                @if($verified)<span class="badge badge-verified">Verified</span>@endif
                @if($s->level === 'TOP')<span class="badge badge-top">Top Seller</span>@endif
              </h3>
              <div class="rating-row">
                <x-stars :rating="$s->rating ?? 0" />
                <span>{{ number_format($s->rating ?? 0, 1) }}</span>
              </div>
              <div class="seller-stats">
                <span>{{ $s->total_products }} products</span> ·
                <span>{{ $s->location }}</span>
              </div>
            </div>
          </a>
        @endforeach
      </div>
    </div>
  </section>

  {{-- CTA Become Seller --}}
  <section class="section">
    <div class="container">
      <div class="home-seller-cta">
        <div class="home-seller-cta-copy">
          <h2>Start Selling on MMT</h2>
          <p>List computers, networking gear, cameras, gaming, DIY electronics, and more - reach tech buyers across Cambodia.</p>
        </div>
        <a href="{{ url('/become-seller') }}" class="btn btn-lg home-seller-cta-btn">Become a Seller</a>
      </div>
    </div>
  </section>
@endsection
