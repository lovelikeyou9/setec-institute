<?php

namespace App\Http\Controllers\Account;

use App\Http\Controllers\Controller;
use App\Models\Address;
use App\Models\Notification;
use App\Models\Order;
use App\Models\WishlistItem;
use App\Services\CartService;
use App\Services\OrderService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class AccountController extends Controller
{
    public function __construct(
        private CartService $cart,
        private OrderService $orders
    ) {}

    public function dashboard()
    {
        $user = Auth::user();
        if ($user->isAdmin()) {
            return redirect('/admin');
        }
        // Only APPROVED sellers enter Seller Center (PENDING must stay on account / become-seller).
        if ($user->isSeller() && $user->seller && $user->seller->status === 'APPROVED') {
            return redirect('/seller-dashboard');
        }

        $orders = Order::where('user_id', $user->id)->orderByDesc('placed_at')->get();
        $pendingStatuses = ['PENDING_PAYMENT', 'PAYMENT_CONFIRMED', 'FUNDS_HELD', 'SELLER_PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY'];
        $totalOrders = $orders->count();
        $pendingOrders = $orders->whereIn('status', $pendingStatuses)->count();
        $completedOrders = $orders->whereIn('status', ['DELIVERED', 'COMPLETED'])->count();
        $wishlistItems = WishlistItem::where('user_id', $user->id)->count();

        return view('account.dashboard', compact(
            'orders', 'totalOrders', 'pendingOrders', 'completedOrders', 'wishlistItems'
        ));
    }

    public function orders(Request $request)
    {
        $q = Order::with('seller', 'items')->where('user_id', Auth::id());
        if ($request->filled('status')) {
            $q->where('status', $request->status);
        }
        $sort = $request->get('sort', 'newest');
        match ($sort) {
            'oldest' => $q->orderBy('placed_at'),
            'total_asc' => $q->orderBy('total'),
            'total_desc' => $q->orderByDesc('total'),
            default => $q->orderByDesc('placed_at'),
        };
        $orders = $q->get();
        $status = $request->status;

        return view('account.orders', compact('orders', 'status', 'sort'));
    }

    public function orderDetail(string $orderNumber)
    {
        $order = Order::with(['items.product', 'seller', 'commission', 'user'])
            ->where('order_number', $orderNumber)->firstOrFail();
        $user = Auth::user();
        if (! $user->isAdmin() && (int) $order->user_id !== (int) $user->id
            && ! ($user->seller && (int) $user->seller->id === (int) $order->seller_id)) {
            abort(403);
        }
        $canOpenDispute = $user && $this->orders->canOpenDispute($order)
            && (int) $order->user_id === (int) $user->id;
        $protectionDays = $this->orders->getBuyerProtectionDays();
        $disputeReasons = ['DAMAGED', 'INCORRECT', 'MISSING', 'OTHER'];

        return view('account.order-detail', compact('order', 'canOpenDispute', 'protectionDays', 'disputeReasons'));
    }

    public function openDispute(Request $request, string $orderNumber)
    {
        $order = Order::where('order_number', $orderNumber)->firstOrFail();
        try {
            $this->orders->openDispute(
                $order->id,
                Auth::user(),
                $request->input('reason'),
                $request->input('note')
            );

            return back()->with('message', 'Dispute submitted. Your payment remains held until resolved.');
        } catch (\Throwable $e) {
            return back()->with('error', $e->getMessage());
        }
    }

    public function cart()
    {
        $groupedCart = $this->cart->getCartGroupedBySeller(Auth::id());
        $subtotal = $this->cart->selectedSubtotal(Auth::id());

        return view('account.cart', compact('groupedCart', 'subtotal'));
    }

    public function wishlist()
    {
        $items = WishlistItem::with('product.brand', 'product.seller')
            ->where('user_id', Auth::id())->orderByDesc('id')->get();

        return view('account.wishlist', compact('items'));
    }

    public function addresses()
    {
        $addresses = Address::where('user_id', Auth::id())->orderByDesc('default_address')->get();

        return view('account.addresses', compact('addresses'));
    }

    public function saveAddress(Request $request)
    {
        $data = $request->validate([
            'full_name' => 'required|string|max:120',
            'phone' => 'required|string|max:30',
            'province' => 'required|string|max:80',
            'district' => 'required|string|max:80',
            'commune' => 'nullable|string|max:80',
            'street' => 'required|string|max:250',
            'country' => 'required|string|max:80',
        ]);

        Address::where('user_id', Auth::id())->update(['default_address' => 0]);
        Address::create([
            ...$data,
            'user_id' => Auth::id(),
            'default_address' => 1,
            'created_at' => now(),
        ]);

        return back()->with('message', 'Address added successfully');
    }

    public function updateAddress(Request $request, int $id)
    {
        $addr = Address::where('user_id', Auth::id())->findOrFail($id);
        $data = $request->validate([
            'full_name' => 'required|string|max:120',
            'phone' => 'required|string|max:30',
            'province' => 'required|string|max:80',
            'district' => 'required|string|max:80',
            'commune' => 'nullable|string|max:80',
            'street' => 'required|string|max:250',
            'country' => 'required|string|max:80',
        ]);
        $addr->update($data);

        return back()->with('message', 'Address updated successfully');
    }

    public function deleteAddress(int $id)
    {
        Address::where('user_id', Auth::id())->where('id', $id)->delete();

        return back()->with('message', 'Address deleted successfully');
    }

    public function notifications()
    {
        $notifications = Notification::where('user_id', Auth::id())
            ->orderByDesc('created_at')->limit(50)->get();

        return view('account.notifications', compact('notifications'));
    }

    public function markNotificationRead(int $id)
    {
        $n = Notification::where('user_id', Auth::id())->findOrFail($id);
        $n->read_flag = 1;
        $n->save();
        if ($n->link) {
            return redirect($n->link);
        }

        return back();
    }

    public function settings()
    {
        return view('account.settings');
    }

    public function updateSettings(Request $request)
    {
        $user = Auth::user();
        $data = $request->validate([
            'full_name' => 'required|string|max:120',
            'phone' => 'nullable|string|max:30',
            'avatar_url' => 'nullable|string|max:500',
            'cover_url' => 'nullable|string|max:500',
            'password' => 'nullable|string|min:6|confirmed',
            'avatar' => 'nullable|image|max:4096',
            'cover' => 'nullable|image|max:6144',
        ]);

        if ($request->hasFile('avatar')) {
            $path = $request->file('avatar')->store('avatars', 'public_uploads');
            $data['avatar_url'] = '/uploads/'.$path;
        }
        if ($request->hasFile('cover')) {
            $path = $request->file('cover')->store('avatars/cover photos', 'public_uploads');
            $data['cover_url'] = '/uploads/'.$path;
        }

        $user->full_name = $data['full_name'];
        $user->phone = $data['phone'] ?? $user->phone;
        if (! empty($data['avatar_url'])) {
            $user->avatar_url = $data['avatar_url'];
        }
        if (! empty($data['cover_url'])) {
            $user->cover_url = $data['cover_url'];
        }
        if (! empty($data['password'])) {
            $user->password = $data['password'];
        }
        $user->updated_at = now();
        $user->save();

        return back()->with('message', 'Profile updated successfully');
    }
}
