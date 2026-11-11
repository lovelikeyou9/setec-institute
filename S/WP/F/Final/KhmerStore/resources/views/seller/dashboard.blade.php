@extends('layouts.seller')
@section('title', 'Seller Dashboard — MMT')
@section('content')
<div class="exec-head">
  <div>
    <h1>My Store · {{ $seller->store_name }}</h1>
    <p>Income after MMT fee · escrow until admin pays you · FY {{ date('Y') }}</p>
  </div>
  <div class="exec-head-actions">
    <span class="exec-range">Private seller view</span>
    <a href="{{ url('/seller-dashboard/orders') }}" class="btn btn-outline btn-sm">Orders</a>
    <a href="{{ url('/seller-dashboard/earnings') }}" class="btn btn-primary btn-sm">Earnings</a>
  </div>
</div>

<div class="exec-kpi-grid">
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Paid to me (net)</span><span class="exec-ico gold">$</span></div>
    <strong>${{ number_format($stats['totalEarnings'], 2) }}</strong>
    <em class="up">Released after admin payout</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Still in escrow</span><span class="exec-ico teal">◷</span></div>
    <strong>${{ number_format($stats['heldEarnings'], 2) }}</strong>
    <em class="warn">Held until delivered + admin pays</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Store revenue</span><span class="exec-ico green">$</span></div>
    <strong>${{ number_format($stats['revenue'], 2) }}</strong>
    <em class="up">{{ $stats['totalOrders'] }} orders</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Orders needing me</span><span class="exec-ico purple">◷</span></div>
    <strong>{{ $stats['pendingOrders'] }}</strong>
    <em class="warn">Stock / ship / OFD</em>
  </article>
</div>

<div class="exec-kpi-grid" style="margin-top:1rem">
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Catalog</span><span class="exec-ico teal">▣</span></div>
    <strong>{{ $stats['activeProducts'] }} / {{ $stats['totalProducts'] }}</strong>
    <em class="purple">Active products</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Rating</span><span class="exec-ico gold">&#9733;</span></div>
    <strong>{{ number_format($seller->rating ?? 0, 1) }}</strong>
    <em class="up">{{ $seller->followers ?? 0 }} followers</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Level</span><span class="exec-ico green">◎</span></div>
    <strong>{{ $seller->level }}</strong>
    <em class="up">{{ $seller->location ?: 'Cambodia' }}</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>MMT fee (admin gets)</span><span class="exec-ico purple">%</span></div>
    <strong>${{ number_format($stats['feesTotal'], 2) }}</strong>
    <em class="warn">3% · ${{ number_format($stats['feesPaid'], 2) }} taken · ${{ number_format($stats['feesPending'], 2) }} pending</em>
  </article>
</div>

<div class="exec-grid-2" style="margin-top:1rem">
  <section class="exec-card">
    <div class="exec-card-head">
      <div><h3>My Store Information</h3><p>Only your seller profile</p></div>
      <a href="{{ url('/seller-dashboard/settings') }}" class="btn btn-outline btn-sm">Edit</a>
    </div>
    <dl class="profile-dl">
      <div><dt>Store name</dt><dd>{{ $seller->store_name }}</dd></div>
      <div><dt>Owner</dt><dd>{{ $seller->owner_name }}</dd></div>
      <div><dt>Email</dt><dd>{{ $seller->user->email ?? Auth::user()->email }}</dd></div>
      <div><dt>Phone</dt><dd>{{ $seller->phone ?: '—' }}</dd></div>
      <div><dt>Location</dt><dd>{{ $seller->location ?: '—' }}</dd></div>
      <div><dt>Level</dt><dd>{{ $seller->level }}</dd></div>
    </dl>
  </section>

  <section class="exec-card">
    <div class="exec-card-head">
      <div><h3>My Recent Orders</h3><p>Buyers of your products</p></div>
      <a href="{{ url('/seller-dashboard/orders') }}" class="btn btn-outline btn-sm">View all</a>
    </div>
    @if($recentOrders->isEmpty())
      <div class="empty-state" style="padding:1.5rem">
        <h3>No orders yet</h3>
        <p>Orders for your store will appear here.</p>
      </div>
    @else
      <div class="data-table-wrap" style="border:none;box-shadow:none">
        <table class="data-table">
          <thead><tr><th>Order</th><th>Customer</th><th>Status</th><th>Total</th><th>Date</th></tr></thead>
          <tbody>
            @foreach($recentOrders as $o)
              <tr>
                <td>{{ $o->order_number }}</td>
                <td>{{ $o->user->full_name ?? $o->recipient_name ?? 'Buyer' }}</td>
                <td><x-status-pill :status="$o->status"/></td>
                <td>${{ number_format($o->total, 2) }}</td>
                <td>{{ optional($o->placed_at)->format('d M') ?: '—' }}</td>
              </tr>
            @endforeach
          </tbody>
        </table>
      </div>
    @endif
  </section>
</div>

<p class="exec-footnote">Your private seller analytics · FY{{ date('Y') }} · Other sellers' data is not shown</p>
@endsection
