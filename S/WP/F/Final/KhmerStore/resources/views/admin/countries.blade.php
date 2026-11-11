@extends('layouts.admin')
@section('title', 'Countries — Admin')
@section('content')
<div class="exec-head" style="margin-bottom:1rem">
  <div>
    <h1 style="font-size:1.4rem;margin:0">Countries</h1>
    <p style="margin:.35rem 0 0;color:var(--text-muted);font-size:.9rem">
      Click a country to see buyers, sellers, what they buy &amp; sell, wishlist activity, and money flow.
    </p>
  </div>
  <form method="get" action="{{ url('/admin/countries') }}" class="exec-head-actions" style="display:flex;gap:.75rem;align-items:center;flex-wrap:wrap">
    <label class="dash-sort" style="font-size:.85rem">
      Sort
      <select name="sort" onchange="this.form.submit()">
        <option value="money_desc" @selected($sort === 'money_desc')>Money (high → low)</option>
        <option value="name_asc" @selected($sort === 'name_asc')>Country A–Z</option>
        <option value="name_desc" @selected($sort === 'name_desc')>Country Z–A</option>
        <option value="sellers_desc" @selected($sort === 'sellers_desc')>Most sellers</option>
        <option value="sellers_asc" @selected($sort === 'sellers_asc')>Fewest sellers</option>
        <option value="buyers_desc" @selected($sort === 'buyers_desc')>Most buyers</option>
        <option value="buyers_asc" @selected($sort === 'buyers_asc')>Fewest buyers</option>
        <option value="buy_money_desc" @selected($sort === 'buy_money_desc')>Money bought</option>
        <option value="sell_money_desc" @selected($sort === 'sell_money_desc')>Money sold</option>
      </select>
    </label>
  </form>
</div>

@if($countries->isEmpty())
  <div class="empty-state">
    <h3>No country data yet</h3>
    <p>Countries appear from seller locations and order shipping destinations.</p>
  </div>
@else
  <div class="data-table-wrap">
    <table class="data-table">
      <thead>
        <tr>
          <th>Country</th>
          <th>Sellers</th>
          <th>Buyers</th>
          <th>Money Bought</th>
          <th>Money Sold</th>
          <th>Units Bought</th>
          <th>Units Sold</th>
          <th>Wishlist</th>
        </tr>
      </thead>
      <tbody>
        @foreach($countries as $c)
          <tr class="profile-row-click" data-href="{{ url('/admin/countries/'.rawurlencode($c['name'])) }}" style="cursor:pointer">
            <td>
              <a class="table-link-btn" href="{{ url('/admin/countries/'.rawurlencode($c['name'])) }}">{{ $c['name'] }}</a>
            </td>
            <td>{{ $c['sellerCount'] }}</td>
            <td>{{ $c['buyerCount'] }}</td>
            <td>${{ number_format($c['buyMoney'], 2) }}</td>
            <td>${{ number_format($c['sellMoney'], 2) }}</td>
            <td>{{ $c['buyUnits'] }}</td>
            <td>{{ $c['sellUnits'] }}</td>
            <td>{{ $c['wishlistViews'] }}</td>
          </tr>
        @endforeach
      </tbody>
    </table>
  </div>
@endif
@endsection

@push('scripts')
<script>
  document.querySelectorAll('.profile-row-click[data-href]').forEach((row) => {
    row.addEventListener('click', (e) => {
      if (e.target.closest('a,button,select,input,label')) return;
      window.location = row.dataset.href;
    });
  });
</script>
@endpush
