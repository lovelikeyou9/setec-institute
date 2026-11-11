@extends('layouts.store')
@section('title', 'Become a Seller — MMT')
@section('content')
@php $existingSeller = Auth::user()?->seller; @endphp
<div class="container" style="padding-top:1.5rem;max-width:720px">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <span>Become a Seller</span>
  </nav>

  <h1 class="section-title" style="margin-bottom:.35rem">Start Selling on MMT</h1>
  <p class="section-sub" style="margin-bottom:1.5rem">Reach tech buyers across Cambodia — computers, networking, gaming, cameras, DIY &amp; more.</p>

  @guest
    <div class="form-card">
      <p style="margin-bottom:1rem">You need a customer account before applying to sell.</p>
      <a class="btn btn-primary" href="{{ url('/login') }}">Login to apply</a>
      <a class="btn btn-outline" href="{{ url('/register') }}" style="margin-left:.5rem">Register</a>
    </div>
  @else
    @if(Auth::check() && !$existingSeller)
      <div class="alert alert-info" style="margin-bottom:1.25rem">
        Applying as <strong>{{ Auth::user()->full_name }}</strong>
        ({{ Auth::user()->email }}).
        You must have a registered customer account first — then submit your store application below.
      </div>
    @endif

    @if($existingSeller)
      <div class="form-card">
        <h3>Application Status</h3>
        <p style="margin-bottom:1rem">
          Store: <strong>{{ $existingSeller->store_name }}</strong>
        </p>
        @if($existingSeller->status === 'PENDING')
          <div class="alert alert-info">Your application is <strong>PENDING</strong> admin review. We'll notify you once a decision is made.</div>
        @elseif($existingSeller->status === 'APPROVED')
          <div class="alert alert-success">
            Congratulations! Your store is <strong>APPROVED</strong>.
            <a href="{{ url('/seller-dashboard') }}" class="btn btn-primary btn-sm" style="margin-left:.75rem">Go to Seller Center</a>
          </div>
        @elseif($existingSeller->status === 'REJECTED')
          <div class="alert alert-error">
            Your application was <strong>REJECTED</strong>.
            @if($existingSeller->rejection_reason) Reason: {{ $existingSeller->rejection_reason }}@endif
          </div>
        @elseif($existingSeller->status === 'SUSPENDED' || $existingSeller->status === 'BANNED')
          <div class="alert alert-error">Your seller account is currently <strong>{{ $existingSeller->status }}</strong>. Please contact MMT support.</div>
        @endif
      </div>
    @else
      <div class="form-card">
        <h3>Seller Application</h3>
        <form action="{{ url('/become-seller') }}" method="post">
          @csrf
          <div class="form-grid">
            <div class="form-group">
              <label for="store_name">Store Name *</label>
              <input type="text" id="store_name" name="store_name" required placeholder="e.g. TechZone PP" value="{{ old('store_name') }}"/>
            </div>
            <div class="form-group">
              <label for="owner_name">Owner Name *</label>
              <input type="text" id="owner_name" name="owner_name" required placeholder="Full legal name" value="{{ old('owner_name', Auth::user()->full_name) }}"/>
            </div>
            <div class="form-group">
              <label for="phone">Phone *</label>
              <input type="tel" id="phone" name="phone" required placeholder="012 345 678" value="{{ old('phone', Auth::user()->phone) }}"/>
            </div>
            <div class="form-group">
              <label for="country">Country *</label>
              <input type="text" id="country" name="country" required value="{{ old('country', 'Cambodia') }}"/>
            </div>
            <div class="form-group">
              <label for="location">City / Location *</label>
              <input type="text" id="location" name="location" required placeholder="Phnom Penh" value="{{ old('location') }}"/>
            </div>
            <div class="form-group full">
              <label for="address">Business Address *</label>
              <input type="text" id="address" name="address" required placeholder="Street, district, city" value="{{ old('address') }}"/>
            </div>
            <div class="form-group full">
              <label for="description">Store Description</label>
              <textarea id="description" name="description" rows="3" placeholder="What do you sell?">{{ old('description') }}</textarea>
            </div>
          </div>
          <button type="submit" class="btn btn-accent" style="margin-top:1rem">Submit Application</button>
        </form>
      </div>
    @endif
  @endguest
</div>
@endsection
