// Store data globally for filtering
let allProducts = [];
let allOrders = [];

document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadAllOrders();
    loadInventory();

    // 1. Add Product Listener
    document.getElementById('addProductForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
        submitBtn.disabled = true;

        const productData = {
            name: document.getElementById('pName').value,
            description: document.getElementById('pDesc').value,
            price: document.getElementById('pPrice').value,
            image: document.getElementById('pImage').value,
            category: document.getElementById('pCategory').value,
            quantity: document.getElementById('pQty').value,
            isFeatured: true
        };

        const res = await fetch('/api/products', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(productData)
        });
        
        const data = await res.json();
        if(data.success) {
            alert('✅ Product Added Successfully!');
            e.target.reset();
            loadInventory(); 
            showSection('inventory');
        } else {
            alert('❌ Error: ' + data.error);
        }

        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    });

    // 2. Product Search Listener
    document.getElementById('productSearchInput').addEventListener('keyup', (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = allProducts.filter(p => p.name.toLowerCase().includes(term));
        renderInventory(filtered);
    });

    // 3. Order Search Listener
    document.getElementById('orderSearchInput').addEventListener('keyup', (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = allOrders.filter(o => 
            o.orderNumber.toLowerCase().includes(term) || 
            (o.user && o.user.name.toLowerCase().includes(term))
        );
        renderOrders(filtered);
    });
});

// --- Tab Switching Logic ---
function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
    document.getElementById('section-' + sectionId).classList.add('active');
    
    const navItems = document.querySelectorAll('.nav-item');
    if(sectionId === 'orders') navItems[0].classList.add('active');
    if(sectionId === 'inventory') navItems[1].classList.add('active');
    if(sectionId === 'add-product') navItems[2].classList.add('active');
}

// --- Data Loading Functions ---

async function loadCategories() {
    const res = await fetch('/api/categories');
    const cats = await res.json();
    const select = document.getElementById('pCategory');
    select.innerHTML = cats.map(c => `<option value="${c.name}">${c.name}</option>`).join('');
}

async function loadAllOrders() {
    const res = await fetch('/api/orders/all-orders');
    const data = await res.json();
    allOrders = data.orders || []; // Save to global
    renderOrders(allOrders);
}

function renderOrders(orders) {
    const tbody = document.getElementById('ordersTableBody');
    if(!orders || orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">No orders found.</td></tr>';
        return;
    }

    tbody.innerHTML = orders.map(o => `
        <tr>
            <td><strong>${o.orderNumber}</strong></td>
            <td>${o.user ? o.user.name : 'Guest'}</td>
            <td>$${o.total.toFixed(2)}</td>
            <td><span class="badge badge-pending">${o.status}</span></td>
            <td>${new Date(o.createdAt).toLocaleDateString()}</td>
        </tr>
    `).join('');
}

async function loadInventory() {
    const res = await fetch('/api/products');
    const products = await res.json();
    allProducts = products; // Save to global
    renderInventory(allProducts);
}

function renderInventory(products) {
    const tbody = document.getElementById('productsTableBody');
    if(products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">No products found.</td></tr>';
        return;
    }

    tbody.innerHTML = products.map(p => `
        <tr>
            <td><img src="${p.image}" style="width: 40px; height: 40px; object-fit: cover; border-radius: 6px;"></td>
            <td style="font-weight:500;">${p.name}</td>
            <td>$${p.price.toFixed(2)}</td>
            <td>${p.quantity}</td>
            <td>
                <button onclick="deleteProduct('${p._id}')" class="delete-btn" title="Delete Product">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

window.deleteProduct = async (id) => {
    if(!confirm('Are you sure you want to delete this product?')) return;
    const res = await fetch(`/api/products/${id}`, { method: 'DELETE' });
    const data = await res.json();
    if(data.success) loadInventory();
    else alert('Failed to delete');
};
