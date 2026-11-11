<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\BrandController;
use App\Http\Controllers\CategoryController;
use App\Http\Controllers\ProductController;
use App\Http\Controllers\StorefrontController;
use Illuminate\Support\Facades\Route;

/*
| Public storefront — no login required
*/
Route::get('/store', [StorefrontController::class, 'home'])->name('store.home');
Route::get('/store/products', [StorefrontController::class, 'products'])->name('store.products');
Route::get('/store/products/{id}', [StorefrontController::class, 'show'])->name('store.show');

/*
| Guest routes — only for users who are NOT logged in
*/
Route::middleware('guest')->group(function () {
    Route::get('/login', [AuthController::class, 'showLogin'])->name('login');
    Route::post('/login', [AuthController::class, 'login']);
    Route::get('/register', [AuthController::class, 'showRegister'])->name('register');
    Route::post('/register', [AuthController::class, 'register']);
});

/*
| Protected routes — must login/register first (auth middleware)
*/
Route::middleware('auth')->group(function () {
    Route::get('/', function () {
        return view('dashboard');
    })->name('home');

    Route::post('/logout', [AuthController::class, 'logout'])->name('logout');

    Route::resource('/products', ProductController::class);
    Route::get('/products-searching', [ProductController::class, 'search'])->name('products.search');
    Route::get('/products/remove/{id}', [ProductController::class, 'remove'])->name('products.remove');
    Route::delete(
        '/products/{product}/photos/{photo}',
        [ProductController::class, 'deletePhoto']
    )->name('products.photos.destroy');

    Route::resource('brands', BrandController::class);
    Route::get('/brands/tempDeletion/{id}', [BrandController::class, 'tempDeletion'])
        ->name('brands.tempDeletion');

    Route::resource('categories', CategoryController::class);
    Route::get('/categories/tempDeletion/{id}', [CategoryController::class, 'tempDeletion'])
        ->name('categories.tempDeletion');
});
