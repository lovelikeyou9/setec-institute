@extends('master.template')
@section('pageTitle', "Edit Existing Category")
@section('rightTitle')
    <h3>Edit category name:</h3> {{ $category->category_name }}
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
        <h2>Add New Category</h2>
        </div>

        <form class="product-form" action="{{ route('categories.update', $category->id) }}" method="POST" enctype="multipart/form-data">
            @csrf
            @method('put')
        <div class="form-grid">
            <!-- Brand -->
            <div class="form-group">
            <label for="category_name">Category name: </label>
            <input type="text" id="category_name" name="category_name"
                value="{{ old('category_name', $category->category_name) }}" required>
            </div>

            <!-- Description -->
            <div class="form-group full-width">
                <label for="description">Description: </label>
                <textarea id="description" name="description">{{ old('description', $category->description) }}</textarea>
            </div>

            <!-- status -->
            <div class="full-width" id="checkboxWrapper">
                <label for="status">Is Publish: </label>
                @if( $category->status == 1)
                    <input type="checkbox" value="1" checked name="status" id="status">
                @else
                    <input type="checkbox" value="1" name="status" id="status">
                @endif
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

