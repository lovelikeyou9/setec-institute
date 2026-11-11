let allProducts = [];
let allOrders = [];
// Track sorting state for both tables
let sortState = {
    inventory: { field: 'name', dir: 'asc' },
    orders: { field: 'createdAt', dir: 'desc' } 
};

document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadAllOrders();
    loadInventory();

    // 1. Handle Form Submit (Create OR Update)
    document.getElementById('addProductForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const id = document.getElementById('pId').value;
        const isEdit = !!id;
        
        const submitBtn = document.getElementById('saveBtn');
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

        const url = isEdit ? `/api/products/${id}` : '/api/products';
        const method = isEdit ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(productData)
            });
            const data = await res.json();
            if(data.success) {
                alert(isEdit ? '✅ Product Updated!' : '✅ Product Added!');
                resetForm();
                loadInventory();
                showSection('inventory');
            } else {
                alert('❌ Error: ' + data.error);
            }
        } catch (err) { alert('Error: ' + err.message); }

        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    });

    // Search Listeners
    document.getElementById('productSearchInput').addEventListener('keyup', (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = allProducts.filter(p => p.name.toLowerCase().includes(term));
        renderInventory(filtered);
    });

    document.getElementById('orderSearchInput').addEventListener('keyup', (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = allOrders.filter(o => 
            o.orderNumber.toLowerCase().includes(term) ||
            (o.user && o.user.name.toLowerCase().includes(term))
        );
        renderOrders(filtered);
    });
});

// --- SORTING LOGIC ---

// Sort Inventory
function sortInventory(field) {
    toggleSort('inventory', field);
    const sorted = [...allProducts].sort((a, b) => compareValues(a, b, field, sortState.inventory.dir));
    renderInventory(sorted);
}

// Sort Orders (NEW)
function sortOrders(field) {
    toggleSort('orders', field);
    const sorted = [...allOrders].sort((a, b) => {
        // Special handling for Customer Name (nested object)
        if (field === 'customer') {
            const nameA = a.user ? a.user.name : 'Guest';
            const nameB = b.user ? b.user.name : 'Guest';
            return compareValues({val: nameA}, {val: nameB}, 'val', sortState.orders.dir);
        }
        return compareValues(a, b, field, sortState.orders.dir);
    });
    renderOrders(sorted);
}

// Helper: Toggle direction
function toggleSort(type, field) {
    if (sortState[type].field === field) {
        sortState[type].dir = sortState[type].dir === 'asc' ? 'desc' : 'asc';
    } else {
        sortState[type].field = field;
        sortState[type].dir = 'asc';
    }
}

// Helper: Compare two values
function compareValues(a, b, field, dir) {
    let valA = a[field];
    let valB = b[field];

    if (typeof valA === 'string') valA = valA.toLowerCase();
    if (typeof valB === 'string') valB = valB.toLowerCase();

    if (valA < valB) return dir === 'asc' ? -1 : 1;
    if (valA > valB) return dir === 'asc' ? 1 : -1;
    return 0;
}

