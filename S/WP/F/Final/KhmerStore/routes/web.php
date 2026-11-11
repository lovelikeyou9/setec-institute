<?php

use App\Http\Controllers\Account\AccountController;
use App\Http\Controllers\Admin\AdminController;
use App\Http\Controllers\Auth\AuthController;
use App\Http\Controllers\Seller\SellerDashboardController;
use App\Http\Controllers\Store\CartController;
use App\Http\Controllers\Store\StorefrontController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Storefront
|--------------------------------------------------------------------------
*/
Route::get('/', [StorefrontController::class, 'home'])->name('home');
Route::get('/home', [StorefrontController::class, 'home']);
Route::get('/products', [StorefrontController::class, 'products'])->name('products.index');
Route::get('/products/{slug}', [StorefrontController::class, 'productDetail'])->name('products.show');
Route::get('/deals', [StorefrontController::class, 'deals'])->name('deals');
Route::get('/brands', [StorefrontController::class, 'brands'])->name('brands.index');
Route::get('/brands/{slug}', [StorefrontController::class, 'brandShow'])->name('brands.show');
Route::get('/categories', [StorefrontController::class, 'categories'])->name('categories.index');
Route::get('/categories/{slug}', [StorefrontController::class, 'categoryShow'])->name('categories.show');
Route::get('/sellers', [StorefrontController::class, 'sellers'])->name('sellers.index');
Route::get('/sellers/{id}', [StorefrontController::class, 'sellerStore'])->name('sellers.show');
Route::get('/become-seller', [StorefrontController::class, 'becomeSellerForm'])->name('become-seller');

/*
|--------------------------------------------------------------------------
| Auth
|--------------------------------------------------------------------------
*/
Route::middleware('guest')->group(function () {
    Route::get('/login', [AuthController::class, 'showLogin'])->name('login');
    Route::post('/login', [AuthController::class, 'login']);
    Route::get('/register', [AuthController::class, 'showRegister'])->name('register');
    Route::post('/register', [AuthController::class, 'register']);
});
Route::post('/logout', [AuthController::class, 'logout'])->middleware('auth')->name('logout');

/*
|--------------------------------------------------------------------------
| Authenticated customer actions
|--------------------------------------------------------------------------
*/
Route::middleware('auth')->group(function () {
    Route::post('/become-seller', [StorefrontController::class, 'becomeSeller']);
    Route::post('/sellers/{id}/follow', [StorefrontController::class, 'followSeller'])->name('sellers.follow');
    Route::post('/products/{slug}/review', [StorefrontController::class, 'submitReview'])->name('products.review');

    Route::post('/cart/add', [CartController::class, 'add'])->name('cart.add');
    Route::post('/cart/update', [CartController::class, 'update'])->name('cart.update');
    Route::post('/cart/toggle', [CartController::class, 'toggle'])->name('cart.toggle');
    Route::post('/cart/select-all', [CartController::class, 'selectAll'])->name('cart.selectAll');
    Route::post('/cart/remove', [CartController::class, 'remove'])->name('cart.remove');
    Route::get('/cart', fn () => redirect('/account/cart'));
    Route::get('/checkout', [CartController::class, 'checkout'])->name('checkout');
    Route::post('/checkout/place', [CartController::class, 'placeOrder'])->name('checkout.place');
    Route::post('/wishlist/toggle', [CartController::class, 'toggleWishlist'])->name('wishlist.toggle');
    Route::get('/wishlist', fn () => redirect('/account/wishlist'));

    Route::get('/account', [AccountController::class, 'dashboard'])->name('account');
    Route::get('/dashboard', [AccountController::class, 'dashboard']);
    Route::get('/account/orders', [AccountController::class, 'orders'])->name('account.orders');
    Route::get('/orders/{orderNumber}', [AccountController::class, 'orderDetail'])->name('orders.show');
    Route::post('/orders/{orderNumber}/dispute', [AccountController::class, 'openDispute'])->name('orders.dispute');
    Route::get('/account/cart', [AccountController::class, 'cart'])->name('account.cart');
    Route::get('/account/wishlist', [AccountController::class, 'wishlist'])->name('account.wishlist');
    Route::get('/account/addresses', [AccountController::class, 'addresses'])->name('account.addresses');
    Route::post('/account/addresses', [AccountController::class, 'saveAddress']);
    Route::post('/account/addresses/{id}/edit', [AccountController::class, 'updateAddress']);
    Route::post('/account/addresses/{id}/delete', [AccountController::class, 'deleteAddress']);
    Route::get('/account/notifications', [AccountController::class, 'notifications'])->name('account.notifications');
    Route::post('/account/notifications/{id}/read', [AccountController::class, 'markNotificationRead']);
    Route::get('/account/settings', [AccountController::class, 'settings'])->name('account.settings');
    Route::post('/account/settings', [AccountController::class, 'updateSettings']);
});

