@extends('layouts.store')
@section('title', 'Brands — MMT')
@section('content')
<div class="container" style="padding-top:1.5rem">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <span>Brands</span>
  </nav>
  <div class="section-head">
    <div>
      <h1 class="section-title">Shop by Brand</h1>
      <p class="section-sub">ASUS, MSI, Intel, AMD, and more trusted brands</p>
    </div>
  </div>
  <div class="brand-shop-grid">
    @foreach($brands as $b)
      <a href="{{ url('/products?brand='.$b->id) }}" class="brand-shop-card">
        @if($b->logo_url)
          <img src="{{ $b->logo_url }}" alt="{{ $b->name }}"/>
        @else
          <span>{{ $b->name }}</span>
        @endif
        <strong>{{ $b->name }}</strong>
      </a>
    @endforeach
  </div>
</div>
@endsection
