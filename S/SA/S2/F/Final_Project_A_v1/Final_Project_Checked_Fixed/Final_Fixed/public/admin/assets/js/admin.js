// Core Dashboard JavaScript Engine for Apex Motors Admin Portal
// Core API & JWT validation
const AdminAPI = {
    getToken: () => localStorage.getItem('token'),
    getUser: () => {
        try {
            return JSON.parse(localStorage.getItem('user'));
        } catch {
            return null;
        }
    },

    validateSession: () => {
        const user = AdminAPI.getUser();
        const token = AdminAPI.getToken();

        if (!token || !user || user.role === 'buyer') {
            console.warn('Access unauthorized or missing session.');
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '../login.html';
            return false;
        }
        return true;
    },
    request: async (endpoint, options = {}) => {
        const token = AdminAPI.getToken();
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
            throw new Error(result.error || 'Server request error');
        }
        return result;
    }
};
// Auto-run security session guard
if (AdminAPI.validateSession()) {
    document.addEventListener('DOMContentLoaded', () => {
        initAdminLayout();
        initDashboard();
        initInventory();
        initBookings();
        initTickets();
        initUsers();
        initReports();
        initAdminNotifications();
    });
}
// Visual Admin layout customizable sidebar navigation
function initAdminLayout() {
    const user = AdminAPI.getUser();
    if (!user) return;

    // Render sidebar profile details
    const profileNameEl = document.getElementById('admin-profile-name');
    const profileRoleEl = document.getElementById('admin-profile-role');
    const avatarTextEl = document.getElementById('admin-avatar-text');

    if (profileNameEl) profileNameEl.innerText = user.name;
    if (profileRoleEl) profileRoleEl.innerText = user.role.toUpperCase();
    if (avatarTextEl) avatarTextEl.innerText = user.name[0].toUpperCase();

    // Customise nav tabs visibility depending on role permissions
    const navContainer = document.getElementById('admin-sidebar-nav');
    if (navContainer) {
        let navHtml = `
      <li class="nav-text-section">Management</li>
      <li><a class="nav-link ${isActive('index.html')}" href="index.html"><i class="ti ti-chart-bar"></i> Dashboard</a></li>
    `;

        if (user.role === 'admin' || user.role === 'staff' || user.role === 'seller') {
            navHtml += `
        <li><a class="nav-link ${isActive('inventory.html')}" href="inventory.html"><i class="ti ti-car"></i> Inventory</a></li>
        <li><a class="nav-link ${isActive('bookings.html')}" href="bookings.html"><i class="ti ti-calendar"></i> Bookings</a></li>
      `;
        }

        if (user.role === 'admin' || user.role === 'staff' || user.role === 'support') {
            navHtml += `
        <li><a class="nav-link ${isActive('tickets.html')}" href="tickets.html"><i class="ti ti-headset"></i> Support Desk</a></li>
      `;
        }

        if (user.role === 'admin') {
            navHtml += `
        <li><a class="nav-link ${isActive('users.html')}" href="users.html"><i class="ti ti-users"></i> Users Setup</a></li>
      `;
        }
        if (user.role === 'admin' || user.role === 'seller') {
            navHtml += `
        <li><a class="nav-link ${isActive('reports.html')}" href="reports.html"><i class="ti ti-file-text"></i> Financials</a></li>
      `;
        }

        navHtml += `
      <li class="nav-text-section">Action</li>
      <li><a class="nav-link" href="../index.html"><i class="ti ti-shopping-cart"></i> Marketplace</a></li>
      <li><a class="nav-link" href="#" onclick="handleAdminSignOut()"><i class="ti ti-logout"></i> Sign Out</a></li>
    `;

        navContainer.innerHTML = navHtml;
    }

}
function isActive(page) {
    return window.location.pathname.endsWith(page) ? 'active' : '';
}
function handleAdminSignOut() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '../login.html';
}
// Global alert toast
function showAdminToast(message, isError = false) {
    let container = document.getElementById('admin-toast');
    if (!container) {
        container = document.createElement('div');
        container.id = 'admin-toast';
        container.style.position = 'fixed';
        container.style.bottom = '24px';
        container.style.right = '24px';
        container.style.zIndex = '3000';
        container.style.background = '#0f172a';
        container.style.borderLeft = '4px solid #06b6d4';
        container.style.padding = '16px 24px';
        container.style.borderRadius = '8px';
        container.style.color = '#fff';
        container.style.boxShadow = '0 10px 30px rgba(0,0,0,0.5)';
        container.style.transform = 'translateY(150%)';
        container.style.transition = 'all 0.3s ease';
        document.body.appendChild(container);
    }

    container.style.borderLeftColor = isError ? '#f43f5e' : '#06b6d4';
    container.innerHTML = `<span style="font-weight:600;">${message}</span>`;

    setTimeout(() => container.style.transform = 'translateY(0)', 50);

    setTimeout(() => {
        container.style.transform = 'translateY(150%)';
    }, 4000);
}
// -------------------------------------------------------------
// Realtime notification bell (polls server, hidden until click)
// -------------------------------------------------------------
let notificationPollTimer = null;
async function initAdminNotifications() {
    const btn = document.getElementById('notificationBtn');
    const menu = document.getElementById('notificationDropdown');
    const countEl = document.getElementById('notificationCount');
    const list = document.getElementById('notificationList');

    if (!btn || !menu || !countEl || !list) return;

    // Keep dropdown closed when page first loads.
    menu.classList.remove('show');
    btn.setAttribute('aria-expanded', 'false');

    const loadNotifications = async () => {
        try {
            const data = await AdminAPI.request('/api/admin/notifications');
            const notifications = Array.isArray(data.notifications) ? data.notifications : [];

            countEl.textContent = notifications.length;
            countEl.style.display = notifications.length ? 'inline-block' : 'none';

            list.innerHTML = notifications.length ? notifications.map(item => `
                <li class="notification-item">
                    <div class="fw-bold"><i class="${item.icon || 'ti ti-bell'} me-2 text-info"></i>${item.title}</div>
                    <div class="small text-muted">${item.message}</div>
                </li>
            `).join('') : `
                <li class="notification-item">
                    <div class="fw-bold">No notifications</div>
                    <div class="small text-muted">Everything is up to date.</div>
                </li>
            `;
        } catch (err) {
            console.error('Notification sync failed', err);
        }
    };

    await loadNotifications();

    if (notificationPollTimer) clearInterval(notificationPollTimer);
    notificationPollTimer = setInterval(loadNotifications, 5000);
}

