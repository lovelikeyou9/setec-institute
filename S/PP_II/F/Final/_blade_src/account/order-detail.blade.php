@extends('layouts.account')
@section('title', 'Order '.$order->order_number.' — MMT')
@php
  $s = $order->status;
  $cancelled = in_array($s, ['CANCELLED', 'REFUNDED'], true);
  $disputed = $s === 'DISPUTED';
  $idx = match ($s) {
    'PENDING_PAYMENT' => 0,
    'PAYMENT_CONFIRMED' => 1,
    'FUNDS_HELD' => 2,
    'SELLER_PROCESSING' => 3,
    'SHIPPED' => 4,
    'OUT_FOR_DELIVERY' => 5,
    'DELIVERED', 'DISPUTED' => 6,
    'COMPLETED' => 7,
    default => -1,
  };
  $commission = $order->commission;
  $fmt = fn ($dt) => $dt ? $dt->format('d M Y H:i') : null;
@endphp
@section('content')
<nav class="breadcrumb">
  <a href="{{ url('/') }}">Home</a>
  <span class="sep">/</span>
  <a href="{{ url('/account/orders') }}">Orders</a>
  <span class="sep">/</span>
  <span>{{ $order->order_number }}</span>
</nav>

<div class="section-head">
  <div>
    <h1 class="section-title">Order {{ $order->order_number }}</h1>
    <p class="section-sub">
      Placed {{ optional($order->placed_at)->format('d M Y H:i') ?: '—' }}
      · Seller:
      @if($order->seller)
        <a href="{{ url('/sellers/'.$order->seller_id) }}">{{ $order->seller->store_name }}</a>
      @else
        —
      @endif
    </p>
  </div>
  <x-status-pill :status="$order->status"/>
</div>

@if($commission)
  <div class="form-card">
    <h3>Payment escrow</h3>
    <p style="font-size:.92rem;color:var(--text-secondary);margin-bottom:.75rem">
      After you pay, MMT holds your money. Seller confirms stock and ships.
      Admin confirms delivery, then deducts
      <strong>{{ $commission->commission_percent }}%</strong> and pays the seller.
    </p>
    <div class="summary-row">
      <span>Escrow status</span>
      <strong><x-status-pill :status="$commission->payout_status" type="payout"/></strong>
    </div>
    <div class="summary-row">
      <span>MMT fee</span>
      <span>${{ number_format($commission->commission_amount, 2) }} ({{ $commission->commission_percent }}%)</span>
    </div>
    <div class="summary-row">
      <span>Seller receives</span>
      <span>${{ number_format($commission->seller_earnings, 2) }}</span>
    </div>
    @if($order->admin_held_at)
      <div class="summary-row"><span>Admin held at</span><span>{{ $fmt($order->admin_held_at) }}</span></div>
    @endif
    @if($order->seller_confirmed_at)
      <div class="summary-row"><span>Seller confirmed stock</span><span>{{ $fmt($order->seller_confirmed_at) }}</span></div>
    @endif
    @if($order->protection_ends_at)
      <div class="summary-row"><span>Buyer protection ends</span><span>{{ $fmt($order->protection_ends_at) }}</span></div>
    @endif
    @if($order->payout_released_at)
      <div class="summary-row"><span>Seller paid at</span><span>{{ $fmt($order->payout_released_at) }}</span></div>
    @endif
  </div>
@endif

