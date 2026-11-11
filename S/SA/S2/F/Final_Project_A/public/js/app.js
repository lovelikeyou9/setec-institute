// Premium Car Dealer Client Application JavaScript Engine
// Base HTTP API Request Helper
const API = {
    getToken: () => localStorage.getItem('token'),
    getUser: () => {
        try {
            return JSON.parse(localStorage.getItem('user'));
        } catch {
            return null;
        }
    },
    setSession: (token, user) => {
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));
    },
    clearSession: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    request: async (endpoint, options = {}) => {
        const token = API.getToken();
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(endpoint, {
            ...options,
            headers
        });

        const result = await response.json();
        if (!response.ok) {
            throw new Error(result.error || 'Server error occurred');
        }
        return result;
    }
};
// Global Interactive Toast Notification System
function showToast(message, isError = false) {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast ${isError ? 'toast-error' : ''}`;
    toast.innerHTML = `<i class="ti ${isError ? 'ti-alert-circle' : 'ti-circle-check'}"></i> <span>${message}</span>`;

    document.body.appendChild(toast);
    setTimeout(() => toast.classList.add('active'), 50);

    setTimeout(() => {
        toast.classList.remove('active');
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}
// -------------------------------------------------------------
// Core UI Render Controllers
// -------------------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    renderNavbar();
    initAuthPage();
    initHomePage();
    initBrowsePage();
    initDetailPage();
    initCartPage();
    initCheckoutPage();
    initProfilePage();
    initSupportPage();
});
// Render navbar links based on role
function renderNavbar() {
    const navContainer = document.getElementById('main-nav');
    if (!navContainer) return;

    const user = API.getUser();
    let linksHtml = `
    <li><a href="index.html" class="${isActive('index.html')}">Home</a></li>
    <li><a href="products.html" class="${isActive('products.html')}">Showroom</a></li>
  `;

    if (user) {
        if (user.role === 'buyer') {
            linksHtml += `
        <li><a href="profile.html" class="${isActive('profile.html')}">My Orders</a></li>
        <li><a href="support.html" class="${isActive('support.html')}">Support Tickets</a></li>
        <li><a href="cart.html" class="${isActive('cart.html')}"><i class="ti ti-shopping-cart"></i> Cart</a></li>
      `;
        } else {
            // Admin/Staff/Support/Seller see Dashboard Link
            linksHtml += `
        <li><a href="admin/index.html" target="_blank" class="accent-link"><i class="ti ti-layout-dashboard"></i> Portal Panel</a></li>
      `;
        }
    }

    let actionsHtml = '';
    if (user) {
        actionsHtml = `
      <div class="user-pill glass-card" style="padding: 8px 16px; border-radius: 30px; display:flex; align-items:center; gap:8px;">
        <div class="seller-avatar" style="width:28px; height:28px; font-size:12px;">${user.name[0].toUpperCase()}</div>
        <span style="font-size:14px; font-weight:600;">${user.name}</span>
      </div>
      <button onclick="handleSignOut()" class="btn btn-secondary" style="padding: 8px 16px; border-radius: 30px;"><i class="ti ti-logout"></i> Exit</button>
    `;
    } else {
        actionsHtml = `
      <a href="login.html" class="btn btn-secondary" style="padding: 10px 20px; border-radius: 30px;">Sign In</a>
      <a href="register.html" class="btn btn-primary" style="padding: 10px 20px; border-radius: 30px;">Register</a>
    `;
    }

    navContainer.innerHTML = `
    <div class="container navbar">
      <a href="index.html" class="logo"><i class="ti ti-car"></i> APEX <span>MOTORS</span></a>
      <ul class="nav-links">${linksHtml}</ul>
      <div class="nav-actions">${actionsHtml}</div>
    </div>
  `;
}
function isActive(page) {
    return window.location.pathname.endsWith(page) ? 'active' : '';
}
function handleSignOut() {
    API.clearSession();
    showToast('Logged out successfully');
    setTimeout(() => window.location.href = 'index.html', 1000);
}
// -------------------------------------------------------------
// 1. Dynamic Authentication Handler
// -------------------------------------------------------------
function initAuthPage() {
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try {
                const res = await API.request('/api/auth/login', {
                    method: 'POST',
                    body: JSON.stringify({ email, password })
                });

                API.setSession(res.token, res.user);
                showToast(res.message || 'Login successful!');

                // Redirect based on role
                setTimeout(() => {
                    if (res.user.role === 'buyer') {
                        window.location.href = 'index.html';
                    } else {
                        window.location.href = 'admin/index.html';
                    }
                }, 800);
            } catch (err) {
                showToast(err.message, true);
            }
        });
    }
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = document.getElementById('name').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const role = document.getElementById('role').value;

            try {
                const res = await API.request('/api/auth/register', {
                    method: 'POST',
                    body: JSON.stringify({ name, email, password, role })
                });

                API.setSession(res.token, res.user);
                showToast('Registration successful! Redirecting...');

                setTimeout(() => {
                    if (res.user.role === 'buyer') {
                        window.location.href = 'index.html';
                    } else {
                        window.location.href = 'admin/index.html';
                    }
                }, 800);
            } catch (err) {
                showToast(err.message, true);
            }
        });
    }

    const forgotForm = document.getElementById('forgot-form');
    if (forgotForm) {
        forgotForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            try {
                const res = await API.request('/api/auth/forgot-password', {
                    method: 'POST',
                    body: JSON.stringify({ email })
                });
                showToast(res.message);
            } catch (err) {
                showToast(err.message, true);
            }
        });
    }
}
// -------------------------------------------------------------
// 2. Home Page Showcase Manager
// -------------------------------------------------------------
async function initHomePage() {
    const grid = document.getElementById('home-cars-grid');
    if (!grid) return;

    // Bind dynamic hero quick filter widget
    const filterForm = document.getElementById('hero-filter-widget');
    if (filterForm) {
        filterForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const make = document.getElementById('filter-make').value;
            const body = document.getElementById('filter-body').value;
            const cond = document.getElementById('filter-condition').value;
            const maxPrice = document.getElementById('filter-price').value;

            let targetUrl = `products.html?make=${make}&body=${body}&condition=${cond}`;
            if (maxPrice) targetUrl += `&max_price=${maxPrice}`;
            window.location.href = targetUrl;
        });
    }

    try {
        grid.innerHTML = '<div style="grid-column: span 3; text-align:center; padding: 40px; color:var(--text-muted);">Syncing live showroom cars...</div>';
        const cars = await API.request('/api/cars');
        // Slice only first 3 premium featured cars
        const featured = cars.filter(c => c.status === 'Available').slice(0, 3);

        if (featured.length === 0) {
            grid.innerHTML = '<div style="grid-column: span 3; text-align:center; padding: 40px;">No vehicles currently listed in the showroom.</div>';
            return;
        }

        grid.innerHTML = featured.map(car => renderCarCard(car)).join('');
    } catch (err) {
        grid.innerHTML = `<div style="grid-column: span 3; text-align:center; color:hsl(var(--color-accent)); padding: 40px;">Error sync: ${err.message}</div>`;
    }
}
function renderCarCard(car) {
    const user = API.getUser();
    const detailLink = `product-detail.html?id=${car.id}`;

    let actionButtons = '';
    if (!user || user.role === 'buyer') {
        actionButtons = `
      <a href="${detailLink}" class="btn btn-secondary">Specifications</a>
      <button onclick="proactiveAddToCart(${car.id})" class="btn btn-primary"><i class="ti ti-shopping-cart"></i> Cart</button>
    `;
    } else {
        actionButtons = `
      <a href="${detailLink}" class="btn btn-secondary">Specifications</a>
      <a href="admin/inventory.html" target="_blank" class="btn btn-primary">Edit Listing</a>
    `;
    }

    const statusBadge = car.status === 'Available'
        ? '<span class="badge badge-available">Available</span>'
        : (car.status === 'Reserved' ? '<span class="badge badge-reserved">Reserved</span>' : '<span class="badge badge-sold">Sold</span>');
    return `
    <div class="glass-card car-card">
      <div class="car-image-wrapper">
        <img src="${car.image_url || '/uploads/tesla_s.svg'}" alt="${car.make} ${car.model}">
        <div class="car-badge-container">${statusBadge}</div>
      </div>
      <div class="car-details-body">
        <div class="car-title-row">
          <div>
            <h3 style="margin-bottom:4px;">${car.make} ${car.model}</h3>
            <span style="font-size:14px; color:hsl(var(--text-muted)); font-weight:500;">Year ${car.year} • ${car.condition}</span>
          </div>
          <div class="car-price-tag">$${parseFloat(car.price).toLocaleString()}</div>
        </div>
        
        <div class="car-stats-grid">
          <div class="stat-item">
            <i class="ti ti-gauge"></i>
            <span>${car.mileage.toLocaleString()} mi</span>
          </div>
          <div class="stat-item">
            <i class="ti ti-settings"></i>
            <span>${car.transmission}</span>
          </div>
          <div class="stat-item">
            <i class="ti ti-charging-pile"></i>
            <span>${car.fuel_type}</span>
          </div>
        </div>
        
        <div class="car-card-actions">
          ${actionButtons}
        </div>
      </div>
    </div>
  `;
}
// Global hook to add to cart proactive
async function proactiveAddToCart(carId) {
    const user = API.getUser();
    if (!user) {
        showToast('Please sign in to add items to your cart', true);
        setTimeout(() => window.location.href = 'login.html', 1500);
        return;
    }

    if (user.role !== 'buyer') {
        showToast('Only Buyers can purchase vehicles!', true);
        return;
    }

    try {
        const res = await API.request('/api/cart/add', {
            method: 'POST',
            body: JSON.stringify({ carId })
        });
        showToast(res.message);
    } catch (err) {
        showToast(err.message, true);
    }
}
// -------------------------------------------------------------
// 3. Browse Showcase Panel (Showroom Page)
// -------------------------------------------------------------
async function initBrowsePage() {
    const grid = document.getElementById('showroom-cars-grid');
    if (!grid) return;

    const searchInput = document.getElementById('browse-search');
    const makeFilter = document.getElementById('browse-make');
    const bodyFilter = document.getElementById('browse-body');
    const condFilter = document.getElementById('browse-condition');
    const transFilter = document.getElementById('browse-transmission');
    const maxPriceInput = document.getElementById('browse-price');

    // Set initial filter state from URL params
    const params = new URLSearchParams(window.location.search);
    if (params.get('make')) makeFilter.value = params.get('make');
    if (params.get('body')) bodyFilter.value = params.get('body');
    if (params.get('condition')) condFilter.value = params.get('condition');
    if (params.get('max_price')) maxPriceInput.value = params.get('max_price');
    if (params.get('search')) searchInput.value = params.get('search');

    // Event listeners for triggers
    const elements = [searchInput, makeFilter, bodyFilter, condFilter, transFilter, maxPriceInput];
    elements.forEach(el => {
        if (el) el.addEventListener('input', fetchFilteredCars);
    });

    async function fetchFilteredCars() {
        try {
            grid.innerHTML = '<div style="grid-column: span 3; text-align:center; padding: 40px;">Filtering luxury listings...</div>';

            const queryParams = new URLSearchParams();
            if (searchInput.value) queryParams.set('search', searchInput.value);
            if (makeFilter.value) queryParams.set('make', makeFilter.value);
            if (bodyFilter.value) queryParams.set('body_type', bodyFilter.value);
            if (condFilter.value) queryParams.set('condition', condFilter.value);
            if (transFilter.value) queryParams.set('transmission', transFilter.value);
            if (maxPriceInput.value) queryParams.set('max_price', maxPriceInput.value);

            const cars = await API.request(`/api/cars?${queryParams.toString()}`);

            const countEl = document.getElementById('active-results-count');
            if (countEl) countEl.innerText = `${cars.length} Vehicles Found`;

            if (cars.length === 0) {
                grid.innerHTML = '<div style="grid-column: span 3; text-align:center; padding: 60px; color:var(--text-muted);">No luxury listings match your specific search criteria.</div>';
                return;
            }

            grid.innerHTML = cars.map(car => renderCarCard(car)).join('');
        } catch (err) {
            grid.innerHTML = `<div style="grid-column: span 3; text-align:center; color:hsl(var(--color-accent)); padding: 40px;">Error sync: ${err.message}</div>`;
        }
    }

    // Initial fire
    fetchFilteredCars();
}
// -------------------------------------------------------------
// 4. Product Details Specification Loader
// -------------------------------------------------------------
async function initDetailPage() {
    const container = document.getElementById('product-detail-container');
    if (!container) return;

    const params = new URLSearchParams(window.location.search);
    const carId = params.get('id');
    if (!carId) {
        container.innerHTML = '<div class="glass-card" style="text-align:center; padding: 40px;">Missing car specifications ID parameter.</div>';
        return;
    }

    try {
        const car = await API.request(`/api/cars/${carId}`);

        // Bind main vehicle markup
        container.innerHTML = `
      <div class="detail-gallery">
        <div class="gallery-main">
          <img src="${car.image_url || '/uploads/tesla_s.svg'}" alt="${car.make} ${car.model}">
        </div>
        <div class="glass-card description-box">
          <h3 class="specs-sheet-title">Technical Description</h3>
          <p>${car.description || 'No custom dealer description is logged for this luxury vehicle.'}</p>
        </div>
      </div>
      
      <div class="detail-info-pane">
        <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:12px;">
          <div>
            <h1>${car.make} ${car.model}</h1>
            <span class="badge ${car.status === 'Available' ? 'badge-available' : (car.status === 'Reserved' ? 'badge-reserved' : 'badge-sold')}">${car.status}</span>
          </div>
          <div class="detail-price">$${parseFloat(car.price).toLocaleString()}</div>
        </div>
        
        <div class="glass-card specs-sheet-card">
          <h3 class="specs-sheet-title">Specifications</h3>
          <div class="specs-grid">
            <div class="spec-row">
              <span class="spec-label">Year</span>
              <span class="spec-val">${car.year}</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">Mileage</span>
              <span class="spec-val">${car.mileage.toLocaleString()} miles</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">Transmission</span>
              <span class="spec-val">${car.transmission}</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">Fuel Type</span>
              <span class="spec-val">${car.fuel_type}</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">Body Type</span>
              <span class="spec-val">${car.body_type}</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">Condition</span>
              <span class="spec-val">${car.condition}</span>
            </div>
          </div>
        </div>
        
        <div class="glass-card">
          <h3 class="specs-sheet-title">Dealership & Actions</h3>
          <div class="seller-card">
            <div class="seller-avatar">${car.seller_name[0].toUpperCase()}</div>
            <div>
              <h4 style="font-size:15px;">Elite Partner: ${car.seller_name}</h4>
              <p style="font-size:12px; color:hsl(var(--text-muted));">${car.seller_email}</p>
            </div>
          </div>
          
          <div style="display:flex; flex-direction:column; gap:12px; margin-top:20px;">
            ${car.status === 'Available' ? `
              <button onclick="proactiveAddToCart(${car.id})" class="btn btn-primary" style="width:100%;"><i class="ti ti-shopping-cart"></i> Add to Cart</button>
              <button onclick="openBookingModal(${car.id}, 'Test Drive')" class="btn btn-secondary" style="width:100%;"><i class="ti ti-calendar"></i> Book Test Drive</button>
              <button onclick="openBookingModal(${car.id}, 'Reservation')" class="btn btn-secondary" style="width:100%; color:hsl(var(--color-primary)); border-color:rgba(6, 182, 212, 0.4);"><i class="ti ti-lock"></i> Reserve Vehicle ($500 Refundable)</button>
            ` : `<button class="btn btn-secondary" style="width:100%; cursor:not-allowed;" disabled>Vehicle is currently ${car.status}</button>`}
          </div>
        </div>
      </div>
    `;
    } catch (err) {
        container.innerHTML = `<div class="glass-card" style="text-align:center; color:hsl(var(--color-accent)); padding: 40px;">Failed specifications fetch: ${err.message}</div>`;
    }
}
// Global modal triggering hooks
let selectedBookingCarId = null;
let selectedBookingType = 'Test Drive';
function openBookingModal(carId, type) {
    const user = API.getUser();
    if (!user) {
        showToast('Please sign in to schedule showroom bookings', true);
        setTimeout(() => window.location.href = 'login.html', 1200);
        return;
    }

    selectedBookingCarId = carId;
    selectedBookingType = type;

    const modal = document.getElementById('booking-modal');
    const typeTitle = document.getElementById('booking-type-title');
    if (typeTitle) typeTitle.innerText = `Schedule ${type}`;

    if (modal) modal.classList.add('active');
}
function closeBookingModal() {
    const modal = document.getElementById('booking-modal');
    if (modal) modal.classList.remove('active');
}
// Bind booking modal form submit hook
const bookingForm = document.getElementById('booking-form');
if (bookingForm) {
    bookingForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const date = document.getElementById('booking-date-input').value;
        const notes = document.getElementById('booking-notes-input').value;

        if (!date) return showToast('Please select a booking date', true);

        try {
            const res = await API.request('/api/bookings', {
                method: 'POST',
                body: JSON.stringify({
                    carId: selectedBookingCarId,
                    bookingDate: date,
                    type: selectedBookingType,
                    notes
                })
            });
            showToast(res.message);
            closeBookingModal();
            setTimeout(() => window.location.reload(), 1500);
        } catch (err) {
            showToast(err.message, true);
        }
    });
}
// -------------------------------------------------------------
// 5. Shopping Cart Operations
// -------------------------------------------------------------
async function initCartPage() {
    const container = document.getElementById('cart-items-wrapper');
    if (!container) return;

    try {
        container.innerHTML = '<div style="text-align:center; padding: 40px;">Opening cart files...</div>';
        const items = await API.request('/api/cart');

        const countEl = document.getElementById('cart-count-header');
        if (countEl) countEl.innerText = `Cart (${items.length} Items)`;

        if (items.length === 0) {
            container.innerHTML = `
        <div style="text-align:center; padding: 60px 40px; color:hsl(var(--text-muted));">
          <i class="ti ti-shopping-cart" style="font-size: 64px; color: var(--glass-border); margin-bottom: 16px; display:block;"></i>
          <p style="font-size:18px; margin-bottom:24px;">Your luxury cart is empty.</p>
          <a href="products.html" class="btn btn-primary">Browse Showroom</a>
        </div>
      `;
            const summaryBtn = document.getElementById('cart-checkout-btn');
            if (summaryBtn) summaryBtn.disabled = true;
            return;
        }

        // Render list
        container.innerHTML = items.map(car => `
      <div class="glass-card cart-item">
        <img class="cart-item-img" src="${car.image_url || '/uploads/tesla_s.svg'}" alt="${car.make} ${car.model}">
        <div class="cart-item-details">
          <div class="cart-item-title">${car.make} ${car.model}</div>
          <span style="font-size:13px; color:hsl(var(--text-muted));">${car.year} • ${car.condition}</span>
          <div class="cart-item-price">$${parseFloat(car.price).toLocaleString()}</div>
        </div>
        <button onclick="handleRemoveCartItem(${car.id})" class="btn btn-danger" style="padding:10px;"><i class="ti ti-trash"></i></button>
      </div>
    `).join('');

        // Compute summary
        const subtotal = items.reduce((sum, car) => sum + parseFloat(car.price), 0);
        const tax = subtotal * 0.08; // 8% luxury sales tax
        const total = subtotal + tax;

        const subtotalEl = document.getElementById('cart-subtotal');
        const taxEl = document.getElementById('cart-tax');
        const totalEl = document.getElementById('cart-total');

        if (subtotalEl) subtotalEl.innerText = `$${subtotal.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
        if (taxEl) taxEl.innerText = `$${tax.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
        if (totalEl) totalEl.innerText = `$${total.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;

        const summaryBtn = document.getElementById('cart-checkout-btn');
        if (summaryBtn) {
            summaryBtn.disabled = false;
            summaryBtn.onclick = () => {
                // Send first car id for purchase routing, or purchase multiple in custom builds
                // Since checkout works by individual reservation models:
                window.location.href = `checkout.html?car_id=${items[0].id}`;
            };
        }
    } catch (err) {
        container.innerHTML = `<div style="text-align:center; color:hsl(var(--color-accent)); padding: 40px;">Failed opening cart: ${err.message}</div>`;
    }
}
async function handleRemoveCartItem(carId) {
    try {
        const res = await API.request(`/api/cart/remove/${carId}`, { method: 'DELETE' });
        showToast(res.message);
        initCartPage(); // Refresh
    } catch (err) {
        showToast(err.message, true);
    }
}
// -------------------------------------------------------------
// 6. Interactive Checkout & Mock Payment Gate
// -------------------------------------------------------------
async function initCheckoutPage() {
    const container = document.getElementById('checkout-items-preview');
    if (!container) return;

    const params = new URLSearchParams(window.location.search);
    const carId = params.get('car_id');
    if (!carId) {
        window.location.href = 'cart.html';
        return;
    }

    try {
        const car = await API.request(`/api/cars/${carId}`);

        const subtotal = parseFloat(car.price);
        const tax = subtotal * 0.08;
        const total = subtotal + tax;

        container.innerHTML = `
      <div class="summary-row">
        <span>Vehicle:</span>
        <span style="font-weight:600;">${car.make} ${car.model} (${car.year})</span>
      </div>
      <div class="summary-row">
        <span>Subtotal:</span>
        <span>$${subtotal.toLocaleString()}</span>
      </div>
      <div class="summary-row">
        <span>Showroom Tax (8%):</span>
        <span>$${tax.toLocaleString()}</span>
      </div>
      <div class="summary-row summary-total">
        <span>Total Due:</span>
        <span style="color:hsl(var(--color-primary));">$${total.toLocaleString(undefined, { minimumFractionDigits: 2 })}</span>
      </div>
    `;

        // Form submission processing
        const checkoutForm = document.getElementById('checkout-payment-form');
        if (checkoutForm) {
            checkoutForm.addEventListener('submit', async (e) => {
                e.preventDefault();

                const paymentMethod = document.querySelector('input[name="payment-method"]:checked').value;
                const address = document.getElementById('billing-address').value;
                const cardName = document.getElementById('card-name').value;
                const cardNumber = document.getElementById('card-number').value;

                if (!address) return showToast('Please input shipping registration address', true);

                // Show mock processing spinner overlay
                const processModal = document.getElementById('payment-processing-modal');
                if (processModal) processModal.classList.add('active');

                try {
                    // Delay to simulate secure connection with bank gate
                    await new Promise(resolve => setTimeout(resolve, 2000));

                    const res = await API.request('/api/checkout', {
                        method: 'POST',
                        body: JSON.stringify({
                            carId: car.id,
                            paymentMethod,
                            shippingAddress: address,
                            cardName,
                            cardNumber
                        })
                    });

                    if (processModal) processModal.classList.remove('active');

                    // Clear standard buyer cart item
                    await API.request(`/api/cart/remove/${car.id}`, { method: 'DELETE' }).catch(() => { });

                    // Show successful receipt modal
                    const receiptModal = document.getElementById('receipt-success-modal');
                    const receiptDetails = document.getElementById('receipt-details-body');
                    if (receiptDetails) {
                        receiptDetails.innerHTML = `
              <div style="text-align:center; margin-bottom:20px;">
                <i class="ti ti-circle-check" style="font-size:64px; color:#10b981; display:block; margin: 0 auto 12px;"></i>
                <h3 style="font-size:22px; font-weight:800;">${res.message}</h3>
                <p style="color:var(--text-muted); font-size:14px; margin-top:4px;">Transaction ID: ${res.receipt.transactionId}</p>
              </div>
              <div class="summary-row">
                <span>Vehicle Purchased:</span>
                <span>${res.receipt.car}</span>
              </div>
              <div class="summary-row">
                <span>Amount Charged:</span>
                <span style="font-weight:700; color:hsl(var(--color-primary));">$${parseFloat(res.receipt.price).toLocaleString()}</span>
              </div>
              <div class="summary-row">
                <span>Transaction Date:</span>
                <span>${res.receipt.date}</span>
              </div>
              <p style="font-size:12px; color:hsl(var(--text-muted)); text-align:center; margin-top:20px;">A receipt has been dispatched. Log in to your profile to track order status.</p>
            `;
                    }
                    if (receiptModal) receiptModal.classList.add('active');
                } catch (err) {
                    if (processModal) processModal.classList.remove('active');
                    showToast(err.message, true);
                }
            });
        }
    } catch (err) {
        showToast(err.message, true);
    }
}
// -------------------------------------------------------------
// 7. Buyer Profile Dashboard (Orders, Bookings & Messages)
// -------------------------------------------------------------
async function initProfilePage() {
    const ordersGrid = document.getElementById('profile-orders-list');
    const bookingsGrid = document.getElementById('profile-bookings-list');
    if (!ordersGrid || !bookingsGrid) return;

    try {
        ordersGrid.innerHTML = '<li>Syncing history...</li>';
        bookingsGrid.innerHTML = '<li>Syncing scheduled list...</li>';

        const orders = await API.request('/api/orders');
        const bookings = await API.request('/api/bookings');

        // Render orders
        if (orders.length === 0) {
            ordersGrid.innerHTML = '<li class="glass-card" style="list-style:none; padding:20px; color:hsl(var(--text-muted)); text-align:center;">No past purchases logged.</li>';
        } else {
            ordersGrid.innerHTML = orders.map(o => `
        <li class="glass-card" style="list-style:none; display:flex; justify-content:space-between; align-items:center; padding:20px;">
          <div>
            <h4 style="font-size:16px;">${o.year} ${o.make} ${o.model}</h4>
            <p style="font-size:12px; color:hsl(var(--text-muted)); margin-top:4px;">Ordered on ${new Date(o.created_at).toLocaleDateString()} • Method: ${o.payment_method}</p>
          </div>
          <div style="text-align:right;">
            <div style="font-weight:800; color:hsl(var(--color-primary));">$${parseFloat(o.price_paid).toLocaleString()}</div>
            <span class="badge badge-available" style="margin-top:6px; font-size:10px;">${o.status}</span>
          </div>
        </li>
      `).join('');
        }

        // Render bookings
        if (bookings.length === 0) {
            bookingsGrid.innerHTML = '<li class="glass-card" style="list-style:none; padding:20px; color:hsl(var(--text-muted)); text-align:center;">No showroom visits currently scheduled.</li>';
        } else {
            bookingsGrid.innerHTML = bookings.map(b => `
        <li class="glass-card" style="list-style:none; display:flex; justify-content:space-between; align-items:center; padding:20px;">
          <div>
            <h4 style="font-size:16px;">${b.year} ${b.make} ${b.model} (${b.type})</h4>
            <p style="font-size:12px; color:hsl(var(--text-muted)); margin-top:4px;">Appt Date: ${new Date(b.booking_date).toLocaleString()} • Host Seller: ${b.seller_name}</p>
            ${b.notes ? `<p style="font-size:12px; color:var(--text-muted); margin-top:4px; font-style:italic;">Notes: ${b.notes}</p>` : ''}
          </div>
          <div style="text-align:right;">
            <span class="badge ${b.status === 'Approved' ? 'badge-available' : (b.status === 'Pending' ? 'badge-reserved' : 'badge-sold')}" style="font-size:10px;">${b.status}</span>
          </div>
        </li>
      `).join('');
        }
    } catch (err) {
        showToast(err.message, true);
    }
}
// -------------------------------------------------------------
// 8. Client Ticketing Support Chat Box
// -------------------------------------------------------------
let activeSupportTicketId = null;
async function initSupportPage() {
    const ticketsList = document.getElementById('support-tickets-list');
    if (!ticketsList) return;

    try {
        ticketsList.innerHTML = '<li>Syncing ticket list...</li>';
        const tickets = await API.request('/api/tickets');

        if (tickets.length === 0) {
            ticketsList.innerHTML = `
        <div style="text-align:center; padding:40px; color:hsl(var(--text-muted));">
          <p>No active support tickets logged.</p>
        </div>
      `;
            return;
        }

        ticketsList.innerHTML = tickets.map(t => {
            const statusBadge = t.status === 'Open'
                ? '<span class="badge badge-reserved" style="font-size:10px;">Open</span>'
                : (t.status === 'In Progress' ? '<span class="badge badge-available" style="font-size:10px;">Reply Received</span>' : '<span class="badge badge-sold" style="font-size:10px;">Resolved</span>');
            return `
        <div onclick="openSupportChat(${t.id})" class="glass-card ticket-item">
          <div>
            <div class="ticket-subject">${t.subject}</div>
            <div style="font-size:11px; color:hsl(var(--text-muted)); margin-top:4px;">Ticket Reference: #${t.id} • Opened on ${new Date(t.created_at).toLocaleDateString()}</div>
          </div>
          <div>${statusBadge}</div>
        </div>
      `;
        }).join('');
    } catch (err) {
        showToast(err.message, true);
    }
}
async function openSupportChat(ticketId) {
    activeSupportTicketId = ticketId;
    const pane = document.getElementById('chat-room-pane');
    const chatBox = document.getElementById('chat-messages-container');
    if (!pane || !chatBox) return;

    pane.style.display = 'block';
    chatBox.innerHTML = '<div>Syncing ticket messages...</div>';

    try {
        const { ticket, messages } = await API.request(`/api/tickets/${ticketId}`);

        const subjectTitle = document.getElementById('chat-ticket-subject');
        if (subjectTitle) subjectTitle.innerText = `${ticket.subject} (Ticket #${ticket.id})`;

        const statusVal = document.getElementById('chat-ticket-status');
        if (statusVal) statusVal.innerText = ticket.status;

        chatBox.innerHTML = messages.map(m => {
            const isSentByMe = m.sender_role === 'buyer'; // For client portal, sent by buyer is self
            return `
        <div class="chat-message ${isSentByMe ? 'sent' : 'received'}">
          <strong>${m.sender_name} (${m.sender_role})</strong>
          <p style="margin-top:4px;">${m.message}</p>
          <span class="chat-meta">${new Date(m.created_at).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
        </div>
      `;
        }).join('');

        // Auto scroll bottom
        chatBox.scrollTop = chatBox.scrollHeight;
    } catch (err) {
        showToast(err.message, true);
    }
}
// Bind chat message sender submission button
const chatSendForm = document.getElementById('chat-send-form');
if (chatSendForm) {
    chatSendForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const textarea = document.getElementById('chat-message-input');
        if (!textarea || !textarea.value.trim() || !activeSupportTicketId) return;

        const text = textarea.value.trim();
        textarea.value = '';

        try {
            await API.request(`/api/tickets/${activeSupportTicketId}/messages`, {
                method: 'POST',
                body: JSON.stringify({ message: text })
            });
            // Refresh chat logs
            openSupportChat(activeSupportTicketId);
        } catch (err) {
            showToast(err.message, true);
        }
    });
}
// Bind modal support form submission
const newTicketForm = document.getElementById('new-ticket-form');
if (newTicketForm) {
    newTicketForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const subject = document.getElementById('ticket-subject').value;
        const desc = document.getElementById('ticket-description').value;

        try {
            const res = await API.request('/api/tickets', {
                method: 'POST',
                body: JSON.stringify({ subject, message: desc })
            });
            showToast(res.message);
            closeNewTicketModal();
            initSupportPage(); // Refresh list
        } catch (err) {
            showToast(err.message, true);
        }
    });
}
function openNewTicketModal() {
    const modal = document.getElementById('ticket-modal');
    if (modal) modal.classList.add('active');
}
function closeNewTicketModal() {
    const modal = document.getElementById('ticket-modal');
    if (modal) modal.classList.remove('active');
}
