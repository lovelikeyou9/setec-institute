@extends('master.storefront')
@section('pageTitle', 'KhmerStore')
@section('bodyClass', 'page-home')

@section('content')
    <section class="sf-hero">
        <div class="sf-wrap sf-hero__inner">
            <div class="sf-hero__copy">
                <p class="sf-kicker">KhmerStore</p>
                <h1>Parts and devices in stock.</h1>
                <p class="sf-hero__lead">Browse CPUs, GPUs, phones, and accessories. Filter by brand, category, or price.</p>
                <div class="sf-hero__actions">
                    <a href="{{ route('store.products') }}" class="sf-btn sf-btn--primary">View products</a>
                    <a href="{{ route('store.products', ['in_stock' => 1]) }}" class="sf-btn sf-btn--secondary">In stock</a>
                </div>
            </div>
        </div>
    </section>

    @if($categories->isNotEmpty())
        <section class="sf-section">
            <div class="sf-wrap">
                <div class="sf-section__head">
                    <h2>Categories</h2>
                </div>
                <ul class="sf-cat-list">
                    @foreach($categories as $category)
                        <li>
                            <a href="{{ route('store.products', ['category' => $category->id]) }}">
                                <span>{{ $category->category_name }}</span>
                                <span class="sf-count">{{ $category->products_count }}</span>
                            </a>
                        </li>
                    @endforeach
                </ul>
            </div>
        </section>
    @endif

    <section class="sf-section sf-section--border">
        <div class="sf-wrap">
            <div class="sf-section__head sf-section__head--split">
                <h2>New arrivals</h2>
                <a href="{{ route('store.products') }}" class="sf-link">All products</a>
            </div>

            @if($featured->isEmpty())
                <p class="sf-empty">No products listed yet.</p>
            @else
                <div class="sf-grid">
                    @foreach($featured as $product)
                        @include('store.partials.product-card', ['product' => $product])
                    @endforeach
                </div>
            @endif
        </div>
    </section>

    @if($brands->isNotEmpty())
        <section class="sf-section">
            <div class="sf-wrap">
                <div class="sf-section__head">
                    <h2>Brands</h2>
                </div>
                <ul class="sf-brand-list">
                    @foreach($brands as $brand)
                        <li>
                            <a href="{{ route('store.products', ['brand' => $brand->id]) }}">{{ $brand->brand_name }}</a>
                        </li>
                    @endforeach
                </ul>
            </div>
        </section>
    @endif
@endsection
