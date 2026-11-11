@extends('master.template')
@section('pageTitle', "Products Searching")
@section('rightTitle', "Products Searching")
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/form.css') }}">
@endsection

@section("toolbar")

@endsection

@section('content')
    <form action="/search" method="GET">
        <!-- Search Input -->
        <div class="form-group">
            {{-- <svg class="search-icon" viewBox="0 0 24 24">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg> --}}
            <label for="productName" class="filter-label">productName</label>
            <input
            type="text"
            name="productName"
            id="productName"
            class="main-search-input"
            placeholder="Search by keywords, model, or brand..."
            required
            >
        </div><br>

        <!-- Filters Section -->
        <div class="filters-grid">

            <!-- Category Filter -->
            <div class="form-group">
            <label for="category" class="filter-label">Category</label>
            <select id="category" name="category" class="select-control">
                <option value="">All Categories</option>
                <option value="electronics">Electronics</option>
                <option value="apparel">Apparel & Fashion</option>
                <option value="home">Home & Kitchen</option>
                <option value="books">Books & Media</option>
            </select>
            </div><br>

            <!-- Sort By Filter -->
            <div class="form-group">
            <label for="sort" class="filter-label">Sort By</label>
            <select id="sort" name="sort" class="select-control">
                <option value="relevance">Relevance</option>
                <option value="price-low">Price: Low to High</option>
                <option value="price-high">Price: High to Low</option>
                <option value="rating">Customer Rating</option>
                <option value="newest">Newest Arrivals</option>
            </select>
            </div><br>

            <!-- Price Range Filter -->
            <div class="form-group">
            <label for="price" class="filter-label">Max Price ($)</label>
            <div class="price-range">
                <input
                type="range"
                id="price"
                name="max_price"
                class="range-slider"
                min="0"
                max="1000"
                step="10"
                value="500"
                oninput="document.getElementById('priceValue').innerText = '$' + this.value"
                >
                <div class="price-values">
                <span>$0</span>
                <span id="priceValue">$500</span>
                <span>$1000+</span>
                </div>
            </div>
            </div><br>

            <!-- Additional Toggles -->
            <div class="form-group">
            <span class="filter-label">Options</span>
            <div class="checkbox-group">
                <label class="checkbox-label">
                <input type="checkbox" name="in_stock" value="1">
                In Stock Only
                </label>
                <label class="checkbox-label">
                <input type="checkbox" name="free_shipping" value="1">
                Free Shipping
                </label>
            </div>
            </div>

        </div><br>

        <!-- Action Buttons -->
        <div class="button-group">
            <button type="reset" class="btn btn-secondary" onclick="document.getElementById('priceValue').innerText = '$500'">Reset Filters</button>
            <button type="submit" class="btn btn-primary">Apply Search</button>
        </div>
    </form>

@endsection
