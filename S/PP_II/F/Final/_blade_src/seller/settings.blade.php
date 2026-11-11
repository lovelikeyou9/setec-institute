@extends('layouts.seller')
@section('title', 'Store Settings — Seller')
@php
  $user = auth()->user();
  $countries = ['Cambodia','Thailand','Vietnam','Laos','Myanmar','Singapore','Malaysia','Indonesia','Philippines','China','Japan','South Korea','United States','Other'];
  $cover = $user->cover_url ?: asset('images/mme-icon.png');
@endphp
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">Store Settings</h1>
  <div style="display:flex;gap:.5rem;flex-wrap:wrap">
    <button type="button" class="btn btn-outline" data-modal-open="modalEditPersonal">Edit Personal</button>
    <button type="button" class="btn btn-primary" data-modal-open="modalEditStore">Edit Store</button>
  </div>
</div>

<div class="form-card">
  <h3>Personal profile</h3>
  <div class="settings-cover-preview store-banner profile-cover"
       style="margin-bottom:1rem;background-image:url('{{ $cover }}');"></div>
  <div class="settings-profile-head">
    @if($user->avatar_url)
      <img class="settings-avatar-img" src="{{ $user->avatar_url }}" alt="{{ $user->full_name }}"/>
    @else
      <div class="settings-avatar">{{ mb_substr($user->full_name ?? 'U', 0, 1) }}</div>
    @endif
    <div>
      <strong>{{ $user->full_name }}</strong>
      <div style="font-size:.85rem;color:var(--text-muted);margin-top:.25rem">{{ $user->email }}</div>
    </div>
  </div>
</div>

<div class="form-card">
  <h3>Store profile</h3>
  <div class="settings-profile-head">
    @if($seller->logo_url)
      <img class="settings-store-logo" src="{{ $seller->logo_url }}" alt="{{ $seller->store_name }}"/>
    @endif
    <div>
      <strong>{{ $seller->store_name }}</strong>
      <div style="font-size:.85rem;color:var(--text-muted);margin-top:.25rem">{{ $seller->level }} · {{ $seller->status }}</div>
    </div>
  </div>
  <div class="form-grid">
    <div class="form-group"><label>Store Name</label><input type="text" value="{{ $seller->store_name }}" readonly/></div>
    <div class="form-group"><label>Owner</label><input type="text" value="{{ $seller->owner_name }}" readonly/></div>
    <div class="form-group"><label>Phone</label><input type="text" value="{{ $seller->phone }}" readonly/></div>
    <div class="form-group"><label>Country</label><input type="text" value="{{ $seller->country ?: '—' }}" readonly/></div>
    <div class="form-group"><label>Location</label><input type="text" value="{{ $seller->location }}" readonly/></div>
    <div class="form-group full"><label>Address</label><input type="text" value="{{ $seller->address }}" readonly/></div>
    <div class="form-group full"><label>Description</label><textarea rows="3" readonly>{{ $seller->description }}</textarea></div>
  </div>
  <a href="{{ url('/sellers/'.$seller->id) }}" class="btn btn-outline btn-sm" style="margin-top:.75rem">View public store profile</a>
</div>
@endsection