// --- PRINT RECEIPT LOGIC (NEW) ---
window.printOrder = (id) => {
    const order = allOrders.find(o => o._id === id);
    if (!order) return;

    // Create a new window for the receipt
    const printWindow = window.open('', '', 'width=800,height=600');
    
    // Generate HTML
    const html = `
        <html>
        <head>
            <title>Receipt ${order.orderNumber}</title>
            <style>
                body { font-family: 'Helvetica', sans-serif; padding: 40px; color: #333; }
                .header { display: flex; justify-content: space-between; margin-bottom: 40px; border-bottom: 2px solid #eee; padding-bottom: 20px; }
                .logo { font-size: 24px; font-weight: bold; color: #2563eb; }
                .meta { text-align: right; }
                table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
                th { text-align: left; border-bottom: 1px solid #ddd; padding: 10px 0; }
                td { padding: 10px 0; border-bottom: 1px solid #eee; }
                .totals { text-align: right; }
                .totals div { margin-bottom: 5px; }
                .grand-total { font-size: 18px; font-weight: bold; margin-top: 10px; }
                .footer { margin-top: 50px; text-align: center; color: #777; font-size: 12px; }
            </style>
        </head>
        <body>
            <div class="header">
                <div>
                    <div class="logo">ComputerStore</div>
                    <div>123 Tech Street, Silicon Valley</div>
                    <div>contact@computerstore.com</div>
                </div>
                <div class="meta">
                    <h2>RECEIPT</h2>
                    <div><strong>Order #:</strong> ${order.orderNumber}</div>
                    <div><strong>Date:</strong> ${new Date(order.createdAt).toLocaleDateString()}</div>
                    <div><strong>Status:</strong> ${order.status}</div>
                </div>
            </div>

            <div style="margin-bottom: 30px;">
                <strong>Bill To:</strong><br>
                ${order.user ? order.user.name : 'Guest Customer'}<br>
                ${order.user ? order.user.email : ''}
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Qty</th>
                        <th>Price</th>
                        <th style="text-align:right">Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    ${order.items.map(item => `
                        <tr>
                            <td>${item.name}</td>
                            <td>${item.quantity}</td>
                            <td>$${item.price.toFixed(2)}</td>
                            <td style="text-align:right">$${(item.price * item.quantity).toFixed(2)}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>

            <div class="totals">
                <div>Subtotal: $${(order.total / 1.08 - 9.99).toFixed(2)}</div>
                <div>Tax (8%): $${(order.total - (order.total / 1.08)).toFixed(2)}</div>
                <div>Shipping: $9.99</div>
                <div class="grand-total">Total Paid: $${order.total.toFixed(2)}</div>
            </div>

            <div class="footer">
                Thank you for your business!<br>
                For support, visit www.computerstore.com/support
            </div>

            <script>
                window.onload = function() { window.print(); }
            </script>
        </body>
        </html>
    `;

    printWindow.document.write(html);
    printWindow.document.close();
};

// --- Standard Functions ---

function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
    document.getElementById('section-' + sectionId).classList.add('active');
    
    const navItems = document.querySelectorAll('.nav-item');
    if(sectionId === 'orders') navItems[0].classList.add('active');
    if(sectionId === 'inventory') navItems[1].classList.add('active');
    if(sectionId === 'add-product') navItems[2].classList.add('active');
}

async function loadCategories() {
    const res = await fetch('/api/categories');
    const cats = await res.json();
    document.getElementById('pCategory').innerHTML = cats.map(c => `<option value="${c.name}">${c.name}</option>`).join('');
}

async function loadAllOrders() {
    const res = await fetch('/api/orders/all-orders');
    const data = await res.json();
    allOrders = data.orders || [];
    renderOrders(allOrders);
}

function renderOrders(orders) {
    const tbody = document.getElementById('ordersTableBody');
    if(!orders || orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">No orders found.</td></tr>';
        return;
    }
    tbody.innerHTML = orders.map(o => `
        <tr>
            <td><strong>${o.orderNumber}</strong></td>
            <td>${o.user ? o.user.name : 'Guest'}</td>
            <td>$${o.total.toFixed(2)}</td>
            <td><span class="badge badge-pending">${o.status}</span></td>
            <td>${new Date(o.createdAt).toLocaleDateString()}</td>
            <td>
                <!-- PRINT BUTTON -->
                <button onclick="printOrder('${o._id}')" class="action-btn print-btn" title="Print Receipt / Save PDF">
                    <i class="fas fa-print"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

async function loadInventory() {
    const res = await fetch('/api/products');
    allProducts = await res.json();
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
                <button onclick="editProduct('${p._id}')" class="action-btn edit-btn" title="Edit"><i class="fas fa-edit"></i></button>
                <button onclick="deleteProduct('${p._id}')" class="action-btn delete-btn" title="Delete"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

window.editProduct = (id) => {
    const product = allProducts.find(p => p._id === id);
    if (!product) return;
    document.getElementById('pId').value = product._id;
    document.getElementById('pName').value = product.name;
    document.getElementById('pDesc').value = product.description;
    document.getElementById('pPrice').value = product.price;
    document.getElementById('pQty').value = product.quantity;
    document.getElementById('pImage').value = product.image;
    const catVal = product.category && product.category.name ? product.category.name : product.category;
    document.getElementById('pCategory').value = catVal;
    document.getElementById('formTitle').innerText = 'Edit Product';
    document.getElementById('saveBtn').innerHTML = '<i class="fas fa-save"></i> Update Product';
    document.getElementById('cancelBtn').style.display = 'block';
    showSection('add-product');
};

function resetForm() {
    document.getElementById('addProductForm').reset();
    document.getElementById('pId').value = '';
    document.getElementById('formTitle').innerText = 'Add New Product';
    document.getElementById('saveBtn').innerHTML = '<i class="fas fa-plus-circle"></i> Save Product';
    document.getElementById('cancelBtn').style.display = 'none';
}

window.deleteProduct = async (id) => {
    if(!confirm('Are you sure you want to delete?')) return;
    const res = await fetch(`/api/products/${id}`, { method: 'DELETE' });
    const data = await res.json();
    if(data.success) loadInventory();
};
