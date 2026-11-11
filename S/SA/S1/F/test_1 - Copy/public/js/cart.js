// cart.js - Cart page functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('Cart page loaded');
    loadCart();
    setupEventListeners();
});

let cartItems = [];

async function loadCart() {
    try {
        const response = await fetch('/api/cart');
        const data = await response.json();
        
        cartItems = data.cart || [];
        
        if (cartItems.length === 0) {
            showEmptyCart();
        } else {
            displayCart(cartItems);
        }
        
        updateCartCount();
        
    } catch (error) {
        console.error('Error loading cart:', error);
        showError('Failed to load cart. Please try again.');
    }
}

function displayCart(items) {
    const container = document.getElementById('cartContent');
    if (!container) return;
    
    // Calculate totals
    const subtotal = items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const shipping = subtotal > 100 ? 0 : 9.99;
    const tax = subtotal * 0.08; // 8% tax
    const total = subtotal + shipping + tax;
    
    container.innerHTML = `
        <div class="cart-container">
            <div class="cart-items">
                <h2>Cart Items (${items.length})</h2>
                ${items.map(item => `
                    <div class="cart-item" id="cart-item-${item.productId}">
                        <img src="${item.image || 'https://via.placeholder.com/300x200?text=Computer'}" 
                             alt="${item.name}" 
                             class="cart-item-image">
                        
                        <div class="cart-item-details">
                            <h4>${item.name}</h4>
                            <div class="cart-item-category">${item.category || 'Uncategorized'}</div>
                            <div class="cart-item-price">$${item.price.toFixed(2)}</div>
                        </div>
                        
                        <div class="quantity-controls">
                            <button class="quantity-btn" 
                                    onclick="updateQuantity('${item.productId}', ${item.quantity - 1})"
                                    ${item.quantity <= 1 ? 'disabled' : ''}>
                                <i class="fas fa-minus"></i>
                            </button>
                            
                            <input type="number" 
                                   value="${item.quantity}" 
                                   min="1" 
                                   max="99"
                                   class="quantity-input"
                                   onchange="updateQuantity('${item.productId}', this.value)"
                                   id="qty-${item.productId}">
                            
                            <button class="quantity-btn" 
                                    onclick="updateQuantity('${item.productId}', ${item.quantity + 1})">
                                <i class="fas fa-plus"></i>
                            </button>
                        </div>
                        
                        <div class="item-total">
                            $${(item.price * item.quantity).toFixed(2)}
                        </div>
                        
                        <button class="remove-item" 
                                onclick="removeFromCart('${item.productId}')"
                                title="Remove item">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                `).join('')}
                
                <div style="margin-top: 30px; text-align: right;">
                    <button class="btn btn-outline" onclick="clearCart()">
                        <i class="fas fa-trash-alt"></i> Clear Cart
                    </button>
                </div>
            </div>
            
            <div class="cart-summary">
                <h2>Order Summary</h2>
                
                <div class="summary-row">
                    <span>Subtotal</span>
                    <span>$${subtotal.toFixed(2)}</span>
                </div>
                
                <div class="summary-row">
                    <span>Shipping</span>
                    <span>${shipping === 0 ? 'FREE' : `$${shipping.toFixed(2)}`}</span>
                </div>
                
                <div class="summary-row">
                    <span>Tax (8%)</span>
                    <span>$${tax.toFixed(2)}</span>
                </div>
                
                <div class="summary-row total">
                    <span>Total</span>
                    <span>$${total.toFixed(2)}</span>
                </div>
                
                <button class="btn btn-primary checkout-btn" onclick="proceedToCheckout()">
                    <i class="fas fa-lock"></i> Proceed to Checkout
                </button>
                
                <a href="/products" class="continue-shopping">
                    <i class="fas fa-arrow-left"></i> Continue Shopping
                </a>
                
                <div class="secure-checkout">
                    <p><i class="fas fa-shield-alt"></i> Secure checkout. Your payment information is encrypted.</p>
                </div>
            </div>
        </div>
    `;
}

