<?php

use App\Http\Controllers\ProductController;
use Illuminate\Support\Facades\Route;


// test_1_m
// test_2_m
// Route::get('/', function () {
//     return view('welcome');
// });

// Route::get('/about', function () {
//     return view ('about');
// });

// Route::get('/service', function () {
//     return view ('service');
// });

// Route::get('/sum/{a}/{b?}', function ($x, $y){
//     $sum = $x + $y;
//     return $sum;
// });


// Route::get('/', function () {
//     return view('welcome');
// });


// Route::get('/products/{a}/{b}', [ProductController::class, "index"]);


// test_3_m
// 3
// Route::get('/products/{x}/{y?}', function (float $a, float $b=0)
// {
//         $sum = $a + $b;
//         $minus = $a - $b;
//         // return view("products.index", compact('sum', 'minus'));
//         // return view("products.index", ['sum'=>$sum, 'minus'=>$minus]);
//         return view("products.index")->with(['sum'=>$sum, 'minus'=>$minus]);
//         // return view("products.index")->with('sum', $sum);
// })->where(['x'=>'\d+(\.\d+)?', 'y'=>'(\d+(\.\d+)?)?']);



// 4
// Route::get(
//     '/products/{x}/{y?}',
//     [ProductController::class, 'index']
// )->where(['x'=>'\d+(\.\d+)?', 'y'=>'(\d+(\.\d+)?)?']);


// 5
// Route::resource('/products', ProductController::class);



// test_5_m
// Route::get('/', function () {
//     return view('welcome');
// });

// Route::resource('/products', ProductController::class);


// test_5_m
    // Route::get('/', function () {
    //     return view('welcome');
    // });

// Route::get('/products', [ProductController::class, 'index']);
// Route::get('/products/create', [ProductController::class, 'create']);
// Route::get('/products/{id}', [ProductController::class, 'show']);
// Route::get('/products/{id}/edit', [ProductController::class, 'edit']);
// Route::put('/products/{id}', [ProductController::class, 'update']);
// Route::post('/products', [ProductController::class, 'store']);
// Route::delete('/products/{id}', [ProductController::class, 'destroy']);



// test_6_m
// Route::get('/', function () {
//     return view('dashboard');
// });

// Route::resource('/products', ProductController::class);


// test_7_m
Route::get('/', function () {
    return view('dashboard');
})->name('home');

Route::resource('/products', ProductController::class);
Route::get('/products-search', [ProductController::class, 'search'])->name('products.search');
Route::get('/products/remove/{id}', [ProductController::class, 'remove'])->name('products.remove');
