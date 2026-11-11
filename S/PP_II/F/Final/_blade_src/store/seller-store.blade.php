@extends('layouts.store')
@section('title', $seller->store_name.' — MMT')
@section('content')
@php $verified = in_array($seller->level ?? '', ['VERIFIED', 'TRUSTED', 'TOP'], true); @endphp
<div class="container" style="padding-top:1.5rem">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <a href="{{ url('/sellers') }}">Sellers</a>
    <span class="sep">/</span>
    <span>{{ $seller->store_name }}</span>
  </nav>

  <div class="store-banner"
       style="background-image:url({{ $seller->banner_url ?: 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=1200&h=400&fit=crop' }})">
  </div>

  <div class="store-header">
    <img src="{{ $seller->logo_url ?: asset('images/mme-icon.png') }}" alt="{{ $seller->store_name }}"/>
    <div style="flex:1">
      <h1 style="font-size:1.5rem;display:flex;align-items:center;gap:.5rem;flex-wrap:wrap">
        <span>{{ $seller->store_name }}</span>
        @if($verified)<span class="badge badge-verified">✓ Verified</span>@endif
        @if(($seller->level ?? '') === 'TOP')<span class="badge badge-top">Top Seller</span>@endif
      </h1>
      <p style="color:var(--text-secondary);margin-top:.35rem;max-width:560px">{{ $seller->description }}</p>
      <div class="seller-stats" style="margin-top:.5rem">
        <span>{{ number_format($seller->rating ?? 0, 1) }} &#9733;</span> ·
        <span>{{ $seller->total_products }} products</span> ·
        <span>{{ $seller->total_sales ?? 0 }} sales</span> ·
        <span>{{ $seller->followers ?? 0 }} followers</span> ·
        <span>{{ $seller->location ?: 'Cambodia' }}</span>
      </div>
    </div>
    <div style="align-self:flex-start">
      @auth
        <form action="{{ url('/sellers/'.$seller->id.'/follow') }}" method="post">
          @csrf
          <button type="submit" class="btn {{ $following ? 'btn-outline' : 'btn-accent' }} btn-sm">
            {{ $following ? 'Unfollow' : 'Follow' }}
          </button>
        </form>
      @else
        <a href="{{ url('/login') }}" class="btn btn-accent btn-sm">Follow</a>
      @endauth
    </div>
  </div>

  <nav class="store-nav">
    <a href="#products" class="active">Products</a>
    <a href="#about">About</a>
    <a href="{{ url('/products?q='.urlencode($seller->store_name)) }}">All Items</a>
  </nav>

  <section id="products" style="padding:1.5rem 0">
    @if($products->isEmpty())
      <div class="empty-state">
        <div class="icon">📦</div>
        <h3>No products listed yet</h3>
        <p>This seller hasn't published any products.</p>
      </div>
    @else
      <div class="product-grid">
        @foreach($products as $product)
          <x-product-card :product="$product"/>
        @endforeach
      </div>
      <div class="pagination" style="margin-top:1.5rem">{{ $products->links() }}</div>
    @endif
  </section>

  <section id="about" class="form-card" style="margin-bottom:2rem">
    <h3>About the Store</h3>
    <p style="color:var(--text-secondary);line-height:1.7">{{ $seller->description }}</p>
    <p style="margin-top:.75rem;font-size:.9rem;color:var(--text-muted)">
      Location: <span>{{ $seller->location ?: '—' }}</span> ·
      Joined: <span>{{ optional($seller->joined_at)->format('M Y') ?: '—' }}</span>
    </p>
  </section>
</div>
@endsection
