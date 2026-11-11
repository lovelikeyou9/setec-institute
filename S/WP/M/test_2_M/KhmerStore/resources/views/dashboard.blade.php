{{-- @extends('master.template')
@section('pageTitle')
    Welcome to My CPanel
@endsection

@section('rightTitle')
    Welcome toKhmerStore Dashboard
@endsection

@section('content')

@endsection --}}


@extends('master.template')
@section('pageTitle', 'Welcome to my CPanel')
@section('rightTitle', 'Welcome toKhmerStore Dashboard')
@section('headerBlock')
    <link rel="stylesheet" href="{{ asset('css/dashboard.css') }}">
@endsection


@section('content')


<!-- Main Content Wrapper -->
<div class="main-wrapper">

    <!-- Top Header -->
    <header class="header">
    <div class="search-box">
        <input type="text" placeholder="Search anything...">
    </div>
    <div class="user-profile">
        <div class="avatar">JD</div>
        <div class="user-info">
        <strong style="font-size: 0.9rem;">John Doe</strong>
        </div>
    </div>
    </header>

    <!-- Main Section -->
    <main class="content">

    <div class="page-title">
        <h1>Overview</h1>
    </div>

    <!-- KPI Metrics Grid -->
    <section class="stats-grid">
        <div class="stat-card">
        <div class="title">Total Revenue</div>
        <div class="value">$45,231.89</div>
        <span class="badge badge-success">+12.5% from last month</span>
        </div>
        <div class="stat-card">
        <div class="title">Total Orders</div>
        <div class="value">1,205</div>
        <span class="badge badge-success">+8.2% from last month</span>
        </div>
        <div class="stat-card">
        <div class="title">Active Users</div>
        <div class="value">3,450</div>
        <span class="badge badge-danger">-1.4% from last week</span>
        </div>
        <div class="stat-card">
        <div class="title">Conversion Rate</div>
        <div class="value">4.8%</div>
        <span class="badge badge-success">+0.6% from last week</span>
        </div>
    </section>

    <!-- Main Layout Grid -->
    <div class="dashboard-grid">

        <!-- Recent Orders Table -->
        <div class="card">
        <div class="card-header">
            <h2 class="card-title">Recent Transactions</h2>
        </div>
        <div class="table-container">
            <table>
            <thead>
                <tr>
                <th>Order ID</th>
                <th>Customer</th>
                <th>Amount</th>
                <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                <td>#ORD-001</td>
                <td>Sarah Jenkins</td>
                <td>$250.00</td>
                <td><span class="status status-completed">Completed</span></td>
                </tr>
                <tr>
                <td>#ORD-002</td>
                <td>Michael Brown</td>
                <td>$80.50</td>
                <td><span class="status status-pending">Pending</span></td>
                </tr>
                <tr>
                <td>#ORD-003</td>
                <td>Alex Rivera</td>
                <td>$420.00</td>
                <td><span class="status status-completed">Completed</span></td>
                </tr>
                <tr>
                <td>#ORD-004</td>
                <td>Emily Chen</td>
                <td>$115.00</td>
                <td><span class="status status-completed">Completed</span></td>
                </tr>
            </tbody>
            </table>
        </div>
        </div>

        <!-- Recent Activity Feed -->
        <div class="card">
        <div class="card-header">
            <h2 class="card-title">Recent Activity</h2>
        </div>
        <ul class="activity-list">
            <li class="activity-item">
            <div class="activity-dot"></div>
            <div class="activity-details">
                <p>New user registered: <strong>@m_brown</strong></p>
                <span>5 minutes ago</span>
            </div>
            </li>
            <li class="activity-item">
            <div class="activity-dot"></div>
            <div class="activity-details">
                <p>Order <strong>#ORD-001</strong> marked as paid</p>
                <span>12 minutes ago</span>
            </div>
            </li>
            <li class="activity-item">
            <div class="activity-dot"></div>
            <div class="activity-details">
                <p>Server backup completed successfully</p>
                <span>1 hour ago</span>
            </div>
            </li>
        </ul>
        </div>

    </div>

    </main>
</div>


@endsection