// -------------------------------------------------------------
// 1. Dashboard Metrics & ApexCharts Visuals
// -------------------------------------------------------------
async function initDashboard() {
    const container = document.getElementById('dashboard-metrics-container');
    if (!container) return;

    const user = AdminAPI.getUser();

    try {
        const data = await AdminAPI.request('/api/reports/dashboard');

        // Set database notice in header title
        const headerTitle = document.querySelector('h1.fs-3');
        if (headerTitle) {
            headerTitle.innerHTML = `Dashboard <small style="font-size:12px; font-weight:normal;" class="badge badge-available">Engine: ${data.dbMode}</small>`;
        }

        // Render dynamic visual cards depending on roles
        let cardsHtml = '';
        const stats = data.stats;

        if (user.role === 'admin' || user.role === 'staff') {
            cardsHtml = `
        <div class="col-lg-3 col-md-6 col-12">
          <div class="card p-4 bg-primary bg-opacity-10 rounded-2">
            <div class="d-flex gap-3 align-items-center">
              <div class="icon-shape bg-primary rounded-2 text-dark"><i class="ti ti-report-analytics"></i></div>
              <div>
                <h2 style="font-size:13px; color:hsl(var(--text-muted)); font-weight:600; margin-bottom:4px;">Platform Revenue</h2>
                <h3 class="fw-bold mb-0">$${stats.totalSales.toLocaleString()}</h3>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-3 col-md-6 col-12">
          <div class="card p-4 bg-success bg-opacity-10 rounded-2">
            <div class="d-flex gap-3 align-items-center">
              <div class="icon-shape bg-success rounded-2 text-white"><i class="ti ti-box-seam"></i></div>
              <div>
                <h2 style="font-size:13px; color:hsl(var(--text-muted)); font-weight:600; margin-bottom:4px;">Total Showroom Cars</h2>
                <h3 class="fw-bold mb-0">${stats.carsCount}</h3>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-3 col-md-6 col-12">
          <div class="card p-4 bg-info bg-opacity-10 rounded-2">
            <div class="d-flex gap-3 align-items-center">
              <div class="icon-shape bg-info rounded-2 text-white"><i class="ti ti-calendar"></i></div>
              <div>
                <h2 style="font-size:13px; color:hsl(var(--text-muted)); font-weight:600; margin-bottom:4px;">Visit Appts</h2>
                <h3 class="fw-bold mb-0">${stats.bookingsCount}</h3>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-3 col-md-6 col-12">
          <div class="card p-4 bg-warning bg-opacity-10 rounded-2">
            <div class="d-flex gap-3 align-items-center">
              <div class="icon-shape bg-warning rounded-2 text-white"><i class="ti ti-headset"></i></div>
              <div>
                <h2 style="font-size:13px; color:hsl(var(--text-muted)); font-weight:600; margin-bottom:4px;">Active Support Tickets</h2>
                <h3 class="fw-bold mb-0">${stats.ticketsCount}</h3>
              </div>
            </div>
          </div>
        </div>
      `;
        } else if (user.role === 'seller') {
            cardsHtml = `
        <div class="col-lg-4 col-md-6 col-12">
          <div class="card p-4 bg-primary bg-opacity-10 rounded-2">
            <div class="d-flex gap-3 align-items-center">
              <div class="icon-shape bg-primary rounded-2 text-dark"><i class="ti ti-coin"></i></div>
              <div>
                <h2 style="font-size:13px; color:hsl(var(--text-muted)); font-weight:600; margin-bottom:4px;">My Total Sales</h2>
                <h3 class="fw-bold mb-0">$${stats.totalSales.toLocaleString()}</h3>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-4 col-md-6 col-12">
          <div class="card p-4 bg-success bg-opacity-10 rounded-2">
            <div class="d-flex gap-3 align-items-center">
              <div class="icon-shape bg-success rounded-2 text-white"><i class="ti ti-car"></i></div>
              <div>
                <h2 style="font-size:13px; color:hsl(var(--text-muted)); font-weight:600; margin-bottom:4px;">My Listed Vehicles</h2>
                <h3 class="fw-bold mb-0">${stats.carsCount}</h3>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-4 col-md-12 col-12">
          <div class="card p-4 bg-info bg-opacity-10 rounded-2">
            <div class="d-flex gap-3 align-items-center">
              <div class="icon-shape bg-info rounded-2 text-white"><i class="ti ti-calendar"></i></div>
              <div>
                <h2 style="font-size:13px; color:hsl(var(--text-muted)); font-weight:600; margin-bottom:4px;">Customer Test Drives</h2>
                <h3 class="fw-bold mb-0">${stats.bookingsCount}</h3>
              </div>
            </div>
          </div>
        </div>
      `;
        } else if (user.role === 'support') {
            cardsHtml = `
        <div class="col-lg-12 col-12">
          <div class="card p-4 bg-warning bg-opacity-10 rounded-2">
            <div class="d-flex gap-3 align-items-center">
              <div class="icon-shape bg-warning rounded-2 text-white"><i class="ti ti-headset"></i></div>
              <div>
                <h2 style="font-size:13px; color:hsl(var(--text-muted)); font-weight:600; margin-bottom:4px;">Inbound Active Tickets</h2>
                <h3 class="fw-bold mb-0">${stats.ticketsCount} Outstanding Inquiries</h3>
              </div>
            </div>
          </div>
        </div>
      `;
        }

        container.innerHTML = cardsHtml;

        // Initialize ApexCharts Column dynamic graphs
        const salesChartEl = document.getElementById('salesPurchaseChart');
        if (salesChartEl && window.ApexCharts) {
            salesChartEl.innerHTML = '';

            const salesData = data.salesChart;
            const options = {
                series: [
                    { name: 'Showroom Revenue', data: salesData.sales },
                    { name: 'Inventory Expense', data: salesData.expenses }
                ],
                chart: {
                    type: 'bar',
                    height: 320,
                    background: 'transparent',
                    theme: 'dark',
                    toolbar: { show: false }
                },
                colors: ['#06b6d4', '#4f46e5'],
                plotOptions: {
                    bar: { borderRadius: 4, columnWidth: '45%' }
                },
                dataLabels: { enabled: false },
                stroke: { show: true, width: 2, colors: ['transparent'] },
                xaxis: {
                    categories: salesData.categories,
                    labels: { style: { colors: '#94a3b8' } }
                },
                yaxis: {
                    labels: {
                        style: { colors: '#94a3b8' },
                        formatter: (val) => '$' + val.toLocaleString()
                    }
                },
                fill: { opacity: 1 },
                legend: { labels: { colors: '#f8fafc' } },
                grid: { borderColor: 'rgba(255,255,255,0.06)' }
            };

            const chart = new ApexCharts(salesChartEl, options);
            chart.render();
        }

        // Initialize Circular Donut chart
        const customerChartEl = document.getElementById('customerChart');
        if (customerChartEl && window.ApexCharts) {
            customerChartEl.innerHTML = '';

            const demoData = data.demographics;
            const options = {
                series: demoData.data,
                chart: {
                    type: 'donut',
                    height: 250,
                    background: 'transparent'
                },
                labels: demoData.labels,
                colors: ['#06b6d4', '#3b82f6', '#8b5cf6', '#ec4899', '#f59e0b'],
                legend: { show: false },
                dataLabels: { enabled: false },
                plotOptions: {
                    pie: {
                        donut: {
                            size: '75%',
                            labels: {
                                show: true,
                                total: {
                                    show: true,
                                    label: 'Inquiries',
                                    color: '#94a3b8',
                                    formatter: () => '87%'
                                }
                            }
                        }
                    }
                }
            };

            const chart = new ApexCharts(customerChartEl, options);
            chart.render();
        }

    } catch (err) {
        console.error('Failed dashboard synchronization', err);
    }
}
// -------------------------------------------------------------
// 2. Inventory Management Console (CRUD)
// -------------------------------------------------------------
let selectedCarEditId = null;
async function initInventory() {
    const tableBody = document.getElementById('admin-inventory-tbody');
    if (!tableBody) return;

    try {
        tableBody.innerHTML = '<tr><td colspan="7" class="text-center">Syncing showroom inventory...</td></tr>';

        const user = AdminAPI.getUser();
        const endpoint = user.role === 'seller' ? `/api/cars?seller_id=${user.id}` : '/api/cars';
        const cars = await AdminAPI.request(endpoint);

        if (cars.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7" class="text-center">No vehicles listed. Click "Add Vehicle" to register.</td></tr>';
            return;
        }

        tableBody.innerHTML = cars.map(c => `
      <tr>
        <td>
          <div class="d-flex align-items-center gap-3">
            <img src="${c.image_url || '/uploads/tesla_s.svg'}" alt="${c.make} ${c.model}" onerror="this.src='/uploads/tesla_s.svg'" class="vehicle-thumb">
            <div>
              <div class="fw-bold">${c.make} ${c.model}</div>
              <small class="text-muted">${c.condition} • Year ${c.year}</small>
            </div>
          </div>
        </td>
        <td>$${parseFloat(c.price).toLocaleString()}</td>
        <td>${c.mileage.toLocaleString()} mi</td>
        <td>${c.transmission}</td>
        <td>${c.fuel_type}</td>
        <td>
          <span class="badge ${c.status === 'Available' ? 'badge-available' : (c.status === 'Reserved' ? 'badge-reserved' : 'badge-sold')}">${c.status}</span>
        </td>
        <td>
          <div class="d-flex gap-2">
            <button onclick="handleEditCarModalOpen(${c.id})" class="btn btn-secondary btn-sm" style="padding:4px 8px; font-size:12px;"><i class="ti ti-edit"></i> Edit</button>
            <button onclick="handleDeleteCar(${c.id})" class="btn btn-danger btn-sm" style="padding:4px 8px; font-size:12px;"><i class="ti ti-trash"></i> Delete</button>
          </div>
        </td>
      </tr>
    `).join('');
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
// Handle Add Car Form Submission
const carSubmitForm = document.getElementById('admin-car-form');
if (carSubmitForm) {
    carSubmitForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const make = document.getElementById('car-make').value;
        const model = document.getElementById('car-model').value;
        const year = document.getElementById('car-year').value;
        const price = document.getElementById('car-price').value;
        const mileage = document.getElementById('car-mileage').value;
        const transmission = document.getElementById('car-transmission').value;
        const fuel_type = document.getElementById('car-fuel').value;
        const body_type = document.getElementById('car-body').value;
        const condition = document.getElementById('car-condition').value;
        const description = document.getElementById('car-desc').value;

        const imageFile = document.getElementById('car-image-file').files[0];
        const imageUrlInput = document.getElementById('car-image-url');

        let imageUrl = imageUrlInput ? imageUrlInput.value.trim() : '';

        try {
            // 1. Process image upload if selected. Uploaded file has priority over URL.
            if (imageFile) {
                const formData = new FormData();
                formData.append('image', imageFile);

                const uploadRes = await fetch('/api/cars/upload', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${AdminAPI.getToken()}`
                    },
                    body: formData
                });
                const uploadData = await uploadRes.json();
                if (!uploadRes.ok) throw new Error(uploadData.error || 'Upload error');
                imageUrl = uploadData.imageUrl;
            }

            const payload = {
                make, model, year, price, mileage, transmission, fuel_type, body_type, condition, description,
                image_url: imageUrl
            };

            if (selectedCarEditId) {
                // Update Action
                payload.status = document.getElementById('car-status-row').style.display !== 'none'
                    ? document.getElementById('car-status').value
                    : 'Available';

                await AdminAPI.request(`/api/cars/${selectedCarEditId}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
                showAdminToast('Vehicle updated successfully!');
            } else {
                // Create Action
                await AdminAPI.request('/api/cars', {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
                showAdminToast('Vehicle registered to showroom catalog!');
            }

            // Close Bootstrap modal programmatically
            const modalEl = document.getElementById('addCarModal');
            if (modalEl && window.bootstrap) {
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
            }

            // Reset variables & reload list
            selectedCarEditId = null;
            carSubmitForm.reset();
            initInventory();
        } catch (err) {
            showAdminToast(err.message, true);
        }
    });
}
function handleAddCarModalOpen() {
    selectedCarEditId = null;
    const form = document.getElementById('admin-car-form');
    if (form) form.reset();

    const title = document.getElementById('carModalLabel');
    if (title) title.innerText = 'Register Showroom Vehicle';

    // Hide status selector for brand new listings
    const statusRow = document.getElementById('car-status-row');
    if (statusRow) statusRow.style.display = 'none';
}
async function handleEditCarModalOpen(carId) {
    selectedCarEditId = carId;
    const title = document.getElementById('carModalLabel');
    if (title) title.innerText = 'Modify Vehicle Specifications';

    const statusRow = document.getElementById('car-status-row');
    if (statusRow) statusRow.style.display = 'block';

    try {
        const car = await AdminAPI.request(`/api/cars/${carId}`);

        document.getElementById('car-make').value = car.make;
        document.getElementById('car-model').value = car.model;
        document.getElementById('car-year').value = car.year;
        document.getElementById('car-price').value = car.price;
        document.getElementById('car-mileage').value = car.mileage;
        document.getElementById('car-transmission').value = car.transmission;
        document.getElementById('car-fuel').value = car.fuel_type;
        document.getElementById('car-body').value = car.body_type;
        document.getElementById('car-condition').value = car.condition;
        document.getElementById('car-desc').value = car.description || '';
        const imageUrlInput = document.getElementById('car-image-url');
        if (imageUrlInput) imageUrlInput.value = car.image_url || '';
        document.getElementById('car-status').value = car.status;

        // Open Bootstrap modal
        const modalEl = document.getElementById('addCarModal');
        if (modalEl && window.bootstrap) {
            const modal = new bootstrap.Modal(modalEl);
            modal.show();
        }
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
async function handleDeleteCar(carId) {
    if (!confirm('Are you absolutely certain you want to purge this vehicle listing?')) return;
    try {
        const res = await AdminAPI.request(`/api/cars/${carId}`, { method: 'DELETE' });
        showAdminToast(res.message);
        initInventory();
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
// -------------------------------------------------------------
// 3. Bookings Scheduling Panel (Staff/Seller Approval)
// -------------------------------------------------------------
async function initBookings() {
    const tableBody = document.getElementById('admin-bookings-tbody');
    if (!tableBody) return;

    try {
        tableBody.innerHTML = '<tr><td colspan="7" class="text-center">Syncing appointments...</td></tr>';

        const user = AdminAPI.getUser();
        const endpoint = user.role === 'seller' ? `/api/bookings?seller_id=${user.id}` : '/api/bookings';
        const bookings = await AdminAPI.request(endpoint);

        if (bookings.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7" class="text-center">No visits scheduled.</td></tr>';
            return;
        }

        tableBody.innerHTML = bookings.map(b => {
            let actionButtons = '';
            if (b.status === 'Pending') {
                actionButtons = `
          <button onclick="handleUpdateBookingStatus(${b.id}, 'Approved')" class="btn btn-success btn-sm" style="padding:4px 8px; font-size:12px;"><i class="ti ti-check"></i> Approve</button>
          <button onclick="handleUpdateBookingStatus(${b.id}, 'Cancelled')" class="btn btn-danger btn-sm" style="padding:4px 8px; font-size:12px;"><i class="ti ti-x"></i> Cancel</button>
        `;
            } else if (b.status === 'Approved') {
                actionButtons = `
          <button onclick="handleUpdateBookingStatus(${b.id}, 'Completed')" class="btn btn-primary btn-sm" style="padding:4px 8px; font-size:12px;"><i class="ti ti-circle-check"></i> Complete</button>
          <button onclick="handleUpdateBookingStatus(${b.id}, 'Cancelled')" class="btn btn-danger btn-sm" style="padding:4px 8px; font-size:12px;"><i class="ti ti-x"></i> Cancel</button>
        `;
            } else {
                actionButtons = `<small class="text-muted">-</small>`;
            }

            const badgeClass = b.status === 'Approved' ? 'badge-available' : (b.status === 'Pending' ? 'badge-reserved' : 'badge-sold');

            return `
        <tr>
          <td>
            <strong>${b.make} ${b.model}</strong>
            <small class="d-block text-muted">Ref #${b.car_id} • Year ${b.year}</small>
          </td>
          <td>
            <strong>${b.buyer_name}</strong>
            <small class="d-block text-muted">${b.buyer_email}</small>
          </td>
          <td>${new Date(b.booking_date).toLocaleString()}</td>
          <td><span class="badge ${b.type === 'Reservation' ? 'badge-available' : 'badge-reserved'}">${b.type}</span></td>
          <td><span class="badge ${badgeClass}">${b.status}</span></td>
          <td><small>${b.notes || '-'}</small></td>
          <td>
            <div class="d-flex gap-2">${actionButtons}</div>
          </td>
        </tr>
      `;
        }).join('');
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
async function handleUpdateBookingStatus(bookingId, status) {
    try {
        const res = await AdminAPI.request(`/api/bookings/${bookingId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });
        showAdminToast(res.message);
        initBookings();
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
// -------------------------------------------------------------
// 4. Support Desk Ticketing Hub
// -------------------------------------------------------------
let selectedAdminTicketId = null;
async function initTickets() {
    const listContainer = document.getElementById('admin-tickets-list');
    if (!listContainer) return;

    try {
        listContainer.innerHTML = '<div>Syncing ticketing feed...</div>';
        const tickets = await AdminAPI.request('/api/tickets');

        if (tickets.length === 0) {
            listContainer.innerHTML = '<div class="text-center p-4 text-muted">No client support tickets.</div>';
            return;
        }

        listContainer.innerHTML = tickets.map(t => {
            const badgeClass = t.status === 'Open' ? 'badge-reserved' : (t.status === 'In Progress' ? 'badge-available' : 'badge-sold');
            return `
        <div onclick="openAdminSupportChat(${t.id})" class="p-3 border-bottom border-secondary border-opacity-25" style="cursor:pointer; transition:all 0.2s ease;">
          <div class="d-flex justify-content-between align-items-start">
            <strong>${t.subject}</strong>
            <span class="badge ${badgeClass}" style="font-size:10px;">${t.status}</span>
          </div>
          <small class="d-block text-muted mt-1">Client: ${t.buyer_name} • Ref #${t.id}</small>
        </div>
      `;
        }).join('');
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
async function openAdminSupportChat(ticketId) {
    selectedAdminTicketId = ticketId;
    const pane = document.getElementById('admin-chat-pane');
    const chatBox = document.getElementById('admin-chat-box');
    if (!pane || !chatBox) return;

    pane.style.display = 'block';
    chatBox.innerHTML = '<div>Loading conversation...</div>';

    try {
        const { ticket, messages } = await AdminAPI.request(`/api/tickets/${ticketId}`);

        const subjectEl = document.getElementById('admin-chat-subject');
        if (subjectEl) subjectEl.innerText = `${ticket.subject} (Client: ${ticket.buyer_name})`;

        // Swap resolving visibility
        const resolveBtn = document.getElementById('admin-resolve-ticket-btn');
        if (resolveBtn) {
            if (ticket.status === 'Resolved') {
                resolveBtn.style.display = 'none';
            } else {
                resolveBtn.style.display = 'block';
                resolveBtn.onclick = () => handleResolveTicket(ticketId);
            }
        }

        chatBox.innerHTML = messages.map(m => {
            // Sent by me if NOT buyer (for support desk portal, sent by support/staff/admin is me)
            const isSentByMe = m.sender_role !== 'buyer';
            return `
        <div class="chat-message ${isSentByMe ? 'sent' : 'received'}" style="max-width:80%; padding:10px 14px; border-radius:12px; margin-bottom:12px; align-self:${isSentByMe ? 'flex-end' : 'flex-start'}; background:${isSentByMe ? 'linear-gradient(135deg, #06b6d4 0%, #4f46e5 100%)' : '#1e293b'}; color:${isSentByMe ? '#000' : '#fff'}; font-weight:${isSentByMe ? '500' : '400'}; border-bottom-${isSentByMe ? 'right' : 'left'}-radius:2px;">
          <strong>${m.sender_name} (${m.sender_role.toUpperCase()})</strong>
          <p class="mb-0 mt-1">${m.message}</p>
          <small class="d-block text-end mt-1" style="font-size:9px; opacity:0.6;">${new Date(m.created_at).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</small>
        </div>
      `;
        }).join('');

        chatBox.scrollTop = chatBox.scrollHeight;
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
// Message send form
const adminChatForm = document.getElementById('admin-chat-send-form');
if (adminChatForm) {
    adminChatForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const input = document.getElementById('admin-chat-input');
        if (!input || !input.value.trim() || !selectedAdminTicketId) return;

        const text = input.value.trim();
        input.value = '';

        try {
            await AdminAPI.request(`/api/tickets/${selectedAdminTicketId}/messages`, {
                method: 'POST',
                body: JSON.stringify({ message: text })
            });
            openAdminSupportChat(selectedAdminTicketId);
            initTickets(); // Refresh lists
        } catch (err) {
            showAdminToast(err.message, true);
        }
    });
}
async function handleResolveTicket(ticketId) {
    try {
        const res = await AdminAPI.request(`/api/tickets/${ticketId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status: 'Resolved' })
        });
        showAdminToast(res.message);
        initTickets();
        document.getElementById('admin-chat-pane').style.display = 'none';
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
// -------------------------------------------------------------
// 5. System User Administration Accounts CRUD (Admin only)
// -------------------------------------------------------------
let selectedUserEditId = null;
async function initUsers() {
    const tableBody = document.getElementById('admin-users-tbody');
    if (!tableBody) return;

    try {
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center">Syncing user profiles...</td></tr>';
        const users = await AdminAPI.request('/api/users');

        tableBody.innerHTML = users.map(u => `
      <tr>
        <td>${u.id}</td>
        <td><strong>${u.name}</strong></td>
        <td>${u.email}</td>
        <td><span class="badge ${u.role === 'admin' ? 'badge-sold' : (u.role === 'buyer' ? 'badge-reserved' : 'badge-available')}">${u.role.toUpperCase()}</span></td>
        <td>
          <div class="d-flex gap-2">
            <button onclick="handleEditUserModalOpen(${u.id})" class="btn btn-secondary btn-sm" style="padding:4px 8px; font-size:12px;"><i class="ti ti-edit"></i> Role</button>
            <button onclick="handleDeleteUser(${u.id})" class="btn btn-danger btn-sm" style="padding:4px 8px; font-size:12px;"><i class="ti ti-trash"></i> Delete</button>
          </div>
        </td>
      </tr>
    `).join('');
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
const userSubmitForm = document.getElementById('admin-user-editor-form');
if (userSubmitForm) {
    userSubmitForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const name = document.getElementById('user-name').value;
        const email = document.getElementById('user-email').value;
        const password = document.getElementById('user-password').value;
        const role = document.getElementById('user-role').value;

        try {
            if (selectedUserEditId) {
                // Edit
                await AdminAPI.request(`/api/users/${selectedUserEditId}`, {
                    method: 'PUT',
                    body: JSON.stringify({ name, email, role })
                });
                showAdminToast('User profile updated successfully');
            } else {
                // Create
                if (!password) return showAdminToast('Password is required for new accounts', true);
                await AdminAPI.request('/api/users', {
                    method: 'POST',
                    body: JSON.stringify({ name, email, password, role })
                });
                showAdminToast('System user created successfully');
            }

            const modalEl = document.getElementById('addUserModal');
            if (modalEl && window.bootstrap) {
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
            }

            selectedUserEditId = null;
            userSubmitForm.reset();
            initUsers();
        } catch (err) {
            showAdminToast(err.message, true);
        }
    });
}
function handleAddUserModalOpen() {
    selectedUserEditId = null;
    const form = document.getElementById('admin-user-editor-form');
    if (form) form.reset();

    const title = document.getElementById('userModalLabel');
    if (title) title.innerText = 'Register System Account';

    const pwdRow = document.getElementById('user-password-row');
    if (pwdRow) pwdRow.style.display = 'block';
}
async function handleEditUserModalOpen(userId) {
    selectedUserEditId = userId;
    const title = document.getElementById('userModalLabel');
    if (title) title.innerText = 'Modify User Account Role';

    const pwdRow = document.getElementById('user-password-row');
    if (pwdRow) pwdRow.style.display = 'none';

    try {
        // We can fetch from API users directly or map local. But standard getById is safe:
        const users = await AdminAPI.request('/api/users');
        const user = users.find(u => u.id === userId);

        if (user) {
            document.getElementById('user-name').value = user.name;
            document.getElementById('user-email').value = user.email;
            document.getElementById('user-role').value = user.role;

            const modalEl = document.getElementById('addUserModal');
            if (modalEl && window.bootstrap) {
                const modal = new bootstrap.Modal(modalEl);
                modal.show();
            }
        }
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
async function handleDeleteUser(userId) {
    if (!confirm('Are you absolutely certain you want to purge this system account?')) return;
    try {
        const res = await AdminAPI.request(`/api/users/${userId}`, { method: 'DELETE' });
        showAdminToast(res.message);
        initUsers();
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
// -------------------------------------------------------------
// 6. Platform Financial Receipts Ledger (Reports Page)
// -------------------------------------------------------------
async function initReports() {
    const tableBody = document.getElementById('admin-reports-tbody');
    if (!tableBody) return;

    try {
        tableBody.innerHTML = '<tr><td colspan="6" class="text-center">Compiling billing ledgers...</td></tr>';
        const orders = await AdminAPI.request('/api/orders');

        if (orders.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="6" class="text-center">No platform transactions logged.</td></tr>';
            return;
        }

        tableBody.innerHTML = orders.map(o => `
      <tr>
        <td>#TRX-${o.id}</td>
        <td><strong>${o.make} ${o.model}</strong></td>
        <td>
          <strong>${o.buyer_name}</strong>
          <small class="d-block text-muted">${o.buyer_email}</small>
        </td>
        <td>$${parseFloat(o.price_paid).toLocaleString()}</td>
        <td>${o.payment_method}</td>
        <td>${new Date(o.created_at).toLocaleDateString()}</td>
      </tr>
    `).join('');

        const totalEarnings = orders.reduce((sum, o) => sum + parseFloat(o.price_paid), 0);
        const summaryCard = document.getElementById('report-revenue-summary');
        if (summaryCard) {
            summaryCard.innerText = `$${totalEarnings.toLocaleString()}`;
        }
    } catch (err) {
        showAdminToast(err.message, true);
    }
}
