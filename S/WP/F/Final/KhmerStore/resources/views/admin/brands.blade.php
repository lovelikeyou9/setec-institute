@extends('layouts.admin')
@section('title', 'Brands — Admin')
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">Brands</h1>
  <button type="button" class="btn btn-primary" data-modal-open="modalAddBrand">+ Add Brand</button>
</div>

<form class="dash-search" action="{{ url('/admin/brands') }}" method="get">
  <input type="search" name="q" value="{{ request('q') }}" placeholder="Search brands..."/>
  <label class="dash-sort">
    <span>Sort</span>
    <select name="sort" onchange="this.form.submit()">
      <option value="name_asc" @selected(request('sort','name_asc')==='name_asc')>Name A–Z</option>
      <option value="name_desc" @selected(request('sort')==='name_desc')>Name Z–A</option>
      <option value="newest" @selected(request('sort')==='newest')>Newest</option>
      <option value="oldest" @selected(request('sort')==='oldest')>Oldest</option>
    </select>
  </label>
  <button type="submit" class="btn btn-primary btn-sm">Search</button>
  @if(request('q') || (request('sort') && request('sort') !== 'name_asc'))
    <a href="{{ url('/admin/brands') }}" class="btn btn-outline btn-sm">Clear</a>
  @endif
</form>

@if($brands->isEmpty())
  <div class="empty-state" style="margin-top:1rem">
    <h3>No brands found</h3>
    <p>Try another search term or clear filters.</p>
  </div>
@else
<div class="data-table-wrap">
  <table class="data-table">
    <thead>
      <tr><th>Logo</th><th>ID</th><th>Name</th><th>Slug</th><th>Active</th><th>Actions</th></tr>
    </thead>
    <tbody>
      @foreach($brands as $b)
        <tr>
          <td>
            @if($b->logo_url)
              <img class="table-thumb table-thumb-logo" src="{{ media_url($b->logo_url) }}" alt="{{ $b->name }}"/>
            @else
              <span class="table-thumb-empty">—</span>
            @endif
          </td>
          <td>{{ $b->id }}</td>
          <td>{{ $b->name }}</td>
          <td>{{ $b->slug }}</td>
          <td>
            <span class="status-pill {{ $b->active ? 'green' : 'gray' }}">{{ $b->active ? 'Yes' : 'No' }}</span>
          </td>
          <td class="table-actions">
            <button type="button" class="btn btn-outline btn-sm"
                    data-edit="editBrandForm"
                    data-modal-open="modalEditBrand"
                    data-action="{{ url('/admin/brands/'.$b->id.'/edit') }}"
                    data-field-name="{{ $b->name }}"
                    data-field-logoUrl="{{ $b->logo_url }}"
                    data-field-active="{{ $b->active ? 'true' : 'false' }}">Edit</button>
            <button type="button" class="btn btn-danger btn-sm"
                    data-delete="{{ url('/admin/brands/'.$b->id.'/delete') }}"
                    data-label="brand {{ $b->name }}">Delete</button>
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>
</div>
@endif
@endsection

@section('modals')
<div class="modal-backdrop" id="modalAddBrand">
  <div class="modal">
    <div class="modal-header"><h3>Add Brand</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form action="{{ url('/admin/brands') }}" method="post" enctype="multipart/form-data" id="addBrandForm">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group full"><label>Brand Name *</label><input type="text" name="name" required placeholder="e.g. Corsair"/></div>
          <div class="form-group full">
            <label>Brand Logo</label>
            <div class="logo-field" data-logo-field>
              <div class="logo-preview-box">
                <img class="logo-preview-img" alt="Logo preview" hidden/>
                <span class="logo-preview-empty">No image selected</span>
              </div>
              <div class="logo-field-controls">
                <input type="file" name="logoFile" accept="image/*" class="logo-file-input"/>
                <input type="text" name="logoUrl" class="logo-url-input" placeholder="Or paste logo URL /uploads/logo/..."/>
                <p class="logo-field-hint">Upload an image or paste a URL. Preview updates instantly.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Add Brand</button>
      </div>
    </form>
  </div>
</div>

<div class="modal-backdrop" id="modalEditBrand">
  <div class="modal">
    <div class="modal-header"><h3>Edit Brand</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form id="editBrandForm" method="post" action="#" enctype="multipart/form-data">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group full"><label>Brand Name *</label><input type="text" name="name" required/></div>
          <div class="form-group full">
            <label>Brand Logo</label>
            <div class="logo-field" data-logo-field>
              <div class="logo-preview-box">
                <img class="logo-preview-img" alt="Logo preview" hidden/>
                <span class="logo-preview-empty">No image</span>
              </div>
              <div class="logo-field-controls">
                <input type="file" name="logoFile" accept="image/*" class="logo-file-input"/>
                <input type="text" name="logoUrl" class="logo-url-input" placeholder="Logo URL"/>
                <p class="logo-field-hint">Current logo shows above. Upload a new file to replace it.</p>
              </div>
            </div>
          </div>
          <div class="form-group"><label><input type="checkbox" name="active" value="true"/> Active</label></div>
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

@push('scripts')
<script>
(function () {
  document.querySelectorAll('[data-edit="editBrandForm"]').forEach((btn) => {
    btn.addEventListener('click', () => {
      requestAnimationFrame(() => {
        const form = document.getElementById('editBrandForm');
        if (!form) return;
        const field = form.querySelector('[data-logo-field]');
        const urlInput = form.querySelector('.logo-url-input');
        const fileInput = form.querySelector('.logo-file-input');
        if (fileInput) fileInput.value = '';
        if (field && field._setLogoPreview) {
          field._setLogoPreview(urlInput ? urlInput.value.trim() : '');
        }
      });
    });
  });
  document.querySelectorAll('[data-modal-open="modalAddBrand"]').forEach((btn) => {
    btn.addEventListener('click', () => {
      const form = document.getElementById('addBrandForm');
      if (!form) return;
      form.reset();
      const field = form.querySelector('[data-logo-field]');
      if (field && field._setLogoPreview) field._setLogoPreview('');
    });
  });
})();
</script>
@endpush
