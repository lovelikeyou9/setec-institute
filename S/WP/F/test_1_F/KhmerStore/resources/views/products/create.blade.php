@extends('master.template')
@section('pageTitle', "Add New Product")
@section('rightTitle', "Add New Product")
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/form.css') }}">
    <link rel="stylesheet" href="{{ asset('css/photos.css') }}">
@endsection

@section('content')
    <div class="form-container">
        <div class="form-header">
            <h2>Add New Product</h2>
        </div>
        @if($errors->any())
            @foreach ($errors->all() as $error)
                <div class="error">{{ $error }}</div>
            @endforeach
        @endif

        <form class="product-form" action="{{ route('products.store') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <div class="form-grid">
                <div class="form-group">
                    <label for="brand_id">Brand</label>
                    <select id="brand_id" name="brand_id" required>
                        <option value="" disabled selected>Select brand</option>
                        @forelse ($brands as $brand)
                            <option value="{{ $brand->id }}" {{ old('brand_id') == $brand->id ? 'selected' : '' }}>
                                {{ $brand->brand_name }}
                            </option>
                        @empty
                            <option disabled>No brand listing</option>
                        @endforelse
                    </select>
                </div>

                <div class="form-group">
                    <label for="model">Model</label>
                    <input type="text" id="model" name="model" value="{{ old('model') }}"
                        placeholder="e.g., XPS 15, WH-1000XM5" required>
                    @error('model') <span class="error">{{ $message }}</span> @enderror
                </div>

                <div class="form-group">
                    <label for="category_id">Category</label>
                    <select id="category_id" name="category_id" required>
                        <option value="" disabled selected>Select Category</option>
                        @forelse ($categories as $category)
                            <option value="{{ $category->id }}" {{ old('category_id') == $category->id ? 'selected' : '' }}>
                                {{ $category->category_name }}
                            </option>
                        @empty
                            <option disabled>No category listing</option>
                        @endforelse
                    </select>
                </div>

                <div class="form-group">
                    <label for="price">Price ($)</label>
                    <input type="text" id="price" name="price" value="{{ old('price') }}" required>
                    @error('price') <span class="error">{{ $message }}</span> @enderror
                </div>

                <div class="form-group">
                    <label for="quantity">Quantity</label>
                    <input type="number" id="quantity" name="quantity" value="{{ old('quantity') }}" min="0" required>
                    @error('quantity') <span class="error">{{ $message }}</span> @enderror
                </div>

                <div class="form-group">
                    <label for="warranty">Warranty Period</label>
                    <input type="text" id="warranty" name="warranty" value="{{ old('warranty') }}" placeholder="e.g., 12">
                    @error('warranty') <span class="error">{{ $message }}</span> @enderror
                </div>

                <div class="form-group full-width">
                    <label for="photos">Product Photos</label>
                    <p class="photo-hint">You can add many photos. Select several at once, or click again to add more.</p>
                    <div class="photo-upload-row">
                        <input type="file" id="photos" name="photos[]" accept="image/*" multiple required>
                        <button type="button" id="btnClearPhotos" class="btn-clear-photos">Clear all</button>
                    </div>
                    <p class="photo-hint">Click a preview to choose the <b>Display</b> photo for the products list. Use × to remove one.</p>
                    <div id="photoPreview" class="photo-preview-grid"></div>
                    <input type="hidden" name="display_index" id="display_index" value="0">
                    <p id="photoCount" class="photo-hint"></p>
                </div>

                <div class="form-group full-width">
                    <label for="description">Description</label>
                    <textarea id="description" name="description"
                        placeholder="Provide product details...">{{ old('description') }}</textarea>
                </div>
            </div>

            <div class="form-actions">
                <button type="reset" class="btn btn-reset">Reset</button>
                <button type="submit" class="btn btn-submit">Add Product</button>
            </div>
        </form>
    </div>
@endsection

@section('footerBlock')
<script>
(function () {
    const input = document.getElementById('photos');
    const grid = document.getElementById('photoPreview');
    const displayInput = document.getElementById('display_index');
    const countLabel = document.getElementById('photoCount');
    const clearBtn = document.getElementById('btnClearPhotos');
    let selectedFiles = [];

    function syncInput() {
        const dt = new DataTransfer();
        selectedFiles.forEach(file => dt.items.add(file));
        input.files = dt.files;
        if (selectedFiles.length === 0) {
            displayInput.value = 0;
        } else if (parseInt(displayInput.value, 10) >= selectedFiles.length) {
            displayInput.value = 0;
        }
        renderPreviews();
    }

    function renderPreviews() {
        grid.innerHTML = '';
        const displayIndex = parseInt(displayInput.value, 10) || 0;
        countLabel.textContent = selectedFiles.length
            ? selectedFiles.length + ' photo(s) selected'
            : 'No photos selected yet';

        selectedFiles.forEach((file, index) => {
            const reader = new FileReader();
            reader.onload = function (ev) {
                const item = document.createElement('div');
                item.className = 'photo-preview-item' + (index === displayIndex ? ' is-display' : '');
                item.innerHTML = `
                    <button type="button" class="photo-remove" title="Remove">&times;</button>
                    <img src="${ev.target.result}" alt="preview">
                    <span class="photo-badge">${index === displayIndex ? 'Display' : 'Set display'}</span>
                `;
                item.querySelector('img').addEventListener('click', function () {
                    displayInput.value = index;
                    renderPreviews();
                });
                item.querySelector('.photo-badge').addEventListener('click', function () {
                    displayInput.value = index;
                    renderPreviews();
                });
                item.querySelector('.photo-remove').addEventListener('click', function (e) {
                    e.stopPropagation();
                    selectedFiles.splice(index, 1);
                    if (parseInt(displayInput.value, 10) === index) {
                        displayInput.value = 0;
                    } else if (parseInt(displayInput.value, 10) > index) {
                        displayInput.value = parseInt(displayInput.value, 10) - 1;
                    }
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
            const exists = selectedFiles.some(f => f.name === file.name && f.size === file.size && f.lastModified === file.lastModified);
            if (!exists) selectedFiles.push(file);
        });
        syncInput();
    });

    clearBtn.addEventListener('click', function () {
        selectedFiles = [];
        displayInput.value = 0;
        syncInput();
    });

    document.querySelector('.product-form').addEventListener('reset', function () {
        setTimeout(function () {
            selectedFiles = [];
            displayInput.value = 0;
            syncInput();
        }, 0);
    });
})();
</script>
@endsection
