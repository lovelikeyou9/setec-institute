@extends('layouts.account')
@section('title', 'Wishlist — MMT')
@section('content')
<h1 style="font-size:1.4rem;margin-bottom:.35rem">My Wishlist</h1>
<p class="section-sub">{{ $items->count() }} saved items</p>

@if($items->isEmpty())
  <div class="empty-state">
    <div class="icon">♡</div>
    <h3>Your wishlist is empty</h3>
    <p>Save products you love and come back when you're ready to buy.</p>
    <a href="{{ url('/products') }}" class="btn btn-primary" style="margin-top:1rem">Browse Products</a>
  </div>
@else
  <div class="product-grid">
    @foreach($items as $item)
      @if($item->product)
        <x-product-card :product="$item->product" :wishlisted="true"/>
      @endif
    @endforeach
  </div>
@endif
@endsection
