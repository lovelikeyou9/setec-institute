<a href="{{ route('store.show', $product->id) }}" class="sf-card">
    <div class="sf-card__img">
        <img src="{{ $product->displayPhotoUrl() }}" alt="{{ $product->model }}" loading="lazy">
        @if($product->quantity <= 0)
            <span class="sf-tag sf-tag--out">Out of stock</span>
        @endif
    </div>
    <div class="sf-card__body">
        <p class="sf-card__brand">{{ $product->brand->brand_name ?? '—' }}</p>
        <h3 class="sf-card__title">{{ $product->model }}</h3>
        @if($product->category)
            <p class="sf-card__cat">{{ $product->category->category_name }}</p>
        @endif
        <p class="sf-card__price">${{ number_format((float) $product->price, 2) }}</p>
    </div>
</a>
