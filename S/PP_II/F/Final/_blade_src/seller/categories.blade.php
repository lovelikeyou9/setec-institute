@extends('layouts.seller')
@section('title', 'Categories — Seller')
@section('content')
<h1 style="font-size:1.4rem;margin-bottom:1rem">Categories</h1>
<p class="section-sub" style="margin-top:-.5rem">Browse categories and open your products in each one</p>

<form class="dash-search" action="{{ url('/seller-dashboard/categories') }}" method="get">
  <input type="search" name="q" value="{{ request('q') }}" placeholder="Search categories..."/>
  <label class="dash-sort">
    <span>Sort</span>
    <select name="sort" onchange="this.form.submit()">
      <option value="sort" @selected(request('sort','sort')==='sort')>Default order</option>
      <option value="name_asc" @selected(request('sort')==='name_asc')>Name A–Z</option>
      <option value="name_desc" @selected(request('sort')==='name_desc')>Name Z–A</option>
      <option value="products" @selected(request('sort')==='products')>Most products</option>
    </select>
  </label>
  <button type="submit" class="btn btn-primary btn-sm">Search</button>
  @if(request('q'))
    <a href="{{ url('/seller-dashboard/categories') }}" class="btn btn-outline btn-sm">Clear</a>
  @endif
</form>

@php
  $list = $categories;
  if (request('q')) {
    $q = mb_strtolower(request('q'));
    $list = $list->filter(fn ($c) => str_contains(mb_strtolower($c->name), $q) || str_contains(mb_strtolower($c->slug ?? ''), $q));
  }
  $list = match (request('sort', 'sort')) {
    'name_asc' => $list->sortBy('name')->values(),
    'name_desc' => $list->sortByDesc('name')->values(),
    'products' => $list->sortByDesc(fn ($c) => $counts[$c->id] ?? 0)->values(),
    default => $list->values(),
  };
@endphp

<div class="data-table-wrap">
  <table class="data-table">
    <thead>
      <tr><th>Icon</th><th>Name</th><th>Slug</th><th>My Products</th><th></th></tr>
    </thead>
    <tbody>
      @foreach($list as $c)
        <tr>
          <td>{{ $c->icon ?: '💻' }}</td>
          <td>{{ $c->name }}</td>
          <td>{{ $c->slug }}</td>
          <td>{{ $counts[$c->id] ?? 0 }}</td>
          <td>
            <a class="btn btn-outline btn-sm" href="{{ url('/seller-dashboard/products?category='.$c->id) }}">View products</a>
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>
</div>
@endsection
