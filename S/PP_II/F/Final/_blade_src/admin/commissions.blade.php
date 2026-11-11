@extends('layouts.admin')
@section('title', 'Commissions — Admin')
@section('content')
<div class="exec-head" style="margin-bottom:1rem">
  <div>
    <h1 style="font-size:1.4rem;margin:0">Commissions &amp; Fees</h1>
    <p style="margin:.35rem 0 0;color:var(--text-muted);font-size:.9rem">
      Platform keeps <strong>3%</strong> only after payout is released. Until then, funds stay in escrow.
    </p>
  </div>
  <div class="exec-head-actions">
    <a href="{{ url('/admin/orders') }}" class="btn btn-primary btn-sm">Orders / Escrow</a>
  </div>
</div>

<div class="exec-kpi-grid" style="margin-bottom:1.25rem">
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Fees earned</span><span class="exec-ico gold">%</span></div>
    <strong>${{ number_format($totalPlatform, 2) }}</strong>
    <em class="up">Released after paying sellers</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Fees in escrow</span><span class="exec-ico teal">◷</span></div>
    <strong>${{ number_format($feesInEscrow, 2) }}</strong>
    <em class="warn">{{ $heldCount }} orders waiting</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Sales held</span><span class="exec-ico purple">$</span></div>
    <strong>${{ number_format($escrowSales, 2) }}</strong>
    <em class="purple">Customer money held by MMT</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Fee rate</span><span class="exec-ico green">%</span></div>
    <strong>3%</strong>
    <em class="up">Deducted on payout</em>
  </article>
</div>

@if($commissions->isEmpty())
  <div class="empty-state">
    <h3>No commission records</h3>
    <p>Escrow rows are created when customers pay.</p>
  </div>
@else
  <div class="data-table-wrap">
    <table class="data-table">
      <thead>
        <tr>
          <th>Order</th>
          <th>Seller</th>
          <th>Sale</th>
          <th>%</th>
          <th>MMT fee</th>
          <th>Seller net</th>
          <th>Payout</th>
          <th>Date</th>
        </tr>
      </thead>
      <tbody>
        @foreach($commissions as $c)
          <tr>
            <td>
              <a href="{{ url('/orders/'.($c->order->order_number ?? $c->order_id)) }}">
                {{ $c->order->order_number ?? $c->order_id }}
              </a>
            </td>
            <td>{{ $c->seller->store_name ?? '—' }}</td>
            <td>${{ number_format($c->sale_amount, 2) }}</td>
            <td>{{ $c->commission_percent }}%</td>
            <td>${{ number_format($c->commission_amount, 2) }}</td>
            <td>${{ number_format($c->seller_earnings, 2) }}</td>
            <td><x-status-pill :status="$c->payout_status" type="payout"/></td>
            <td>{{ optional($c->created_at)->format('d M Y') ?: '—' }}</td>
          </tr>
        @endforeach
      </tbody>
    </table>
  </div>
@endif
@endsection
