@extends('layouts.admin')
@section('title', 'Products — Admin')
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">All Products</h1>
</div>

<form class="dash-search" action="{{ url('/admin/products') }}" method="get">
  <input type="search" name="q" value="{{ request('q') }}" placeholder="Search products, brand, category, seller..."/>
  <label class="dash-sort">
    <span>Category</span>
    <select name="category" onchange="this.form.submit()">
      <option value="">All</option>
      @foreach($categories as $c)
        <option value="{{ $c->id }}" @selected(request('category')==$c->id)>{{ $c->name }}</option>
      @endforeach
    </select>
  </label>
  <label class="dash-sort">
    <span>Brand</span>
    <select name="brand" onchange="this.form.submit()">
      <option value="">All</option>
      @foreach($brands as $b)
        <option value="{{ $b->id }}" @selected(request('brand')==$b->id)>{{ $b->name }}</option>
      @endforeach
    </select>
  </label>
  <button type="submit" class="btn btn-primary btn-sm">Search</button>
  @if(request('q') || request('category') || request('brand'))
    <a href="{{ url('/admin/products') }}" class="btn btn-outline btn-sm">Clear</a>
  @endif
</form>

<div class="data-table-wrap">
  <table class="data-table">
    <thead>
      <tr><th>Image</th><th>ID</th><th>Name</th><th>Seller</th><th>Category</th><th>Brand</th><th>Price</th><th>Stock</th><th>Active</th><th>Actions</th></tr>
    </thead>
    <tbody>
      @foreach($products as $p)
        <tr class="product-row-click"
            data-view-product
            data-id="{{ $p->id }}"
            data-name="{{ $p->name }}"
            data-slug="{{ $p->slug }}"
            data-seller="{{ $p->seller->store_name ?? '—' }}"
            data-category="{{ $p->category->name ?? '—' }}"
            data-brand="{{ $p->brand->name ?? '—' }}"
            data-price="{{ number_format($p->price, 2, '.', '') }}"
            data-original="{{ $p->original_price ? number_format($p->original_price, 2, '.', '') : '' }}"
            data-stock="{{ $p->stock }}"
            data-sku="{{ $p->sku }}"
            data-warranty="{{ $p->warranty }}"
            data-image="{{ media_url($p->primaryImageUrl()) }}"
            data-gallery="{{ implode(',', $p->galleryList()) }}"
            data-description="{{ $p->description }}"
            data-specs="{{ $p->specifications }}"
            data-color="{{ $p->color_options }}"
            data-storage="{{ $p->storage_options }}"
            data-ram="{{ $p->ram_options }}"
            data-option-groups="{{ $p->option_groups }}"
            data-active="{{ $p->active ? 'true' : 'false' }}"
            data-edit-action="{{ url('/admin/products/'.$p->id.'/edit') }}"
            data-field-name="{{ $p->name }}"
            data-field-categoryId="{{ $p->category_id }}"
            data-field-brandId="{{ $p->brand_id }}"
            data-field-price="{{ $p->price }}"
            data-field-originalPrice="{{ $p->original_price }}"
            data-field-stock="{{ $p->stock }}"
            data-field-sku="{{ $p->sku }}"
            data-field-warranty="{{ $p->warranty }}"
            data-field-imageUrl="{{ $p->primaryImageUrl() }}"
            data-field-galleryKeep="{{ implode(',', $p->galleryList()) }}"
            data-field-description="{{ $p->description }}"
            data-field-specifications="{{ $p->specifications }}"
            data-field-colorOptions="{{ $p->color_options }}"
            data-field-storageOptions="{{ $p->storage_options }}"
            data-field-ramOptions="{{ $p->ram_options }}"
            data-field-optionGroupsJson="{{ $p->option_groups }}"
            data-field-active="{{ $p->active ? 'true' : 'false' }}">
          <td>
            <button type="button" class="table-thumb-btn" data-view-product tabindex="-1">
              <img class="table-thumb" src="{{ media_url($p->primaryImageUrl()) }}" alt="{{ $p->name }}"/>
            </button>
          </td>
          <td>{{ $p->id }}</td>
          <td><button type="button" class="table-link-btn" data-view-product>{{ $p->name }}</button></td>
          <td>{{ $p->seller->store_name ?? '—' }}</td>
          <td>{{ $p->category->name ?? '—' }}</td>
          <td>{{ $p->brand->name ?? '—' }}</td>
          <td>${{ number_format($p->price, 2) }}</td>
          <td>{{ $p->stock }}</td>
          <td>
            <span class="status-pill {{ $p->active ? 'green' : 'gray' }}">{{ $p->active ? 'Yes' : 'No' }}</span>
          </td>
          <td class="table-actions" onclick="event.stopPropagation()">
            <button type="button" class="btn btn-outline btn-sm"
                    data-edit="editProductForm"
                    data-modal-open="modalEditProduct"
                    data-action="{{ url('/admin/products/'.$p->id.'/edit') }}"
                    data-field-name="{{ $p->name }}"
                    data-field-categoryId="{{ $p->category_id }}"
                    data-field-brandId="{{ $p->brand_id }}"
                    data-field-price="{{ $p->price }}"
                    data-field-originalPrice="{{ $p->original_price }}"
                    data-field-stock="{{ $p->stock }}"
                    data-field-sku="{{ $p->sku }}"
                    data-field-warranty="{{ $p->warranty }}"
                    data-field-imageUrl="{{ $p->primaryImageUrl() }}"
                    data-field-galleryKeep="{{ implode(',', $p->galleryList()) }}"
                    data-field-description="{{ $p->description }}"
                    data-field-specifications="{{ $p->specifications }}"
                    data-field-colorOptions="{{ $p->color_options }}"
                    data-field-storageOptions="{{ $p->storage_options }}"
                    data-field-ramOptions="{{ $p->ram_options }}"
                    data-field-optionGroupsJson="{{ $p->option_groups }}"
                    data-field-active="{{ $p->active ? 'true' : 'false' }}">Edit</button>
            <button type="button" class="btn btn-danger btn-sm"
                    data-delete="{{ url('/admin/products/'.$p->id.'/delete') }}"
                    data-label="product {{ $p->name }}">Delete</button>
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>
</div>
{{ $products->links() }}
@endsection

