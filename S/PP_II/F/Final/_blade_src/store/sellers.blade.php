@extends('layouts.store')
@section('title', 'Sellers — MMT')
@section('content')
<div class="container" style="padding-top:1.5rem">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <span>Sellers</span>
  </nav>
  <div class="section-head">
    <div>
      <h1 class="section-title">Trusted Sellers</h1>
      <p class="section-sub">Verified stores for computers, networking, gaming, cameras &amp; every kind of tech</p>
    </div>
    <a href="{{ url('/become-seller') }}" class="btn btn-accent">Become a Seller</a>
  </div>

  @if($sellers->isEmpty())
    <div class="empty-state">
      <div class="icon">🏪</div>
      <h3>No sellers yet</h3>
      <p>Be the first to open a store on MMT.</p>
    </div>
  @else
    <div class="seller-grid">
      @foreach($sellers as $s)
        @php $verified = in_array($s->level ?? '', ['VERIFIED', 'TRUSTED', 'TOP'], true); @endphp
        <a href="{{ url('/sellers/'.$s->id) }}" class="seller-card">
          <img src="{{ $s->logo_url ?: asset('images/mme-icon.png') }}" alt="{{ $s->store_name }}" class="seller-logo"/>
          <div class="seller-info">
            <h3>
              <span>{{ $s->store_name }}</span>
              @if($verified)<span class="badge badge-verified">✓ Verified</span>@endif
              @if(($s->level ?? '') === 'TOP')<span class="badge badge-top">Top Seller</span>@endif
            </h3>
            <div class="rating-row">
              <x-stars :rating="$s->rating ?? 0" />
              <span>{{ number_format($s->rating ?? 0, 1) }}</span>
            </div>
            <div class="seller-stats">
              <span>{{ $s->total_products }} products</span> ·
              <span>{{ $s->followers ?? 0 }} followers</span> ·
              <span>{{ $s->location ?: ($s->country ?: 'Cambodia') }}</span>
            </div>
          </div>
        </a>
      @endforeach
    </div>
    <div class="pagination" style="margin-top:1.5rem">{{ $sellers->links() }}</div>
  @endif
</div>
@endsection
