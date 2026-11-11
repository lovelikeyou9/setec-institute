@extends('layouts.admin')
@section('title', 'Checkout — '.$checkout['customer'].' — Admin')
@section('content')
<div class="exec-head" style="margin-bottom:1rem">
  <div>
    <a href="{{ url('/admin/orders') }}" class="btn btn-outline btn-sm" style="margin-bottom:.65rem">← Back to checkouts</a>
    <h1 style="font-size:1.4rem;margin:0">Customer checkout</h1>
    <p style="margin:.35rem 0 0;color:var(--text-muted);font-size:.9rem">
      One checkout can include packages from multiple sellers. Confirm / refund each seller below.
    </p>
  </div>
</div>

<div class="detail-block" style="margin-bottom:1.25rem">
  <div class="form-grid" style="grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:.85rem 1.25rem">
    <div>
      <div style="font-size:.75rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:.04em">Customer</div>
      <strong>{{ $checkout['customer'] }}</strong>
      @if($checkout['email'])<div style="font-size:.85rem;color:var(--text-secondary)">{{ $checkout['email'] }}</div>@endif
      @if($checkout['phone'])<div style="font-size:.85rem;color:var(--text-secondary)">{{ $checkout['phone'] }}</div>@endif
    </div>
    <div>
      <div style="font-size:.75rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:.04em">Placed</div>
      <strong>{{ optional($checkout['placed_at'])->format('d M Y H:i') ?: '—' }}</strong>
    </div>
    <div>
      <div style="font-size:.75rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:.04em">Payment</div>
      <strong>{{ $checkout['payment'] ? str_replace('_', ' ', $checkout['payment']) : '—' }}</strong>
      <div style="font-size:.85rem;color:var(--text-secondary)">{{ $checkout['delivery'] ? str_replace('_', ' ', $checkout['delivery']) : '' }}</div>
    </div>
    <div>
      <div style="font-size:.75rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:.04em">Sellers / Total</div>
      <strong>{{ $checkout['seller_count'] }} seller{{ $checkout['seller_count'] === 1 ? '' : 's' }}</strong>
      <div style="font-size:1.05rem;font-weight:700;color:var(--brand)">${{ number_format($checkout['total'], 2) }}</div>
    </div>
  </div>
  @if($checkout['address'])
    <div style="margin-top:1rem;padding-top:1rem;border-top:1px solid var(--border)">
      <div style="font-size:.75rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:.04em;margin-bottom:.25rem">Ship to</div>
      <div style="color:var(--text-secondary);line-height:1.5">{{ $checkout['address'] }}</div>
    </div>
  @endif
</div>

<div class="data-table-wrap">
  <table class="data-table">
    <thead>
      <tr>
        <th>Order #</th>
        <th>Seller</th>
        <th>Items</th>
        <th>Status</th>
        <th>Total</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody>
      @foreach($orders as $o)
        @php $s = $o->status; @endphp
        <tr>
          <td>{{ $o->order_number }}</td>
          <td>{{ $o->seller->store_name ?? '—' }}</td>
          <td>
            @foreach($o->items as $item)
              <div style="font-size:.82rem">{{ $item->quantity }}× {{ $item->product_name }}</div>
            @endforeach
          </td>
          <td><x-status-pill :status="$o->status"/></td>
          <td>${{ number_format($o->total, 2) }}</td>
          <td class="table-actions">
            <a href="{{ url('/orders/'.$o->order_number) }}" class="btn btn-outline btn-sm">View</a>

            @if(in_array($s, ['PAYMENT_CONFIRMED', 'PENDING_PAYMENT'], true))
              <form method="post" action="{{ url('/admin/orders/'.$o->id.'/confirm-hold') }}" style="display:inline">@csrf
                <button type="submit" class="btn btn-primary btn-sm">Confirm &amp; hold</button>
              </form>
            @endif

            @if(in_array($s, ['OUT_FOR_DELIVERY', 'SHIPPED'], true))
              <form method="post" action="{{ url('/admin/orders/'.$o->id.'/confirm-delivered') }}" style="display:inline">@csrf
                <button type="submit" class="btn btn-primary btn-sm">Confirm delivered</button>
              </form>
            @endif

            @if(in_array($s, ['DELIVERED', 'DISPUTED'], true))
              <form method="post" action="{{ url('/admin/orders/'.$o->id.'/release-payout') }}" style="display:inline">@csrf
                <button type="submit" class="btn btn-primary btn-sm">Deduct 3% &amp; pay</button>
              </form>
            @endif

            @if(!in_array($s, ['REFUNDED', 'COMPLETED', 'CANCELLED'], true))
              <form method="post" action="{{ url('/admin/orders/'.$o->id.'/refund') }}" style="display:inline"
                    onsubmit="return confirm('Refund held funds to customer for this seller package?');">@csrf
                <button type="submit" class="btn btn-danger btn-sm">Refund</button>
              </form>
            @endif
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>
</div>
@endsection
