@extends('master.storefront')
@section('pageTitle', $product->model . ' — KhmerStore')

@section('content')
    <div class="sf-wrap sf-crumbs">
        <a href="{{ route('store.home') }}">Home</a>
        <span>/</span>
        <a href="{{ route('store.products') }}">Products</a>
        <span>/</span>
        <span>{{ $product->model }}</span>
    </div>

    <section class="sf-wrap sf-pdp">
        <div class="sf-pdp__gallery">
            @php
                $photos = $product->photos;
                $mainUrl = $product->displayPhotoUrl();
            @endphp
            <div class="sf-pdp__main">
                <img id="sfMainPhoto" src="{{ $mainUrl }}" alt="{{ $product->model }}">
            </div>
            @if($photos->count() > 1)
                <div class="sf-pdp__thumbs">
                    @foreach($photos as $photo)
                        <button
                            type="button"
                            class="sf-pdp__thumb {{ $photo->is_display ? 'is-active' : '' }}"
                            data-src="{{ $photo->url() }}"
                            aria-label="Photo {{ $loop->iteration }}"
                        >
                            <img src="{{ $photo->url() }}" alt="">
                        </button>
                    @endforeach
                </div>
            @endif
        </div>

        <div class="sf-pdp__buy">
            <p class="sf-pdp__brand">
                @if($product->brand)
                    <a href="{{ route('store.products', ['brand' => $product->brand_id]) }}">{{ $product->brand->brand_name }}</a>
                @else
                    —
                @endif
                @if($product->category)
                    <span class="sf-sep">|</span>
                    <a href="{{ route('store.products', ['category' => $product->category_id]) }}">{{ $product->category->category_name }}</a>
                @endif
            </p>

            <h1>{{ $product->model }}</h1>
            <p class="sf-pdp__price">${{ number_format((float) $product->price, 2) }}</p>

            <table class="sf-spec-table">
                <tr>
                    <th>Availability</th>
                    <td>
                        @if($product->quantity > 0)
                            <span class="sf-stock sf-stock--in">In stock ({{ $product->quantity }})</span>
                        @else
                            <span class="sf-stock sf-stock--out">Out of stock</span>
                        @endif
                    </td>
                </tr>
                <tr>
                    <th>Warranty</th>
                    <td>{{ $product->warranty ? $product->warranty . ' months' : 'N/A' }}</td>
                </tr>
                <tr>
                    <th>SKU / Model</th>
                    <td>{{ $product->model }}</td>
                </tr>
            </table>

            @if($product->description)
                <div class="sf-pdp__desc">
                    <h2>Description</h2>
                    <p>{{ $product->description }}</p>
                </div>
            @endif

            <div class="sf-pdp__links">
                <a href="{{ route('store.products') }}" class="sf-btn sf-btn--secondary">Back to list</a>
            </div>
        </div>
    </section>

    @if($related->isNotEmpty())
        <section class="sf-section sf-section--border">
            <div class="sf-wrap">
                <div class="sf-section__head">
                    <h2>Related products</h2>
                </div>
                <div class="sf-grid">
                    @foreach($related as $item)
                        @include('store.partials.product-card', ['product' => $item])
                    @endforeach
                </div>
            </div>
        </section>
    @endif
@endsection

@section('footerBlock')
<script>
    (function () {
        var main = document.getElementById('sfMainPhoto');
        var thumbs = document.querySelectorAll('.sf-pdp__thumb');
        if (!main || !thumbs.length) return;
        thumbs.forEach(function (btn) {
            btn.addEventListener('click', function () {
                main.src = btn.getAttribute('data-src');
                thumbs.forEach(function (t) { t.classList.remove('is-active'); });
                btn.classList.add('is-active');
            });
        });
    })();
</script>
@endsection
