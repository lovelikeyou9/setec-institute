@extends('layouts.store')
@section('title', 'Products — MMT')
@section('mainClass', 'products-page')
@section('content')
@php
  $selectedBrandId = request('brand');
  $q = request('q');
@endphp
<div class="container">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    @if($selectedCategory)
      <a href="{{ url('/products') }}">Products</a>
      <span class="sep">/</span>
      <span>{{ $selectedCategory->name }}</span>
    @else
      <span>Products</span>
    @endif
  </nav>

  <div class="products-hero">
    <div>
      <h1 class="products-title">{{ $selectedCategory->name ?? 'All Technology' }}</h1>
      <p class="products-sub">Computers, networking, gaming, cameras, power, DIY &amp; pro IT from verified sellers</p>
    </div>
    <button type="button" class="btn btn-outline btn-sm products-filter-toggle" id="productsFilterToggle" aria-expanded="false">Filters</button>
  </div>

  @if(!empty($navMenuGroups) && count($navMenuGroups))
    <div class="products-chips">
      <a href="{{ url('/products') }}" class="products-chip {{ !$selectedCategory ? 'active' : '' }}">All</a>
      @foreach($navMenuGroups as $g)
        @php
          $firstId = $g['items'][0]['id'] ?? null;
          $groupIds = collect($g['items'])->pluck('id');
          $activeGroup = $selectedCategory && $groupIds->contains($selectedCategory->id);
        @endphp
        <a href="{{ url('/products?category='.$firstId) }}" class="products-chip {{ $activeGroup ? 'active' : '' }}">
          <span>{{ $g['icon'] }}</span>
          <span>{{ $g['title'] }}</span>
        </a>
      @endforeach
    </div>
  @endif

  <div class="list-layout">
    <aside class="filter-panel" id="productsFilterPanel">
      <div class="filter-panel-head">
        <h3>Filters</h3>
        <button type="button" class="filter-panel-close" id="productsFilterClose" aria-label="Close filters">×</button>
      </div>
      <form action="{{ url('/products') }}" method="get" id="filterForm">
        <input type="hidden" name="q" value="{{ $q }}"/>

        <div class="filter-group">
          <h4>Category</h4>
          <div class="filter-group-scroll">
            @if(!empty($navMenuGroups) && count($navMenuGroups))
              @foreach($navMenuGroups as $gi => $g)
                @php
                  $groupIds = collect($g['items'])->pluck('id');
                  $isActive = ($selectedCategory && $groupIds->contains($selectedCategory->id)) || (!$selectedCategory && $gi === 0);
                @endphp
                <details class="filter-cat-group" @if($isActive) open @endif>
                  <summary class="filter-cat-title">
                    <span>{{ $g['icon'] }}</span>
                    <span>{{ $g['title'] }}</span>
                  </summary>
                  @foreach($g['items'] as $item)
                    @php $c = $categories->firstWhere('id', $item['id']); @endphp
                    @if($c)
                      <label>
                        <input type="radio" name="category" value="{{ $c->id }}" @checked($selectedCategory && $selectedCategory->id == $c->id)/>
                        <span>{{ $c->name }}</span>
                      </label>
                    @endif
                  @endforeach
                </details>
              @endforeach
            @else
              @foreach($categories as $c)
                <label>
                  <input type="radio" name="category" value="{{ $c->id }}" @checked($selectedCategory && $selectedCategory->id == $c->id)/>
                  <span>{{ $c->name }}</span>
                </label>
              @endforeach
            @endif
          </div>
        </div>

        <div class="filter-group">
          <h4>Brand</h4>
          <div class="filter-group-scroll filter-group-scroll-sm">
            @foreach($brands as $b)
              <label>
                <input type="radio" name="brand" value="{{ $b->id }}" @checked($selectedBrandId == $b->id)/>
                <span>{{ $b->name }}</span>
              </label>
            @endforeach
          </div>
        </div>

        <div class="filter-group">
          <h4>Price Range</h4>
          <div class="price-inputs">
            <input type="number" name="minPrice" placeholder="Min" min="0" step="1" value="{{ request('minPrice') }}"/>
            <span>–</span>
            <input type="number" name="maxPrice" placeholder="Max" min="0" step="1" value="{{ request('maxPrice') }}"/>
          </div>
          <label style="margin-top:.5rem"><input type="radio" name="pricePreset" value="0-100" onclick="document.querySelector('[name=minPrice]').value=0;document.querySelector('[name=maxPrice]').value=100"/> Under $100</label>
          <label><input type="radio" name="pricePreset" value="100-500" onclick="document.querySelector('[name=minPrice]').value=100;document.querySelector('[name=maxPrice]').value=500"/> $100 – $500</label>
          <label><input type="radio" name="pricePreset" value="500-1000" onclick="document.querySelector('[name=minPrice]').value=500;document.querySelector('[name=maxPrice]').value=1000"/> $500 – $1,000</label>
          <label><input type="radio" name="pricePreset" value="1000-" onclick="document.querySelector('[name=minPrice]').value=1000;document.querySelector('[name=maxPrice]').value=''"/> $1,000+</label>
        </div>

        <div class="filter-group">
          <h4>Seller &amp; Stock</h4>
          <label>
            <input type="checkbox" name="inStock" value="1" @checked(request()->boolean('inStock'))/>
            In Stock
          </label>
        </div>

        <input type="hidden" name="sort" value="{{ $sort ?? 'recommended' }}"/>
        <div class="filter-actions">
          <button type="submit" class="btn btn-primary btn-block">Apply Filters</button>
          <a href="{{ url('/products') }}" class="btn btn-outline btn-block">Clear All</a>
        </div>
      </form>
    </aside>

    <div class="products-main">
      <div class="list-toolbar">
        <div class="list-toolbar-count">
          <strong>{{ $products->total() }}</strong>
          <span>{{ $products->total() === 1 ? ' result' : ' results' }}</span>
          @if($q)<span> for "{{ $q }}"</span>@endif
        </div>
        <form action="{{ url('/products') }}" method="get" class="list-sort-form">
          @if($q)<input type="hidden" name="q" value="{{ $q }}"/>@endif
          @if($selectedCategory)<input type="hidden" name="category" value="{{ $selectedCategory->id }}"/>@endif
          @if($selectedBrandId)<input type="hidden" name="brand" value="{{ $selectedBrandId }}"/>@endif
          @if(request('minPrice') !== null && request('minPrice') !== '')<input type="hidden" name="minPrice" value="{{ request('minPrice') }}"/>@endif
          @if(request('maxPrice') !== null && request('maxPrice') !== '')<input type="hidden" name="maxPrice" value="{{ request('maxPrice') }}"/>@endif
          @if(request()->boolean('inStock'))<input type="hidden" name="inStock" value="1"/>@endif
          <label for="sortSelect">Sort by</label>
          <select name="sort" id="sortSelect" onchange="this.form.submit()">
            <option value="recommended" @selected(($sort ?? '') === 'recommended')>Recommended</option>
            <option value="sold" @selected(($sort ?? '') === 'sold')>Most Popular</option>
            <option value="newest" @selected(($sort ?? '') === 'newest')>Newest</option>
            <option value="price_asc" @selected(($sort ?? '') === 'price_asc')>Price: Low to High</option>
            <option value="price_desc" @selected(($sort ?? '') === 'price_desc')>Price: High to Low</option>
            <option value="rating" @selected(($sort ?? '') === 'rating')>Top Rated</option>
          </select>
        </form>
      </div>

      @if($products->total() === 0)
        <div class="empty-state">
          <div class="icon">🖥️</div>
          <h3>No products found</h3>
          <p>Try adjusting your filters or search another technology category.</p>
          <a href="{{ url('/products') }}" class="btn btn-primary" style="margin-top:1rem">Browse All Products</a>
        </div>
      @else
        <div class="product-grid">
          @foreach($products as $product)
            <x-product-card :product="$product"/>
          @endforeach
        </div>
        <div class="pagination" style="margin-top:1.5rem">{{ $products->links() }}</div>
      @endif
    </div>
  </div>
  <div class="filter-backdrop" id="productsFilterBackdrop" hidden></div>
</div>
@endsection
