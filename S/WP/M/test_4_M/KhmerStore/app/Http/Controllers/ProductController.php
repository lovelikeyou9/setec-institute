<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class ProductController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('products.index');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view("products.create");
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        return "<h1 style='color: brown;'>You called Store method.</h1>";
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        return "<h1 style='color: orange;'>You called Show method.</h1>";
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        return view("products.edit");
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        return "<h1 style='color: purple;'>You called Update method.</h1>";
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        return "<h1 style='color: red;'>You called Destroy method.</h1>";
    }
    public function search() {
        return view('products.search');
    }
    public function remove($id) {
        return "Hello, You called remove function.";
    }
}
