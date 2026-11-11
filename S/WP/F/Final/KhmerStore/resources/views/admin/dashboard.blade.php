@extends('layouts.admin')
@section('title', 'Admin Dashboard — MMT')
@push('head')
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
@endpush
@section('content')
<div class="exec-head">
  <div>
    <h1>Marketplace Performance Dashboard</h1>
    <p>GMV, escrow fees (3%) &amp; payout queue · Calendar Year {{ $year }}</p>
  </div>
  <div class="exec-head-actions">
    <span class="exec-range">Jan 1 – Dec 31, {{ $year }}</span>
    <a href="{{ url('/admin/commissions') }}" class="btn btn-outline btn-sm">Commissions</a>
    <a href="{{ url('/admin/orders') }}" class="btn btn-primary btn-sm">Orders / Escrow</a>
  </div>
</div>

<div class="exec-kpi-grid">
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Gross Merchandise Vol</span><span class="exec-ico teal">$</span></div>
    <strong>${{ number_format($stats['revenue'], 2) }}</strong>
    <em class="up">{{ $stats['orders'] }} orders</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Fees earned (3%)</span><span class="exec-ico gold">%</span></div>
    <strong>${{ number_format($stats['platformFees'], 2) }}</strong>
    <em class="up">Released after payout to sellers</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Fees in escrow</span><span class="exec-ico green">&#9716;</span></div>
    <strong>${{ number_format($stats['feesInEscrow'], 2) }}</strong>
    <em class="warn">{{ $stats['escrowOrders'] }} orders · not yours until release</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Sales held (escrow)</span><span class="exec-ico purple">$</span></div>
    <strong>${{ number_format($stats['escrow'], 2) }}</strong>
    <em class="purple">{{ $stats['users'] }} users · {{ $stats['sellers'] }} sellers</em>
  </article>
</div>

<div class="exec-kpi-grid" style="margin-top:1rem">
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Await confirm &amp; hold</span><span class="exec-ico teal">1</span></div>
    <strong>{{ $stats['awaitHold'] }}</strong>
    <em class="warn">Customer paid — hold funds</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Await confirm delivered</span><span class="exec-ico green">2</span></div>
    <strong>{{ $stats['awaitDelivered'] }}</strong>
    <em class="up">Seller shipped / out for delivery</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Await pay seller</span><span class="exec-ico gold">3</span></div>
    <strong>{{ $stats['awaitPayout'] }}</strong>
    <em class="warn">Deduct 3% then release</em>
  </article>
  <article class="exec-kpi">
    <div class="exec-kpi-top"><span>Catalog / pending</span><span class="exec-ico purple">&#9635;</span></div>
    <strong>{{ $stats['products'] }}</strong>
    <em class="purple">{{ $stats['pendingSellers'] }} sellers · {{ $stats['pendingOrders'] }} open orders</em>
  </article>
</div>

<div class="exec-grid-2">
  <section class="exec-card">
    <div class="exec-card-head">
      <div>
        <h3>Orders &amp; Revenue Trend</h3>
        <p>Monthly marketplace activity</p>
      </div>
    </div>
    <div class="exec-chart-lg"><canvas id="adminTrendChart"></canvas></div>
  </section>
  <section class="exec-card">
    <div class="exec-card-head">
      <div>
        <h3>Revenue by Category</h3>
        <p>Estimated from sold volume</p>
      </div>
      <strong class="exec-donut-total">${{ number_format($stats['revenue'], 2) }}</strong>
    </div>
    <div class="exec-donut-wrap">
      <div class="exec-chart-donut"><canvas id="adminCategoryChart"></canvas></div>
      <ul class="exec-legend">
        @foreach($categoryRows as $i => $row)
          <li>
            <span class="dot c{{ $i }}"></span>
            <div>
              <strong>{{ $row['label'] }}</strong>
              <small>${{ number_format($row['value'], 2) }} / {{ $row['percent'] }}%</small>
            </div>
          </li>
        @endforeach
      </ul>
    </div>
  </section>
</div>

<div class="exec-grid-3">
  <section class="exec-card">
    <div class="exec-card-head">
      <div><h3>Activation Funnel</h3><p>Platform engagement path</p></div>
    </div>
    <div class="exec-chart-md"><canvas id="adminFunnelChart"></canvas></div>
  </section>
  <section class="exec-card">
    <div class="exec-card-head">
      <div><h3>Top Countries by GMV</h3><p>Revenue by destination · <a href="{{ url('/admin/countries') }}">All countries</a></p></div>
    </div>
    <div class="exec-chart-md"><canvas id="adminCountryChart"></canvas></div>
    <ul class="exec-legend" style="margin-top:.75rem">
      @foreach($countryRows as $i => $row)
        <li>
          <span class="dot c{{ $i }}"></span>
          <div>
            <strong><a href="{{ url('/admin/countries/'.rawurlencode($row['label'])) }}" style="color:inherit;text-decoration:none">{{ $row['label'] }}</a></strong>
            <small>${{ number_format($row['value'], 2) }} / {{ $row['percent'] }}%</small>
          </div>
        </li>
      @endforeach
    </ul>
  </section>
  <section class="exec-card">
    <div class="exec-card-head">
      <div><h3>Seller Performance</h3><p>Top stores</p></div>
    </div>
    <div class="exec-mini-table-wrap">
      <table class="exec-mini-table">
        <thead><tr><th>Store</th><th>SKU</th><th>ARPU</th><th>Health</th></tr></thead>
        <tbody>
          @forelse($perfRows as $row)
            <tr>
              <td>{{ $row['name'] }}</td>
              <td>{{ $row['products'] }}</td>
              <td>${{ number_format($row['arpu'], 2) }}</td>
              <td><span class="health {{ strtolower($row['health']) }}">{{ $row['health'] }}</span></td>
            </tr>
          @empty
            <tr><td colspan="4" style="text-align:center;color:var(--text-muted)">No sellers yet</td></tr>
          @endforelse
        </tbody>
      </table>
    </div>
  </section>
