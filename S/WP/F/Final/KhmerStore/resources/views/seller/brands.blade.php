@extends('layouts.seller')
@section('title', 'Brands — Seller')
@section('content')
<h1 style="font-size:1.4rem;margin-bottom:1rem">Brands</h1>
<p class="section-sub" style="margin-top:-.5rem">Browse brands and open your products for each brand</p>

<form class="dash-search" action="{{ url('/seller-dashboard/brands') }}" method="get">
  <input type="search" name="q" value="{{ request('q') }}" placeholder="Search brands..."/>
  <label class="dash-sort">
    <span>Sort</span>
    <select name="sort" onchange="this.form.submit()">
      <option value="name_asc" @selected(request('sort','name_asc')==='name_asc')>Name A–Z</option>
      <option value="name_desc" @selected(request('sort')==='name_desc')>Name Z–A</option>
      <option value="products" @selected(request('sort')==='products')>Most products</option>
    </select>
  </label>
  <button type="submit" class="btn btn-primary btn-sm">Search</button>
  @if(request('q'))
    <a href="{{ url('/seller-dashboard/brands') }}" class="btn btn-outline btn-sm">Clear</a>
  @endif
</form>

@php
  $list = $brands;
  if (request('q')) {
    $q = mb_strtolower(request('q'));
    $list = $list->filter(fn ($b) => str_contains(mb_strtolower($b->name), $q) || str_contains(mb_strtolower($b->slug ?? ''), $q));
  }
  $list = match (request('sort', 'name_asc')) {
    'name_desc' => $list->sortByDesc('name')->values(),
    'products' => $list->sortByDesc(fn ($b) => $counts[$b->id] ?? 0)->values(),
    default => $list->sortBy('name')->values(),
  };
@endphp

<div class="data-table-wrap">
  <table class="data-table">
    <thead>
      <tr><th>Logo</th><th>Name</th><th>Slug</th><th>My Products</th><th></th></tr>
    </thead>
    <tbody>
      @foreach($list as $b)
        <tr>
          <td>
            @if($b->logo_url)
              <img class="table-thumb" src="{{ $b->logo_url }}" alt="{{ $b->name }}"/>
            @else
              <span class="table-thumb-empty">—</span>
            @endif
          </td>
          <td>{{ $b->name }}</td>
          <td>{{ $b->slug }}</td>
          <td>{{ $counts[$b->id] ?? 0 }}</td>
          <td>
            <a class="btn btn-outline btn-sm" href="{{ url('/seller-dashboard/products?brand='.$b->id) }}">View products</a>
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>
</div>
@endsection
