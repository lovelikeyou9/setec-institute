<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Brand;
use App\Models\Category;
use App\Models\Commission;
use App\Models\Order;
use App\Models\Product;
use App\Models\Review;
use App\Models\Seller;
use App\Models\User;
use App\Services\AdminCountryService;
use App\Services\OrderService;
use App\Services\ProductVariantOptions;
use Illuminate\Http\Request;
use Illuminate\Support\Str;

class AdminController extends Controller
{
    public function __construct(
        private OrderService $orders,
        private AdminCountryService $countries,
    ) {}

    /** Map demo camelCase / alias fields onto Laravel snake_case inputs. */
    private function normalizeDemoRequest(Request $request): void
    {
        // Alias uploaded files FIRST. exists()/has()/all() cache convertedFiles.
        foreach (['logoFile' => 'logo', 'imageFile' => 'image'] as $from => $to) {
            if ($request->files->has($from) && ! $request->files->has($to)) {
                $request->files->set($to, $request->files->get($from));
            }
        }

        $aliases = [
            'fullName' => 'full_name',
            'logoUrl' => 'logo_url',
            'imageUrl' => 'image_url',
            'categoryId' => 'category_id',
            'brandId' => 'brand_id',
            'originalPrice' => 'original_price',
            'storeName' => 'store_name',
            'ownerName' => 'owner_name',
            'colorOptions' => 'color_options',
            'storageOptions' => 'storage_options',
            'ramOptions' => 'ram_options',
            'optionGroupsJson' => 'option_groups',
        ];
        $merge = [];
        foreach ($aliases as $from => $to) {
            if ($request->exists($from) && ! $request->filled($to)) {
                $merge[$to] = $request->input($from);
            }
        }
        if ($merge) {
            $request->merge($merge);
        }
        foreach (['active', 'enabled'] as $flag) {
            if ($request->has($flag)) {
                $raw = $request->input($flag);
                $request->merge([
                    $flag => filter_var($raw, FILTER_VALIDATE_BOOLEAN)
                        || $raw === 'on'
                        || $raw === '1'
                        || $raw === 1,
                ]);
            }
        }
    }

