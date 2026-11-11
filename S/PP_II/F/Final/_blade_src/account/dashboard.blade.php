@extends('layouts.account')
@section('title', 'My Account — MMT')
@section('content')
@php $activeNav = 'overview'; @endphp
<div style="display:flex;align-items:flex-start;justify-content:space-between;gap:1rem;flex-wrap:wrap;margin-bottom:1.5rem">
  <div>
    <h1 style="font-size:1.4rem;margin-bottom:.35rem">Welcome back, <span>{{ Auth::user()->full_name }}</span></h1>
    <p style="color:var(--text-secondary)">Manage your orders, wishlist, and account settings.</p>
  </div>
</div>

<div class="stat-grid">
  <div class="stat-card">
    <div class="label">Total Orders</div>
    <div class="value brand">{{ $totalOrders }}</div>
  </div>
  <div class="stat-card">
    <div class="label">In Progress</div>
    <div class="value accent">{{ $pendingOrders }}</div>
  </div>
  <div class="stat-card">
    <div class="label">Delivered</div>
    <div class="value">{{ $completedOrders }}</div>
  </div>
  <div class="stat-card">
    <div class="label">Wishlist</div>
    <div class="value">{{ $wishlistItems }}</div>
  </div>
</div>

<h2 style="font-size:1.1rem;margin-bottom:1rem">Recent Orders</h2>
@if($orders->isEmpty())
  <div class="empty-state">
    <div class="icon">📦</div>
    <h3>No orders yet</h3>
    <p>When you place an order, it will show up here.</p>
    <a href="{{ url('/products') }}" class="btn btn-primary" style="margin-top:1rem">Start Shopping</a>
  </div>
@else
  <div class="data-table-wrap">
    <table class="data-table">
      <thead>
        <tr>
          <th>Order</th>
          <th>Seller</th>
          <th>Date</th>
          <th>Status</th>
          <th>Total</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        @foreach($orders->take(5) as $o)
          <tr>
            <td>{{ $o->order_number }}</td>
            <td>{{ $o->seller->store_name ?? '—' }}</td>
            <td>{{ optional($o->placed_at)->format('d M Y') ?: '—' }}</td>
            <td><x-status-pill :status="$o->status"/></td>
            <td>${{ number_format($o->total, 2) }}</td>
            <td><a href="{{ url('/orders/'.$o->order_number) }}" class="btn btn-outline btn-sm">Track</a></td>
          </tr>
        @endforeach
      </tbody>
    </table>
  </div>
  <a href="{{ url('/account/orders') }}" style="display:inline-block;margin-top:1rem">View all orders →</a>
@endif
@endsection
