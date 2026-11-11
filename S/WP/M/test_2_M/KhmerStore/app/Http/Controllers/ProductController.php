<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Route;
use Illuminate\Http\Request;


class ProductController extends Controller
{
    // test_2_m and test_3_m
    // /**
    //  * Display a listing of the resource.
    //  */
    // public function index()
    // {
    //     return "You called Index method.";
    //     //
    // }

    // /**
    //  * Show the form for creating a new resource.
    //  */
    // public function create()
    // {
    //     //
    // }

    // /**
    //  * Store a newly created resource in storage.
    //  */
    // public function store(Request $request)
    // {
    //     //
    // }

    // /**
    //  * Display the specified resource.
    //  */
    // public function show(string $id)
    // {
    //     //
    // }

    // /**
    //  * Show the form for editing the specified resource.
    //  */
    // public function edit(string $id)
    // {
    //     //
    // }

    // /**
    //  * Update the specified resource in storage.
    //  */
    // public function update(Request $request, string $id)
    // {
    //     //
    // }

    // /**
    //  * Remove the specified resource from storage.
    //  */
    // public function destroy(string $id)
    // {
    //     //
    // }


    // test_3_m

    // 1
    // //  * Display a listing of the resource.
    // //  */
    // public function index($a, $b)
    // {
    //     $sum = $a + $b;
    //     $minus = $a - $b;

    //     // return view("products.index", compact('sum', 'minus'));
    //     // return view("products.index", ['sum'=>$sum, 'minus'=>$minus]);
    //     return view("products.index")->with(['sum'=>$sum, 'minus'=>$minus]);
    //     // return view('products.index')->with('sum', $sum);
    // }
    // // http://127.0.0.1:8000/products/10/5


    // 2
    // //  * Display a listing of the resource.
    // //  */
    // public function index(float $a, float $b)
    // {
    //     $sum = $a + $b;
    //     $minus = $a - $b;

    //     // return view("products.index", compact('sum', 'minus'));
    //     // return view("products.index", ['sum'=>$sum, 'minus'=>$minus]);
    //     return view("products.index")->with(['sum'=>$sum, 'minus'=>$minus]);
    //     // return view('products.index')->with('sum', $sum);

    // }
    // // http://127.0.0.1:8000/products/10/5


    // test_5_M
    // 5
    public function index(){
        // return view("products.index");
        // return "<h1>This is the Index method</h1>";
        return view('products.index');
    }

    // /**
    //  * Display the specified resource.
    //  */
    public function store(Request $request){
        return "<h1 style='color: Brown;'>You called Store method.</h1>";
    }

    // /**
    //  * Display the specified resource.
    //  */
    public function create(){
        return view("products.create");
    }

    // /**
    //  * Display the specified resource.
    //  */
    public function show(){
        return "<h1 style='color: orange;'>You called Show method.</h1>";
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        // return "<h1 style='color: blue;'>You called Edit method.</h1>";
        return view("products.edit");
    }

    // /**
    //  * Display the specified resource.
    //  */
    public function update(){
        return "<h1 style='color: yellow;'>You called Update method.</h1>";
    }

    // /**
    //  * Display the specified resource.
    //  */
    public function destroy(){
        return "<h1 style='color: red;'>You called Destroy method.</h1>";
    }

    public function search() {
        return view('products.search');
    }

    public function remove($id) {
        return "Hello, You called remove function.";
    }


}
