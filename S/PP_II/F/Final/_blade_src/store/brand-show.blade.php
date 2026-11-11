@extends('layouts.store')
@section('title', $brand->name.' — MMT')
@section('content')
<div class="container" style="padding-top:1.5rem">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <a href="{{ url('/brands') }}">Brands</a>
    <span class="sep">/</span>
    <span>{{ $brand->name }}</span>
  </nav>
  <div class="section-head">
    <div>
      <h1 class="section-title">{{ $brand->name }}</h1>
      <p class="section-sub">{{ $brand->description ?: 'Products from '.$brand->name.' on MMT' }}</p>
    </div>
    <a href="{{ url('/products?brand='.$brand->id) }}">View in catalog →</a>
  </div>
  @if($products->isEmpty())
    <div class="empty-state">
      <div class="icon">🏷️</div>
      <h3>No products yet</h3>
      <p>Check back soon for {{ $brand->name }} listings.</p>
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
@endsection
