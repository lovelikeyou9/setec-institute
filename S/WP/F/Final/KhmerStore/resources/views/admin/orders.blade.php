@extends('layouts.admin')
@section('title', 'Orders — Admin')
@section('content')
<div class="exec-head" style="margin-bottom:1rem">
  <div>
    <h1 style="font-size:1.4rem;margin:0">Orders &amp; Escrow</h1>
    <p style="margin:.35rem 0 0;color:var(--text-muted);font-size:.9rem">
      Grouped by customer checkout. Open a checkout to Confirm / Refund each seller package.
      Confirm &amp; hold → seller ships → <strong>Confirm delivered</strong> → <strong>Deduct 3% &amp; pay seller</strong>
    </p>
  </div>
  <div class="exec-head-actions">
    <a href="{{ url('/admin/commissions') }}" class="btn btn-outline btn-sm">Commissions</a>
  </div>
</div>

@if($checkouts->isEmpty())
  <div class="empty-state">
    <h3>No orders yet</h3>
    <p>Orders appear here when customers check out.</p>
  </div>
@else
  <div class="data-table-wrap">
    <table class="data-table">
      <thead>
        <tr>
          <th>Customer</th>
          <th>Sellers</th>
          <th>Items</th>
          <th>Payment</th>
          <th>Status</th>
          <th>Total</th>
          <th>Date</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        @foreach($checkouts as $c)
          <tr>
            <td>
              <strong>{{ $c['customer'] }}</strong>
              @if($c['email'])
                <div style="font-size:.78rem;color:var(--text-muted)">{{ $c['email'] }}</div>
              @endif
            </td>
            <td>{{ $c['seller_count'] }} seller{{ $c['seller_count'] === 1 ? '' : 's' }}</td>
            <td>{{ $c['item_count'] }}</td>
            <td>{{ $c['payment'] ? str_replace('_', ' ', $c['payment']) : '—' }}</td>
            <td>
              @if($c['statuses']->count() === 1)
                <x-status-pill :status="$c['statuses']->first()"/>
              @else
                <span class="status-pill gray">{{ $c['status_label'] }}</span>
              @endif
            </td>
            <td>${{ number_format($c['total'], 2) }}</td>
            <td>{{ optional($c['placed_at'])->format('d M Y H:i') ?: '—' }}</td>
            <td class="table-actions">
              <a href="{{ url('/admin/orders/checkout/'.$c['user_id'].'/'.$c['placed_at_ts']) }}"
                 class="btn btn-primary btn-sm">Open checkout</a>
            </td>
          </tr>
        @endforeach
      </tbody>
    </table>
  </div>
@endif
@endsection
