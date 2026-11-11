@extends('layouts.admin')
@section('title', 'Sellers — Admin')
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">Sellers Lists</h1>
  <button type="button" class="btn btn-primary" data-modal-open="modalAddSeller">+ Add Seller</button>
</div>
<p style="color:var(--text-muted);margin:-.5rem 0 1rem;font-size:.9rem">
  Manage stores. <strong>Ban store</strong> stops selling only — the person can still log in and shop as a customer.
  To block login entirely, use <strong>Users Lists → Ban login</strong>.
</p>

<form class="dash-search" action="{{ url('/admin/sellers') }}" method="get">
  <input type="search" name="q" value="{{ request('q') }}" placeholder="Search store, owner, email, location..."/>
  <label class="dash-sort">
    <span>Sort</span>
    <select name="sort" onchange="this.form.submit()">
      <option value="newest" @selected(request('sort','newest')==='newest')>Newest</option>
      <option value="oldest" @selected(request('sort')==='oldest')>Oldest</option>
      <option value="name_asc" @selected(request('sort')==='name_asc')>Store A–Z</option>
      <option value="name_desc" @selected(request('sort')==='name_desc')>Store Z–A</option>
    </select>
  </label>
  <button type="submit" class="btn btn-primary btn-sm">Search</button>
  @if(request('q') || (request('sort') && request('sort') !== 'newest'))
    <a href="{{ url('/admin/sellers') }}" class="btn btn-outline btn-sm">Clear</a>
  @endif
</form>

<div class="data-table-wrap">
  <table class="data-table">
    <thead>
      <tr><th>Store</th><th>Owner</th><th>Email</th><th>Location</th><th>Status</th><th>Level</th><th>Products</th><th>Actions</th></tr>
    </thead>
    <tbody>
      @foreach($sellers as $s)
        <tr>
          <td>{{ $s->store_name }}</td>
          <td>{{ $s->owner_name }}</td>
          <td>{{ $s->user->email ?? '—' }}</td>
          <td>{{ $s->location ?? '—' }}</td>
          <td>
            @php
              $statusClass = match($s->status) {
                'APPROVED' => 'green',
                'SUSPENDED' => 'red',
                'PENDING' => 'yellow',
                default => 'gray',
              };
            @endphp
            <span class="status-pill {{ $statusClass }}">{{ $s->status }}</span>
          </td>
          <td>{{ $s->level }}</td>
          <td>{{ $s->total_products ?? 0 }}</td>
          <td class="table-actions">
            <button type="button" class="btn btn-outline btn-sm"
                    data-edit="editSellerForm"
                    data-modal-open="modalEditSeller"
                    data-action="{{ url('/admin/sellers/'.$s->id.'/edit') }}"
                    data-field-storeName="{{ $s->store_name }}"
                    data-field-ownerName="{{ $s->owner_name }}"
                    data-field-phone="{{ $s->phone }}"
                    data-field-country="{{ $s->country }}"
                    data-field-location="{{ $s->location }}"
                    data-field-address="{{ $s->address }}"
                    data-field-status="{{ $s->status }}"
                    data-field-level="{{ $s->level }}">Edit</button>
            @if($s->status !== 'SUSPENDED' && $s->status !== 'REJECTED')
              <form action="{{ url('/admin/sellers/'.$s->id.'/ban') }}" method="post" style="display:inline" title="Stop selling; keep login as customer">@csrf
                <button type="submit" class="btn btn-sm btn-danger">Ban store</button>
              </form>
            @endif
            @if($s->status === 'SUSPENDED')
              <form action="{{ url('/admin/sellers/'.$s->id.'/unban') }}" method="post" style="display:inline" title="Restore seller role and products">@csrf
                <button type="submit" class="btn btn-sm btn-primary">Unban store</button>
              </form>
            @endif
            <button type="button" class="btn btn-danger btn-sm"
                    data-delete="{{ url('/admin/sellers/'.$s->id.'/delete') }}"
                    data-label="seller {{ $s->store_name }}">Delete</button>
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>
</div>
{{ $sellers->links() }}
@endsection

@section('modals')
@php $countries = $countryOptions ?? ['Cambodia','Thailand','Vietnam','Other']; @endphp
<div class="modal-backdrop" id="modalAddSeller">
  <div class="modal modal-lg">
    <div class="modal-header"><h3>Add Seller</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form action="{{ url('/admin/sellers') }}" method="post">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group"><label>Store Name *</label><input type="text" name="storeName" required/></div>
          <div class="form-group"><label>Owner Name *</label><input type="text" name="ownerName" required/></div>
          <div class="form-group"><label>Login Email *</label><input type="email" name="email" required/></div>
          <div class="form-group"><label>Password *</label><input type="password" name="password" required minlength="6"/></div>
          <div class="form-group"><label>Phone</label><input type="text" name="phone"/></div>
          <div class="form-group">
            <label>Country</label>
            <select name="country">
              <option value="">Select country</option>
              @foreach($countries as $c)
                <option value="{{ $c }}">{{ $c }}</option>
              @endforeach
            </select>
          </div>
          <div class="form-group"><label>Location</label><input type="text" name="location"/></div>
          <div class="form-group"><label>Address</label><input type="text" name="address"/></div>
          <div class="form-group">
            <label>Level</label>
            <select name="level">
              <option value="VERIFIED">VERIFIED</option>
              <option value="NEW">NEW</option>
              <option value="TRUSTED">TRUSTED</option>
              <option value="TOP">TOP</option>
            </select>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Add Seller</button>
      </div>
    </form>
  </div>
</div>

<div class="modal-backdrop" id="modalEditSeller">
  <div class="modal modal-lg">
    <div class="modal-header"><h3>Edit Seller</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form id="editSellerForm" method="post" action="#">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group"><label>Store Name *</label><input type="text" name="storeName" required/></div>
          <div class="form-group"><label>Owner Name *</label><input type="text" name="ownerName" required/></div>
          <div class="form-group"><label>Phone</label><input type="text" name="phone"/></div>
          <div class="form-group">
            <label>Country</label>
            <select name="country">
              <option value="">Select country</option>
              @foreach($countries as $c)
                <option value="{{ $c }}">{{ $c }}</option>
              @endforeach
            </select>
          </div>
          <div class="form-group"><label>Location</label><input type="text" name="location"/></div>
          <div class="form-group"><label>Address</label><input type="text" name="address"/></div>
          <div class="form-group">
            <label>Status</label>
            <select name="status">
              <option value="PENDING">PENDING</option>
              <option value="APPROVED">APPROVED</option>
              <option value="REJECTED">REJECTED</option>
              <option value="SUSPENDED">SUSPENDED</option>
            </select>
          </div>
          <div class="form-group">
            <label>Level</label>
            <select name="level">
              <option value="NEW">NEW</option>
              <option value="VERIFIED">VERIFIED</option>
              <option value="TRUSTED">TRUSTED</option>
              <option value="TOP">TOP</option>
            </select>
          </div>
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