    public function dashboard()
    {
        $year = (int) date('Y');
        $stats = [
            'users' => User::count(),
            'customers' => User::where('role', 'CUSTOMER')->count(),
            'sellers' => Seller::where('status', 'APPROVED')->count(),
            'pendingSellers' => Seller::where('status', 'PENDING')->count(),
            'products' => Product::count(),
            'orders' => Order::count(),
            'revenue' => (float) Order::whereNotIn('status', ['CANCELLED', 'REFUNDED'])->sum('total'),
            'platformFees' => (float) Commission::where('payout_status', 'RELEASED')->sum('commission_amount'),
            'feesInEscrow' => (float) Commission::whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->sum('commission_amount'),
            'escrow' => (float) Commission::whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->sum('sale_amount'),
            'escrowOrders' => Commission::whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->count(),
            'awaitHold' => Order::where('status', 'PAYMENT_CONFIRMED')->count(),
            'awaitDelivered' => Order::whereIn('status', ['SHIPPED', 'OUT_FOR_DELIVERY'])->count(),
            'awaitPayout' => Order::whereIn('status', ['DELIVERED', 'COMPLETED'])->whereNull('payout_released_at')->count(),
            'pendingOrders' => Order::whereNotIn('status', ['CANCELLED', 'REFUNDED', 'COMPLETED'])->count(),
        ];
        $recentOrders = Order::with('user', 'seller')->orderByDesc('placed_at')->limit(10)->get();
        $this->orders->autoReleaseEligibleOrders();

        $months = [];
        $ordersTrend = [];
        $revenueTrend = [];
        for ($m = 1; $m <= 12; $m++) {
            $months[] = date('M', mktime(0, 0, 0, $m, 1));
            $ordersTrend[] = Order::whereYear('placed_at', $year)->whereMonth('placed_at', $m)->count();
            $revenueTrend[] = (float) Order::whereYear('placed_at', $year)->whereMonth('placed_at', $m)
                ->whereNotIn('status', ['CANCELLED', 'REFUNDED'])->sum('total');
        }

        $categoryAgg = Product::query()
            ->selectRaw('categories.name as label, COALESCE(SUM(products.sold_count * products.price),0) as value')
            ->join('categories', 'categories.id', '=', 'products.category_id')
            ->groupBy('categories.id', 'categories.name')
            ->orderByDesc('value')
            ->limit(5)
            ->get();
        $catTotal = max(1, (float) $categoryAgg->sum('value'));
        $categoryRows = $categoryAgg->map(fn ($r) => [
            'label' => $r->label,
            'value' => (float) $r->value,
            'percent' => round(((float) $r->value / $catTotal) * 100, 1),
        ]);
        $categoryLabels = $categoryRows->pluck('label')->values()->all();
        $categoryValues = $categoryRows->pluck('value')->values()->all();

        $funnelLabels = ['Users', 'Customers', 'Sellers', 'Products', 'Orders'];
        $funnelValues = [
            $stats['users'], $stats['customers'], $stats['sellers'], $stats['products'], $stats['orders'],
        ];

        $countryAgg = Order::query()
            ->selectRaw("COALESCE(NULLIF(shipping_country,''),'Cambodia') as label, SUM(total) as value")
            ->whereNotIn('status', ['CANCELLED', 'REFUNDED'])
            ->groupBy('label')
            ->orderByDesc('value')
            ->limit(5)
            ->get();
        $countryTotal = max(1, (float) $countryAgg->sum('value'));
        $countryRows = $countryAgg->map(fn ($r) => [
            'label' => $r->label,
            'value' => (float) $r->value,
            'percent' => round(((float) $r->value / $countryTotal) * 100, 1),
        ]);
        if ($countryRows->isEmpty()) {
            $countryRows = collect([['label' => 'Cambodia', 'value' => $stats['revenue'], 'percent' => 100]]);
        }
        $countryLabels = $countryRows->pluck('label')->values()->all();
        $countryValues = $countryRows->pluck('value')->values()->all();

        $perfRows = Seller::where('status', 'APPROVED')->orderByDesc('total_sales')->limit(5)->get()
            ->map(function ($s) {
                $products = max(1, (int) $s->total_products);
                $arpu = (float) $s->total_sales / $products;

                return [
                    'name' => $s->store_name,
                    'products' => (int) $s->total_products,
                    'arpu' => $arpu,
                    'health' => ($s->rating ?? 0) >= 4.5 ? 'Good' : (($s->rating ?? 0) >= 3.5 ? 'Fair' : 'Watch'),
                ];
            });

        return view('admin.dashboard', compact(
            'stats', 'recentOrders', 'year', 'months', 'ordersTrend', 'revenueTrend',
            'categoryRows', 'categoryLabels', 'categoryValues',
            'funnelLabels', 'funnelValues',
            'countryRows', 'countryLabels', 'countryValues',
            'perfRows'
        ));
    }

    public function users(Request $request)
    {
        $q = User::query();
        if ($request->filled('q')) {
            $term = '%'.$request->q.'%';
            $q->where(function ($w) use ($term) {
                $w->where('full_name', 'like', $term)
                    ->orWhere('email', 'like', $term)
                    ->orWhere('phone', 'like', $term);
            });
        }
        if ($request->filled('role')) {
            $q->where('role', $request->role);
        }
        match ($request->get('sort', 'newest')) {
            'oldest' => $q->orderBy('created_at')->orderBy('id'),
            'name_asc' => $q->orderBy('full_name')->orderBy('id'),
            'name_desc' => $q->orderByDesc('full_name')->orderByDesc('id'),
            default => $q->orderByDesc('created_at')->orderByDesc('id'),
        };
        $users = $q->paginate(20)->withQueryString();

        return view('admin.users', compact('users'));
    }

