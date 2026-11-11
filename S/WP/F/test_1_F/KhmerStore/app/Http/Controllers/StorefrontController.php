<?php

namespace App\Http\Controllers;

use App\Models\Brand;
use App\Models\Category;
use App\Models\Product;
use Illuminate\Http\Request;
use Illuminate\View\View;

class StorefrontController extends Controller
{
    public function home(): View
    {
        $featured = Product::with(['brand', 'category', 'displayPhoto', 'photos'])
            ->where('status', '1')
            ->where('trash', '0')
            ->latest('id')
            ->take(8)
            ->get();

        $categories = Category::withCount([
            'products' => fn ($q) => $q->where('status', '1')->where('trash', '0'),
        ])
            ->having('products_count', '>', 0)
            ->orderBy('category_name')
            ->take(6)
            ->get();

        $brands = Brand::withCount([
            'products' => fn ($q) => $q->where('status', '1')->where('trash', '0'),
        ])
            ->having('products_count', '>', 0)
            ->orderBy('brand_name')
            ->take(8)
            ->get();

        return view('store.home', compact('featured', 'categories', 'brands'));
    }

    public function products(Request $request): View
    {
        $query = Product::with(['brand', 'category', 'displayPhoto', 'photos'])
            ->where('status', '1')
            ->where('trash', '0');

        if ($search = trim((string) $request->input('q', ''))) {
            $query->where(function ($q) use ($search) {
                $q->where('model', 'like', "%{$search}%")
                    ->orWhere('description', 'like', "%{$search}%");
            });
        }

        if ($request->filled('category')) {
            $query->where('category_id', $request->integer('category'));
        }

        if ($request->filled('brand')) {
            $query->where('brand_id', $request->integer('brand'));
        }

        if ($request->filled('min_price')) {
            $query->where('price', '>=', (float) $request->input('min_price'));
        }

        if ($request->filled('max_price')) {
            $query->where('price', '<=', (float) $request->input('max_price'));
        }

        if ($request->boolean('in_stock')) {
            $query->where('quantity', '>', 0);
        }

        $sort = $request->input('sort', 'newest');
        match ($sort) {
            'price_asc' => $query->orderBy('price', 'asc'),
            'price_desc' => $query->orderBy('price', 'desc'),
            'name_asc' => $query->orderBy('model', 'asc'),
            'name_desc' => $query->orderBy('model', 'desc'),
            default => $query->latest('id'),
        };

        $products = $query->paginate(12)->withQueryString();

        $categories = Category::orderBy('category_name')->get();
        $brands = Brand::orderBy('brand_name')->get();

        $filters = [
            'q' => $request->input('q', ''),
            'category' => $request->input('category', ''),
            'brand' => $request->input('brand', ''),
            'min_price' => $request->input('min_price', ''),
            'max_price' => $request->input('max_price', ''),
            'in_stock' => $request->boolean('in_stock'),
            'sort' => $sort,
        ];

        return view('store.products', compact('products', 'categories', 'brands', 'filters'));
    }

    public function show(string $id): View
    {
        $product = Product::with(['brand', 'category', 'photos', 'displayPhoto'])
            ->where('status', '1')
            ->where('trash', '0')
            ->findOrFail($id);

        $related = Product::with(['brand', 'displayPhoto', 'photos'])
            ->where('status', '1')
            ->where('trash', '0')
            ->where('id', '!=', $product->id)
            ->where(function ($q) use ($product) {
                $q->where('category_id', $product->category_id)
                    ->orWhere('brand_id', $product->brand_id);
            })
            ->latest('id')
            ->take(4)
            ->get();

        return view('store.show', compact('product', 'related'));
    }
}
