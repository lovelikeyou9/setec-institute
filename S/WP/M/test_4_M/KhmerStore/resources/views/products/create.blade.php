@extends('master.template')
@section('pageTitle', "Add New Product")
@section('rightTitle', "Add New Product")
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/form.css') }}">
@endsection

@section("toolbar")
    <!-- Form Actions -->
    <div class="form-actions">
        <button type="reset" class="btn btn-reset">Reset</button>
        <button type="submit" class="btn btn-submit">Add Product</button>
    </div>
@endsection

@section('content')

    <div class="form-container">
        <div class="form-header">
        <h2>Add New Product</h2>
        </div>

        <form class="product-form" action="#" method="POST" enctype="multipart/form-data">
       
        <div class="form-grid">
            <!-- Brand -->
            <div class="form-group">
            <label for="brand">Brand</label>
            <input type="text" id="brand" name="brand" placeholder="e.g., Apple, Sony" required>
            </div>

            <!-- Model -->
            <div class="form-group">
            <label for="model">Model</label>
            <input type="text" id="model" name="model" placeholder="e.g., XPS 15, WH-1000XM5" required>
            </div>

            <!-- Category -->
            <div class="form-group">
            <label for="category">Category</label>
            <select id="category" name="category" required>
                <option value="" disabled selected>Select Category</option>
                <option value="electronics">Electronics</option>
                <option value="appliances">Appliances</option>
                <option value="fashion">Fashion</option>
                <option value="accessories">Accessories</option>
            </select>
            </div>

            <!-- Price -->
            <div class="form-group">
            <label for="price">Price ($)</label>
            <input type="number" id="price" name="price" step="0.01" min="0" placeholder="0.00" required>
            </div>

            <!-- Quantity -->
            <div class="form-group">
            <label for="quantity">Quantity</label>
            <input type="number" id="quantity" name="quantity" min="0" placeholder="1" required>
            </div>

            <!-- Warranty -->
            <div class="form-group">
            <label for="warranty">Warranty Period</label>
            <input type="text" id="warranty" name="warranty" placeholder="e.g., 1 Year, 24 Months">
            </div>

            <!-- Photo -->
            <div class="form-group full-width">
            <label for="photo">Product Photo</label>
            <input type="file" id="photo" name="photo" accept="image/*" required>
            </div>

            <!-- Status -->
            <div class="form-group full-width">
            <label for="status">Status</label>
            <select id="status" name="status" required>
                <option value="active">Active</option>
                <option value="draft">Draft</option>
                <option value="out_of_stock">Out of Stock</option>
            </select>
            </div>

            <!-- Description -->
            <div class="form-group full-width">
            <label for="description">Description</label>
            <textarea id="description" name="description" placeholder="Provide product details and specifications..."></textarea>
            </div>

            <!-- Trash Option -->
            <div class="form-group full-width">
            <div class="checkbox-group">
                <input type="checkbox" id="trash" name="trash" value="1">
                <label for="trash">Move to Trash immediately (Mark as Soft Deleted)</label>
            </div>
            </div>
        </div>

        <!-- Form Actions -->
        <div class="form-actions">
            <button type="reset" class="btn btn-reset">Reset</button>
            <button type="submit" class="btn btn-submit">Add Product</button>
        </div>

        </form>
    </div>
@endsection
