@extends('master.template')
@section('pageTitle', "Product Detail")
@section('rightTitle')
    Detail information of <b>{{ $product->model }}</b>
@endsection
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/table.css') }}">
    <link rel="stylesheet" href="{{ asset('css/photos.css') }}">
@endsection

@section('content')
    @php
        $photos = $product->photos;
        $photoCount = $photos->count();
        $display = $photos->firstWhere('is_display') ?? $photos->first();
    @endphp

    <div class="detail-layout">
        <div class="detail-gallery">
            @if($photoCount === 0)
                <img src="{{ asset('images/no_image.jpg') }}" alt="No image" class="gallery-single-img">
            @elseif($photoCount === 1)
                <div class="gallery-main gallery-main-single">
                    <img id="mainPhoto" src="{{ $display->url() }}" alt="{{ $product->model }}">
                </div>
            @else
                <div class="gallery-main">
                    <img id="mainPhoto" src="{{ $display->url() }}" alt="{{ $product->model }}">
                </div>
                <div class="gallery-thumbs {{ $photoCount === 2 ? 'gallery-thumbs-2' : '' }}">
                    @foreach($photos->take(3) as $photo)
                        <button type="button" class="gallery-thumb" data-full="{{ $photo->url() }}">
                            <img src="{{ $photo->url() }}" alt="thumb">
                        </button>
                    @endforeach
                </div>
                @if($photoCount > 3)
                    <button type="button" id="btnSeeMore" class="btn-see-more">
                        See more photos ({{ $photoCount }})
                    </button>
                @endif
            @endif
        </div>

        <div class="detail-info">
            Id: <b>{{ $product->id }}</b><br><br>
            Model: <b>{{ $product->model }}</b><br><br>
            Brand: <b>{{ $product->brand->brand_name ?? '-' }}</b><br><br>
            Category: <b>{{ $product->category->category_name ?? '-' }}</b><br><br>
            Price: <b>{{ $product->price }}</b><br><br>
            Quantity: <b>{{ $product->quantity }}</b><br><br>
            Warranty: <b>{{ $product->warranty }}</b><br><br>
            Description: <b>{{ $product->description }}</b><br><br>
            Status: <b>{{ $product->status }}</b><br><br>
            Created By: <b>{{ $product->user->name ?? '-' }}</b><br><br>
            Edited By: <b>{{ $product->editor->name ?? '-' }}</b><br><br>
            Date posted: <b>{{ $product->created_at }}</b><br><br>
            Last edited date: <b>{{ $product->updated_at }}</b><br><br>

            <form class="product-form" action="{{ route('products.destroy', $product->id) }}" method="POST"
                onsubmit="return confirm('Delete this product?');">
                @csrf
                @method('delete')
                <input type="submit" value="Delete" name="buttonDelete">
            </form>
        </div>
    </div>

    @if($photoCount > 1)
        <div id="photoLightbox" class="lightbox hidden">
            <div class="lightbox-content">
                <button type="button" class="lightbox-close" id="lightboxClose">&times;</button>
                <img id="lightboxImage" src="" alt="full photo">
                <div class="lightbox-thumbs">
                    @foreach($photos as $photo)
                        <button type="button" class="gallery-thumb lightbox-thumb" data-full="{{ $photo->url() }}">
                            <img src="{{ $photo->url() }}" alt="thumb">
                        </button>
                    @endforeach
                </div>
            </div>
        </div>
    @endif
@endsection

@section('footerBlock')
@if($photoCount > 1)
<script>
(function () {
    const main = document.getElementById('mainPhoto');
    const lightbox = document.getElementById('photoLightbox');
    const lightboxImage = document.getElementById('lightboxImage');
    const closeBtn = document.getElementById('lightboxClose');
    const seeMore = document.getElementById('btnSeeMore');

    function openLightbox(src) {
        lightboxImage.src = src;
        lightbox.classList.remove('hidden');
    }

    document.querySelectorAll('.gallery-thumb').forEach(btn => {
        btn.addEventListener('click', function () {
            const src = this.dataset.full;
            if (main) main.src = src;
            openLightbox(src);
        });
    });

    if (main) {
        main.addEventListener('click', function () {
            openLightbox(this.src);
        });
    }

    if (seeMore) {
        seeMore.addEventListener('click', function () {
            openLightbox(main ? main.src : '');
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', () => lightbox.classList.add('hidden'));
    }
    if (lightbox) {
        lightbox.addEventListener('click', function (e) {
            if (e.target === lightbox) lightbox.classList.add('hidden');
        });
    }
})();
</script>
@endif
@endsection
