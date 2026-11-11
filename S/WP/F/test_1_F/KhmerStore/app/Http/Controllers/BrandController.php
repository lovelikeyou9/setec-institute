<?php

namespace App\Http\Controllers;

use App\Models\Brand;
use Illuminate\Http\Request;

class BrandController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $brands = Brand::all();
        // $brands = Brand::withTrashed()->get();
        // $brands = Brand::onlyTrashed()->get();
        // ---------------------
        // $brands = Brand::select('id', 'brand_name', 'description')->get();
        // ---------------------
        // $brands = Brand::where('id', '>='  ,2)->where('id', '<=', 6)->get();
        // ---------------------
        // $brands = Brand::orWhere('id', '='  ,2)->orWhere('id', '=', 6)->get();
        // $brands = Brand::where('id', '>='  ,2)->where('id', '<=', 6)->orderBy('id','desc')->limit(2)->get();
        return view('brands.index', compact('brands'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('brands.create');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        Brand::create(
            [
                'brand_name' => $request->input('brand_name'),
                'description' => $request->input('description'),
            ]
        );
        // ------------------------
        // Brand::create($request->all());
        // -------------------------
        // $brand = new Brand();
        // $brand->brand_name = $request->input('brand_name');
        // $brand->description = $request->input('description');
        // $brand->save();
        return redirect()->route('brands.create');
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        $brand = Brand::findOrFail($id);
        return view('brands.show')->with('brand', $brand);
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        $brand = Brand::findOrFail($id);
        // $brand = Brand::find($id);
        return view("brands.edit", compact('brand'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        Brand::find($id)->update($request->all());
        return redirect()->route('brands.index');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        Brand::find($id)->forceDelete();
        return redirect()->route('brands.index');
    }
    public function tempDeletion(string $id) {
        Brand::find($id)->delete();
        return redirect('brands');
    }
}


