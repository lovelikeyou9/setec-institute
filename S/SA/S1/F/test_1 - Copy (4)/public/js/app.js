// app.js - Main application JavaScript
document.addEventListener('DOMContentLoaded', function() {
    console.log('ComputerStore app loaded');
    
    // Check current page and load appropriate content
    if (document.getElementById('featuredProducts')) {
        loadFeaturedProducts();
    }
    
    if (document.getElementById('productsGrid')) {
        loadAllProducts();
        loadCategoriesForFilter();
    }
    
    // Update UI
    updateCartCount();
    updateLoginStatus();
    setupLogout();
    
    // Setup event listeners
    setupEventListeners();
    
    // Add animations
    addAnimations();
});

function setupEventListeners() {
    // Search form
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const searchInput = document.getElementById('searchInput');
            if (searchInput && searchInput.value.trim()) {
                window.location.href = `/search?q=${encodeURIComponent(searchInput.value.trim())}`;
            }
        });
    }
    
    // Mobile menu toggle (if needed)
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    if (mobileMenuBtn) {
        mobileMenuBtn.addEventListener('click', toggleMobileMenu);
    }
}

async function loadFeaturedProducts() {
    try {
        const response = await fetch('/api/products');
        const products = await response.json();
        
        // Get featured products
        let featuredProducts = products.filter(p => p.isFeatured).slice(0, 6);
        if (featuredProducts.length === 0) {
            featuredProducts = products.slice(0, 6);
        }
        
        const container = document.getElementById('featuredProducts');
        if (container) {
            container.innerHTML = featuredProducts.map(product => `
                <div class="card">
                    ${product.isFeatured ? '<span class="card-badge">Featured</span>' : ''}
                    <img src="${product.image || 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=400&h=300&fit=crop'}" 
                         alt="${product.name}" 
                         class="card-image">
                    <div class="card-content">
                        <h3>${product.name}</h3>
                        <p>${product.description ? product.description.substring(0, 80) + '...' : 'High-quality computer product'}</p>
                        <div class="price">$${product.price.toFixed(2)}</div>
                        <div class="stock-status">
                            ${product.quantity > 0 ? 
                                `<span class="in-stock"><i class="fas fa-check-circle"></i> In Stock</span>` : 
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
    } catch (error) {
        console.error('Error loading featured products:', error);
        const container = document.getElementById('featuredProducts');
        if (container) {
            container.innerHTML = `
                <div class="error-message" style="grid-column: 1 / -1; text-align: center; padding: 2rem; color: var(--danger);">
                    <i class="fas fa-exclamation-triangle"></i> Failed to load products. Please try again later.
                </div>
            `;
        }
    }
}

async function loadAllProducts() {
    try {
        const response = await fetch('/api/products');
        const products = await response.json();
        
        const container = document.getElementById('productsGrid');
        const countElement = document.getElementById('productsCount');
        
        if (countElement) {
            countElement.textContent = `${products.length} products found`;
        }
        
        if (container) {
            if (products.length === 0) {
                container.innerHTML = `
                    <div class="no-products" style="grid-column: 1 / -1; text-align: center; padding: 4rem;">
                        <i class="fas fa-search" style="font-size: 3rem; color: var(--gray-light); margin-bottom: 1rem;"></i>
                        <h3 style="color: var(--gray); margin-bottom: 0.5rem;">No products found</h3>
                        <p style="color: var(--gray);">Try adjusting your filters or search terms</p>
                    </div>
                `;
            } else {
                container.innerHTML = products.map(product => `
                    <div class="card">
                        ${product.isFeatured ? '<span class="card-badge">Featured</span>' : ''}
                        <img src="${product.image || 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=400&h=300&fit=crop'}" 
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
        }
    } catch (error) {
        console.error('Error loading products:', error);
        showError('Failed to load products. Please try again later.');
    }
}

async function loadCategoriesForFilter() {
    try {
        const response = await fetch('/api/categories');
        const categories = await response.json();
        
        const filter = document.getElementById('categoryFilter');
        if (filter) {
            filter.innerHTML = '<option value="">All Categories</option>' + 
                categories.map(cat => `<option value="${cat._id}">${cat.name}</option>`).join('');
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

// REAL CART FUNCTIONALITY
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
        console.error('Error adding to cart:', error);
        showNotification('⚠ Network error. Please try again.', 'error');
    }
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

// Check login status and update UI
async function updateLoginStatus() {
    try {
        const response = await fetch('/api/auth/me');
        const data = await response.json();
        
        const guestLinks = document.getElementById('guestLinks');
        const userLinks = document.getElementById('userLinks');
        const userName = document.getElementById('userName');
        
        if (data.success && data.user) {
            // User is logged in
            if (guestLinks) guestLinks.style.display = 'none';
            if (userLinks) {
                userLinks.style.display = 'flex';
                if (userName) {
                    // Get first name
                    const firstName = data.user.name.split(' ')[0];
                    userName.innerHTML = `<i class="fas fa-user"></i> ${firstName}`;
                    userName.href = '/profile';
                }
            }
            
            // Store in localStorage
            localStorage.setItem('user', JSON.stringify(data.user));
            localStorage.setItem('isLoggedIn', 'true');
            
        } else {
            // User is not logged in
            if (guestLinks) guestLinks.style.display = 'flex';
            if (userLinks) userLinks.style.display = 'none';
            
            // Clear localStorage
            localStorage.removeItem('user');
            localStorage.removeItem('isLoggedIn');
        }
    } catch (error) {
        console.error('Error checking login status:', error);
    }
}

// Setup logout functionality
function setupLogout() {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async function(e) {
            e.preventDefault();
            
            if (!confirm('Are you sure you want to logout?')) {
                return;
            }
            
            try {
                const response = await fetch('/api/auth/logout', {
                    method: 'POST'
                });
                
                const data = await response.json();
                
                if (data.success) {
                    // Clear localStorage
                    localStorage.removeItem('user');
                    localStorage.removeItem('isLoggedIn');
                    
                    // Show notification
                    showNotification('Logged out successfully', 'success');
                    
                    // Update UI and redirect
                    updateLoginStatus();
                    setTimeout(() => {
                        window.location.href = '/';
                    }, 1000);
                }
            } catch (error) {
                console.error('Logout error:', error);
                showNotification('Logout failed. Please try again.', 'error');
            }
        });
    }
}

function applyFilters() {
    const category = document.getElementById('categoryFilter')?.value;
    const sortBy = document.getElementById('sortBy')?.value;
    
    console.log('Applying filters:', { category, sortBy });
    // Will implement filter logic later
}

function clearFilters() {
    const categoryFilter = document.getElementById('categoryFilter');
    const sortBy = document.getElementById('sortBy');
    
    if (categoryFilter) categoryFilter.value = '';
    if (sortBy) sortBy.value = 'newest';
    
    console.log('Filters cleared');
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

function addAnimations() {
    // Add scroll animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');
            }
        });
    }, observerOptions);
    
    // Observe cards
    document.querySelectorAll('.card, .mission-card, .category-card').forEach(card => {
        observer.observe(card);
    });
}

// Make functions available globally
window.addToCart = addToCart;
window.updateCartCount = updateCartCount;
window.showNotification = showNotification;
window.applyFilters = applyFilters;
window.clearFilters = clearFilters;
