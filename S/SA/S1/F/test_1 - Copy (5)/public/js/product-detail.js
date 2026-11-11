// product-detail.js - Product detail page functionality
console.log('Product detail script loaded');

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded');
    updateCartCount();
    
    // Extract product ID from URL
    const path = window.location.pathname;
    const pathParts = path.split('/');
    const productId = pathParts[2];
    
    console.log('Product ID:', productId);
    
    if (productId && productId.length > 10) {
        loadProductDetails(productId);
    } else {
        showError('Invalid product URL. Please check the link.');
    }
});

async function loadProductDetails(productId) {
    console.log('Loading product:', productId);
    
    try {
        const response = await fetch(`/api/products/${productId}`);
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('Product not found');
            }
            throw new Error(`Server error: ${response.status}`);
        }
        
        const product = await response.json();
        console.log('Product loaded:', product.name);
        
        displayProductDetails(product);
        
    } catch (error) {
        console.error('Error:', error);
        showError(`Could not load product: ${error.message}`);
    }
}

function displayProductDetails(product) {
    const container = document.getElementById('productInfo');
    if (!container) {
        console.error('productInfo element not found!');
        return;
    }
    
    // Escape product ID for JavaScript
    const safeProductId = product._id.replace(/'/g, "\\'");
    
    container.innerHTML = `
        <div class="product-main">
            <div class="product-gallery">
                <img src="${product.image || 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=600&h=400&fit=crop'}" 
                     alt="${product.name}">
            </div>
            
            <div class="product-info">
                <h1>${product.name}</h1>
                <div class="product-category">
                    <i class="fas fa-tag"></i> ${product.category?.name || 'Uncategorized'}
                </div>
                
                <div class="product-price">$${parseFloat(product.price || 0).toFixed(2)}</div>
                
                <div class="product-stock">
                    ${product.quantity > 0 ? 
                        `<i class="fas fa-check-circle" style="color: var(--success);"></i>
                         <span>In Stock (${product.quantity} available)</span>` : 
                        `<i class="fas fa-times-circle" style="color: var(--danger);"></i>
                         <span>Out of Stock</span>`
                    }
                </div>
                
                <div class="product-description">
                    <h3>Description</h3>
                    <p>${product.description || 'High-quality computer product with excellent performance. Perfect for gaming, work, or creative tasks.'}</p>
                </div>
                
                <div class="product-features">
                    <h3>Key Features</h3>
                    <ul>
                        <li>High-performance components for optimal speed</li>
                        <li>Reliable and durable construction</li>
                        <li>Great value for money</li>
                        <li>Perfect for gaming, work, or study</li>
                        <li>Easy to upgrade and maintain</li>
                        <li>Energy efficient design</li>
                    </ul>
                </div>
                
                <div style="margin-top: 2rem; display: flex; gap: 1rem;">
                    <button onclick="addToCart('${safeProductId}')" 
                            class="btn btn-primary"
                            ${product.quantity <= 0 ? 'disabled' : ''}
                            style="padding: 1rem 2rem; font-size: 1.1rem;">
                        <i class="fas fa-cart-plus"></i> Add to Cart
                    </button>
                    
                    <a href="/products" class="btn btn-outline" style="padding: 1rem 2rem; font-size: 1.1rem;">
                        <i class="fas fa-arrow-left"></i> Back to Products
                    </a>
                </div>
            </div>
        </div>
        
        <div style="text-align: center; margin-top: 3rem;">
            <a href="/products" class="btn btn-primary">
                <i class="fas fa-shopping-bag"></i> Browse More Products
            </a>
        </div>
    `;
}

function showError(message) {
    const container = document.getElementById('productInfo');
    if (!container) return;
    
    container.innerHTML = `
        <div style="text-align: center; padding: 4rem; background: white; border-radius: 16px; box-shadow: var(--shadow);">
            <h2 style="color: var(--danger); margin-bottom: 1rem;">Oops!</h2>
            <p style="color: var(--gray); margin-bottom: 2rem;">${message}</p>
            <div>
                <a href="/products" class="btn btn-primary">
                    <i class="fas fa-laptop"></i> Browse Products
                </a>
                <a href="/" class="btn btn-outline" style="margin-left: 1rem;">
                    <i class="fas fa-home"></i> Go Home
                </a>
            </div>
        </div>
    `;
}

// Make addToCart available globally
window.addToCart = async function(productId) {
    console.log('Adding to cart:', productId);
    
    try {
        const response = await fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                productId: productId,
                quantity: 1
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('✓ Added to cart!', 'success');
            updateCartCount();
        } else {
            showNotification(`✗ Error: ${data.error}`, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('⚠ Network error. Please try again.', 'error');
    }
};

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
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Add CSS animations if not already present
if (!document.querySelector('#product-detail-styles')) {
    const style = document.createElement('style');
    style.id = 'product-detail-styles';
    style.textContent = `
        .animate-in {
            animation: fadeInUp 0.6s ease;
        }
        
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    `;
    document.head.appendChild(style);
}
