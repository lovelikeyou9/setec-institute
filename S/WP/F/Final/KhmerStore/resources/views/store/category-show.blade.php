@extends('layouts.store')
@section('title', $category->name.' — MMT')
@section('content')
<div class="container" style="padding-top:1.5rem">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <a href="{{ url('/categories') }}">Categories</a>
    <span class="sep">/</span>
    <span>{{ $category->name }}</span>
  </nav>
  <div class="section-head">
    <div>
      <h1 class="section-title">{{ $category->name }}</h1>
      <p class="section-sub">{{ $category->description ?: 'Shop '.$category->name.' on MMT' }}</p>
    </div>
    <a href="{{ url('/products?category='.$category->id) }}">Open with filters →</a>
  </div>
  @if($products->isEmpty())
    <div class="empty-state">
      <div class="icon">📦</div>
      <h3>No products in this category</h3>
      <a href="{{ url('/products') }}" class="btn btn-primary" style="margin-top:1rem">Browse all</a>
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
