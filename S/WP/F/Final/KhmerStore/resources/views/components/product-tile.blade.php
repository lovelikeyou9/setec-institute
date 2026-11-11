@props(['product'])
<a href="{{ url('/products/'.$product->slug) }}" class="product-tile">
    <div class="thumb">
        <img src="{{ media_url($product->primaryImageUrl()) }}" alt="{{ $product->name }}" loading="lazy">
    </div>
    <div class="body">
        @if($product->discount_percent)
            <span class="pill">-{{ $product->discount_percent }}%</span>
        @endif
        <div class="name">{{ $product->name }}</div>
        <div class="meta">{{ $product->brand->name ?? '' }} · &#9733; {{ number_format($product->rating ?? 0, 1) }}</div>
        <div class="price">
            ${{ number_format($product->price, 2) }}
            @if($product->original_price && $product->original_price > $product->price)
                <span class="old">${{ number_format($product->original_price, 2) }}</span>
            @endif
        </div>
    </div>
</a>
