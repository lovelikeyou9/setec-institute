@extends('layouts.account')
@section('title', 'My Orders — MMT')
@section('content')
<h1 style="font-size:1.4rem;margin-bottom:.35rem">My Orders</h1>
<p style="color:var(--text-secondary);margin-bottom:1.5rem">Track purchases and escrow status.</p>

<form method="get" class="dash-search" style="margin-bottom:1rem">
  <label class="dash-sort">
    <span>Status</span>
    <select name="status" onchange="this.form.submit()">
      <option value="">All statuses</option>
      @foreach(['PENDING_PAYMENT','PAYMENT_CONFIRMED','FUNDS_HELD','SELLER_PROCESSING','SHIPPED','OUT_FOR_DELIVERY','DELIVERED','COMPLETED','DISPUTED','CANCELLED','REFUNDED'] as $s)
        <option value="{{ $s }}" @selected(($status ?? '')===$s)>{{ str_replace('_',' ',$s) }}</option>
      @endforeach
    </select>
  </label>
  <label class="dash-sort">
    <span>Sort</span>
    <select name="sort" onchange="this.form.submit()">
      <option value="newest" @selected(($sort ?? '')==='newest')>Newest</option>
      <option value="oldest" @selected(($sort ?? '')==='oldest')>Oldest</option>
      <option value="total_desc" @selected(($sort ?? '')==='total_desc')>Total ↓</option>
      <option value="total_asc" @selected(($sort ?? '')==='total_asc')>Total ↑</option>
    </select>
  </label>
</form>

@if($orders->isEmpty())
  <div class="empty-state">
    <div class="icon">📦</div>
    <h3>No orders found</h3>
    <a href="{{ url('/products') }}" class="btn btn-primary" style="margin-top:1rem">Start Shopping</a>
  </div>
@else
  <div class="data-table-wrap">
    <table class="data-table">
      <thead>
        <tr><th>Order</th><th>Seller</th><th>Date</th><th>Status</th><th>Total</th><th></th></tr>
      </thead>
      <tbody>
        @foreach($orders as $order)
          <tr>
            <td>{{ $order->order_number }}</td>
            <td>{{ $order->seller->store_name ?? '—' }}</td>
            <td>{{ optional($order->placed_at)->format('d M Y') ?: '—' }}</td>
            <td><x-status-pill :status="$order->status"/></td>
            <td>${{ number_format($order->total, 2) }}</td>
            <td><a href="{{ url('/orders/'.$order->order_number) }}" class="btn btn-outline btn-sm">View</a></td>
          </tr>
        @endforeach
      </tbody>
    </table>
  </div>
@endif
@endsection
