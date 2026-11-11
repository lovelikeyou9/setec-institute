// products.js - Products page functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('Products page loaded');
    loadCategories();
    setupFilterListeners();
    loadProducts();
    updateCartCount();
});

let currentProducts = [];

async function loadCategories() {
    try {
        const response = await fetch('/api/categories');
        const categories = await response.json();
        
        const categoryFilter = document.getElementById('categoryFilter');
        if (categoryFilter) {
            categoryFilter.innerHTML = `
                <option value="">All Categories</option>
                ${categories.map(cat => `<option value="${cat._id}">${cat.name}</option>`).join('')}
            `;
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

async function loadProducts() {
    try {
        const response = await fetch('/api/products');
        currentProducts = await response.json();
        
        displayProducts(currentProducts);
        updateProductCount(currentProducts.length);
    } catch (error) {
        console.error('Error loading products:', error);
        showError('Failed to load products. Please try again later.');
    }
}

function displayProducts(products) {
    const container = document.getElementById('productsGrid');
    if (!container) return;
    
    if (products.length === 0) {
        container.innerHTML = `
            <div class="no-products" style="grid-column: 1 / -1; text-align: center; padding: 4rem; background: white; border-radius: 16px; box-shadow: var(--shadow);">
                <i class="fas fa-search" style="font-size: 3rem; color: var(--gray-light); margin-bottom: 1rem;"></i>
                <h3 style="color: var(--gray); margin-bottom: 0.5rem;">No products found</h3>
                <p style="color: var(--gray);">Try adjusting your filters or search terms</p>
                <button class="btn btn-outline mt-2" onclick="clearFilters()">
                    <i class="fas fa-times"></i> Clear All Filters
                </button>
            </div>
        `;
        return;
    }
    
    container.innerHTML = products.map(product => `
        <div class="card">
            ${product.isFeatured ? '<span class="card-badge">Featured</span>' : ''}
            <img src="${product.image || 'https://via.placeholder.com/300x200?text=Computer'}" 
                 alt="${product.name}" 
                 class="card-image">
            <div class="card-content">
                <div style="color: var(--gray); font-size: 0.9rem; margin-bottom: 0.5rem;">
                    <i class="fas fa-tag"></i> ${product.category?.name || 'Uncategorized'}
                </div>
                <h3>${product.name}</h3>
                <p style="color: var(--gray); font-size: 0.9rem; margin: 0.5rem 0;">
                    ${product.description ? product.description.substring(0, 60) + '...' : ''}
                </p>
                <div class="price">$${product.price.toFixed(2)}</div>
                <div class="stock-status">
                    ${product.quantity > 0 ? 
                        `<span class="in-stock"><i class="fas fa-check-circle"></i> In Stock (${product.quantity})</span>` : 
                        '<span class="out-of-stock"><i class="fas fa-times-circle"></i> Out of Stock</span>'
                    }
                </div>
                <div class="card-actions">
                    <button class="btn btn-primary btn-block" onclick="addToCart('${product._id}')" ${product.quantity <= 0 ? 'disabled' : ''}>
                        <i class="fas fa-cart-plus"></i> Add to Cart
                    </button>
                    <a href="/product/${product._id}" class="btn btn-outline btn-block">
                        <i class="fas fa-eye"></i> View Details
                    </a>
                </div>
            </div>
        </div>
    `).join('');
}

function updateProductCount(count) {
    const countElement = document.getElementById('productsCount');
    if (countElement) {
        countElement.textContent = `${count} product${count !== 1 ? 's' : ''} found`;
    }
}

function setupFilterListeners() {
    const categoryFilter = document.getElementById('categoryFilter');
    const sortBy = document.getElementById('sortBy');
    
    if (categoryFilter) {
        categoryFilter.addEventListener('change', filterProducts);
    }
    
    if (sortBy) {
        sortBy.addEventListener('change', filterProducts);
    }
}

function filterProducts() {
    const category = document.getElementById('categoryFilter')?.value;
    const sortBy = document.getElementById('sortBy')?.value;
    
    let filtered = [...currentProducts];
    
    // Filter by category
    if (category) {
        filtered = filtered.filter(product => 
            product.category?._id === category || product.category === category
        );
    }
    
    // Sort products
    filtered.sort((a, b) => {
        switch (sortBy) {
            case 'price-low':
                return a.price - b.price;
            case 'price-high':
                return b.price - a.price;
            case 'name':
                return a.name.localeCompare(b.name);
            default: // 'newest'
                return new Date(b.createdAt) - new Date(a.createdAt);
        }
    });
    
    displayProducts(filtered);
    updateProductCount(filtered.length);
}

// Add to cart function
async function addToCart(productId, quantity = 1) {
    try {
        const response = await fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                productId: productId,
                quantity: quantity
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('✓ Product added to cart!', 'success');
            updateCartCount();
        } else {
            showNotification(`✗ ${data.error || 'Failed to add to cart'}`, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('⚠ Network error. Please try again.', 'error');
    }
}

function showError(message) {
    const container = document.getElementById('productsGrid');
    if (!container) return;
    
    container.innerHTML = `
        <div class="error-message" style="grid-column: 1 / -1; text-align: center; padding: 4rem; background: white; border-radius: 16px; box-shadow: var(--shadow);">
            <i class="fas fa-exclamation-triangle" style="font-size: 3rem; color: var(--danger); margin-bottom: 1rem;"></i>
            <h3 style="color: var(--danger); margin-bottom: 0.5rem;">Error Loading Products</h3>
            <p style="color: var(--gray); margin-bottom: 1.5rem;">${message}</p>
            <button class="btn btn-primary" onclick="loadProducts()">
                <i class="fas fa-redo"></i> Try Again
            </button>
        </div>
    `;
}

function showNotification(message, type = 'info') {
    // Create notification element
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
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// Update cart count
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
window.addToCart = addToCart;
window.filterProducts = filterProducts;
window.clearFilters = function() {
    const categoryFilter = document.getElementById('categoryFilter');
    const sortBy = document.getElementById('sortBy');
    
    if (categoryFilter) categoryFilter.value = '';
    if (sortBy) sortBy.value = 'newest';
    
    displayProducts(currentProducts);
    updateProductCount(currentProducts.length);
};