    public function storeUser(Request $request)
    {
        $this->normalizeDemoRequest($request);
        $data = $request->validate([
            'full_name' => 'required|string|max:120',
            'email' => 'required|email|unique:users,email',
            'phone' => 'nullable|string|max:30',
            'password' => 'required|string|min:6',
            'role' => 'required|in:ADMIN,CUSTOMER,SELLER',
            'enabled' => 'nullable|boolean',
        ]);
        User::create([
            'full_name' => $data['full_name'],
            'email' => $data['email'],
            'phone' => $data['phone'] ?? null,
            'password' => $data['password'],
            'role' => $data['role'],
            'enabled' => $request->has('enabled') ? $request->boolean('enabled') : 1,
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        return back()->with('message', 'User created successfully');
    }

    public function updateUser(Request $request, int $id)
    {
        $this->normalizeDemoRequest($request);
        $user = User::findOrFail($id);
        $data = $request->validate([
            'full_name' => 'required|string|max:120',
            'email' => 'required|email|unique:users,email,'.$id,
            'phone' => 'nullable|string|max:30',
            'password' => 'nullable|string|min:6',
            'role' => 'required|in:ADMIN,CUSTOMER,SELLER',
            'enabled' => 'nullable|boolean',
        ]);
        $user->full_name = $data['full_name'];
        $user->email = $data['email'];
        $user->phone = $data['phone'] ?? null;
        $user->role = $data['role'];
        $user->enabled = $request->boolean('enabled');
        if (! empty($data['password'])) {
            $user->password = $data['password'];
        }
        $user->updated_at = now();
        $user->save();

        return back()->with('message', 'User updated successfully');
    }

    public function banUser(int $id)
    {
        User::where('id', $id)->update(['enabled' => 0, 'updated_at' => now()]);

        return back()->with('message', 'User banned');
    }

    public function unbanUser(int $id)
    {
        User::where('id', $id)->update(['enabled' => 1, 'updated_at' => now()]);

        return back()->with('message', 'User unbanned');
    }

    public function deleteUser(int $id)
    {
        User::where('id', $id)->delete();

        return back()->with('message', 'User deleted');
    }

    public function sellers(Request $request)
    {
        $q = Seller::with('user');
        if ($request->filled('q')) {
            $term = '%'.$request->q.'%';
            $q->where(function ($w) use ($term) {
                $w->where('store_name', 'like', $term)
                    ->orWhere('owner_name', 'like', $term)
                    ->orWhere('location', 'like', $term)
                    ->orWhereHas('user', fn ($u) => $u->where('email', 'like', $term));
            });
        }
        match ($request->get('sort', 'newest')) {
            'oldest' => $q->orderBy('created_at')->orderBy('id'),
            'name_asc' => $q->orderBy('store_name')->orderBy('id'),
            'name_desc' => $q->orderByDesc('store_name')->orderByDesc('id'),
            default => $q->orderByDesc('created_at')->orderByDesc('id'),
        };
        $sellers = $q->paginate(20)->withQueryString();
        $countryOptions = ['Cambodia', 'Thailand', 'Vietnam', 'Laos', 'Myanmar', 'Singapore', 'Malaysia', 'Indonesia', 'Philippines', 'China', 'Japan', 'South Korea', 'United States', 'Other'];

        return view('admin.sellers', compact('sellers', 'countryOptions'));
    }

    public function storeSeller(Request $request)
    {
        $this->normalizeDemoRequest($request);
        $data = $request->validate([
            'store_name' => 'required|string|max:150',
            'owner_name' => 'required|string|max:100',
            'email' => 'required|email|unique:users,email',
            'password' => 'required|string|min:6',
            'phone' => 'nullable|string|max:30',
            'country' => 'nullable|string|max:80',
            'location' => 'nullable|string|max:100',
            'address' => 'nullable|string|max:200',
            'level' => 'nullable|in:NEW,TOP,TRUSTED,VERIFIED',
        ]);

        $user = User::create([
            'full_name' => $data['owner_name'],
            'email' => $data['email'],
            'phone' => $data['phone'] ?? null,
            'password' => $data['password'],
            'role' => 'SELLER',
            'enabled' => 1,
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        Seller::create([
            'user_id' => $user->id,
            'store_name' => $data['store_name'],
            'owner_name' => $data['owner_name'],
            'phone' => $data['phone'] ?? null,
            'country' => $data['country'] ?? null,
            'location' => $data['location'] ?? null,
            'address' => $data['address'] ?? null,
            'status' => 'APPROVED',
            'level' => $data['level'] ?? 'VERIFIED',
            'rating' => 0,
            'followers' => 0,
            'response_rate' => 100,
            'total_products' => 0,
            'total_sales' => 0,
            'created_at' => now(),
            'joined_at' => now(),
        ]);

        return back()->with('message', 'Seller created successfully');
    }

    public function deleteSeller(int $id)
    {
        $seller = Seller::findOrFail($id);
        $userId = $seller->user_id;
        $seller->delete();
        if ($userId) {
            User::where('id', $userId)->where('role', 'SELLER')->delete();
        }

        return back()->with('message', 'Seller deleted successfully');
    }

    public function applications()
    {
        $applications = Seller::with('user')->where('status', 'PENDING')->orderByDesc('created_at')->get();

        return view('admin.applications', compact('applications'));
    }

    public function approveSeller(int $id)
    {
        $seller = Seller::findOrFail($id);
        $seller->status = 'APPROVED';
        $seller->joined_at = $seller->joined_at ?? now();
        $seller->save();
        if ($seller->user) {
            $seller->user->role = 'SELLER';
            $seller->user->save();
        }

        return back()->with('message', 'Seller approved');
    }

    public function rejectSeller(Request $request, int $id)
    {
        $seller = Seller::findOrFail($id);
        $seller->status = 'REJECTED';
        $seller->rejection_reason = $request->input('reason', 'Rejected');
        $seller->save();
        if ($seller->user && $seller->user->role === 'SELLER') {
            $seller->user->role = 'CUSTOMER';
            $seller->user->save();
        }

        return back()->with('message', 'Seller rejected');
    }

    /**
     * Ban store only — user stays able to log in as a customer and shop.
     * Selling is blocked until admin unbans (mirrors customer → seller lifecycle).
     */
    public function banSeller(int $id)
    {
        $seller = Seller::with('user')->findOrFail($id);
        $seller->status = 'SUSPENDED';
        $seller->save();

        if ($seller->user) {
            $seller->user->role = 'CUSTOMER';
            $seller->user->enabled = 1; // never disable login on seller ban
            $seller->user->save();
        }

        Product::where('seller_id', $seller->id)->update([
            'active' => 0,
            'updated_at' => now(),
        ]);

        return back()->with(
            'message',
            'Store banned. They can still log in and shop as a customer, but cannot sell until unbanned.'
        );
    }

    public function unbanSeller(int $id)
    {
        $seller = Seller::with('user')->findOrFail($id);
        $seller->status = 'APPROVED';
        $seller->save();

        if ($seller->user) {
            $seller->user->role = 'SELLER';
            $seller->user->enabled = 1;
            $seller->user->save();
        }

        Product::where('seller_id', $seller->id)->update([
            'active' => 1,
            'updated_at' => now(),
        ]);

        return back()->with('message', 'Store unbanned. Seller role restored — they can sell again.');
    }

    public function updateSeller(Request $request, int $id)
    {
        $this->normalizeDemoRequest($request);
        $seller = Seller::with('user')->findOrFail($id);
        $data = $request->validate([
            'store_name' => 'required|string|max:150',
            'owner_name' => 'required|string|max:100',
            'phone' => 'nullable|string|max:30',
            'location' => 'nullable|string|max:100',
            'address' => 'nullable|string|max:200',
            'country' => 'nullable|string|max:80',
            'status' => 'required|in:APPROVED,PENDING,REJECTED,SUSPENDED',
            'level' => 'required|in:NEW,TOP,TRUSTED,VERIFIED',
        ]);
        $seller->update($data);

        if ($seller->user) {
            if (in_array($data['status'], ['APPROVED'], true)) {
                $seller->user->role = 'SELLER';
                $seller->user->enabled = 1;
            } elseif (in_array($data['status'], ['SUSPENDED', 'REJECTED', 'PENDING'], true)) {
                $seller->user->role = 'CUSTOMER';
            }
            $seller->user->save();
        }

        if ($data['status'] === 'SUSPENDED') {
            Product::where('seller_id', $seller->id)->update(['active' => 0, 'updated_at' => now()]);
        } elseif ($data['status'] === 'APPROVED') {
            Product::where('seller_id', $seller->id)->update(['active' => 1, 'updated_at' => now()]);
        }

        return back()->with('message', 'Seller updated');
    }

    public function products(Request $request)
    {
        $q = Product::with(['brand', 'category', 'seller']);
        if ($request->filled('q')) {
            $q->where('name', 'like', '%'.$request->q.'%');
        }
        if ($request->filled('category')) {
            $q->where('category_id', $request->category);
        }
        if ($request->filled('brand')) {
            $q->where('brand_id', $request->brand);
        }
        $products = $q->orderByDesc('created_at')->paginate(20)->withQueryString();
        $categories = Category::orderBy('name')->get();
        $brands = Brand::orderBy('name')->get();

        return view('admin.products', compact('products', 'categories', 'brands'));
    }

    public function editProduct(int $id)
    {
        $product = Product::findOrFail($id);
        $categories = Category::orderBy('name')->get();
        $brands = Brand::orderBy('name')->get();

        return view('admin.product-form', compact('product', 'categories', 'brands'));
    }

    public function updateProduct(Request $request, int $id)
    {
        $this->normalizeDemoRequest($request);
        $product = Product::findOrFail($id);
        $data = $request->validate([
            'name' => 'required|string|max:250',
            'category_id' => 'required|exists:categories,id',
            'brand_id' => 'required|exists:brands,id',
            'description' => 'required|string',
            'specifications' => 'nullable|string',
            'option_groups' => 'nullable|string',
            'color_options' => 'nullable|string|max:2000',
            'storage_options' => 'nullable|string|max:2000',
            'ram_options' => 'nullable|string|max:2000',
            'price' => 'required|numeric|min:0',
            'original_price' => 'nullable|numeric|min:0',
            'stock' => 'required|integer|min:0',
            'sku' => 'nullable|string|max:80',
            'warranty' => 'nullable|string|max:80',
            'galleryKeep' => 'nullable|string',
            'imageFiles' => 'nullable|array|max:12',
            'imageFiles.*' => 'file|max:10240|mimes:jpg,jpeg,png,gif,webp,avif,bmp',
            'image' => 'nullable|file|max:10240|mimes:jpg,jpeg,png,gif,webp,avif,bmp',
            'active' => 'nullable|boolean',
        ]);

        $kept = $request->has('galleryKeep')
            ? array_values(array_filter(array_map('trim', explode(',', (string) $request->input('galleryKeep')))))
            : product_gallery_urls($product->image_url, $product->gallery_urls);

        $newUrls = [];
        $bag = $request->files;
        $candidates = [];
        foreach (['imageFiles', 'images', 'image', 'imageFile'] as $key) {
            if (! $bag->has($key)) {
                continue;
            }
            $raw = $bag->get($key);
            if ($raw instanceof \Illuminate\Http\UploadedFile) {
                $candidates[] = $raw;
            } elseif (is_array($raw)) {
                foreach ($raw as $f) {
                    if ($f instanceof \Illuminate\Http\UploadedFile) {
                        $candidates[] = $f;
                    }
                }
            }
        }
        foreach (array_slice($candidates, 0, 12) as $file) {
            if (! $file->isValid()) {
                continue;
            }
            $stored = $file->store('products', 'public_uploads');
            if ($stored) {
                $newUrls[] = '/uploads/'.$stored;
            }
        }

        $gallery = [];
        foreach (array_merge($kept, $newUrls) as $url) {
            $url = trim((string) $url);
            if ($url === '' || in_array($url, $gallery, true)) {
                continue;
            }
            $gallery[] = $url;
        }
        if ($gallery === []) {
            $gallery = [default_product_image()];
        }

        $data['image_url'] = $gallery[0];
        $data['gallery_urls'] = implode(',', $gallery);
        $data['active'] = $request->boolean('active');
        unset($data['image'], $data['imageFiles'], $data['galleryKeep']);
        $category = Category::find($data['category_id']);
        $variants = ProductVariantOptions::normalizePosted(
            $category,
            $data['option_groups'] ?? null,
            $data['color_options'] ?? null,
            $data['storage_options'] ?? null,
            $data['ram_options'] ?? null,
        );
        $data['option_groups'] = $variants['option_groups'];
        $data['color_options'] = $variants['color_options'];
        $data['storage_options'] = $variants['storage_options'];
        $data['ram_options'] = $variants['ram_options'];
        $product->update($data);

        return redirect('/admin/products')->with('message', 'Product updated');
    }

    public function deleteProduct(int $id)
    {
        Product::where('id', $id)->delete();

        return back()->with('message', 'Product deleted');
    }

    public function categories(Request $request)
    {
        $q = Category::query();
        if ($request->filled('q')) {
            $term = '%'.$request->q.'%';
            $q->where(function ($w) use ($term) {
                $w->where('name', 'like', $term)
                    ->orWhere('slug', 'like', $term)
                    ->orWhere('icon', 'like', $term);
            });
        }
        match ($request->get('sort', 'sort')) {
            'name_asc' => $q->orderBy('name'),
            'name_desc' => $q->orderByDesc('name'),
            'newest' => $q->orderByDesc('id'),
            'oldest' => $q->orderBy('id'),
            default => $q->orderBy('sort_order')->orderBy('name'),
        };
        $categories = $q->get();

        return view('admin.categories', compact('categories'));
    }

    public function storeCategory(Request $request)
    {
        $this->normalizeDemoRequest($request);
        $data = $request->validate([
            'name' => 'required|string|max:80',
            'icon' => 'nullable|string|max:50',
        ]);
        Category::create([
            'name' => $data['name'],
            'slug' => Str::slug($data['name']),
            'icon' => $data['icon'] ?? 'tag',
            'active' => 1,
            'sort_order' => (Category::max('sort_order') ?? 0) + 1,
        ]);

        return back()->with('message', 'Category added');
    }

    public function updateCategory(Request $request, int $id)
    {
        $this->normalizeDemoRequest($request);
        $cat = Category::findOrFail($id);
        $data = $request->validate([
            'name' => 'required|string|max:80',
            'icon' => 'nullable|string|max:50',
            'active' => 'nullable|boolean',
        ]);
        $cat->update([
            'name' => $data['name'],
            'slug' => Str::slug($data['name']),
            'icon' => $data['icon'] ?? $cat->icon,
            'active' => $request->boolean('active'),
        ]);

        return back()->with('message', 'Category updated');
    }

    public function deleteCategory(int $id)
    {
        Category::where('id', $id)->delete();

        return back()->with('message', 'Category deleted');
    }

    public function brands(Request $request)
    {
        $q = Brand::query();
        if ($request->filled('q')) {
            $term = '%'.$request->q.'%';
            $q->where(function ($w) use ($term) {
                $w->where('name', 'like', $term)
                    ->orWhere('slug', 'like', $term);
            });
        }
        match ($request->get('sort', 'name_asc')) {
            'name_desc' => $q->orderByDesc('name'),
            'newest' => $q->orderByDesc('id'),
            'oldest' => $q->orderBy('id'),
            default => $q->orderBy('name'),
        };
        $brands = $q->get();

        return view('admin.brands', compact('brands'));
    }

    public function storeBrand(Request $request)
    {
        $this->normalizeDemoRequest($request);
        $data = $request->validate([
            'name' => 'required|string|max:80',
            'logo' => 'nullable|image|max:4096',
            'logo_url' => 'nullable|string|max:500',
        ]);
        $logo = $data['logo_url'] ?? null;
        if ($request->hasFile('logo')) {
            $logo = '/uploads/'.$request->file('logo')->store('logo', 'public_uploads');
        }
        Brand::create([
            'name' => $data['name'],
            'slug' => Str::slug($data['name']),
            'logo_url' => $logo,
            'active' => 1,
        ]);

        return back()->with('message', 'Brand added');
    }

    public function updateBrand(Request $request, int $id)
    {
        $this->normalizeDemoRequest($request);
        $brand = Brand::findOrFail($id);
        $data = $request->validate([
            'name' => 'required|string|max:80',
            'logo' => 'nullable|image|max:4096',
            'logo_url' => 'nullable|string|max:500',
            'active' => 'nullable|boolean',
        ]);
        $logo = $data['logo_url'] ?? $brand->logo_url;
        if ($request->hasFile('logo')) {
            $logo = '/uploads/'.$request->file('logo')->store('logo', 'public_uploads');
        }
        $brand->update([
            'name' => $data['name'],
            'slug' => Str::slug($data['name']),
            'logo_url' => $logo,
            'active' => $request->boolean('active'),
        ]);

        return back()->with('message', 'Brand updated');
    }

    public function deleteBrand(int $id)
    {
        Brand::where('id', $id)->delete();

        return back()->with('message', 'Brand deleted');
    }

    public function orders()
    {
        $this->orders->autoReleaseEligibleOrders();
        $orders = Order::with(['user', 'seller', 'items', 'commission'])
            ->orderByDesc('placed_at')
            ->orderByDesc('id')
            ->get();

        // One checkout (same customer + same placed_at) can create one order per seller.
        $checkouts = $orders
            ->groupBy(fn (Order $o) => $o->user_id.'|'.(optional($o->placed_at)->getTimestamp() ?? 0))
            ->map(function ($group, $key) {
                [$userId, $ts] = array_map('intval', explode('|', $key, 2));
                $first = $group->first();
                $statuses = $group->pluck('status')->unique()->values();

                return [
                    'user_id' => $userId,
                    'placed_at_ts' => $ts,
                    'customer' => $first->recipient_name ?: ($first->user->full_name ?? '—'),
                    'email' => $first->user->email ?? '',
                    'payment' => $first->payment_method,
                    'placed_at' => $first->placed_at,
                    'seller_count' => $group->count(),
                    'item_count' => $group->sum(fn ($o) => $o->items->sum('quantity')),
                    'total' => $group->sum('total'),
                    'statuses' => $statuses,
                    'status_label' => $statuses->count() === 1
                        ? str_replace('_', ' ', $statuses->first())
                        : $statuses->count().' statuses',
                ];
            })
            ->values();

        return view('admin.orders', compact('checkouts'));
    }

    public function orderCheckout(int $userId, int $placedAt)
    {
        $this->orders->autoReleaseEligibleOrders();
        $orders = Order::with(['user', 'seller', 'items', 'commission'])
            ->where('user_id', $userId)
            ->where('placed_at', date('Y-m-d H:i:s', $placedAt))
            ->orderBy('id')
            ->get();

        if ($orders->isEmpty()) {
            abort(404);
        }

        $first = $orders->first();
        $checkout = [
            'user_id' => $userId,
            'placed_at_ts' => $placedAt,
            'customer' => $first->recipient_name ?: ($first->user->full_name ?? '—'),
            'email' => $first->user->email ?? '',
            'phone' => $first->recipient_phone ?: ($first->user->phone ?? ''),
            'address' => $first->shipping_address,
            'payment' => $first->payment_method,
            'delivery' => $first->delivery_type,
            'placed_at' => $first->placed_at,
            'total' => $orders->sum('total'),
            'seller_count' => $orders->count(),
        ];

        return view('admin.order-checkout', compact('checkout', 'orders'));
    }

    public function confirmHold(int $id)
    {
        try {
            $this->orders->adminConfirmHold($id);

            return back()->with('message', 'Payment confirmed — funds held. Seller can confirm stock.');
        } catch (\Throwable $e) {
            return back()->with('error', $e->getMessage());
        }
    }

    public function confirmDelivered(int $id)
    {
        try {
            $this->orders->adminConfirmDelivered($id);

            return back()->with('message', 'Delivery confirmed. Buyer protection started.');
        } catch (\Throwable $e) {
            return back()->with('error', $e->getMessage());
        }
    }

    public function releasePayout(int $id)
    {
        try {
            $this->orders->forceReleasePayout($id);

            return back()->with('message', '3% commission deducted — seller paid.');
        } catch (\Throwable $e) {
            return back()->with('error', $e->getMessage());
        }
    }

    public function refundOrder(int $id)
    {
        try {
            $this->orders->refundOrder($id);

            return back()->with('message', 'Order refunded — held funds returned.');
        } catch (\Throwable $e) {
            return back()->with('error', $e->getMessage());
        }
    }

    public function commissions()
    {
        $commissions = Commission::with(['order', 'seller'])->orderByDesc('created_at')->get();
        $totalPlatform = $commissions->where('payout_status', 'RELEASED')->sum('commission_amount');
        $feesInEscrow = $commissions->whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->sum('commission_amount');
        $escrowSales = $commissions->whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->sum('sale_amount');
        $heldCount = $commissions->whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->count();

        return view('admin.commissions', compact(
            'commissions', 'totalPlatform', 'feesInEscrow', 'escrowSales', 'heldCount'
        ));
    }

    public function countries(Request $request)
    {
        $sort = $request->get('sort', 'money_desc');
        $countries = $this->countries->countriesList($sort);

        return view('admin.countries', compact('countries', 'sort'));
    }

    public function countryDetail(Request $request, string $name)
    {
        try {
            $data = $this->countries->countryProfile(
                $name,
                $request->get('sellerSort', 'revenue_desc'),
                $request->get('buyerSort', 'spent_desc')
            );

            return view('admin.country-detail', $data);
        } catch (\Throwable $e) {
            return redirect('/admin/countries')->with('error', $e->getMessage());
        }
    }

    public function reviews()
    {
        $reviews = Review::with(['product', 'user'])->orderByDesc('created_at')->get();

        return view('admin.reviews', compact('reviews'));
    }

    public function hideReview(int $id)
    {
        Review::where('id', $id)->update(['hidden' => 1]);

        return back()->with('message', 'Review hidden');
    }

    public function settings()
    {
        return view('admin.settings');
    }
}
