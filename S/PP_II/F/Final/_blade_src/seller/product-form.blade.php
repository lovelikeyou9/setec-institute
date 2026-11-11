@extends('layouts.seller')
@section('title', (isset($product) ? 'Edit' : 'Add').' Product — MMT')
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">{{ isset($product) ? 'Edit product' : 'Add product' }}</h1>
  <a href="{{ url('/seller-dashboard/products') }}" class="btn btn-outline btn-sm">Back</a>
</div>

<form method="post" enctype="multipart/form-data" class="form-card" style="max-width:720px"
      action="{{ isset($product) ? url('/seller-dashboard/products/'.$product->id.'/edit') : url('/seller-dashboard/products/add') }}">
  @csrf
  <div class="form-grid">
    <div class="form-group full">
      <label>Name</label>
      <input name="name" required value="{{ old('name', $product->name ?? '') }}"/>
    </div>
    <div class="form-group">
      <label>Category</label>
      <select name="category_id" required>
        @foreach($categories as $c)
          <option value="{{ $c->id }}" data-slug="{{ $c->slug }}" @selected(old('category_id', $product->category_id ?? '')==$c->id)>{{ $c->name }}</option>
        @endforeach
      </select>
    </div>
    <div class="form-group">
      <label>Brand</label>
      <select name="brand_id" required>
        @foreach($brands as $b)
          <option value="{{ $b->id }}" @selected(old('brand_id', $product->brand_id ?? '')==$b->id)>{{ $b->name }}</option>
        @endforeach
      </select>
    </div>
    <div class="form-group full">
      <label>Description</label>
      <textarea name="description" rows="4" required>{{ old('description', $product->description ?? '') }}</textarea>
    </div>
    @include('partials.product-variant-fields', [
      'prefix' => 'page',
      'optionGroups' => old('optionGroupsJson', isset($product) ? ($product->option_groups ?: json_encode($product->selectableOptionGroups())) : '[]'),
      'specs' => old('specifications', $product->specifications ?? ''),
    ])
    <div class="form-group">
      <label>Price</label>
      <input type="number" step="0.01" name="price" required value="{{ old('price', $product->price ?? '') }}"/>
    </div>
    <div class="form-group">
      <label>Original price</label>
      <input type="number" step="0.01" name="original_price" value="{{ old('original_price', $product->original_price ?? '') }}"/>
    </div>
    <div class="form-group">
      <label>Stock</label>
      <input type="number" name="stock" required value="{{ old('stock', $product->stock ?? 0) }}"/>
    </div>
    <div class="form-group">
      <label>SKU</label>
      <input name="sku" value="{{ old('sku', $product->sku ?? '') }}"/>
    </div>
    <div class="form-group full">
      <label>Warranty</label>
      <input name="warranty" value="{{ old('warranty', $product->warranty ?? '') }}"/>
    </div>
    <div class="form-group full">
      <label>Product Images</label>
      @isset($product)
        <input type="hidden" name="galleryKeep" value="{{ implode(',', $product->galleryList()) }}"/>
        <div class="multi-image-previews" data-existing-gallery>
          @foreach($product->galleryList() as $url)
            <div class="multi-image-thumb" data-gallery-url="{{ $url }}">
              <img src="{{ media_url($url) }}" alt=""/>
            </div>
          @endforeach
        </div>
      @endisset
      <input type="file" name="imageFiles[]" accept="image/*" multiple data-image-multi="page"/>
      <small style="color:var(--text-muted);font-size:.75rem">
        Select one or many images. If none on create, “No Image Available” is used.
      </small>
      <div class="multi-image-previews" data-image-previews="page"></div>
    </div>
    @isset($product)
      <div class="form-group full">
        <label><input type="checkbox" name="active" value="1" style="width:auto" @checked($product->active)/> Active</label>
      </div>
    @endisset
  </div>
  <button class="btn btn-primary" type="submit" style="margin-top:1rem">Save</button>
</form>
@endsection

@push('scripts')
<script src="{{ asset('js/product-form.js') }}?v=8"></script>
@endpush