@section('modals')
<div class="modal-backdrop" id="modalEditPersonal">
  <div class="modal">
    <div class="modal-header"><h3>Edit Personal Profile</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form action="{{ url('/seller-dashboard/settings/personal') }}" method="post" enctype="multipart/form-data">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group full">
            <label>Cover photo (owner profile)</label>
            <div class="logo-field logo-field-banner" data-logo-field>
              <div class="logo-preview-box logo-preview-banner">
                <img class="logo-preview-img" src="{{ $user->cover_url }}" alt="Cover" @if(!$user->cover_url) hidden @endif/>
                <span class="logo-preview-empty" @if($user->cover_url) hidden @endif>No cover</span>
              </div>
              <div class="logo-field-controls">
                <input type="file" name="coverFile" accept="image/*" class="logo-file-input"/>
                <input type="hidden" name="coverUrl" class="logo-url-input" value="{{ $user->cover_url }}"/>
                <p class="logo-field-hint">Wide banner on your public owner profile.</p>
              </div>
            </div>
          </div>
          <div class="form-group full">
            <label>Profile image</label>
            <div class="logo-field" data-logo-field>
              <div class="logo-preview-box logo-preview-round">
                <img class="logo-preview-img" src="{{ $user->avatar_url }}" alt="{{ $user->full_name }}" @if(!$user->avatar_url) hidden @endif/>
                <span class="logo-preview-empty" @if($user->avatar_url) hidden @endif>No image</span>
              </div>
              <div class="logo-field-controls">
                <input type="file" name="avatarFile" accept="image/*" class="logo-file-input"/>
                <input type="hidden" name="avatarUrl" class="logo-url-input" value="{{ $user->avatar_url }}"/>
                <p class="logo-field-hint">Shown on your public owner profile and reviews.</p>
              </div>
            </div>
          </div>
          <div class="form-group"><label>Full Name *</label><input type="text" name="fullName" required value="{{ $user->full_name }}"/></div>
          <div class="form-group"><label>Phone</label><input type="tel" name="phone" value="{{ $user->phone }}"/></div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Save Personal</button>
      </div>
    </form>
  </div>
</div>

<div class="modal-backdrop" id="modalEditStore">
  <div class="modal modal-lg">
    <div class="modal-header"><h3>Edit Store</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form action="{{ url('/seller-dashboard/settings') }}" method="post" enctype="multipart/form-data">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group full">
            <label>Store logo</label>
            <div class="logo-field" data-logo-field>
              <div class="logo-preview-box">
                <img class="logo-preview-img" src="{{ $seller->logo_url }}" alt="{{ $seller->store_name }}" @if(!$seller->logo_url) hidden @endif/>
                <span class="logo-preview-empty" @if($seller->logo_url) hidden @endif>No logo</span>
              </div>
              <div class="logo-field-controls">
                <input type="file" name="logoFile" accept="image/*" class="logo-file-input"/>
                <input type="hidden" name="logoUrl" class="logo-url-input" value="{{ $seller->logo_url }}"/>
                <p class="logo-field-hint">Square logo works best for your storefront.</p>
              </div>
            </div>
          </div>
          <div class="form-group full">
            <label>Cover photo (store profile)</label>
            <div class="logo-field logo-field-banner" data-logo-field>
              <div class="logo-preview-box logo-preview-banner">
                <img class="logo-preview-img" src="{{ $seller->banner_url }}" alt="Banner" @if(!$seller->banner_url) hidden @endif/>
                <span class="logo-preview-empty" @if($seller->banner_url) hidden @endif>No banner</span>
              </div>
              <div class="logo-field-controls">
                <input type="file" name="bannerFile" accept="image/*" class="logo-file-input"/>
                <input type="hidden" name="bannerUrl" class="logo-url-input" value="{{ $seller->banner_url }}"/>
                <p class="logo-field-hint">Wide cover at the top of your seller store page.</p>
              </div>
            </div>
          </div>
          <div class="form-group"><label>Store Name *</label><input type="text" name="storeName" required value="{{ $seller->store_name }}"/></div>
          <div class="form-group"><label>Owner *</label><input type="text" name="ownerName" required value="{{ $seller->owner_name }}"/></div>
          <div class="form-group"><label>Phone</label><input type="text" name="phone" value="{{ $seller->phone }}"/></div>
          <div class="form-group">
            <label>Country *</label>
            <select name="country" required>
              <option value="">Select country</option>
              @foreach($countries as $c)
                <option value="{{ $c }}" @selected($seller->country === $c)>{{ $c }}</option>
              @endforeach
            </select>
          </div>
          <div class="form-group"><label>Location</label><input type="text" name="location" value="{{ $seller->location }}"/></div>
          <div class="form-group full"><label>Address</label><input type="text" name="address" value="{{ $seller->address }}"/></div>
          <div class="form-group full"><label>Description</label><textarea name="description" rows="3">{{ $seller->description }}</textarea></div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Save Store</button>
      </div>
    </form>
  </div>
</div>
@endsection
