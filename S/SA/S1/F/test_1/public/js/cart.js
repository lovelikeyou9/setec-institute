document.addEventListener('DOMContentLoaded', () => {
    loadCart();
    updateCartCount();
});

async function loadCart() {
    const res = await fetch('/api/cart');
    const data = await res.json();
    displayCart(data.cart || []);
}

function displayCart(items) {
    const container = document.getElementById('cartContent');
    if (!container) return;
    
    if(items.length === 0) {
        container.innerHTML = '<div style="text-align:center; padding:50px;"><h2>Cart is Empty</h2><a href="/products" class="btn btn-primary">Go Shop</a></div>';
        return;
    }

    const total = items.reduce((sum, item) => sum + (item.price * item.quantity), 0) * 1.08 + 9.99;
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

    const buttons = isLoggedIn 
        ? `<button class="btn btn-primary w-100" onclick="location.href='/checkout'">Proceed to Checkout</button>`
        : `<a href="/login" class="btn btn-primary w-100 mb-2">Login to Checkout</a>
           <button class="btn btn-outline w-100" onclick="location.href='/checkout'">Guest Checkout</button>`;

    container.innerHTML = `
        <div class="cart-container">
            <div class="cart-items">
                ${items.map(item => `
                    <div class="cart-item" style="border-bottom:1px solid #eee; padding:15px 0; display:flex; justify-content:space-between; align-items:center;">
                        <img src="${item.image}" style="width:80px; height:80px; object-fit:cover; border-radius:8px;">
                        <div style="flex:1; margin-left:15px;">
                            <h4>${item.name}</h4>
                            <p>$${item.price}</p>
                        </div>
                        <div style="display:flex; align-items:center; gap:10px;">
                            <input type="number" value="${item.quantity}" style="width:50px; padding:5px;" onchange="updateQty('${item.productId}', this.value)">
                            <button onclick="removeItem('${item.productId}')" style="color:red; border:none; background:none; cursor:pointer;"><i class="fas fa-trash"></i></button>
                        </div>
                    </div>
                `).join('')}
            </div>
            <div class="cart-summary">
                <h3>Total: $${total.toFixed(2)}</h3>
                <br>
                ${buttons}
            </div>
        </div>
    `;
}

async function updateQty(id, qty) {
    await fetch('/api/cart/update', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ productId: id, quantity: qty })
    });
    loadCart();
}

async function removeItem(id) {
    await fetch(`/api/cart/remove/${id}`, { method: 'DELETE' });
    loadCart();
    updateCartCount();
}

async function updateCartCount() {
    const res = await fetch('/api/cart');
    const data = await res.json();
    const el = document.getElementById('cartCount');
    if(el) el.innerText = data.totalItems || 0;
}
