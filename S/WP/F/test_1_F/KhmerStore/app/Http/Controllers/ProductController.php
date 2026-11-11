<?php

namespace App\Http\Controllers;

use App\Models\Brand;
use App\Models\Category;
use App\Models\Product;
use App\Models\ProductPhoto;
use App\Rules\ToUpperCase;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Storage;

class ProductController extends Controller
{
    public function index()
    {
        $products = Product::with(['brand', 'category', 'user', 'editor', 'displayPhoto', 'photos'])
            ->latest('id')
            ->paginate(10);

        return view('products.index', compact('products'));
    }

    public function create()
    {
        $brands = Brand::all();
        $categories = Category::all();
        return view("products.create", compact('brands', 'categories'));
    }

    public function store(Request $request)
    {
        // Save the logged-in user's ID (who added this product)
        $request["user_id"] = Auth::id();
        $validated = $request->validate([
            'model' => ["required", "unique:products,model", "min:2", "max:50", new ToUpperCase],
            'price' => 'required|regex:/^[0-9]+(\.[0-9]{2,}$)?/',
            'quantity' => 'required|numeric',
            'warranty' => 'nullable|numeric',
            'category_id' => 'required|exists:categories,id',
            'brand_id' => 'required|exists:brands,id',
            'user_id' => 'required|exists:users,id',
            'description' => 'nullable|string',
            'photos' => 'required|array|min:1',
            'photos.*' => 'image|mimes:jpg,jpeg,png,webp,gif|max:5120',
            'display_index' => 'nullable|integer|min:0',
        ], [
            "model.required" => "Model can not be empty.",
            "model.unique" => "This model name has already exist.",
            "price.required" => "Price can not be empty.",
            "price.regex" => "Invalid the format of price.",
            "photos.required" => "Please upload at least one photo.",
        ]);

        try {
            $product = Product::create(collect($validated)->except(['photos', 'display_index'])->toArray());
            $this->storePhotos($product, $request->file('photos'), (int) $request->input('display_index', 0));

            return redirect()->route("products.index")->with(
                'success',
                'Product created successfully by ' . Auth::user()->name . '.'
            );
        } catch (\Throwable $e) {
            return redirect()->back()->withInput()->with('error', 'Failed to create product: ' . $e->getMessage());
        }
    }

    public function show(string $id)
    {
        $product = Product::with(['brand', 'category', 'user', 'editor', 'photos'])->findOrFail($id);
        return view('products.show', compact('product'));
    }

    public function edit(string $id)
    {
        $brands = Brand::all();
        $categories = Category::all();
        $product = Product::with(['brand', 'category', 'photos'])->where('id', $id)->firstOrFail();
        return view("products.edit", compact('brands', 'categories', 'product'));
    }

    public function update(Request $request, string $id)
    {
        $product = Product::with('photos')->findOrFail($id);

        $validated = $request->validate([
            'model' => ["required", "unique:products,model,$id", "min:2", "max:50", new ToUpperCase],
            'price' => 'required|regex:/^[0-9]+(\.[0-9]{2,}$)?/',
            'quantity' => 'required|numeric',
            'warranty' => 'nullable|numeric',
            'category_id' => 'required|exists:categories,id',
            'brand_id' => 'required|exists:brands,id',
            'description' => 'nullable|string',
            'photos' => 'nullable|array',
            'photos.*' => 'image|mimes:jpg,jpeg,png,webp,gif|max:5120',
            'delete_photos' => 'nullable|array',
            'delete_photos.*' => 'integer|exists:product_photos,id',
            'display_photo_id' => 'nullable|integer',
            'new_display_index' => 'nullable|integer|min:0',
        ], [
            "model.required" => "Model can not be empty.",
            "model.unique" => "This model name has already exist.",
            "price.required" => "Price can not be empty.",
            "price.regex" => "Invalid the format of price.",
        ]);

        try {
            // Keep original creator (user_id). Save who edited in edited_by.
            $data = collect($validated)->except([
                'photos', 'delete_photos', 'display_photo_id', 'new_display_index'
            ])->toArray();
            $data['edited_by'] = Auth::id();

            $product->update($data);

            $deleteIds = $request->input('delete_photos', []);
            if (!empty($deleteIds)) {
                $photosToDelete = $product->photos()->whereIn('id', $deleteIds)->get();
                foreach ($photosToDelete as $photo) {
                    Storage::disk('public')->delete($photo->path);
                    $photo->delete();
                }
            }

            $product->refresh()->load('photos');

            if ($request->hasFile('photos')) {
                $startOrder = (int) ($product->photos()->max('sort_order') ?? -1) + 1;
                $this->storePhotos(
                    $product,
                    $request->file('photos'),
                    null,
                    $startOrder,
                    false
                );
                $product->refresh()->load('photos');
            }

            if ($product->photos()->count() === 0) {
                return redirect()->back()->withInput()->with('error', 'Product must keep at least one photo.');
            }

            $this->setDisplayPhoto(
                $product,
                $request->input('display_photo_id'),
                $request->hasFile('photos') ? $request->input('new_display_index') : null
            );

            return redirect()->route("products.index")->with(
                'success',
                'Product updated successfully by ' . Auth::user()->name . '.'
            );
        } catch (\Throwable $e) {
            return redirect()->back()->withInput()->with('error', 'Failed to update product: ' . $e->getMessage());
        }
    }

