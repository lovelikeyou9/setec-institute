@extends('layouts.account')
@section('title', 'Shopping Cart — MMT')
@section('content')
@php $activeNav = 'cart'; @endphp
<h1 style="font-size:1.4rem;margin-bottom:.35rem">Shopping Cart</h1>
<p style="color:var(--text-secondary);margin-bottom:1.5rem">Review items from multiple sellers before checkout.</p>

@if($groupedCart->isEmpty())
  <div class="empty-state">
    <div class="icon">🛒</div>
    <h3>Your cart is empty</h3>
    <p>Add routers, cameras, consoles, components, or any tech gear to get started.</p>
    <a href="{{ url('/products') }}" class="btn btn-primary" style="margin-top:1rem">Browse Products</a>
  </div>
@else
  <div class="cart-layout">
    <div>
      <form action="{{ url('/cart/select-all') }}" method="post" style="margin-bottom:.75rem;display:inline">
        @csrf
        <input type="hidden" name="selected" value="1"/>
        <input type="hidden" name="redirect" value="/account/cart"/>
        <button type="submit" class="btn btn-outline btn-sm">Select All</button>
      </form>
      <form action="{{ url('/cart/select-all') }}" method="post" style="display:inline;margin-left:.35rem">
        @csrf
        <input type="hidden" name="selected" value="0"/>
        <input type="hidden" name="redirect" value="/account/cart"/>
        <button type="submit" class="btn btn-outline btn-sm">Deselect All</button>
      </form>

      @foreach($groupedCart as $sellerId => $items)
        @php $seller = $items->first()->product->seller; $verified = in_array($seller->level ?? '', ['VERIFIED','TRUSTED','TOP'], true); @endphp
        <div class="cart-seller-group" style="margin-top:1rem">
          <div class="cart-seller-head">
            <span>{{ $seller->store_name ?? 'Seller' }}</span>
            @if($verified)<span class="badge badge-verified">✓</span>@endif
          </div>
          @foreach($items as $item)
            <div class="cart-item">
              <form action="{{ url('/cart/toggle') }}" method="post">
                @csrf
                <input type="hidden" name="itemId" value="{{ $item->id }}"/>
                <input type="hidden" name="redirect" value="/account/cart"/>
                <input type="checkbox" @checked($item->selected) onchange="this.form.submit()"/>
              </form>
              <a href="{{ url('/products/'.$item->product->slug) }}">
                <img src="{{ $item->product->image_url }}" alt="{{ $item->product->name }}"/>
              </a>
              <div>
                <a href="{{ url('/products/'.$item->product->slug) }}" style="font-weight:600">{{ $item->product->name }}</a>
                <div class="price-row" style="margin-top:.35rem">
                  <span class="price">${{ number_format($item->product->price, 2) }}</span>
                </div>
                <form action="{{ url('/cart/remove') }}" method="post" style="margin-top:.5rem">
                  @csrf
                  <input type="hidden" name="itemId" value="{{ $item->id }}"/>
                  <input type="hidden" name="redirect" value="/account/cart"/>
                  <button type="submit" class="btn btn-outline btn-sm">Remove</button>
                </form>
              </div>
              <div>
                <form action="{{ url('/cart/update') }}" method="post" style="display:flex;align-items:center;gap:.35rem">
                  @csrf
                  <input type="hidden" name="itemId" value="{{ $item->id }}"/>
                  <input type="hidden" name="redirect" value="/account/cart"/>
                  <div class="qty-selector">
                    <button type="button" onclick="var i=this.parentElement.querySelector('input[name=quantity]');i.value=Math.max(1,+i.value-1);i.form.submit()">−</button>
                    <input type="number" name="quantity" value="{{ $item->quantity }}" min="1" onchange="this.form.submit()"/>
                    <button type="button" onclick="var i=this.parentElement.querySelector('input[name=quantity]');i.value=+i.value+1;i.form.submit()">+</button>
                  </div>
                </form>
                <div style="text-align:right;margin-top:.5rem;font-weight:700;color:var(--accent)">
                  ${{ number_format($item->product->price * $item->quantity, 2) }}
                </div>
              </div>
            </div>
          @endforeach
        </div>
      @endforeach
    </div>

    <aside class="cart-summary">
      <h3 style="margin-bottom:1rem">Order Summary</h3>
      <div class="summary-row">
        <span>Subtotal</span>
        <span>${{ number_format($subtotal, 2) }}</span>
      </div>
      <div class="summary-row">
        <span>Shipping</span>
        <span>Calculated at checkout</span>
      </div>
      <div class="summary-row total">
        <span>Total</span>
        <span>${{ number_format($subtotal, 2) }}</span>
      </div>
      <a href="{{ url('/checkout') }}" class="btn btn-accent btn-block" style="width:100%;margin-top:1rem;text-align:center">Proceed to Checkout</a>
      <a href="{{ url('/products') }}" class="btn btn-outline btn-block" style="width:100%;margin-top:.5rem;text-align:center">Continue Shopping</a>
    </aside>
  </div>
@endif
@endsection