function showEmptyCart() {
    const container = document.getElementById('cartContent');
    if (!container) return;
    
    container.innerHTML = `
        <div class="cart-empty">
            <i class="fas fa-shopping-cart"></i>
            <h2>Your cart is empty</h2>
            <p>Looks like you haven't added any products to your cart yet.</p>
            <a href="/products" class="btn btn-primary" style="margin-top: 20px;">
                <i class="fas fa-shopping-bag"></i> Start Shopping
            </a>
        </div>
    `;
}

function showError(message) {
    const container = document.getElementById('cartContent');
    if (!container) return;
    
    container.innerHTML = `
        <div class="cart-empty">
            <i class="fas fa-exclamation-triangle" style="color: var(--danger);"></i>
            <h2>Oops! Something went wrong</h2>
            <p>${message}</p>
            <button onclick="loadCart()" class="btn btn-primary" style="margin-top: 20px;">
                <i class="fas fa-redo"></i> Try Again
            </button>
        </div>
    `;
}

function setupEventListeners() {
    // Update cart count in navbar
    updateCartCount();
}

async function updateQuantity(productId, newQuantity) {
    try {
        if (newQuantity < 1) {
            await removeFromCart(productId);
            return;
        }
        
        const response = await fetch('/api/cart/update', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                productId: productId,
                quantity: parseInt(newQuantity)
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            // Update the input field
            const input = document.getElementById(`qty-${productId}`);
            if (input) {
                input.value = newQuantity;
            }
            
            // Reload cart display
            await loadCart();
            showNotification('✓ Cart updated!', 'success');
        } else {
            showNotification(`✗ ${data.error || 'Failed to update quantity'}`, 'error');
        }
        
    } catch (error) {
        console.error('Error updating quantity:', error);
        showNotification('⚠ Network error. Please try again.', 'error');
    }
}

async function removeFromCart(productId) {
    if (!confirm('Are you sure you want to remove this item from your cart?')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/cart/remove/${productId}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (data.success) {
            await loadCart();
            showNotification('✓ Item removed from cart', 'success');
        } else {
            showNotification(`✗ ${data.error || 'Failed to remove item'}`, 'error');
        }
        
    } catch (error) {
        console.error('Error removing from cart:', error);
        showNotification('⚠ Network error. Please try again.', 'error');
    }
}

async function clearCart() {
    if (!confirm('Are you sure you want to clear your entire cart?')) {
        return;
    }
    
    try {
        const response = await fetch('/api/cart/clear', {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (data.success) {
            await loadCart();
            showNotification('✓ Cart cleared successfully', 'success');
        } else {
            showNotification(`✗ ${data.error || 'Failed to clear cart'}`, 'error');
        }
        
    } catch (error) {
        console.error('Error clearing cart:', error);
        showNotification('⚠ Network error. Please try again.', 'error');
    }
}

function proceedToCheckout() {
    // Check if cart is empty
    if (cartItems.length === 0) {
        showNotification('Your cart is empty!', 'error');
        return;
    }
    
    // Redirect to checkout page
    window.location.href = '/checkout';
}

// Show notification function
function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div>${message}</div>
    `;
    
    document.body.appendChild(notification);
    
    // Remove after 3 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Update cart count in navbar
async function updateCartCount() {
    try {
        const response = await fetch('/api/cart');
        const data = await response.json();
        
        const cartCount = document.getElementById('cartCount');
        if (cartCount) {
            const totalItems = data.totalItems || 0;
            cartCount.textContent = totalItems;
            cartCount.style.display = totalItems > 0 ? 'flex' : 'none';
        }
    } catch (error) {
        console.error('Error updating cart count:', error);
    }
}

// Make functions available globally
window.updateQuantity = updateQuantity;
window.removeFromCart = removeFromCart;
window.clearCart = clearCart;
window.proceedToCheckout = proceedToCheckout;
window.showNotification = showNotification;
