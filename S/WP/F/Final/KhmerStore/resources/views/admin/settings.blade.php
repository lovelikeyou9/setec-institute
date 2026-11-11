@extends('layouts.admin')
@section('title', 'Settings — Admin')
@section('content')
<h1 style="font-size:1.4rem;margin-bottom:1rem">Platform Settings</h1>

<div class="form-card">
  <h3>MMT Configuration</h3>
  <div class="form-grid">
    <div class="form-group">
      <label>Platform Name</label>
      <input type="text" value="MMT" readonly/>
    </div>
    <div class="form-group">
      <label>Default Commission</label>
      <input type="text" value="3%" readonly/>
    </div>
    <div class="form-group">
      <label>Currency</label>
      <input type="text" value="USD ($)" readonly/>
    </div>
    <div class="form-group">
      <label>Support Email</label>
      <input type="text" value="support@mmt.store" readonly/>
    </div>
    <div class="form-group">
      <label>Buyer protection</label>
      <input type="text" value="3 days after delivery" readonly/>
    </div>
    <div class="form-group">
      <label>Shipping</label>
      <input type="text" value="STANDARD $2 · EXPRESS $5" readonly/>
    </div>
  </div>
  <p style="font-size:.85rem;color:var(--text-muted);margin-top:.75rem">
    Platform settings are currently managed via application configuration. Category and brand lists are controlled from their respective admin pages.
  </p>
</div>

<div class="form-card">
  <h3>Quick Actions</h3>
  <div style="display:flex;flex-wrap:wrap;gap:.5rem">
    <a href="{{ url('/admin/seller-applications') }}" class="btn btn-outline btn-sm">Review Applications</a>
    <a href="{{ url('/admin/categories') }}" class="btn btn-outline btn-sm">Manage Categories</a>
    <a href="{{ url('/admin/brands') }}" class="btn btn-outline btn-sm">Manage Brands</a>
    <a href="{{ url('/admin/commissions') }}" class="btn btn-outline btn-sm">View Commissions</a>
  </div>
</div>
@endsection