    public function destroy(string $id)
    {
        try {
            $product = Product::with('photos')->findOrFail($id);
            $deletedByName = Auth::user()->name;

            foreach ($product->photos as $photo) {
                Storage::disk('public')->delete($photo->path);
            }
            $product->delete();

            return redirect()->route('products.index')->with(
                'success',
                "Product deleted successfully by {$deletedByName}."
            );
        } catch (\Throwable $e) {
            return redirect()->route('products.index')->with('error', 'Failed to delete product.');
        }
    }

    public function search()
    {
        return view('products.search');
    }

    public function remove($id)
    {
        return $this->destroy($id);
    }

    public function deletePhoto(string $productId, string $photoId)
    {
        try {
            $product = Product::with('photos')->findOrFail($productId);
            $photo = $product->photos()->where('id', $photoId)->firstOrFail();

            if ($product->photos()->count() <= 1) {
                return redirect()->back()->with('error', 'Cannot delete the last photo.');
            }

            $wasDisplay = $photo->is_display;
            Storage::disk('public')->delete($photo->path);
            $photo->delete();

            if ($wasDisplay) {
                $next = $product->photos()->first();
                if ($next) {
                    $next->update(['is_display' => true]);
                    $product->update(['photo' => $next->path]);
                }
            }

            return redirect()->back()->with('success', 'Photo deleted successfully.');
        } catch (\Throwable $e) {
            return redirect()->back()->with('error', 'Failed to delete photo.');
        }
    }

    private function storePhotos(Product $product, array $files, ?int $displayIndex = 0, int $startOrder = 0, bool $setDisplay = true): void
    {
        $displayIndex = $displayIndex ?? 0;

        foreach ($files as $index => $file) {
            $path = $file->store('products', 'public');
            $isDisplay = $setDisplay && $index === $displayIndex;

            ProductPhoto::create([
                'product_id' => $product->id,
                'path' => $path,
                'is_display' => $isDisplay,
                'sort_order' => $startOrder + $index,
            ]);

            if ($isDisplay) {
                $product->update(['photo' => $path]);
            }
        }

        if ($setDisplay && !$product->photos()->where('is_display', true)->exists()) {
            $first = $product->photos()->first();
            if ($first) {
                $first->update(['is_display' => true]);
                $product->update(['photo' => $first->path]);
            }
        }
    }

    private function setDisplayPhoto(Product $product, $displayPhotoId = null, $newDisplayIndex = null): void
    {
        $product->load('photos');

        if ($displayPhotoId) {
            $target = $product->photos->firstWhere('id', (int) $displayPhotoId);
            if ($target) {
                $product->photos()->update(['is_display' => false]);
                $target->update(['is_display' => true]);
                $product->update(['photo' => $target->path]);
                return;
            }
        }

        if ($newDisplayIndex !== null && $newDisplayIndex !== '') {
            // Prefer newly uploaded photos for new_display_index relative to new batch only is complex;
            // instead if user chose an existing display_photo_id we already handled it.
            // Fallback: keep current display or first photo.
        }

        if (!$product->photos()->where('is_display', true)->exists()) {
            $first = $product->photos()->first();
            if ($first) {
                $first->update(['is_display' => true]);
                $product->update(['photo' => $first->path]);
            }
        }
    }
}
