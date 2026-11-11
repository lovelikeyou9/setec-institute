@extends('master.storefront')
@section('pageTitle', 'Products — KhmerStore')

@section('content')
    <div class="sf-wrap sf-crumbs">
        <a href="{{ route('store.home') }}">Home</a>
        <span>/</span>
        <span>Products</span>
    </div>

    <section class="sf-wrap sf-shop">
        <aside class="sf-filters">
            <h1 class="sf-filters__title">Filter</h1>
            <form method="GET" action="{{ route('store.products') }}" class="sf-filters__form">
                <div class="sf-field">
                    <label for="q">Search</label>
                    <input type="search" id="q" name="q" value="{{ $filters['q'] }}" placeholder="Model name">
                </div>

                <div class="sf-field">
                    <label for="category">Category</label>
                    <select id="category" name="category">
                        <option value="">All</option>
                        @foreach($categories as $category)
                            <option value="{{ $category->id }}" @selected((string) $filters['category'] === (string) $category->id)>
                                {{ $category->category_name }}
                            </option>
                        @endforeach
                    </select>
                </div>

                <div class="sf-field">
                    <label for="brand">Brand</label>
                    <select id="brand" name="brand">
                        <option value="">All</option>
                        @foreach($brands as $brand)
                            <option value="{{ $brand->id }}" @selected((string) $filters['brand'] === (string) $brand->id)>
                                {{ $brand->brand_name }}
                            </option>
                        @endforeach
                    </select>
                </div>

                <div class="sf-field-row">
                    <div class="sf-field">
                        <label for="min_price">Min $</label>
                        <input type="number" id="min_price" name="min_price" min="0" step="0.01" value="{{ $filters['min_price'] }}">
                    </div>
                    <div class="sf-field">
                        <label for="max_price">Max $</label>
                        <input type="number" id="max_price" name="max_price" min="0" step="0.01" value="{{ $filters['max_price'] }}">
                    </div>
                </div>

                <div class="sf-field sf-field--check">
                    <label>
                        <input type="checkbox" name="in_stock" value="1" @checked($filters['in_stock'])>
                        In stock only
                    </label>
                </div>

                <div class="sf-field">
                    <label for="sort">Sort</label>
                    <select id="sort" name="sort">
                        <option value="newest" @selected($filters['sort'] === 'newest')>Newest</option>
                        <option value="price_asc" @selected($filters['sort'] === 'price_asc')>Price: low → high</option>
                        <option value="price_desc" @selected($filters['sort'] === 'price_desc')>Price: high → low</option>
                        <option value="name_asc" @selected($filters['sort'] === 'name_asc')>Name: A–Z</option>
                        <option value="name_desc" @selected($filters['sort'] === 'name_desc')>Name: Z–A</option>
                    </select>
                </div>

                <div class="sf-filters__actions">
                    <button type="submit" class="sf-btn sf-btn--primary sf-btn--full">Apply</button>
                    <a href="{{ route('store.products') }}" class="sf-btn sf-btn--secondary sf-btn--full">Clear</a>
                </div>
            </form>
        </aside>

        <div class="sf-results">
            <div class="sf-results__bar">
                <h2>Products</h2>
                <p class="sf-results__count">
                    @if($products->total() === 0)
                        0 results
                    @else
                        {{ $products->firstItem() }}–{{ $products->lastItem() }} of {{ $products->total() }}
                    @endif
                </p>
            </div>

            @if($products->isNotEmpty())
                <div class="sf-grid">
                    @foreach($products as $product)
                        @include('store.partials.product-card', ['product' => $product])
                    @endforeach
                </div>

                @if($products->hasPages())
                    <div class="sf-pagination">
                        {{ $products->links('vendor.pagination.khmerstore') }}
                    </div>
                @endif
            @else
                <div class="sf-empty-box">
                    <p>No products match these filters.</p>
                    <a href="{{ route('store.products') }}" class="sf-btn sf-btn--secondary">Reset</a>
                </div>
            @endif
        </div>
    </section>
@endsection
