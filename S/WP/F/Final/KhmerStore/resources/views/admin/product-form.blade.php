@extends('layouts.admin')
@section('title', 'Edit Product — Admin')
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">Edit product</h1>
  <a href="{{ url('/admin/products') }}" class="btn btn-outline btn-sm">Back</a>
</div>

<form method="post" enctype="multipart/form-data" action="{{ url('/admin/products/'.$product->id.'/edit') }}" class="form-card" style="max-width:720px">
  @csrf
  <div class="form-grid">
    <div class="form-group full"><label>Name</label><input name="name" required value="{{ $product->name }}"/></div>
    <div class="form-group">
      <label>Category</label>
      <select name="category_id">
        @foreach($categories as $c)
          <option value="{{ $c->id }}" data-slug="{{ $c->slug }}" @selected($product->category_id==$c->id)>{{ $c->name }}</option>
        @endforeach
      </select>
    </div>
    <div class="form-group">
      <label>Brand</label>
      <select name="brand_id">
        @foreach($brands as $b)
          <option value="{{ $b->id }}" @selected($product->brand_id==$b->id)>{{ $b->name }}</option>
        @endforeach
      </select>
    </div>
    <div class="form-group full"><label>Description</label><textarea name="description" rows="4" required>{{ $product->description }}</textarea></div>
    @include('partials.product-variant-fields', [
      'prefix' => 'page',
      'optionGroups' => $product->option_groups ?: json_encode($product->selectableOptionGroups()),
      'specs' => $product->specifications ?? '',
    ])
    <div class="form-group"><label>Price</label><input type="number" step="0.01" name="price" required value="{{ $product->price }}"/></div>
    <div class="form-group"><label>Original</label><input type="number" step="0.01" name="original_price" value="{{ $product->original_price }}"/></div>
    <div class="form-group"><label>Stock</label><input type="number" name="stock" required value="{{ $product->stock }}"/></div>
    <div class="form-group"><label>SKU</label><input name="sku" value="{{ $product->sku }}"/></div>
    <div class="form-group full"><label>Warranty</label><input name="warranty" value="{{ $product->warranty }}"/></div>
    <div class="form-group full"><label>Replace image</label><input type="file" name="image" accept="image/*"/></div>
    <div class="form-group full"><label><input type="checkbox" name="active" value="1" style="width:auto" @checked($product->active)/> Active</label></div>
  </div>
  <button class="btn btn-primary" type="submit" style="margin-top:1rem">Save</button>
</form>
@endsection

@push('scripts')
<script src="{{ asset('js/product-form.js') }}?v=4"></script>
@endpush
