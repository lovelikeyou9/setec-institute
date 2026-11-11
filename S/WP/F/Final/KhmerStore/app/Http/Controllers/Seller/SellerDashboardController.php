<?php

namespace App\Http\Controllers\Seller;

use App\Http\Controllers\Controller;
use App\Models\Brand;
use App\Models\Category;
use App\Models\Commission;
use App\Models\Order;
use App\Models\Product;
use App\Models\Review;
use App\Models\Seller;
use App\Services\OrderService;
use App\Services\ProductVariantOptions;
use Illuminate\Http\Request;
use Illuminate\Http\UploadedFile;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Str;

class SellerDashboardController extends Controller
{
    public function __construct(private OrderService $orders) {}

    /** Map demo camelCase / alias fields onto Laravel snake_case inputs. */
    private function normalizeDemoRequest(Request $request): void
    {
        // Alias uploaded files FIRST. Request::exists()/has()/all() call allFiles()
        // and cache convertedFiles — any set() after that leaves aliases invisible.
        foreach (['logoFile' => 'logo', 'imageFile' => 'image', 'bannerFile' => 'banner', 'avatarFile' => 'avatar', 'coverFile' => 'cover'] as $from => $to) {
            if ($request->files->has($from) && ! $request->files->has($to)) {
                $request->files->set($to, $request->files->get($from));
            }
        }

        $aliases = [
            'fullName' => 'full_name',
            'logoUrl' => 'logo_url',
            'bannerUrl' => 'banner_url',
            'coverUrl' => 'cover_url',
            'avatarUrl' => 'avatar_url',
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
        if ($request->has('active')) {
            $raw = $request->input('active');
            $request->merge([
                'active' => filter_var($raw, FILTER_VALIDATE_BOOLEAN)
                    || $raw === 'on'
                    || $raw === '1'
                    || $raw === 1,
            ]);
        }
    }

    /**
     * @param  mixed  $raw
     * @param  list<UploadedFile>  $files
     */
    private function pushUploadedFiles(mixed $raw, array &$files): void
    {
        if ($raw instanceof UploadedFile) {
            $files[] = $raw;

            return;
        }
        if (! is_array($raw)) {
            return;
        }
        foreach ($raw as $f) {
            $this->pushUploadedFiles($f, $files);
        }
    }

    /** @return list<UploadedFile> */
    private function collectUploadedImages(Request $request): array
    {
        $files = [];

        foreach (['imageFiles', 'images', 'image', 'imageFile'] as $key) {
            $this->pushUploadedFiles($request->file($key), $files);
        }

        // Fallback if field name differs slightly (imageFiles[] / ImageFiles)
        if ($files === []) {
            foreach ($request->allFiles() as $key => $raw) {
                if (preg_match('/^image/i', (string) $key)) {
                    $this->pushUploadedFiles($raw, $files);
                }
            }
        }

        $valid = [];
        foreach ($files as $f) {
            if ($f->isValid()) {
                $valid[] = $f;
            }
        }

        return array_values($valid);
    }

    /** @return list<string> human-readable PHP upload errors */
    private function phpUploadErrors(): array
    {
        $msgs = [];
        $map = [
            UPLOAD_ERR_INI_SIZE => 'file exceeds server upload_max_filesize',
            UPLOAD_ERR_FORM_SIZE => 'file exceeds form MAX_FILE_SIZE',
            UPLOAD_ERR_PARTIAL => 'file was only partially uploaded',
            UPLOAD_ERR_NO_TMP_DIR => 'missing temporary folder on server',
            UPLOAD_ERR_CANT_WRITE => 'failed to write file to disk',
            UPLOAD_ERR_EXTENSION => 'a PHP extension blocked the upload',
        ];
        $walk = function ($node, $name = 'image') use (&$walk, &$msgs, $map) {
            if (! is_array($node)) {
                return;
            }
            if (isset($node['error']) && is_array($node['error'])) {
                foreach ($node['error'] as $i => $err) {
                    $walk([
                        'error' => $err,
                        'name' => is_array($node['name'] ?? null) ? ($node['name'][$i] ?? $name) : $name,
                    ], $name);
                }

                return;
            }
            $err = $node['error'] ?? UPLOAD_ERR_NO_FILE;
            if ($err === UPLOAD_ERR_OK || $err === UPLOAD_ERR_NO_FILE) {
                return;
            }
            $label = $node['name'] ?? $name;
            $msgs[] = $label.': '.($map[$err] ?? ('upload error #'.$err));
        };
        foreach ($_FILES as $key => $node) {
            if (preg_match('/^image/i', (string) $key)) {
                $walk($node, (string) $key);
            }
        }

        return $msgs;
    }

    /**
     * Store uploaded images under public/uploads/products.
     *
     * @param  list<UploadedFile>  $files
     * @return list<string> public paths like /uploads/products/...
     */
    private function storeUploadedImages(array $files, int $max = 12): array
    {
        $urls = [];
        foreach (array_slice($files, 0, $max) as $file) {
            $ext = strtolower($file->getClientOriginalExtension() ?: $file->extension() ?: 'jpg');
            if (! in_array($ext, ['jpg', 'jpeg', 'png', 'gif', 'webp', 'avif', 'bmp'], true)) {
                $ext = 'jpg';
            }
            $stored = $file->storeAs(
                'products',
                Str::random(40).'.'.$ext,
                'public_uploads'
            );
            if ($stored) {
                $urls[] = '/uploads/'.$stored;
            }
        }

        return $urls;
    }

    /** @return list<string> */
    private function parseGalleryKeep(?string $csv): array
    {
        if ($csv === null || trim($csv) === '') {
            return [];
        }
        $out = [];
        foreach (explode(',', $csv) as $part) {
            $part = trim($part);
            if ($part === '' || in_array($part, $out, true)) {
                continue;
            }
            // Only keep local upload paths (or the default placeholder)
            if (str_starts_with($part, '/uploads/') || str_starts_with($part, 'uploads/')) {
                $out[] = str_starts_with($part, '/') ? $part : '/'.$part;
            }
        }

        return $out;
    }

    public function callAction($method, $parameters)
    {
        try {
            return $this->{$method}(...array_values($parameters));
        } catch (\Illuminate\Validation\ValidationException $e) {
            throw $e;
        } catch (\RuntimeException $e) {
            $seller = Auth::user()?->seller;
            // Suspended sellers go to account (not become-seller), with a clear message.
            if ($seller && $seller->status === 'SUSPENDED') {
                return redirect('/account')->with(
                    'error',
                    'Your seller account has been suspended by an administrator.'
                );
            }

            return redirect('/become-seller')->with('error', $e->getMessage());
        }
    }

    private function requireSeller(): Seller
    {
        $seller = Auth::user()->seller;
        if (! $seller || $seller->status !== 'APPROVED') {
            throw new \RuntimeException('Seller account required. Your store may be pending or banned.');
        }

        return $seller;
    }

    public function dashboard()
    {
        $seller = $this->requireSeller();
        $orders = Order::where('seller_id', $seller->id)->get();
        $products = Product::where('seller_id', $seller->id)->get();
        $commissions = Commission::where('seller_id', $seller->id)->get();

        $stats = [
            'totalOrders' => $orders->count(),
            'pendingOrders' => $orders->whereIn('status', ['FUNDS_HELD', 'SELLER_PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY'])->count(),
            'totalProducts' => $products->count(),
            'activeProducts' => $products->where('active', true)->count(),
            'totalEarnings' => $commissions->where('payout_status', 'RELEASED')->sum('seller_earnings'),
            'heldEarnings' => $commissions->whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->sum('seller_earnings'),
            'revenue' => $orders->whereNotIn('status', ['CANCELLED', 'REFUNDED'])->sum('subtotal'),
            // What MMT/admin keeps from this store (3% of sale)
            'feesPaid' => $commissions->where('payout_status', 'RELEASED')->sum('commission_amount'),
            'feesPending' => $commissions->whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->sum('commission_amount'),
        ];
        $stats['feesTotal'] = $stats['feesPaid'] + $stats['feesPending'];
        $recentOrders = Order::with('user', 'items')
            ->where('seller_id', $seller->id)->orderByDesc('placed_at')->limit(8)->get();

        return view('seller.dashboard', compact('seller', 'stats', 'recentOrders'));
    }

    public function products(Request $request)
    {
        $seller = $this->requireSeller();
        $q = Product::with(['brand', 'category'])->where('seller_id', $seller->id);
        if ($request->filled('q')) {
            $q->where('name', 'like', '%'.$request->q.'%');
        }
        if ($request->filled('category')) {
            $q->where('category_id', $request->category);
        }
        if ($request->filled('brand')) {
            $q->where('brand_id', $request->brand);
        }
        match ($request->get('sort', 'newest')) {
            'price_asc' => $q->orderBy('price'),
            'price_desc' => $q->orderByDesc('price'),
            'name' => $q->orderBy('name'),
            default => $q->orderByDesc('created_at'),
        };
        $products = $q->paginate(20)->withQueryString();
        $categories = Category::where('active', 1)->orderBy('sort_order')->get();
        $brands = Brand::where('active', 1)->orderBy('name')->get();

        return view('seller.products', compact('seller', 'products', 'categories', 'brands'));
    }

    public function createProduct()
    {
        $seller = $this->requireSeller();
        $categories = Category::where('active', 1)->orderBy('sort_order')->get();
        $brands = Brand::where('active', 1)->orderBy('name')->get();

        return view('seller.product-form', compact('seller', 'categories', 'brands'));
    }

    public function storeProduct(Request $request)
    {
        $seller = $this->requireSeller();
        $this->normalizeDemoRequest($request);

        try {
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
                // Keep image rules loose — strict mimes often rejects valid webp/avif uploads.
                'imageFiles' => 'nullable',
                'imageFiles.*' => 'nullable|file|max:10240',
                'image' => 'nullable|file|max:10240',
            ], [
                'category_id.required' => 'Please select a category.',
                'brand_id.required' => 'Please select a brand.',
            ]);

            $phpErrors = $this->phpUploadErrors();
            if ($phpErrors !== []) {
                return redirect('/seller-dashboard/products')
                    ->withInput()
                    ->withErrors(['imageFiles' => implode(' ', $phpErrors)])
                    ->with('open_modal', 'modalAddProduct');
            }

            $picked = $this->collectUploadedImages($request);
            $uploaded = $this->storeUploadedImages($picked);
            if ($picked !== [] && $uploaded === []) {
                return redirect('/seller-dashboard/products')
                    ->withInput()
                    ->withErrors(['imageFiles' => 'Could not save the uploaded image(s). Check that public/uploads is writable.'])
                    ->with('open_modal', 'modalAddProduct');
            }
            if ($uploaded === []) {
                $uploaded = [default_product_image()];
            }
            $imageUrl = $uploaded[0];
            $galleryCsv = implode(',', $uploaded);

            $original = $data['original_price'] ?? $data['price'];
            $discount = 0;
            if ($original > $data['price']) {
                $discount = (int) round(($original - $data['price']) * 100 / $original);
            }

            $category = Category::find($data['category_id']);
            $variants = ProductVariantOptions::normalizePosted(
                $category,
                $data['option_groups'] ?? null,
                $data['color_options'] ?? null,
                $data['storage_options'] ?? null,
                $data['ram_options'] ?? null,
            );

            Product::create([
                'name' => $data['name'],
                'slug' => Str::slug($data['name']).'-'.(time() % 100000),
                'description' => $data['description'],
                'specifications' => $data['specifications'] ?? null,
                'option_groups' => $variants['option_groups'],
                'color_options' => $variants['color_options'],
                'storage_options' => $variants['storage_options'],
                'ram_options' => $variants['ram_options'],
                'category_id' => $data['category_id'],
                'brand_id' => $data['brand_id'],
                'seller_id' => $seller->id,
                'price' => $data['price'],
                'original_price' => $original,
                'discount_percent' => $discount,
                'stock' => $data['stock'],
                'sku' => $data['sku'] ?? null,
                'warranty' => $data['warranty'] ?? null,
                'condition_type' => 'NEW',
                'image_url' => $imageUrl,
                'gallery_urls' => $galleryCsv,
                'active' => 1,
                'featured' => 0,
                'flash_deal' => 0,
                'rating' => 0,
                'review_count' => 0,
                'sold_count' => 0,
                'created_at' => now(),
                'updated_at' => now(),
            ]);

            $seller->total_products = ($seller->total_products ?? 0) + 1;
            $seller->save();

            return redirect('/seller-dashboard/products')->with('message', 'Product added successfully');
        } catch (\Illuminate\Validation\ValidationException $e) {
            return redirect('/seller-dashboard/products')
                ->withErrors($e->errors())
                ->withInput()
                ->with('open_modal', 'modalAddProduct');
        } catch (\Throwable $e) {
            report($e);

            return redirect('/seller-dashboard/products')
                ->withInput()
                ->with('error', 'Could not add product: '.$e->getMessage())
                ->with('open_modal', 'modalAddProduct');
        }
    }

    public function editProduct(int $id)
    {
        $seller = $this->requireSeller();
        $product = Product::where('seller_id', $seller->id)->findOrFail($id);
        $categories = Category::where('active', 1)->orderBy('sort_order')->get();
        $brands = Brand::where('active', 1)->orderBy('name')->get();

        return view('seller.product-form', compact('seller', 'product', 'categories', 'brands'));
    }

    public function updateProduct(Request $request, int $id)
    {
        $seller = $this->requireSeller();
        $this->normalizeDemoRequest($request);
        $product = Product::where('seller_id', $seller->id)->findOrFail($id);

        try {
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
                'imageFiles' => 'nullable',
                'imageFiles.*' => 'nullable|file|max:10240',
                'image' => 'nullable|file|max:10240',
                'active' => 'nullable|boolean',
            ]);

            $phpErrors = $this->phpUploadErrors();
            if ($phpErrors !== []) {
                return redirect('/seller-dashboard/products')
                    ->withInput()
                    ->withErrors(['imageFiles' => implode(' ', $phpErrors)])
                    ->with('open_modal', 'modalEditProduct');
            }

            $kept = $request->has('galleryKeep')
                ? $this->parseGalleryKeep($request->input('galleryKeep'))
                : $product->galleryList();
            $picked = $this->collectUploadedImages($request);
            $newUrls = $this->storeUploadedImages($picked);
            if ($picked !== [] && $newUrls === []) {
                return redirect('/seller-dashboard/products')
                    ->withInput()
                    ->withErrors(['imageFiles' => 'Could not save the uploaded image(s). Check that public/uploads is writable.'])
                    ->with('open_modal', 'modalEditProduct');
            }
            $gallery = [];
            foreach (array_merge($kept, $newUrls) as $url) {
                if ($url !== '' && ! in_array($url, $gallery, true)) {
                    $gallery[] = $url;
                }
            }
            if ($gallery === []) {
                $gallery = [default_product_image()];
            }
            $imageUrl = $gallery[0];
            $galleryCsv = implode(',', $gallery);

            $original = $data['original_price'] ?? $data['price'];
            $discount = 0;
            if ($original > $data['price']) {
                $discount = (int) round(($original - $data['price']) * 100 / $original);
            }

            $category = Category::find($data['category_id']);
            $variants = ProductVariantOptions::normalizePosted(
                $category,
                $data['option_groups'] ?? null,
                $data['color_options'] ?? null,
                $data['storage_options'] ?? null,
                $data['ram_options'] ?? null,
            );

            $product->update([
                'name' => $data['name'],
                'description' => $data['description'],
                'specifications' => $data['specifications'] ?? null,
                'option_groups' => $variants['option_groups'],
                'color_options' => $variants['color_options'],
                'storage_options' => $variants['storage_options'],
                'ram_options' => $variants['ram_options'],
                'category_id' => $data['category_id'],
                'brand_id' => $data['brand_id'],
                'price' => $data['price'],
                'original_price' => $original,
                'discount_percent' => $discount,
                'stock' => $data['stock'],
                'sku' => $data['sku'] ?? null,
                'warranty' => $data['warranty'] ?? null,
                'image_url' => $imageUrl,
                'gallery_urls' => $galleryCsv,
                'active' => $request->boolean('active'),
                'updated_at' => now(),
            ]);

            return redirect('/seller-dashboard/products')->with('message', 'Product updated successfully');
        } catch (\Illuminate\Validation\ValidationException $e) {
            return redirect('/seller-dashboard/products')
                ->withErrors($e->errors())
                ->withInput()
                ->with('open_modal', 'modalEditProduct');
        } catch (\Throwable $e) {
            report($e);

            return redirect('/seller-dashboard/products')
                ->withInput()
                ->with('error', 'Could not update product: '.$e->getMessage())
                ->with('open_modal', 'modalEditProduct');
        }
    }

    public function deleteProduct(int $id)
    {
        $seller = $this->requireSeller();
        $product = Product::where('seller_id', $seller->id)->findOrFail($id);
        $product->delete();

        return back()->with('message', 'Product deleted successfully');
    }

    public function orders(Request $request)
    {
        $seller = $this->requireSeller();
        $q = Order::with(['user', 'items', 'commission'])->where('seller_id', $seller->id);
        match ($request->get('sort', 'newest')) {
            'oldest' => $q->orderBy('placed_at'),
            'total_desc' => $q->orderByDesc('total'),
            default => $q->orderByDesc('placed_at'),
        };
        $orders = $q->get();

        return view('seller.orders', compact('seller', 'orders'));
    }

    public function acceptOrder(int $id)
    {
        try {
            $seller = $this->requireSeller();
            $this->orders->sellerAcceptOrder($id, $seller->id);

            return back()->with('message', 'Stock confirmed — order is now Processing.');
        } catch (\Throwable $e) {
            return back()->with('error', $e->getMessage());
        }
    }

    public function declineOrder(int $id)
    {
        try {
            $seller = $this->requireSeller();
            $this->orders->sellerDeclineOutOfStock($id, $seller->id);

            return back()->with('message', 'Order declined (out of stock). Customer will be refunded.');
        } catch (\Throwable $e) {
            return back()->with('error', $e->getMessage());
        }
    }

    public function updateOrderStatus(Request $request, int $id)
    {
        try {
            $seller = $this->requireSeller();
            $this->orders->sellerUpdateFulfillment($id, $seller->id, $request->input('status'));

            return back()->with('message', 'Status updated.');
        } catch (\Throwable $e) {
            return back()->with('error', $e->getMessage());
        }
    }

    public function earnings()
    {
        $seller = $this->requireSeller();
        $commissions = Commission::with('order')->where('seller_id', $seller->id)
            ->orderByDesc('created_at')->get();
        $totalEarnings = $commissions->where('payout_status', 'RELEASED')->sum('seller_earnings');
        $heldEarnings = $commissions->whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->sum('seller_earnings');
        $feesPaid = $commissions->where('payout_status', 'RELEASED')->sum('commission_amount');
        $feesPending = $commissions->whereIn('payout_status', ['HELD', 'IN_PROTECTION'])->sum('commission_amount');

        return view('seller.earnings', compact(
            'seller', 'commissions', 'totalEarnings', 'heldEarnings', 'feesPaid', 'feesPending'
        ));
    }

    public function reviews()
    {
        $seller = $this->requireSeller();
        $reviews = Review::with(['product', 'user'])
            ->whereHas('product', fn ($q) => $q->where('seller_id', $seller->id))
            ->orderByDesc('created_at')->get();

        return view('seller.reviews', compact('seller', 'reviews'));
    }

    public function categories()
    {
        $seller = $this->requireSeller();
        $categories = Category::where('active', 1)->orderBy('sort_order')->get();
        $counts = [];
        foreach ($categories as $c) {
            $counts[$c->id] = Product::where('seller_id', $seller->id)->where('category_id', $c->id)->count();
        }

        return view('seller.categories', compact('seller', 'categories', 'counts'));
    }

    public function brands()
    {
        $seller = $this->requireSeller();
        $brands = Brand::where('active', 1)->orderBy('name')->get();
        $counts = [];
        foreach ($brands as $b) {
            $counts[$b->id] = Product::where('seller_id', $seller->id)->where('brand_id', $b->id)->count();
        }

        return view('seller.brands', compact('seller', 'brands', 'counts'));
    }

    public function settings()
    {
        $seller = $this->requireSeller();

        return view('seller.settings', compact('seller'));
    }

    public function updateSettings(Request $request)
    {
        $seller = $this->requireSeller();
        $this->normalizeDemoRequest($request);
        $data = $request->validate([
            'store_name' => 'required|string|max:150',
            'owner_name' => 'required|string|max:100',
            'phone' => 'nullable|string|max:30',
            'location' => 'nullable|string|max:100',
            'address' => 'nullable|string|max:200',
            'country' => 'nullable|string|max:80',
            'description' => 'nullable|string|max:1000',
            'logo' => 'nullable|image|max:4096',
            'banner' => 'nullable|image|max:6144',
            'logo_url' => 'nullable|string|max:500',
            'banner_url' => 'nullable|string|max:500',
        ]);

        if ($request->hasFile('logo')) {
            $data['logo_url'] = '/uploads/'.$request->file('logo')->store('logo', 'public_uploads');
        } elseif (! empty($data['logo_url'])) {
            // keep pasted URL
        } else {
            unset($data['logo_url']);
        }
        if ($request->hasFile('banner')) {
            $data['banner_url'] = '/uploads/'.$request->file('banner')->store('avatars/cover photos', 'public_uploads');
        } elseif (! empty($data['banner_url'])) {
            // keep pasted URL
        } else {
            unset($data['banner_url']);
        }
        unset($data['logo'], $data['banner']);
        $seller->update($data);

        return back()->with('message', 'Store profile updated successfully');
    }

    public function updatePersonal(Request $request)
    {
        $seller = $this->requireSeller();
        $this->normalizeDemoRequest($request);
        $user = Auth::user();
        $data = $request->validate([
            'full_name' => 'required|string|max:120',
            'phone' => 'nullable|string|max:30',
            'avatar' => 'nullable|image|max:4096',
            'cover' => 'nullable|image|max:6144',
            'avatar_url' => 'nullable|string|max:500',
            'cover_url' => 'nullable|string|max:500',
        ]);

        $user->full_name = $data['full_name'];
        $user->phone = $data['phone'] ?? null;
        if ($request->hasFile('avatar')) {
            $user->avatar_url = '/uploads/'.$request->file('avatar')->store('avatars', 'public_uploads');
        } elseif (! empty($data['avatar_url'])) {
            $user->avatar_url = $data['avatar_url'];
        }
        if ($request->hasFile('cover')) {
            $user->cover_url = '/uploads/'.$request->file('cover')->store('avatars/cover photos', 'public_uploads');
        } elseif (! empty($data['cover_url'])) {
            $user->cover_url = $data['cover_url'];
        }
        $user->updated_at = now();
        $user->save();

        if ($seller->owner_name !== $data['full_name']) {
            $seller->owner_name = $data['full_name'];
            $seller->save();
        }

        return back()->with('message', 'Personal profile updated successfully');
    }
}
