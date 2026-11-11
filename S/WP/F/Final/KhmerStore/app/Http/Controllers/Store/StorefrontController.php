<?php

namespace App\Http\Controllers\Store;

use App\Http\Controllers\Controller;
use App\Models\Brand;
use App\Models\Category;
use App\Models\Product;
use App\Models\Review;
use App\Models\Seller;
use App\Models\SellerFollow;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class StorefrontController extends Controller
{
    public function home()
    {
        $flashDeals = Product::with(['brand', 'seller'])
            ->where('active', 1)->where('flash_deal', 1)
            ->orderByDesc('discount_percent')->limit(8)->get();
        $featured = Product::with(['brand', 'seller'])
            ->where('active', 1)->where('featured', 1)
            ->orderByDesc('sold_count')->limit(8)->get();
        $categories = Category::where('active', 1)->orderBy('sort_order')->get();
        $brands = Brand::where('active', 1)->orderBy('name')->limit(16)->get();
        $sellers = Seller::where('status', 'APPROVED')->orderByDesc('total_sales')->limit(6)->get();

        $promoSlides = $this->buildPromoSlides($categories);
        $showcases = $this->buildShowcases();

        return view('store.home', compact(
            'flashDeals', 'featured', 'categories', 'brands', 'sellers', 'promoSlides', 'showcases'
        ));
    }

    private function buildPromoSlides($categories): array
    {
        $bySlug = $categories->keyBy('slug');
        $defs = [
            ['Computers & Components', 'PCS, LAPTOPS, CPUS, GPUS, AND FULL BUILDS.', 'Shop Computers', 'computers', '/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif'],
            ['Networking & Wi-Fi', 'ROUTERS, SWITCHES, MESH, AND NAS STORAGE.', 'Shop Networking', 'wifi-routers', '/uploads/products/BE9300 Tri-Band Wi-Fi 7 Router.jpg'],
            ['Gaming Consoles', 'CONSOLES, CONTROLLERS, AND DIGITAL GAME CARDS.', 'Shop Gaming', 'gaming-consoles', '/uploads/products/Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif'],
            ['Cameras, Video & Drones', 'ACTION CAMS, CAPTURE CARDS, AND AERIAL TECH.', 'Shop Capture', 'drones', '/uploads/products/DJI Mini 4 Pro.jpg'],
            ['Power & Pro IT', 'CHARGERS, UPS, SERVERS, AND WORKSTATIONS.', 'Shop Power & IT', 'servers', '/uploads/products/Dell PowerEdge R760.jpg'],
        ];

        $slides = [];
        foreach ($defs as $d) {
            $cat = $bySlug->get($d[3]);
            $slides[] = [
                'title' => $d[0],
                'subtitle' => $d[1],
                'cta' => $d[2],
                'href' => $cat ? url('/products?category='.$cat->id) : url('/products'),
                'image' => $d[4],
            ];
        }

        return $slides;
    }

    private function buildShowcases(): array
    {
        $defs = [
            ['wifi-routers', 'teal'],
            ['drones', 'blue'],
            ['gaming-consoles', 'purple'],
            ['earbuds', 'orange'],
        ];
        $panels = [];
        foreach ($defs as [$slug, $theme]) {
            $cat = Category::where('slug', $slug)->where('active', 1)->first();
            if (! $cat) {
                continue;
            }
            $products = Product::where('category_id', $cat->id)->where('active', 1)
                ->orderByDesc('sold_count')->limit(3)->get();
            if ($products->isEmpty()) {
                continue;
            }
            $hero = $products->first();
            $panels[] = [
                'category' => $cat,
                'banner_title' => $hero->name,
                'banner_image' => $hero->image_url ?: $cat->image_url,
                'theme' => $theme,
                'price_from' => $products->min('price'),
                'products' => $products,
            ];
        }

        return $panels;
    }

    public function products(Request $request)
    {
        $q = Product::with(['brand', 'category', 'seller'])->where('active', 1);

        if ($request->filled('q')) {
            $term = '%'.$request->q.'%';
            $q->where(function ($w) use ($term) {
                $w->where('name', 'like', $term)->orWhere('description', 'like', $term);
            });
        }
        if ($request->filled('category')) {
            $q->where('category_id', $request->category);
        }
        if ($request->filled('brand')) {
            $q->where('brand_id', $request->brand);
        }
        if ($request->filled('minPrice')) {
            $q->where('price', '>=', $request->minPrice);
        }
        if ($request->filled('maxPrice')) {
            $q->where('price', '<=', $request->maxPrice);
        }
        if ($request->boolean('inStock')) {
            $q->where('stock', '>', 0);
        }

        $sort = $request->get('sort', 'recommended');
        match ($sort) {
            'price_asc' => $q->orderBy('price'),
            'price_desc' => $q->orderByDesc('price'),
            'newest' => $q->orderByDesc('created_at'),
            'rating' => $q->orderByDesc('rating'),
            'sold' => $q->orderByDesc('sold_count'),
            default => $q->orderByDesc('featured')->orderByDesc('sold_count'),
        };

        $products = $q->paginate(12)->withQueryString();
        $categories = Category::where('active', 1)->orderBy('sort_order')->get();
        $brands = Brand::where('active', 1)->orderBy('name')->get();
        $selectedCategory = $request->filled('category')
            ? Category::find($request->category) : null;

        return view('store.products', compact('products', 'categories', 'brands', 'selectedCategory', 'sort'));
    }

    public function productDetail(string $slug)
    {
        $product = Product::with(['brand', 'category', 'seller.user'])
            ->where('slug', $slug)
            ->where('active', 1)
            ->whereHas('seller', fn ($q) => $q->where('status', 'APPROVED'))
            ->firstOrFail();
        $reviews = Review::with('user')
            ->where('product_id', $product->id)->where('hidden', 0)
            ->orderByDesc('created_at')->get();
        $related = Product::with('brand')
            ->where('category_id', $product->category_id)
            ->where('id', '!=', $product->id)->where('active', 1)
            ->limit(4)->get();
        $following = false;
        if (Auth::check() && $product->seller) {
            $following = SellerFollow::where('user_id', Auth::id())
                ->where('seller_id', $product->seller_id)->exists();
        }

        return view('store.product-detail', compact('product', 'reviews', 'related', 'following'));
    }

    public function deals()
    {
        $flashDeals = Product::with(['brand', 'seller'])
            ->where('active', 1)->where('flash_deal', 1)
            ->orderByDesc('discount_percent')->get();

        return view('store.deals', compact('flashDeals'));
    }

    public function brands()
    {
        $brands = Brand::where('active', 1)->orderBy('name')->get();

        return view('store.brands', compact('brands'));
    }

    public function brandShow(string $slug)
    {
        $brand = Brand::where('slug', $slug)->firstOrFail();
        $products = Product::with('seller')->where('brand_id', $brand->id)
            ->where('active', 1)->orderByDesc('sold_count')->paginate(12);

        return view('store.brand-show', compact('brand', 'products'));
    }

    public function categories()
    {
        $categories = Category::where('active', 1)->orderBy('sort_order')->get();

        return view('store.categories', compact('categories'));
    }

    public function categoryShow(string $slug)
    {
        $category = Category::where('slug', $slug)->firstOrFail();
        $products = Product::with(['brand', 'seller'])->where('category_id', $category->id)
            ->where('active', 1)->orderByDesc('sold_count')->paginate(12);

        return view('store.category-show', compact('category', 'products'));
    }

    public function sellers()
    {
        $sellers = Seller::where('status', 'APPROVED')
            ->orderByDesc('total_sales')->paginate(12);

        return view('store.sellers', compact('sellers'));
    }

    public function sellerStore(int $id)
    {
        $seller = Seller::with('user')->where('status', 'APPROVED')->findOrFail($id);
        $products = Product::with('brand')->where('seller_id', $seller->id)
            ->where('active', 1)->orderByDesc('sold_count')->paginate(12);
        $following = Auth::check()
            && SellerFollow::where('user_id', Auth::id())->where('seller_id', $seller->id)->exists();

        return view('store.seller-store', compact('seller', 'products', 'following'));
    }

    public function followSeller(int $id)
    {
        $seller = Seller::where('status', 'APPROVED')->findOrFail($id);
        $exists = SellerFollow::where('user_id', Auth::id())->where('seller_id', $seller->id)->first();
        if ($exists) {
            $exists->delete();
            $seller->followers = max(0, ($seller->followers ?? 0) - 1);
            $seller->save();

            return back()->with('message', 'Unfollowed store.');
        }
        SellerFollow::create([
            'user_id' => Auth::id(),
            'seller_id' => $seller->id,
            'created_at' => now(),
        ]);
        $seller->followers = ($seller->followers ?? 0) + 1;
        $seller->save();

        return back()->with('message', 'Following store.');
    }

    public function becomeSellerForm()
    {
        return view('store.become-seller');
    }

    public function becomeSeller(Request $request)
    {
        $user = Auth::user();
        if ($user->seller) {
            return redirect('/become-seller')->with('error', 'You already have a seller application or store.');
        }

        $data = $request->validate([
            'store_name' => 'required|string|max:150',
            'owner_name' => 'required|string|max:100',
            'phone' => 'nullable|string|max:30',
            'location' => 'nullable|string|max:100',
            'address' => 'nullable|string|max:200',
            'country' => 'nullable|string|max:80',
            'description' => 'nullable|string|max:1000',
        ]);

        Seller::create([
            'user_id' => $user->id,
            'store_name' => $data['store_name'],
            'owner_name' => $data['owner_name'],
            'phone' => $data['phone'] ?? $user->phone,
            'location' => $data['location'] ?? null,
            'address' => $data['address'] ?? null,
            'country' => $data['country'] ?? null,
            'description' => $data['description'] ?? null,
            'status' => 'PENDING',
            'level' => 'NEW',
            'rating' => 0,
            'followers' => 0,
            'total_products' => 0,
            'total_sales' => 0,
            'created_at' => now(),
            'joined_at' => now(),
        ]);

        // Keep CUSTOMER role until admin approves (avoids /account ↔ seller-dashboard redirect loop).
        if ($user->role !== 'ADMIN') {
            $user->role = 'CUSTOMER';
            $user->save();
        }

        return redirect('/become-seller')->with('message', 'Seller application submitted. Waiting for admin approval.');
    }

    public function submitReview(Request $request, string $slug)
    {
        $product = Product::where('slug', $slug)->firstOrFail();
        $data = $request->validate([
            'rating' => 'required|integer|min:1|max:5',
            'comment' => 'nullable|string|max:2000',
        ]);

        Review::create([
            'product_id' => $product->id,
            'user_id' => Auth::id(),
            'rating' => $data['rating'],
            'comment' => $data['comment'] ?? null,
            'hidden' => 0,
            'verified_purchase' => 0,
            'created_at' => now(),
        ]);

        $avg = Review::where('product_id', $product->id)->where('hidden', 0)->avg('rating');
        $product->rating = round((float) $avg, 1);
        $product->review_count = Review::where('product_id', $product->id)->where('hidden', 0)->count();
        $product->save();

        return back()->with('message', 'Review submitted.');
    }
}
