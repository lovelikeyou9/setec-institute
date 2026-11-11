@extends('layouts.account')
@section('title', 'Addresses — MMT')
@php
  $countries = $countryOptions ?? ['Cambodia', 'Thailand', 'Vietnam', 'Other'];
@endphp
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">My Addresses</h1>
  <button type="button" class="btn btn-primary" data-modal-open="modalAddAddress">+ Add Address</button>
</div>

@if($addresses->isEmpty())
  <div class="empty-state" style="padding:2rem">
    <div class="icon">📍</div>
    <h3>No saved addresses</h3>
    <p>Add a delivery address for faster checkout.</p>
    <button type="button" class="btn btn-primary" style="margin-top:1rem" data-modal-open="modalAddAddress">Add Address</button>
  </div>
@else
  @foreach($addresses as $addr)
    <div class="form-card">
      <div style="display:flex;justify-content:space-between;gap:1rem;flex-wrap:wrap;align-items:flex-start">
        <div>
          <strong>{{ $addr->full_name }}</strong>
          @if($addr->default_address)
            <span class="status-pill green" style="margin-left:.5rem">Default</span>
          @endif
          <p style="margin-top:.35rem;color:var(--text-secondary)">{{ $addr->phone }}</p>
          <p style="color:var(--text-secondary)">{{ $addr->formatted() }}</p>
        </div>
        <div class="table-actions">
          <button type="button" class="btn btn-outline btn-sm"
                  data-edit="editAddressForm"
                  data-modal-open="modalEditAddress"
                  data-action="{{ url('/account/addresses/'.$addr->id.'/edit') }}"
                  data-field-full_name="{{ $addr->full_name }}"
                  data-field-phone="{{ $addr->phone }}"
                  data-field-country="{{ $addr->country }}"
                  data-field-province="{{ $addr->province }}"
                  data-field-district="{{ $addr->district }}"
                  data-field-commune="{{ $addr->commune }}"
                  data-field-street="{{ $addr->street }}">Edit</button>
          <button type="button" class="btn btn-danger btn-sm"
                  data-delete="{{ url('/account/addresses/'.$addr->id.'/delete') }}"
                  data-label="address for {{ $addr->full_name }}">Delete</button>
        </div>
      </div>
    </div>
  @endforeach
@endif
@endsection

@section('modals')
<div class="modal-backdrop" id="modalAddAddress">
  <div class="modal modal-lg">
    <div class="modal-header"><h3>Add Address</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form action="{{ url('/account/addresses') }}" method="post">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group"><label>Full Name *</label><input type="text" name="full_name" required/></div>
          <div class="form-group"><label>Phone *</label><input type="tel" name="phone" required/></div>
          <div class="form-group">
            <label>Country *</label>
            <select name="country" required>
              <option value="">Select country</option>
              @foreach($countries as $c)
                <option value="{{ $c }}" @selected($c === 'Cambodia')>{{ $c }}</option>
              @endforeach
            </select>
          </div>
          <div class="form-group"><label>Province / State *</label><input type="text" name="province" required placeholder="e.g. Phnom Penh"/></div>
          <div class="form-group"><label>District / City *</label><input type="text" name="district" required/></div>
          <div class="form-group"><label>Commune / Sangkat</label><input type="text" name="commune"/></div>
          <div class="form-group"><label>Street Address *</label><input type="text" name="street" required/></div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Save Address</button>
      </div>
    </form>
  </div>
</div>

<div class="modal-backdrop" id="modalEditAddress">
  <div class="modal modal-lg">
    <div class="modal-header"><h3>Edit Address</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form id="editAddressForm" method="post" action="#">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group"><label>Full Name *</label><input type="text" name="full_name" required/></div>
          <div class="form-group"><label>Phone *</label><input type="tel" name="phone" required/></div>
          <div class="form-group">
            <label>Country *</label>
            <select name="country" required>
              <option value="">Select country</option>
              @foreach($countries as $c)
                <option value="{{ $c }}">{{ $c }}</option>
              @endforeach
            </select>
          </div>
          <div class="form-group"><label>Province / State *</label><input type="text" name="province" required/></div>
          <div class="form-group"><label>District / City *</label><input type="text" name="district" required/></div>
          <div class="form-group"><label>Commune / Sangkat</label><input type="text" name="commune"/></div>
          <div class="form-group"><label>Street Address *</label><input type="text" name="street" required/></div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Save Changes</button>
      </div>
    </form>
  </div>
</div>

<div class="modal-backdrop" id="modalDelete">
  <div class="modal">
    <div class="modal-header"><h3>Confirm Delete</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form id="deleteForm" method="post" action="#">
      @csrf
      <div class="modal-body"><p>Are you sure you want to delete <strong id="deleteLabel">this item</strong>?</p></div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-danger">Delete</button>
      </div>
    </form>
  </div>
</div>
@endsection