<div class="form-card">
  <h3>Order Progress</h3>
  <div class="timeline">
    @if($cancelled)
      <div class="timeline-item active">
        <h4>{{ $s }}</h4>
        @if(($order->cancel_reason ?? '') === 'OUT_OF_STOCK')
          <p>Seller declined — product out of stock. Payment refunded.</p>
        @elseif($s === 'CANCELLED')
          <p>This order was cancelled.</p>
        @elseif($s === 'REFUNDED')
          <p>Held funds were returned to you.</p>
        @endif
      </div>
    @else
      <div class="timeline-item {{ $idx > 0 ? 'done' : ($idx === 0 ? 'active' : '') }}">
        <h4>1. Customer pays</h4>
        <p>{{ $fmt($order->paid_at) ?: 'Waiting for payment' }}</p>
      </div>
      <div class="timeline-item {{ $idx > 1 ? 'done' : ($idx === 1 ? 'active' : '') }}">
        <h4>2. Admin confirms &amp; holds money</h4>
        <p>{{ $order->admin_held_at ? $fmt($order->admin_held_at).' — funds held by MMT' : 'Waiting for admin confirmation' }}</p>
      </div>
      <div class="timeline-item {{ $idx > 2 ? 'done' : ($idx === 2 ? 'active' : '') }}">
        <h4>3. Seller confirms stock</h4>
        <p>{{ $order->seller_confirmed_at ? $fmt($order->seller_confirmed_at).' — seller accepted' : 'Seller checks stock (can decline if out of stock)' }}</p>
      </div>
      <div class="timeline-item {{ $idx > 3 ? 'done' : ($idx === 3 ? 'active' : '') }}">
        <h4>4. Seller processing</h4>
        <p>Seller is preparing your package</p>
      </div>
      <div class="timeline-item {{ $idx > 4 ? 'done' : ($idx === 4 ? 'active' : '') }}">
        <h4>5. Seller shipped</h4>
        <p>{{ $fmt($order->shipped_at) ?: 'Awaiting shipment' }}</p>
      </div>
      <div class="timeline-item {{ $idx > 5 ? 'done' : ($idx === 5 ? 'active' : '') }}">
        <h4>6. Out for delivery</h4>
        <p>{{ $order->out_for_delivery_at ? $fmt($order->out_for_delivery_at).' — courier delivering' : 'Courier delivering to your address' }}</p>
      </div>
      <div class="timeline-item {{ $idx > 6 ? 'done' : ($idx === 6 ? 'active' : '') }}">
        <h4>7. Admin confirms delivered</h4>
        @if($order->delivered_at)
          <p>{{ $fmt($order->delivered_at) }} · buyer protection: report missing / damaged / incorrect within {{ $protectionDays }} day(s)</p>
        @else
          <p>Admin confirms you received the product</p>
        @endif
        @if($disputed)
          <p style="color:#c62828;margin-top:.35rem">
            Dispute open: <strong>{{ $order->dispute_reason ?? 'ISSUE' }}</strong>
            @if($order->dispute_note) — {{ $order->dispute_note }}@endif
          </p>
        @endif
      </div>
      <div class="timeline-item {{ $idx >= 7 ? 'done' : '' }}">
        <h4>8. Admin pays seller</h4>
        @if($order->payout_released_at)
          <p>{{ $fmt($order->payout_released_at) }} — 3% commission deducted, seller paid</p>
        @else
          <p>Admin deducts commission and pays the seller</p>
        @endif
      </div>
    @endif
  </div>

  @if($order->tracking_number)
    <p style="margin-top:.5rem">Tracking number: <strong>{{ $order->tracking_number }}</strong></p>
  @endif
  @if($order->estimated_delivery)
    <p style="font-size:.9rem;color:var(--text-muted)">
      Estimated delivery: {{ optional($order->estimated_delivery)->format('d M Y') }}
    </p>
  @endif
</div>

@if($canOpenDispute)
  <div class="form-card">
    <h3>Buyer protection</h3>
    <p style="font-size:.9rem;color:var(--text-secondary);margin-bottom:.5rem">
      Admin confirmed delivery. Report missing, damaged, or incorrect items during buyer protection.
      Payment stays held until this is resolved.
    </p>
    <form method="post" action="{{ url('/orders/'.$order->order_number.'/dispute') }}" class="form-grid">
      @csrf
      <div class="form-group">
        <label>Issue type *</label>
        <select name="reason" required>
          @foreach($disputeReasons as $r)
            <option value="{{ $r }}">
              {{ $r === 'MISSING' ? 'Missing item' : ($r === 'DAMAGED' ? 'Damaged item' : ($r === 'INCORRECT' ? 'Incorrect item' : 'Other')) }}
            </option>
          @endforeach
        </select>
      </div>
      <div class="form-group" style="grid-column:1/-1">
        <label>Details</label>
        <textarea name="note" rows="3" placeholder="Describe what went wrong..."></textarea>
      </div>
      <div>
        <button type="submit" class="btn btn-danger">Report issue</button>
      </div>
    </form>
  </div>
@endif

<div class="form-card">
  <h3>Order Items</h3>
  @foreach($order->items as $item)
    <div class="cart-item" style="grid-template-columns:80px 1fr auto">
      <img src="{{ $item->product_image }}" alt="{{ $item->product_name }}"/>
      <div>
        <strong>{{ $item->product_name }}</strong>
        <div style="font-size:.85rem;color:var(--text-secondary);margin-top:.25rem">
          Qty: {{ $item->quantity }} × ${{ number_format($item->unit_price, 2) }}
        </div>
      </div>
      <div style="font-weight:700">${{ number_format($item->line_total, 2) }}</div>
    </div>
  @endforeach
</div>

<div class="form-card">
  <h3>Payment &amp; Shipping</h3>
  <div class="summary-row"><span>Subtotal</span><span>${{ number_format($order->subtotal, 2) }}</span></div>
  <div class="summary-row"><span>Shipping</span><span>${{ number_format($order->shipping_fee, 2) }}</span></div>
  @if(($order->discount ?? 0) > 0)
    <div class="summary-row"><span>Discount</span><span>-${{ number_format($order->discount, 2) }}</span></div>
  @endif
  <div class="summary-row total"><span>Total paid</span><span>${{ number_format($order->total, 2) }}</span></div>
  <p style="margin-top:1rem;font-size:.9rem;color:var(--text-secondary)">
    Payment: <strong>{{ $order->payment_method ?: '—' }}</strong> ·
    Delivery: <strong>{{ $order->delivery_type ?: 'STANDARD' }}</strong>
  </p>
  <p style="margin-top:.5rem;font-size:.9rem;color:var(--text-secondary)">
    Ship to: <strong>{{ $order->recipient_name }}</strong>,
    <span>{{ $order->recipient_phone }}</span><br/>
    <span>{{ $order->shipping_address }}</span>
  </p>
</div>

<a href="{{ url('/account/orders') }}" class="btn btn-outline">&larr; Back to Orders</a>
@endsection
