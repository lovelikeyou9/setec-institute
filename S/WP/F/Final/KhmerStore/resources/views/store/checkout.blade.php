@extends('layouts.store')
@section('title', 'Checkout — MMT')
@section('content')
@php $addr = $addresses->first(); @endphp
<div class="container">
  <nav class="breadcrumb">
    <a href="{{ url('/') }}">Home</a>
    <span class="sep">/</span>
    <a href="{{ url('/account/cart') }}">Cart</a>
    <span class="sep">/</span>
    <span>Checkout</span>
  </nav>

  <div class="checkout-steps">
    <div class="step done">1. Cart</div>
    <div class="step active">2. Shipping &amp; Payment</div>
    <div class="step">3. Confirmation</div>
  </div>

  <form action="{{ url('/checkout/place') }}" method="post">
    @csrf
    <div class="cart-layout">
      <div>
        <div class="form-card">
          <h3>Shipping Address</h3>
          @if($addresses->isNotEmpty())
            <div style="margin-bottom:1rem">
              <p style="font-size:.85rem;color:var(--text-muted);margin-bottom:.5rem">Saved addresses</p>
              @foreach($addresses as $i => $a)
                <label class="radio-card" data-full-name="{{ $a->full_name }}" data-phone="{{ $a->phone }}"
                       data-province="{{ $a->province }}" data-district="{{ $a->district }}"
                       data-commune="{{ $a->commune }}" data-street="{{ $a->street }}" data-country="{{ $a->country }}"
                       onclick="fillAddress(this)">
                  <input type="radio" name="savedAddress" @checked($i === 0)/>
                  <div>
                    <strong>{{ $a->full_name }}</strong> · <span>{{ $a->phone }}</span><br/>
                    <span style="font-size:.85rem;color:var(--text-secondary)">
                      {{ collect([$a->street, $a->commune, $a->district, $a->province, $a->country])->filter()->implode(', ') }}
                    </span>
                  </div>
                </label>
              @endforeach
            </div>
          @endif
          <div class="form-grid">
            <div class="form-group">
              <label for="fullName">Full Name *</label>
              <input type="text" id="fullName" name="fullName" required value="{{ old('fullName', $addr->full_name ?? Auth::user()->full_name) }}"/>
            </div>
            <div class="form-group">
              <label for="phone">Phone *</label>
              <input type="tel" id="phone" name="phone" required value="{{ old('phone', $addr->phone ?? Auth::user()->phone) }}"/>
            </div>
            <div class="form-group">
              <label for="country">Country *</label>
              <input type="text" id="country" name="country" required value="{{ old('country', $addr->country ?? 'Cambodia') }}"/>
            </div>
            <div class="form-group">
              <label for="province">Province / State *</label>
              <input type="text" id="province" name="province" required value="{{ old('province', $addr->province ?? '') }}" placeholder="e.g. Phnom Penh"/>
            </div>
            <div class="form-group">
              <label for="district">District / City *</label>
              <input type="text" id="district" name="district" required value="{{ old('district', $addr->district ?? '') }}"/>
            </div>
            <div class="form-group">
              <label for="commune">Commune / Sangkat</label>
              <input type="text" id="commune" name="commune" value="{{ old('commune', $addr->commune ?? '') }}"/>
            </div>
            <div class="form-group">
              <label for="street">Street Address *</label>
              <input type="text" id="street" name="street" required value="{{ old('street', $addr->street ?? '') }}" placeholder="House number, street name"/>
            </div>
          </div>
        </div>

        <div class="form-card">
          <h3>Delivery Option</h3>
          <label class="radio-card">
            <input type="radio" name="deliveryType" value="STANDARD" checked/>
            <div>
              <strong>Standard Delivery</strong>
              <div style="font-size:.85rem;color:var(--text-secondary)">2–5 business days · $2.00</div>
            </div>
          </label>
          <label class="radio-card">
            <input type="radio" name="deliveryType" value="EXPRESS"/>
            <div>
              <strong>Express Delivery</strong>
              <div style="font-size:.85rem;color:var(--text-secondary)">1–2 business days · $5.00</div>
            </div>
          </label>
        </div>

        <div class="form-card">
          <h3>Payment Method</h3>
          <label class="radio-card">
            <input type="radio" name="paymentMethod" value="CREDIT_CARD" checked/>
            <div><strong>Credit Card</strong></div>
          </label>
          <label class="radio-card">
            <input type="radio" name="paymentMethod" value="VISA"/>
            <div><strong>Visa</strong></div>
          </label>
          <label class="radio-card">
            <input type="radio" name="paymentMethod" value="QR_PAYMENT"/>
            <div><strong>QR Payment</strong></div>
          </label>
          <label class="radio-card">
            <input type="radio" name="paymentMethod" value="CASH_ON_DELIVERY"/>
            <div><strong>Cash on Delivery</strong></div>
          </label>
          <p style="font-size:.85rem;color:var(--text-muted);margin-top:.75rem">
            Online payments go to PAYMENT_CONFIRMED; COD starts as PENDING_PAYMENT. Admin then holds funds before seller confirms stock.
          </p>
        </div>
      </div>

      <aside class="cart-summary">
        <h3 style="margin-bottom:1rem">Order Summary</h3>
        @foreach($groupedCart as $sellerId => $items)
          <div style="margin-bottom:1rem">
            <strong style="font-size:.9rem">{{ $items->first()->product->seller->store_name ?? 'Seller' }}</strong>
            @foreach($items as $item)
              <div style="display:flex;gap:.75rem;margin-top:.5rem;align-items:center">
                <img src="{{ $item->product->image_url }}" alt="" style="width:48px;height:48px;object-fit:contain;border-radius:8px;background:#fff"/>
                <div style="flex:1;font-size:.85rem">
                  <div>{{ $item->product->name }}</div>
                  <div style="color:var(--text-muted)">×{{ $item->quantity }} · ${{ number_format($item->product->price, 2) }}</div>
                </div>
              </div>
            @endforeach
          </div>
        @endforeach
        <div class="summary-row">
          <span>Subtotal</span>
          <span>${{ number_format($subtotal, 2) }}</span>
        </div>
        <div class="summary-row">
          <span>Shipping</span>
          <span>Per seller at place</span>
        </div>
        <div class="summary-row total">
          <span>Total</span>
          <span>${{ number_format($subtotal, 2) }}+</span>
        </div>
        <button type="submit" class="btn btn-accent btn-block" style="width:100%;margin-top:1rem">Place Order</button>
        <a href="{{ url('/account/cart') }}" class="btn btn-outline btn-block" style="width:100%;margin-top:.5rem;text-align:center">Back to Cart</a>
      </aside>
    </div>
  </form>
</div>
@push('scripts')
<script>
function fillAddress(el) {
  const map = {fullName:'fullName', phone:'phone', province:'province', district:'district', commune:'commune', street:'street', country:'country'};
  Object.keys(map).forEach(function(k) {
    const input = document.getElementById(map[k]);
    if (input && el.dataset[k.replace(/[A-Z]/g, m => '-' + m.toLowerCase()).replace(/^-/, '')] !== undefined) {
      /* use dataset camelCase */
    }
    if (input) {
      const ds = el.dataset;
      if (k === 'fullName') input.value = ds.fullName || '';
      if (k === 'phone') input.value = ds.phone || '';
      if (k === 'province') input.value = ds.province || '';
      if (k === 'district') input.value = ds.district || '';
      if (k === 'commune') input.value = ds.commune || '';
      if (k === 'street') input.value = ds.street || '';
      if (k === 'country') input.value = ds.country || '';
    }
  });
}
</script>
@endpush
@endsection
