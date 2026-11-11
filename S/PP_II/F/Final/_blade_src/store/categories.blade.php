@extends('layouts.store')
@section('title', 'Categories — MMT')
@section('content')
<div class="container" style="padding-top:1.5rem">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <span>Categories</span>
  </nav>
  <div class="section-head">
    <div>
      <h1 class="section-title">Shop by Category</h1>
      <p class="section-sub">Computers, networking, gaming, cameras, power, DIY, and pro IT gear</p>
    </div>
    <a href="{{ url('/products') }}">Browse all products →</a>
  </div>

  @if(!empty($navMenuGroups) && count($navMenuGroups))
    <div class="cat-groups">
      @foreach($navMenuGroups as $g)
        <section class="cat-group">
          <div class="cat-group-head">
            <span class="cat-group-icon">{{ $g['icon'] }}</span>
            <h2>{{ $g['title'] }}</h2>
          </div>
          <div class="cat-grid cat-grid-media">
            @foreach($g['items'] as $item)
              <a href="{{ url('/products?category='.$item['id']) }}" class="cat-card cat-card-media">
                @if(!empty($item['image_url']))
                  <div class="cat-thumb"><img src="{{ $item['image_url'] }}" alt="{{ $item['name'] }}" loading="lazy"/></div>
                @else
                  <div class="cat-icon">{{ $item['icon'] ?? $g['icon'] }}</div>
                @endif
                <span>{{ $item['name'] }}</span>
              </a>
            @endforeach
          </div>
        </section>
      @endforeach
    </div>
  @else
    <div class="cat-grid">
      @foreach($categories as $c)
        <a href="{{ url('/products?category='.$c->id) }}" class="cat-card cat-card-media">
          @if($c->image_url)
            <div class="cat-thumb"><img src="{{ $c->image_url }}" alt="{{ $c->name }}" loading="lazy"/></div>
          @else
            <div class="cat-icon">{{ $c->icon ?: '💻' }}</div>
          @endif
          <span>{{ $c->name }}</span>
        </a>
      @endforeach
    </div>
  @endif
</div>
@endsection
