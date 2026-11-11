@extends('layouts.admin')
@section('title', 'Categories — Admin')
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">Categories</h1>
  <button type="button" class="btn btn-primary" data-modal-open="modalAddCategory">+ Add Category</button>
</div>

<form class="dash-search" action="{{ url('/admin/categories') }}" method="get">
  <input type="search" name="q" value="{{ request('q') }}" placeholder="Search categories..."/>
  <label class="dash-sort">
    <span>Sort</span>
    <select name="sort" onchange="this.form.submit()">
      <option value="sort" @selected(request('sort','sort')==='sort')>Sort order</option>
      <option value="name_asc" @selected(request('sort')==='name_asc')>Name A–Z</option>
      <option value="name_desc" @selected(request('sort')==='name_desc')>Name Z–A</option>
      <option value="newest" @selected(request('sort')==='newest')>Newest</option>
      <option value="oldest" @selected(request('sort')==='oldest')>Oldest</option>
    </select>
  </label>
  <button type="submit" class="btn btn-primary btn-sm">Search</button>
  @if(request('q') || (request('sort') && request('sort') !== 'sort'))
    <a href="{{ url('/admin/categories') }}" class="btn btn-outline btn-sm">Clear</a>
  @endif
</form>
@if($categories->isEmpty())
  <div class="empty-state" style="margin-top:1rem">
    <h3>No categories found</h3>
    <p>Try another search term or clear filters.</p>
  </div>
@else

<div class="data-table-wrap">
  <table class="data-table">
    <thead>
      <tr><th>Icon</th><th>Name</th><th>Slug</th><th>Sort</th><th>Active</th><th>Actions</th></tr>
    </thead>
    <tbody>
      @foreach($categories as $c)
        <tr>
          <td>{{ $c->icon }}</td>
          <td>{{ $c->name }}</td>
          <td>{{ $c->slug }}</td>
          <td>{{ $c->sort_order }}</td>
          <td>
            <span class="status-pill {{ $c->active ? 'green' : 'gray' }}">{{ $c->active ? 'Yes' : 'No' }}</span>
          </td>
          <td class="table-actions">
            <button type="button" class="btn btn-outline btn-sm"
                    data-edit="editCategoryForm"
                    data-modal-open="modalEditCategory"
                    data-action="{{ url('/admin/categories/'.$c->id.'/edit') }}"
                    data-field-name="{{ $c->name }}"
                    data-field-icon="{{ $c->icon }}"
                    data-field-active="{{ $c->active ? 'true' : 'false' }}">Edit</button>
            <button type="button" class="btn btn-danger btn-sm"
                    data-delete="{{ url('/admin/categories/'.$c->id.'/delete') }}"
                    data-label="category {{ $c->name }}">Delete</button>
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>
</div>
@endif
@endsection

@section('modals')
<div class="modal-backdrop" id="modalAddCategory">
  <div class="modal">
    <div class="modal-header"><h3>Add Category</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form action="{{ url('/admin/categories') }}" method="post">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group"><label>Name *</label><input type="text" name="name" required placeholder="e.g. Cooling"/></div>
          <div class="form-group"><label>Icon *</label><input type="text" name="icon" required placeholder="❄️"/></div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Add Category</button>
      </div>
    </form>
  </div>
</div>

<div class="modal-backdrop" id="modalEditCategory">
  <div class="modal">
    <div class="modal-header"><h3>Edit Category</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form id="editCategoryForm" method="post" action="#">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group"><label>Name *</label><input type="text" name="name" required/></div>
          <div class="form-group"><label>Icon *</label><input type="text" name="icon" required placeholder="❄️"/></div>
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
