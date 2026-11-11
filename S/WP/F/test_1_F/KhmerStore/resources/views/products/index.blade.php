@extends('master.template')
@section('pageTitle', "Products Listing")
@section('rightTitle', "Products Listing")
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/table.css') }}">
@endsection

@section('content')
    <div class="table">
        <div class="row rowHeading">
            <div class="column-01">ID</div>
            <div class="column-02">Model</div>
            <div class="column-03">Brand Name</div>
            <div class="column-04">Price</div>
            <div class="column-05">Photo</div>
            <div class="column-06">Created By</div>
            <div class="column-07">Edited By</div>
            <div class="column-08">Detail</div>
            <div class="column-09">Edit</div>
            <div class="column-10">Delete</div>
        </div>
        @forelse($products as $product)
            <div class="row">
                <div class="column-01">{{ $product->id }}</div>
                <div class="column-02">{{ $product->model }}</div>
                <div class="column-03">{{ $product->brand->brand_name ?? '-' }}</div>
                <div class="column-04">{{ $product->price }}</div>
                <div class="column-05">
                    <img src="{{ $product->displayPhotoUrl() }}" style="width: 60px; height: 60px; object-fit: cover;" alt="{{ $product->model }}">
                </div>
                <div class="column-06">{{ $product->user->name ?? '-' }}</div>
                <div class="column-07">{{ $product->editor->name ?? '-' }}</div>
                <div class="column-08"><a href="{{ route('products.show', $product->id) }}">Detail</a></div>
                <div class="column-09"><a href="{{ route('products.edit', $product->id) }}">Edit</a></div>
                <div class="column-10">
                    <a href="{{ route('products.remove', $product->id) }}"
                       onclick="return confirm('Delete this product?');">Delete</a>
                </div>
            </div>
        @empty
            There are no any products.
        @endforelse
    </div>

    @if ($products->hasPages())
        <div class="pagination-wrap">
            <p class="pagination-info">
                Showing {{ $products->firstItem() }} to {{ $products->lastItem() }}
                of {{ $products->total() }} products
            </p>
            {{ $products->links('vendor.pagination.khmerstore') }}
        </div>
    @endif
@endsection
