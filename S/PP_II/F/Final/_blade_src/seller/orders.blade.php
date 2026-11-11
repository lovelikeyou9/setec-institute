@extends('layouts.seller')
@section('title', 'Seller Orders — MMT')
@section('content')
<div class="exec-head" style="margin-bottom:1rem">
  <div>
    <h1 style="font-size:1.4rem;margin:0">Orders</h1>
    <p style="margin:.35rem 0 0;color:var(--text-muted);font-size:.9rem">
      Confirm stock → <strong>Shipped</strong> → <strong>Out for delivery</strong>.
      Admin confirms delivered and pays you (minus 3%).
    </p>
  </div>
  <div class="exec-head-actions">
    <a href="{{ url('/seller-dashboard/earnings') }}" class="btn btn-outline btn-sm">Earnings</a>
  </div>
</div>

<form class="dash-search" action="{{ url('/seller-dashboard/orders') }}" method="get">
  <label class="dash-sort">
    <span>Sort</span>
    <select name="sort" onchange="this.form.submit()">
      <option value="newest" @selected(request('sort','newest')==='newest')>Newest first</option>
      <option value="oldest" @selected(request('sort')==='oldest')>Oldest first</option>
      <option value="total_desc" @selected(request('sort')==='total_desc')>Total: High to Low</option>
      <option value="total_asc" @selected(request('sort')==='total_asc')>Total: Low to High</option>
    </select>
  </label>
</form>

@if($orders->isEmpty())
  <div class="empty-state">
    <div class="icon">📦</div>
    <h3>No orders yet</h3>
    <p>When customers buy your products, orders show up here.</p>
  </div>
@else
  <div class="data-table-wrap">
    <table class="data-table">
      <thead>
        <tr>
          <th>Order #</th><th>Customer</th><th>Items</th><th>Total</th><th>Status</th><th>Date</th><th>Actions</th>
        </tr>
      </thead>
      <tbody>
        @foreach($orders as $o)
          @php $s = $o->status; @endphp
          <tr>
            <td>{{ $o->order_number }}</td>
            <td>
              <div>{{ $o->recipient_name ?: ($o->user->full_name ?? 'Buyer') }}</div>
              <div style="font-size:.8rem;color:var(--text-muted)">{{ $o->recipient_phone }}</div>
            </td>
            <td>{{ $o->items->count() }}</td>
            <td>${{ number_format($o->total, 2) }}</td>
            <td><x-status-pill :status="$o->status"/></td>
            <td>{{ optional($o->placed_at)->format('d M Y') ?: '—' }}</td>
            <td class="table-actions">
              @if(in_array($s, ['PAYMENT_CONFIRMED', 'PENDING_PAYMENT'], true))
                <span class="hint-muted">Waiting for admin hold</span>
              @endif

              @if($s === 'FUNDS_HELD')
                <form method="post" action="{{ url('/seller-dashboard/orders/'.$o->id.'/accept') }}" style="display:inline">@csrf
                  <button type="submit" class="btn btn-primary btn-sm">Confirm stock</button>
                </form>
                <form method="post" action="{{ url('/seller-dashboard/orders/'.$o->id.'/decline-stock') }}" style="display:inline"
                      onsubmit="return confirm('Decline because out of stock? Customer will be refunded.');">@csrf
                  <button type="submit" class="btn btn-danger btn-sm">Out of stock</button>
                </form>
              @endif

              @if($s === 'SELLER_PROCESSING')
                <form method="post" action="{{ url('/seller-dashboard/orders/'.$o->id.'/status') }}" style="display:inline">@csrf
                  <input type="hidden" name="status" value="SHIPPED"/>
                  <button type="submit" class="btn btn-primary btn-sm">Mark shipped</button>
                </form>
              @endif

              @if($s === 'SHIPPED')
                <form method="post" action="{{ url('/seller-dashboard/orders/'.$o->id.'/status') }}" style="display:inline">@csrf
                  <input type="hidden" name="status" value="OUT_FOR_DELIVERY"/>
                  <button type="submit" class="btn btn-primary btn-sm">Out for delivery</button>
                </form>
              @endif

              @if($s === 'OUT_FOR_DELIVERY')
                <span class="hint-muted">Waiting for admin: delivered → pay</span>
              @endif
              @if($s === 'DELIVERED')
                <span class="hint-muted">Buyer protection — await payout</span>
              @endif
              @if($s === 'COMPLETED')
                <span class="hint-ok">Paid (minus 3%)</span>
              @endif

              <a href="{{ url('/orders/'.$o->order_number) }}" class="btn btn-outline btn-sm">View</a>
            </td>
          </tr>
        @endforeach
      </tbody>
    </table>
  </div>
@endif
@endsection
