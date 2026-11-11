@extends('layouts.account')
@section('title', 'Account Settings — MMT')
@php
  $user = Auth::user();
  $cover = $user->cover_url ?: asset('images/mme-icon.png');
@endphp
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">Account Settings</h1>
  <div style="display:flex;gap:.5rem;flex-wrap:wrap">
    <button type="button" class="btn btn-primary" data-modal-open="modalEditProfile">Edit Profile</button>
  </div>
</div>

<div class="form-card">
  <h3>Profile</h3>
  <div class="settings-cover-preview store-banner profile-cover"
       style="margin-bottom:1rem;background-image:url('{{ str_replace(' ', '%20', $cover) }}')"></div>
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
  <div class="form-grid">
    <div class="form-group"><label>Full Name</label><input type="text" value="{{ $user->full_name }}" readonly/></div>
    <div class="form-group"><label>Email</label><input type="email" value="{{ $user->email }}" readonly/></div>
    <div class="form-group"><label>Phone</label><input type="tel" value="{{ $user->phone }}" readonly/></div>
    <div class="form-group"><label>Role</label><input type="text" value="{{ $user->role }}" readonly/></div>
    <div class="form-group">
      <label>Member Since</label>
      <input type="text" value="{{ optional($user->created_at)->format('d M Y') ?: '—' }}" readonly/>
    </div>
  </div>
</div>

<div class="form-card">
  <h3>Quick Links</h3>
  <div style="display:flex;flex-wrap:wrap;gap:.5rem">
    <a href="{{ url('/account/addresses') }}" class="btn btn-outline btn-sm">Manage Addresses</a>
    <a href="{{ url('/account/orders') }}" class="btn btn-outline btn-sm">View Orders</a>
    @if($user->role === 'CUSTOMER')
      <a href="{{ url('/become-seller') }}" class="btn btn-outline btn-sm">Become a Seller</a>
    @endif
    @if(in_array($user->role, ['SELLER', 'ADMIN'], true))
      <a href="{{ url('/seller-dashboard') }}" class="btn btn-outline btn-sm">Seller Center</a>
    @endif
  </div>
</div>
@endsection

@section('modals')
<div class="modal-backdrop" id="modalEditProfile">
  <div class="modal">
    <div class="modal-header"><h3>Edit Profile</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form action="{{ url('/account/settings') }}" method="post" enctype="multipart/form-data">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group full">
            <label>Cover photo</label>
            <div class="logo-field logo-field-banner" data-logo-field>
              <div class="logo-preview-box logo-preview-banner">
                <img class="logo-preview-img" src="{{ $user->cover_url }}" alt="Cover"
                     style="{{ $user->cover_url ? '' : 'display:none' }}"/>
                <span class="logo-preview-empty" style="{{ $user->cover_url ? 'display:none' : '' }}">No cover</span>
              </div>
              <div class="logo-field-controls">
                <input type="file" name="cover" accept="image/*" class="logo-file-input"/>
                <input type="hidden" name="cover_url" class="logo-url-input" value="{{ $user->cover_url }}"/>
                <p class="logo-field-hint">Wide banner for your public profile.</p>
              </div>
            </div>
          </div>
          <div class="form-group full">
            <label>Profile image</label>
            <div class="logo-field" data-logo-field>
              <div class="logo-preview-box logo-preview-round">
                <img class="logo-preview-img" src="{{ $user->avatar_url }}" alt="{{ $user->full_name }}"
                     style="{{ $user->avatar_url ? '' : 'display:none' }}"/>
                <span class="logo-preview-empty" style="{{ $user->avatar_url ? 'display:none' : '' }}">No image</span>
              </div>
              <div class="logo-field-controls">
                <input type="file" name="avatar" accept="image/*" class="logo-file-input"/>
                <input type="hidden" name="avatar_url" class="logo-url-input" value="{{ $user->avatar_url }}"/>
                <p class="logo-field-hint">Upload a square photo (PNG/JPG). Preview updates instantly.</p>
              </div>
            </div>
          </div>
          <div class="form-group"><label>Full Name *</label><input type="text" name="full_name" required value="{{ $user->full_name }}"/></div>
          <div class="form-group"><label>Phone</label><input type="tel" name="phone" value="{{ $user->phone }}"/></div>
          <div class="form-group full">
            <label>Email</label>
            <input type="email" value="{{ $user->email }}" readonly/>
            <small style="color:var(--text-muted);font-size:.75rem">Email cannot be changed here</small>
          </div>
          <div class="form-group"><label>New password</label><input type="password" name="password" autocomplete="new-password"/></div>
          <div class="form-group"><label>Confirm password</label><input type="password" name="password_confirmation" autocomplete="new-password"/></div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Save Changes</button>
      </div>
    </form>
  </div>
</div>
@endsection
