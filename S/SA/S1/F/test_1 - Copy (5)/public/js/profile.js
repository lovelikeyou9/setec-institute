document.addEventListener("DOMContentLoaded", async () => {
    
    // 1. Check if user is logged in
    const userRes = await fetch('/api/auth/me');
    const userData = await userRes.json();

    if (!userData.success) {
        // If not logged in, force them to login page
        window.location.href = '/login';
        return;
    }

    // 2. Update Profile Info on the Sidebar
    document.getElementById('profileName').innerText = userData.user.name;
    // We display "Member" for email to keep it simple, or you can fetch the email if your API sends it
    document.getElementById('profileEmail').innerText = "Verified Member"; 

    // 3. Load the Orders
    loadOrders();

    // 4. Handle Logout Button
    document.getElementById('logoutBtn').addEventListener('click', async (e) => {
        e.preventDefault();
        if(confirm("Are you sure you want to logout?")) {
            await fetch('/api/auth/logout', { method: 'POST' });
            window.location.href = '/';
        }
    });
});

async function loadOrders() {
    const container = document.getElementById('orders-container');
    
    try {
        const res = await fetch('/api/orders/my-orders');
        const data = await res.json();

        // If no orders found
        if (!data.success || !data.orders || data.orders.length === 0) {
            container.innerHTML = `
                <div style="text-align: center; padding: 40px; background: white; border-radius: 12px; border: 1px solid #e2e8f0;">
                    <i class="fas fa-shopping-bag" style="font-size: 40px; color: #cbd5e1; margin-bottom: 15px;"></i>
                    <h3 style="color: #64748b;">No orders yet</h3>
                    <p style="color: #94a3b8; margin-bottom: 20px;">You haven't placed any orders yet.</p>
                    <a href="/products" class="btn btn-primary">Start Shopping</a>
                </div>
            `;
            return;
        }

        // Generate HTML for each order
        container.innerHTML = data.orders.map(order => `
            <div style="background: white; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 1px solid #f1f5f9; padding-bottom: 15px; margin-bottom: 15px;">
                    <div>
                        <div style="font-weight: 700; font-size: 1.1rem; color: #1e293b;">${order.orderNumber}</div>
                        <div style="color: #64748b; font-size: 0.9rem;">
                            <i class="far fa-calendar"></i> ${new Date(order.createdAt).toLocaleDateString()} 
                            &nbsp;•&nbsp; 
                            <i class="far fa-clock"></i> ${new Date(order.createdAt).toLocaleTimeString()}
                        </div>
                    </div>
                    <span style="background: #dbeafe; color: #1e40af; padding: 5px 12px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">
                        ${order.status || 'Processing'}
                    </span>
                </div>

                <div style="margin-bottom: 15px;">
                    ${order.items.map(item => `
                        <div style="display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 0.95rem;">
                            <div style="display:flex; align-items:center; gap: 10px;">
                                <span style="background:#f1f5f9; width:24px; height:24px; display:flex; align-items:center; justify-content:center; border-radius:4px; font-size:0.8rem; font-weight:600;">${item.quantity}</span>
                                <span style="color: #334155;">${item.name}</span>
                            </div>
                            <span style="color: #475569;">$${item.price.toFixed(2)}</span>
                        </div>
                    `).join('')}
                </div>

                <div style="display: flex; justify-content: space-between; align-items: center; padding-top: 15px; border-top: 1px solid #f1f5f9;">
                    <div style="font-size: 0.9rem; color: #64748b;">
                        Payment: ${order.paymentMethod || 'Credit Card'}
                    </div>
                    <div style="font-size: 1.2rem; font-weight: 700; color: #2563eb;">
                        Total: $${order.total.toFixed(2)}
                    </div>
                </div>
            </div>
        `).join('');

    } catch (error) {
        console.error("Error loading orders:", error);
        container.innerHTML = '<p style="color:red; text-align:center;">Failed to load order history. Please try again later.</p>';
    }
}