/*
|--------------------------------------------------------------------------
| Seller dashboard
|--------------------------------------------------------------------------
*/
Route::prefix('seller-dashboard')->middleware(['auth', 'role:SELLER,ADMIN'])->group(function () {
    Route::get('/', [SellerDashboardController::class, 'dashboard'])->name('seller.dashboard');
    Route::get('/products', [SellerDashboardController::class, 'products'])->name('seller.products');
    Route::get('/products/add', [SellerDashboardController::class, 'createProduct'])->name('seller.products.create');
    Route::post('/products/add', [SellerDashboardController::class, 'storeProduct']);
    Route::get('/products/{id}/edit', [SellerDashboardController::class, 'editProduct'])->name('seller.products.edit');
    Route::post('/products/{id}/edit', [SellerDashboardController::class, 'updateProduct']);
    Route::post('/products/{id}/delete', [SellerDashboardController::class, 'deleteProduct']);
    Route::get('/orders', [SellerDashboardController::class, 'orders'])->name('seller.orders');
    Route::post('/orders/{id}/accept', [SellerDashboardController::class, 'acceptOrder']);
    Route::post('/orders/{id}/decline-stock', [SellerDashboardController::class, 'declineOrder']);
    Route::post('/orders/{id}/status', [SellerDashboardController::class, 'updateOrderStatus']);
    Route::get('/earnings', [SellerDashboardController::class, 'earnings'])->name('seller.earnings');
    Route::get('/reviews', [SellerDashboardController::class, 'reviews'])->name('seller.reviews');
    Route::get('/categories', [SellerDashboardController::class, 'categories'])->name('seller.categories');
    Route::get('/brands', [SellerDashboardController::class, 'brands'])->name('seller.brands');
    Route::get('/settings', [SellerDashboardController::class, 'settings'])->name('seller.settings');
    Route::post('/settings', [SellerDashboardController::class, 'updateSettings']);
    Route::post('/settings/personal', [SellerDashboardController::class, 'updatePersonal']);
});

/*
|--------------------------------------------------------------------------
| Admin
|--------------------------------------------------------------------------
*/
Route::prefix('admin')->middleware(['auth', 'role:ADMIN'])->group(function () {
    Route::get('/', [AdminController::class, 'dashboard'])->name('admin.dashboard');
    Route::get('/users', [AdminController::class, 'users'])->name('admin.users');
    Route::post('/users', [AdminController::class, 'storeUser']);
    Route::post('/users/{id}/edit', [AdminController::class, 'updateUser']);
    Route::post('/users/{id}/ban', [AdminController::class, 'banUser']);
    Route::post('/users/{id}/unban', [AdminController::class, 'unbanUser']);
    Route::post('/users/{id}/delete', [AdminController::class, 'deleteUser']);
    Route::get('/sellers', [AdminController::class, 'sellers'])->name('admin.sellers');
    Route::post('/sellers', [AdminController::class, 'storeSeller']);
    Route::get('/seller-applications', [AdminController::class, 'applications'])->name('admin.applications');
    Route::post('/sellers/{id}/approve', [AdminController::class, 'approveSeller']);
    Route::post('/sellers/{id}/reject', [AdminController::class, 'rejectSeller']);
    Route::post('/sellers/{id}/ban', [AdminController::class, 'banSeller']);
    Route::post('/sellers/{id}/unban', [AdminController::class, 'unbanSeller']);
    Route::post('/sellers/{id}/edit', [AdminController::class, 'updateSeller']);
    Route::post('/sellers/{id}/delete', [AdminController::class, 'deleteSeller']);
    Route::get('/products', [AdminController::class, 'products'])->name('admin.products');
    Route::get('/products/{id}/edit', [AdminController::class, 'editProduct']);
    Route::post('/products/{id}/edit', [AdminController::class, 'updateProduct']);
    Route::post('/products/{id}/delete', [AdminController::class, 'deleteProduct']);
    Route::get('/categories', [AdminController::class, 'categories'])->name('admin.categories');
    Route::post('/categories', [AdminController::class, 'storeCategory']);
    Route::post('/categories/{id}/edit', [AdminController::class, 'updateCategory']);
    Route::post('/categories/{id}/delete', [AdminController::class, 'deleteCategory']);
    Route::get('/brands', [AdminController::class, 'brands'])->name('admin.brands');
    Route::post('/brands', [AdminController::class, 'storeBrand']);
    Route::post('/brands/{id}/edit', [AdminController::class, 'updateBrand']);
    Route::post('/brands/{id}/delete', [AdminController::class, 'deleteBrand']);
    Route::get('/orders', [AdminController::class, 'orders'])->name('admin.orders');
    Route::get('/orders/checkout/{userId}/{placedAt}', [AdminController::class, 'orderCheckout'])->name('admin.orders.checkout');
    Route::post('/orders/{id}/confirm-hold', [AdminController::class, 'confirmHold']);
    Route::post('/orders/{id}/confirm-delivered', [AdminController::class, 'confirmDelivered']);
    Route::post('/orders/{id}/release-payout', [AdminController::class, 'releasePayout']);
    Route::post('/orders/{id}/refund', [AdminController::class, 'refundOrder']);
    Route::get('/commissions', [AdminController::class, 'commissions'])->name('admin.commissions');
    Route::get('/countries', [AdminController::class, 'countries'])->name('admin.countries');
    Route::get('/countries/{name}', [AdminController::class, 'countryDetail'])->name('admin.countries.show')->where('name', '.*');
    Route::get('/reviews', [AdminController::class, 'reviews'])->name('admin.reviews');
    Route::post('/reviews/{id}/hide', [AdminController::class, 'hideReview']);
    Route::get('/settings', [AdminController::class, 'settings'])->name('admin.settings');
});
