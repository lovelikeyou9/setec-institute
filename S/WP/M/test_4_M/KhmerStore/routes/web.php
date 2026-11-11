<?php

use App\Http\Controllers\BrandController;
use App\Http\Controllers\ProductController;
use Illuminate\Support\Facades\Route;

Route::get('/', function () {
    return view('dashboard');
})->name('home');
Route::resource('/products', ProductController::class);
Route::get('/products-searching', [ProductController::class, 'search'])->name('products.search');
Route::get(
    '/products/remove/{id}',
    [ProductController::class, 'remove']
)->name('products.remove');

route::resource('brands', BrandController::class);
