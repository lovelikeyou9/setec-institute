@extends('master.template')
@section('pageTitle', "Edit Existing Brand")
@section('rightTitle')
    <h3>Edit brand name:</h3> {{ $brand->brand_name }}
@endsection
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/form.css') }}">
@endsection

@section("toolbar")
    <!-- Form Actions -->
    <div class="form-actions">
        <button type="reset" class="btn btn-reset">Reset</button>
        <button type="submit" class="btn btn-submit">Submit</button>
    </div>
@endsection

@section('content')

    <div class="form-container">
        <div class="form-header">
        <h2>Add New Brand</h2>
        </div>

        <form class="product-form" action="{{ route('brands.update', $brand->id) }}" method="POST" enctype="multipart/form-data">
            @csrf
            @method('put')
        <div class="form-grid">
            <!-- Brand -->
            <div class="form-group">
            <label for="brand">Brand name: </label>
            <input type="text" id="brand_name" name="brand_name"
                value="{{ old('brand_name', $brand->brand_name) }}" required>
            </div>

            <!-- Description -->
            <div class="form-group full-width">
                <label for="description">Description: </label>
                <textarea id="description" name="description">{{ old('description', $brand->description) }}</textarea>
            </div>


        </div>

        <!-- Form Actions -->
        <div class="form-actions">
            <button type="reset" class="btn btn-reset">Reset</button>
            <button type="submit" class="btn btn-submit">Submit</button>
        </div>

        </form>
    </div>
@endsection

