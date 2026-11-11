@extends('layouts.seller')
@section('title', 'Reviews — MMT')
@section('content')
<h1 style="font-size:1.4rem;margin-bottom:1rem">Product Reviews</h1>

@if($reviews->isEmpty())
  <div class="empty-state">
    <div class="icon">⭐</div>
    <h3>No reviews yet</h3>
    <p>Customer reviews for your products will appear here.</p>
  </div>
@else
  @foreach($reviews as $r)
    <div class="form-card">
      <div class="review-header">
        <div class="review-avatar">{{ mb_substr($r->user->full_name ?? 'U', 0, 1) }}</div>
        <div>
          <strong>{{ $r->user->full_name ?? 'Buyer' }}</strong>
          <span class="stars">@for($i=1;$i<=5;$i++){!! $i <= $r->rating ? '&#9733;' : '&#9734;' !!}@endfor</span>
          <div style="font-size:.8rem;color:var(--text-muted)">
            @if($r->product)
              <a href="{{ url('/products/'.$r->product->slug) }}" style="color:var(--brand);font-weight:600">{{ $r->product->name }}</a> ·
            @endif
            <span>{{ optional($r->created_at)->format('d M Y') }}</span>
          </div>
        </div>
      </div>
      <p style="margin-top:.5rem">{{ $r->comment }}</p>
      @if($r->seller_response)
        <div class="seller-reply">
          <strong>Your reply:</strong> <span>{{ $r->seller_response }}</span>
        </div>
      @endif
    </div>
  @endforeach
@endif
@endsection
