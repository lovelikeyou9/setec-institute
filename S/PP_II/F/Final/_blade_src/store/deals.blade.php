@extends('layouts.store')
@section('title', 'Flash Deals — MMT')
@section('content')
<div class="container" style="padding-top:1.5rem">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <span>Flash Deals</span>
  </nav>

  <div class="deal-banner">
    <div>
      <h1>⚡ Flash Deals</h1>
      <p style="opacity:.8;font-size:.9rem;margin-top:.25rem">Limited-time deals across computers, networking, gaming, cameras &amp; more</p>
    </div>
    <div class="countdown" data-countdown="2026-12-31T23:59:59">
      <div class="countdown-unit"><strong data-h>00</strong><span>Hours</span></div>
      <div class="countdown-unit"><strong data-m>00</strong><span>Mins</span></div>
      <div class="countdown-unit"><strong data-s>00</strong><span>Secs</span></div>
    </div>
  </div>

  @if($flashDeals->isEmpty())
    <div class="empty-state">
      <div class="icon">🏷️</div>
      <h3>No flash deals right now</h3>
      <p>Check back soon — sellers drop limited-time offers throughout the week.</p>
      <a href="{{ url('/products') }}" class="btn btn-primary" style="margin-top:1rem">Browse Products</a>
    </div>
  @else
    <div class="product-grid">
      @foreach($flashDeals as $product)
        <x-product-card :product="$product"/>
      @endforeach
    </div>
  @endif
</div>
@endsection
