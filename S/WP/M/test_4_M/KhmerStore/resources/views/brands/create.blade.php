@extends('master.template')
@section('pageTitle', "Add New Brand")
@section('rightTitle', "Add New Brand")
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

        <form class="product-form" action="/brands" method="POST" enctype="multipart/form-data">
            @csrf
            @method('post')
        <div class="form-grid">
            <!-- Brand -->
            <div class="form-group">
            <label for="brand">Brand name: </label>
            <input type="text" id="brand_name" name="brand_name"
                placeholder="e.g., Apple, Sony" required>
            </div>

            <!-- Description -->
            <div class="form-group full-width">
                <label for="description">Description: </label>
                <textarea id="description" name="description"></textarea>
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