@section('modals')
<div class="modal-backdrop" id="modalViewProduct">
  <div class="modal modal-lg">
    <div class="modal-header"><h3>Product Details</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <div class="modal-body">
      @include('partials.product-view-body', ['showSeller' => true])
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-outline" data-modal-close>Close</button>
      <a id="viewProductStorefront" href="#" class="btn btn-outline" target="_blank" rel="noopener">Open storefront</a>
      <button type="button" class="btn btn-primary" id="viewProductEditBtn">Edit</button>
    </div>
  </div>
</div>

<div class="modal-backdrop" id="modalEditProduct">
  <div class="modal modal-lg">
    <div class="modal-header"><h3>Edit Product</h3><button type="button" class="modal-close" data-modal-close>&times;</button></div>
    <form id="editProductForm" method="post" action="#" enctype="multipart/form-data">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group full"><label>Product Name *</label><input type="text" name="name" required/></div>
          <div class="form-group">
            <label>Category *</label>
            <select name="categoryId" required>
              @foreach($categories as $c)
                <option value="{{ $c->id }}" data-slug="{{ $c->slug }}">{{ $c->name }}</option>
              @endforeach
            </select>
          </div>
          <div class="form-group">
            <label>Brand *</label>
            <select name="brandId" required>
              @foreach($brands as $b)
                <option value="{{ $b->id }}">{{ $b->name }}</option>
              @endforeach
            </select>
          </div>
          <div class="form-group"><label>Price *</label><input type="number" name="price" required min="0" step="0.01"/></div>
          <div class="form-group"><label>Original Price</label><input type="number" name="originalPrice" min="0" step="0.01"/></div>
          <div class="form-group"><label>Stock *</label><input type="number" name="stock" required min="0"/></div>
          <div class="form-group"><label>SKU</label><input type="text" name="sku"/></div>
          <div class="form-group"><label>Warranty</label><input type="text" name="warranty"/></div>
          <div class="form-group full">
            <label>Product Images</label>
            <input type="hidden" name="galleryKeep" id="editGalleryKeep" value=""/>
            <div class="multi-image-previews" id="editGalleryList" data-existing-gallery></div>
            <input type="file" name="imageFiles[]" accept="image/*" multiple data-image-multi="edit"/>
            <small style="color:var(--text-muted);font-size:.75rem;display:block;margin-top:.35rem">
              Add more images (max 12). Remove current ones with ×. Empty → default No Image.
            </small>
            <div class="multi-image-previews" data-image-previews="edit"></div>
          </div>
          <div class="form-group full"><label>Description *</label><textarea name="description" rows="3" required></textarea></div>
          @include('partials.product-variant-fields', ['prefix' => 'edit'])
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
<script src="{{ asset('js/product-form.js') }}?v=8"></script>
<script>
(function () {
  const input = document.getElementById('editProductImageFile');
  const preview = document.getElementById('editProductImagePreview');
  if (!input || !preview) return;
  input.addEventListener('change', () => {
    const file = input.files && input.files[0];
    if (!file) return;
    preview.src = URL.createObjectURL(file);
    preview.style.display = 'block';
  });
})();
</script>
@endpush