</div>

<section class="exec-card" style="margin-top:1rem">
  <div class="exec-card-head">
    <div><h3>Recent Orders</h3><p>Latest marketplace transactions</p></div>
    <a href="{{ url('/admin/orders') }}" class="btn btn-outline btn-sm">View all</a>
  </div>
  <div class="data-table-wrap" style="border:none;box-shadow:none">
    <table class="data-table">
      <thead>
        <tr><th>Order</th><th>Seller</th><th>Customer</th><th>Status</th><th>Total</th><th>Date</th></tr>
      </thead>
      <tbody>
        @forelse($recentOrders as $o)
          <tr>
            <td>{{ $o->order_number }}</td>
            <td>{{ $o->seller->store_name ?? '—' }}</td>
            <td>{{ $o->recipient_name ?: ($o->user->full_name ?? '—') }}</td>
            <td><x-status-pill :status="$o->status"/></td>
            <td>${{ number_format($o->total, 2) }}</td>
            <td>{{ optional($o->placed_at)->format('d M') ?: '—' }}</td>
          </tr>
        @empty
          <tr><td colspan="6" style="text-align:center;color:var(--text-muted)">No orders yet</td></tr>
        @endforelse
      </tbody>
    </table>
  </div>
</section>

<p class="exec-footnote">MMT Analytics · Generated for FY{{ $year }} reporting · Figures in USD</p>
@endsection

@push('scripts')
<script>
  const months = @json($months);
  const ordersTrend = @json($ordersTrend);
  const revenueTrend = @json($revenueTrend);
  const categoryLabels = @json($categoryLabels);
  const categoryValues = @json($categoryValues);
  const funnelLabels = @json($funnelLabels);
  const funnelValues = @json($funnelValues);
  const countryLabels = @json($countryLabels);
  const countryValues = @json($countryValues);
  const palette = ['#2563eb', '#f59e0b', '#10b981', '#8b5cf6', '#0d7377'];

  Chart.defaults.font.family = getComputedStyle(document.body).fontFamily;
  Chart.defaults.color = '#5a6570';

  new Chart(document.getElementById('adminTrendChart'), {
    type: 'line',
    data: {
      labels: months,
      datasets: [
        { label: 'Orders', data: ordersTrend, borderColor: '#1d4ed8', backgroundColor: 'rgba(37,99,235,.18)', fill: true, tension: .35, pointRadius: 0 },
        { label: 'Revenue', data: revenueTrend, borderColor: '#38bdf8', backgroundColor: 'rgba(56,189,248,.12)', fill: true, tension: .35, pointRadius: 0 }
      ]
    },
    options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } }, scales: { y: { beginAtZero: true, grid: { color: '#eef2f6' } }, x: { grid: { display: false } } } }
  });

  new Chart(document.getElementById('adminCategoryChart'), {
    type: 'doughnut',
    data: { labels: categoryLabels, datasets: [{ data: categoryValues, backgroundColor: palette, borderWidth: 0 }] },
    options: { responsive: true, maintainAspectRatio: false, cutout: '68%', plugins: { legend: { display: false } } }
  });

  new Chart(document.getElementById('adminFunnelChart'), {
    type: 'bar',
    data: { labels: funnelLabels, datasets: [{ data: funnelValues, backgroundColor: ['#93c5fd', '#60a5fa', '#3b82f6', '#2563eb', '#1d4ed8'], borderRadius: 8 }] },
    options: { indexAxis: 'y', responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { x: { beginAtZero: true, grid: { color: '#eef2f6' } }, y: { grid: { display: false } } } }
  });

  new Chart(document.getElementById('adminCountryChart'), {
    type: 'bar',
    data: { labels: countryLabels, datasets: [{ data: countryValues, backgroundColor: ['#3b82f6', '#60a5fa', '#93c5fd', '#bfdbfe', '#dbeafe'], borderRadius: 8 }] },
    options: { indexAxis: 'y', responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { x: { beginAtZero: true, grid: { color: '#eef2f6' } }, y: { grid: { display: false } } } }
  });
</script>
@endpush
