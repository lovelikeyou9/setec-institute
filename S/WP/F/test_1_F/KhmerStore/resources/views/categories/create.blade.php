@extends('master.template')
@section('pageTitle', "Add New Category")
@section('rightTitle', "Add New Category")
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

        <form class="product-form" action="/categories" method="POST" enctype="multipart/form-data">
            @csrf
            @method('post')
        <div class="form-grid">
            <!-- Brand -->
            <div class="form-group">
            <label for="category_name">Category name: </label>
            <input type="text" id="category_name" name="category_name"
                value="{{ old("category_name") }}"
                placeholder="e.g., Computers, Accessories" required>
            @error("category_name")
                <span class="error">{{ $message }}</span>
            @enderror
            </div>
            {{-- parent category --}}
            <div class="form-group full-width">
                <label for="description">Parent Category: </label>
                <select name="parent_id" id="parent_id">
                    <option value="0"> -- No Parent ID</option>
                    <option value="1"> -- Computers</option>
                    <option value="2"> -- Clothes</option>
                </select>
            </div>

            <!-- Description -->
            <div class="form-group full-width">
                <label for="description">Description: </label>
                <textarea id="description" name="description"></textarea>
            </div>

            <!-- status -->
            <div class="full-width" id="checkboxWrapper">
                <label for="status">Is Publish: </label>
                <input type="checkbox" value="1" name="status" id="status">
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


