document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadAllOrders();
    loadInventory(); // <--- New Function

    // Handle Add Product
    document.getElementById('addProductForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
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
            alert('✅ Product Added!');
            e.target.reset();
            loadInventory(); // Refresh list immediately
        } else {
            alert('❌ Error: ' + data.error);
        }
    });
});

async function loadCategories() {
    const res = await fetch('/api/categories');
    const cats = await res.json();
    const select = document.getElementById('pCategory');
    select.innerHTML = cats.map(c => `<option value="${c.name}">${c.name}</option>`).join('');
}

async function loadAllOrders() {
    const res = await fetch('/api/orders/all-orders');
    const data = await res.json();
    const tbody = document.getElementById('ordersTableBody');
    
    if(!data.orders || data.orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">No orders found.</td></tr>';
        return;
    }

    tbody.innerHTML = data.orders.map(o => `
        <tr>
            <td><strong>${o.orderNumber}</strong></td>
            <td>${o.user ? o.user.name : 'Guest'}</td>
            <td>$${o.total.toFixed(2)}</td>
            <td><span class="badge badge-pending">${o.status}</span></td>
            <td>${new Date(o.createdAt).toLocaleDateString()}</td>
        </tr>
    `).join('');
}

// NEW: Load Products into Table
async function loadInventory() {
    const res = await fetch('/api/products');
    const products = await res.json();
    const tbody = document.getElementById('productsTableBody');

    if(products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">No products in inventory.</td></tr>';
        return;
    }

    tbody.innerHTML = products.map(p => `
        <tr>
            <td><img src="${p.image}" style="width: 40px; height: 40px; object-fit: cover; border-radius: 4px;"></td>
            <td>${p.name}</td>
            <td>$${p.price.toFixed(2)}</td>
            <td>${p.quantity}</td>
            <td>
                <button onclick="deleteProduct('${p._id}')" style="background: #fee2e2; color: #ef4444; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// NEW: Delete Function (Global)
window.deleteProduct = async (id) => {
    if(!confirm('Are you sure you want to delete this product?')) return;
    
    const res = await fetch(`/api/products/${id}`, { method: 'DELETE' });
    const data = await res.json();
    
    if(data.success) {
        loadInventory(); // Refresh list
    } else {
        alert('Failed to delete');
    }
};
