document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadAllOrders();

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
            alert('✅ Product Added Successfully!');
            e.target.reset();
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
