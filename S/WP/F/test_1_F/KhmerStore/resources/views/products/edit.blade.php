@extends('master.template')
@section('pageTitle', "Edit Existing Product")
@section('rightTitle', "Edit Existing Product")
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/form.css') }}">
    <link rel="stylesheet" href="{{ asset('css/photos.css') }}">
@endsection

@section('content')
    <div class="form-container">
        <div class="form-header">
            <h2>Edit Existing Product</h2>
        </div>
        @if($errors->any())
            @foreach ($errors->all() as $error)
                <div class="error">{{ $error }}</div>
            @endforeach
        @endif

        <form class="product-form" action="{{ route('products.update', $product->id) }}" method="POST" enctype="multipart/form-data">
            @csrf
            @method('put')
            <div class="form-grid">
                <div class="form-group">
                    <label for="brand_id">Brand</label>
                    <select id="brand_id" name="brand_id" required>
                        <option value="" disabled>Select brand</option>
                        @forelse ($brands as $brand)
                            <option value="{{ $brand->id }}"
                                {{ old('brand_id', $product->brand_id) == $brand->id ? 'selected' : '' }}>
                                {{ $brand->brand_name }}
                            </option>
                        @empty
                            <option disabled>No brand listing</option>
                        @endforelse
                    </select>
                </div>

                <div class="form-group">
                    <label for="model">Model</label>
                    <input type="text" id="model" name="model"
                        value="{{ old('model', $product->model) }}" required>
                    @error('model') <span class="error">{{ $message }}</span> @enderror
                </div>

                <div class="form-group">
                    <label for="category_id">Category</label>
                    <select id="category_id" name="category_id" required>
                        <option value="" disabled>Select Category</option>
                        @forelse ($categories as $category)
                            <option value="{{ $category->id }}"
                                {{ old('category_id', $product->category_id) == $category->id ? 'selected' : '' }}>
                                {{ $category->category_name }}
                            </option>
                        @empty
                            <option disabled>No category listing</option>
                        @endforelse
                    </select>
                </div>

                <div class="form-group">
                    <label for="price">Price ($)</label>
                    <input type="text" id="price" name="price"
                        value="{{ old('price', $product->price) }}" required>
                    @error('price') <span class="error">{{ $message }}</span> @enderror
                </div>

                <div class="form-group">
                    <label for="quantity">Quantity</label>
                    <input type="number" id="quantity" name="quantity"
                        value="{{ old('quantity', $product->quantity) }}" min="0" required>
                    @error('quantity') <span class="error">{{ $message }}</span> @enderror
                </div>

                <div class="form-group">
                    <label for="warranty">Warranty Period</label>
                    <input type="text" id="warranty" name="warranty"
                        value="{{ old('warranty', $product->warranty) }}">
                    @error('warranty') <span class="error">{{ $message }}</span> @enderror
                </div>

                <div class="form-group full-width">
                    <label>Current Photos</label>
                    @php $photoCount = $product->photos->count(); @endphp

                    @if($photoCount === 0)
                        <p class="photo-hint">No photos yet. Add photos below.</p>
                    @elseif($photoCount === 1)
                        @php $photo = $product->photos->first(); @endphp
                        <p class="photo-hint">This product has 1 photo. It is used as the display photo on the products list.</p>
                        <div class="photo-preview-grid photo-preview-grid-single">
                            <div class="photo-preview-item is-display">
                                <img src="{{ $photo->url() }}" alt="product photo">
                                <div class="photo-actions">
                                    <input type="hidden" name="display_photo_id" value="{{ $photo->id }}">
                                    <span class="photo-badge">Display</span>
                                    <label class="check-delete">
                                        <input type="checkbox" name="delete_photos[]" value="{{ $photo->id }}">
                                        Delete
                                    </label>
                                </div>
                            </div>
                        </div>
                    @else
                        <p class="photo-hint">Choose only 1 display photo for the products list. Check Delete to remove a photo.</p>
                        <div class="photo-preview-grid">
                            @foreach ($product->photos as $photo)
                                <div class="photo-preview-item {{ $photo->is_display ? 'is-display' : '' }}">
                                    <img src="{{ $photo->url() }}" alt="product photo">
                                    <div class="photo-actions">
                                        <label class="radio-display">
                                            <input type="radio" name="display_photo_id" value="{{ $photo->id }}"
                                                {{ $photo->is_display ? 'checked' : '' }} required>
                                            Display
                                        </label>
                                        <label class="check-delete">
                                            <input type="checkbox" name="delete_photos[]" value="{{ $photo->id }}">
                                            Delete
                                        </label>
                                    </div>
                                </div>
                            @endforeach
                        </div>
                    @endif
                </div>

                <div class="form-group full-width">
                    <label for="photos">Add More Photos</label>
                    <p class="photo-hint">Select many photos, or choose again to add more. Click × to remove a new photo before saving.</p>
                    <div class="photo-upload-row">
                        <input type="file" id="photos" name="photos[]" accept="image/*" multiple>
                        <button type="button" id="btnClearNewPhotos" class="btn-clear-photos">Clear new photos</button>
                    </div>
                    <div id="photoPreview" class="photo-preview-grid"></div>
                    <p id="newPhotoCount" class="photo-hint"></p>
                </div>

                <div class="form-group full-width">
                    <label for="description">Description</label>
                    <textarea id="description" name="description">{{ old('description', $product->description) }}</textarea>
                </div>
            </div>

            <div class="form-actions">
                <button type="reset" class="btn btn-reset">Reset</button>
                <button type="submit" class="btn btn-submit">Save Changes</button>
            </div>
        </form>
    </div>
@endsection

@section('footerBlock')
<script>
(function () {
    const input = document.getElementById('photos');
    const grid = document.getElementById('photoPreview');
    const countLabel = document.getElementById('newPhotoCount');
    const clearBtn = document.getElementById('btnClearNewPhotos');
    let selectedFiles = [];

    function syncInput() {
        const dt = new DataTransfer();
        selectedFiles.forEach(file => dt.items.add(file));
        input.files = dt.files;
        renderPreviews();
    }

    function renderPreviews() {
        grid.innerHTML = '';
        countLabel.textContent = selectedFiles.length
            ? selectedFiles.length + ' new photo(s) ready (not saved yet)'
            : '';

        selectedFiles.forEach((file, index) => {
            const reader = new FileReader();
            reader.onload = function (ev) {
                const item = document.createElement('div');
                item.className = 'photo-preview-item';
                item.innerHTML = `
                    <button type="button" class="photo-remove" title="Remove">&times;</button>
                    <img src="${ev.target.result}" alt="preview">
                    <span class="photo-badge">New</span>
                `;
                item.querySelector('.photo-remove').addEventListener('click', function (e) {
                    e.stopPropagation();
                    selectedFiles.splice(index, 1);
                    syncInput();
                });
                grid.appendChild(item);
            };
            reader.readAsDataURL(file);
        });
    }

    input.addEventListener('change', function () {
        const incoming = Array.from(input.files || []);
        incoming.forEach(file => {
            const exists = selectedFiles.some(f =>
                f.name === file.name && f.size === file.size && f.lastModified === file.lastModified
            );
            if (!exists) selectedFiles.push(file);
        });
        syncInput();
    });

    clearBtn.addEventListener('click', function () {
        selectedFiles = [];
        syncInput();
    });

    document.querySelector('.product-form').addEventListener('reset', function () {
        setTimeout(function () {
            selectedFiles = [];
            syncInput();
        }, 0);
    });
})();
</script>
@endsection
