<?php

namespace App\Http\Controllers\Store;

use App\Http\Controllers\Controller;
use App\Models\Address;
use App\Models\WishlistItem;
use App\Services\CartService;
use App\Services\OrderService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class CartController extends Controller
{
    public function __construct(
        private CartService $cart,
        private OrderService $orders
    ) {}

    public function add(Request $request)
    {
        $productId = (int) ($request->input('product_id') ?: $request->input('productId'));
        $request->merge(['product_id' => $productId]);
        $request->validate(['product_id' => 'required|integer|min:1', 'quantity' => 'nullable|integer|min:1']);
        $this->cart->addToCart(Auth::id(), $productId, (int) ($request->quantity ?? 1));

        return redirect($request->get('redirect', '/account/cart'))
            ->with('message', 'Product added to cart');
    }

    public function update(Request $request)
    {
        $this->cart->updateQuantity((int) $request->itemId, (int) $request->quantity);

        return redirect($request->get('redirect') ?: '/account/cart');
    }

    public function toggle(Request $request)
    {
        $this->cart->toggleSelect((int) $request->itemId);

        return redirect($request->get('redirect') ?: '/account/cart');
    }

    public function selectAll(Request $request)
    {
        $this->cart->selectAll(Auth::id(), $request->boolean('selected'));

        return redirect($request->get('redirect') ?: '/account/cart');
    }

    public function remove(Request $request)
    {
        $this->cart->remove((int) $request->itemId);

        return redirect($request->get('redirect') ?: '/account/cart');
    }

    public function checkout()
    {
        $selected = $this->cart->getSelected(Auth::id());
        if ($selected->isEmpty()) {
            return redirect('/account/cart')->with('error', 'Select at least one product to checkout');
        }
        $groupedCart = $this->cart->getCartGroupedBySeller(Auth::id())
            ->map(fn ($items) => $items->filter(fn ($i) => $i->selected))
            ->filter(fn ($items) => $items->isNotEmpty());
        $subtotal = $this->cart->selectedSubtotal(Auth::id());
        $addresses = Address::where('user_id', Auth::id())->orderByDesc('default_address')->get();

        return view('store.checkout', compact('groupedCart', 'subtotal', 'addresses'));
    }

    public function placeOrder(Request $request)
    {
        $data = $request->validate([
            'fullName' => 'required|string|max:120',
            'phone' => 'required|string|max:30',
            'province' => 'required|string|max:80',
            'district' => 'required|string|max:80',
            'commune' => 'nullable|string|max:80',
            'street' => 'required|string|max:250',
            'country' => 'required|string|max:80',
            'deliveryType' => 'required|in:STANDARD,EXPRESS',
            'paymentMethod' => 'required|in:CREDIT_CARD,VISA,STRIPE,QR_PAYMENT,CASH_ON_DELIVERY',
        ]);

        try {
            $address = $data['street']
                .(! empty($data['commune']) ? ', '.$data['commune'] : '')
                .', '.$data['district'].', '.$data['province'].', '.trim($data['country']);

            $orders = $this->orders->placeOrders(
                Auth::user(),
                $data['fullName'],
                $data['phone'],
                $address,
                trim($data['country']),
                $data['deliveryType'],
                $data['paymentMethod']
            );

            return redirect('/orders/'.$orders->first()->order_number.'?placed=true')
                ->with('message', 'Order placed successfully!');
        } catch (\Throwable $e) {
            return redirect('/checkout')->with('error', $e->getMessage());
        }
    }

    public function toggleWishlist(Request $request)
    {
        $productId = (int) ($request->input('product_id') ?: $request->input('productId'));
        $existing = WishlistItem::where('user_id', Auth::id())->where('product_id', $productId)->first();
        if ($existing) {
            $existing->delete();
        } else {
            WishlistItem::create([
                'user_id' => Auth::id(),
                'product_id' => $productId,
                'created_at' => now(),
            ]);
        }

        return redirect($request->get('redirect') ?: '/account/wishlist');
    }
}
