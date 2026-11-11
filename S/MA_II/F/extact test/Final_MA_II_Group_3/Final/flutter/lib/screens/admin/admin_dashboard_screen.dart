import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '../../models/admin_stats.dart';
import '../../models/order.dart';
import '../../models/product.dart';
import '../../providers/product_provider.dart';
import '../../services/admin_service.dart';
import '../../services/api_client.dart';
import '../../services/product_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/formatters.dart';
import '../../widgets/admin_crud_header.dart';
import '../../widgets/product_image.dart';
import '../../widgets/stock_badge.dart';
import '../product/product_detail_screen.dart';
import 'admin_brands_tab.dart';
import 'admin_categories_tab.dart';
import 'admin_customers_tab.dart';
import 'admin_product_form_screen.dart';

class AdminDashboardScreen extends StatefulWidget {
  const AdminDashboardScreen({super.key});

  @override
  State<AdminDashboardScreen> createState() => _AdminDashboardScreenState();
}

class _AdminDashboardScreenState extends State<AdminDashboardScreen> with SingleTickerProviderStateMixin {
  late final TabController _tabs;
  AdminStats? _stats;
  List<AuditLogEntry> _logs = [];
  List<Product> _inventory = [];
  List<ShopOrder> _orders = [];
  bool _loading = true;
  String? _error;

  AdminService? _admin;
  ProductService? _products;

  @override
  void initState() {
    super.initState();
    _tabs = TabController(length: 6, vsync: this);
    _tabs.addListener(() {
      if (!_tabs.indexIsChanging) setState(() {});
    });
    WidgetsBinding.instance.addPostFrameCallback((_) => _refresh());
  }

  @override
  void dispose() {
    _tabs.dispose();
    super.dispose();
  }

  Future<void> _refresh() async {
    final api = context.read<ApiClient>();
    _admin ??= AdminService(api);
    _products ??= ProductService(api);
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final results = await Future.wait([
        _admin!.fetchStats(),
        _admin!.fetchAuditLogs(),
        _products!.fetchProducts(),
        _admin!.fetchOrders(),
      ]);
      _stats = results[0] as AdminStats;
      _logs = results[1] as List<AuditLogEntry>;
      _inventory = results[2] as List<Product>;
      _orders = results[3] as List<ShopOrder>;
      if (mounted) {
        await context.read<ProductProvider>().loadHomeData();
      }
    } catch (e) {
      _error = e.toString();
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _deleteProduct(Product product) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete product?'),
        content: Text('Remove ${product.name} (${product.sku}) permanently?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancel')),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            style: TextButton.styleFrom(foregroundColor: NexusColors.error),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
    if (confirm != true) return;
    try {
      await _products!.deleteProduct(product.id);
      await _refresh();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Product deleted')));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
      }
    }
  }

  Future<void> _editStock(Product product) async {
    final controller = TextEditingController(text: '${product.stock}');
    final newStock = await showDialog<int>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('Update stock — ${product.sku}'),
        content: TextField(
          controller: controller,
          keyboardType: TextInputType.number,
          decoration: const InputDecoration(labelText: 'Stock quantity'),
          autofocus: true,
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          TextButton(
            onPressed: () => Navigator.pop(ctx, int.tryParse(controller.text.trim())),
            child: const Text('Save'),
          ),
        ],
      ),
    );
    if (newStock == null) return;
    try {
      await _admin!.updateStock(product.id, newStock);
      await _refresh();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final stats = _stats;

    return Scaffold(
      appBar: AppBar(
        title: Text('MMT Admin', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
        actions: [
          IconButton(onPressed: _refresh, icon: const Icon(Icons.refresh)),
        ],
        bottom: TabBar(
          controller: _tabs,
          isScrollable: true,
          tabAlignment: TabAlignment.start,
          labelStyle: GoogleFonts.inter(fontWeight: FontWeight.w700, fontSize: 12),
          tabs: const [
            Tab(text: 'Products'),
            Tab(text: 'Categories'),
            Tab(text: 'Brands'),
            Tab(text: 'Customers'),
            Tab(text: 'Orders'),
            Tab(text: 'Audit'),
          ],
        ),
      ),
      body: _loading || _admin == null
          ? const Center(child: CircularProgressIndicator())
          : Column(
              children: [
                if (_error != null)
                  Padding(
                    padding: const EdgeInsets.all(12),
                    child: Text(_error!, style: const TextStyle(color: NexusColors.error)),
                  ),
                if (stats != null && _tabs.index >= 3)
                  Padding(
                    padding: const EdgeInsets.fromLTRB(16, 12, 16, 4),
                    child: Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: [
                        _StatTile(label: 'Products', value: '${stats.totalProducts}'),
                        _StatTile(label: 'Customers', value: '${stats.totalCustomers}'),
                        _StatTile(label: 'Orders', value: '${stats.totalOrders}'),
                        _StatTile(label: 'Revenue', value: formatMoney(stats.totalRevenue)),
                        _StatTile(label: 'Low stock', value: '${stats.lowStockCount}'),
                        _StatTile(label: 'Pending', value: '${stats.pendingOrders}'),
                      ],
                    ),
                  ),
                Expanded(
                  child: TabBarView(
                    controller: _tabs,
                    children: [
                      _InventoryTab(
                        inventory: _inventory,
                        onAdd: () async {
                          final changed = await Navigator.of(context).push<bool>(
                            MaterialPageRoute(builder: (_) => const AdminProductFormScreen()),
                          );
                          if (changed == true) await _refresh();
                        },
                        onEdit: (p) async {
                          final changed = await Navigator.of(context).push<bool>(
                            MaterialPageRoute(builder: (_) => AdminProductFormScreen(product: p)),
                          );
                          if (changed == true) await _refresh();
                        },
                        onStock: _editStock,
                        onDelete: _deleteProduct,
                        onRefresh: _refresh,
                      ),
                      AdminCategoriesTab(admin: _admin!),
                      AdminBrandsTab(admin: _admin!),
                      AdminCustomersTab(admin: _admin!),
                      _OrdersTab(
                        orders: _orders,
                        onRefresh: _refresh,
                        onUpdateStatus: (order, status) async {
                          final messenger = ScaffoldMessenger.of(context);
                          try {
                            await _admin!.updateOrderStatus(order.id, status);
                            await _refresh();
                            messenger.showSnackBar(
                              SnackBar(content: Text('${order.displayId} → $status')),
                            );
                          } catch (e) {
                            messenger.showSnackBar(SnackBar(content: Text(e.toString())));
                          }
                        },
                      ),
                      _AuditTab(logs: _logs, onRefresh: _refresh),
                    ],
                  ),
                ),
              ],
            ),
    );
  }
}

class _InventoryTab extends StatelessWidget {
  const _InventoryTab({
    required this.inventory,
    required this.onAdd,
    required this.onEdit,
    required this.onStock,
    required this.onDelete,
    required this.onRefresh,
  });

  final List<Product> inventory;
  final Future<void> Function() onAdd;
  final Future<void> Function(Product) onEdit;
  final Future<void> Function(Product) onStock;
  final Future<void> Function(Product) onDelete;
  final Future<void> Function() onRefresh;

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: onRefresh,
      child: ListView(
        padding: const EdgeInsets.only(bottom: 24),
        children: [
          AdminCrudHeader(
            title: 'Products',
            countLabel: '${inventory.length} product${inventory.length == 1 ? '' : 's'}',
            addLabel: 'Add product',
            onAdd: () => onAdd(),
            onRefresh: onRefresh,
          ),
          if (inventory.isEmpty)
            Padding(
              padding: const EdgeInsets.only(top: 40),
              child: Center(
                child: Text('No products yet. Tap Add product.', style: GoogleFonts.inter(color: NexusColors.outlineStrong)),
              ),
            ),
          ...inventory.map(
            (p) => Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 10),
              child: Container(
                padding: const EdgeInsets.all(14),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: NexusColors.outline),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    InkWell(
                      borderRadius: BorderRadius.circular(6),
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => ProductDetailScreen(
                              productId: p.id,
                              initialProduct: p,
                            ),
                          ),
                        );
                      },
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          ClipRRect(
                            borderRadius: BorderRadius.circular(6),
                            child: SizedBox(
                              width: 64,
                              child: ProductImage(url: p.imageUrl, height: 64, fit: BoxFit.cover),
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(p.name, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 15)),
                                const SizedBox(height: 2),
                                Text(
                                  '${p.brand} · ${p.sku}',
                                  style: GoogleFonts.jetBrainsMono(fontSize: 11, color: NexusColors.outlineStrong),
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  formatMoney(p.price),
                                  style: context.monoStyle.copyWith(fontWeight: FontWeight.w700, color: NexusColors.primary),
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  'Tap to view product detail',
                                  style: GoogleFonts.inter(fontSize: 11, color: NexusColors.primary),
                                ),
                              ],
                            ),
                          ),
                          StockBadge(product: p),
                        ],
                      ),
                    ),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        Expanded(
                          child: OutlinedButton.icon(
                            onPressed: () => onStock(p),
                            icon: const Icon(Icons.inventory_2_outlined, size: 16),
                            label: const Text('Stock'),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    AdminActionBar(
                      onEdit: () => onEdit(p),
                      onDelete: () => onDelete(p),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _OrdersTab extends StatelessWidget {
  const _OrdersTab({
    required this.orders,
    required this.onRefresh,
    required this.onUpdateStatus,
  });

  final List<ShopOrder> orders;
  final Future<void> Function() onRefresh;
  final Future<void> Function(ShopOrder order, String status) onUpdateStatus;

  List<String> _nextStatuses(String current) {
    switch (current.toUpperCase()) {
      case 'PENDING':
        return ['DELIVERED', 'PAID', 'PROCESSING', 'SHIPPED', 'CANCELLED'];
      case 'PAID':
        return ['PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];
      case 'PROCESSING':
        return ['SHIPPED', 'DELIVERED', 'CANCELLED'];
      case 'SHIPPED':
        return ['DELIVERED', 'CANCELLED'];
      default:
        return const [];
    }
  }

  Color _statusColor(String status) {
    switch (status.toUpperCase()) {
      case 'PENDING':
        return NexusColors.lowStock;
      case 'PAID':
      case 'PROCESSING':
        return NexusColors.primary;
      case 'SHIPPED':
        return const Color(0xFF0F766E);
      case 'DELIVERED':
        return NexusColors.success;
      case 'CANCELLED':
        return NexusColors.error;
      default:
        return NexusColors.outlineStrong;
    }
  }

  String _statusLabel(String status) {
    switch (status.toUpperCase()) {
      case 'DELIVERED':
        return 'Successful';
      case 'CANCELLED':
        return 'Cancelled';
      case 'PROCESSING':
        return 'Processing';
      case 'SHIPPED':
        return 'Shipped';
      case 'PAID':
        return 'Paid';
      case 'PENDING':
        return 'Pending';
      default:
        return status;
    }
  }

  Future<void> _pickStatus(BuildContext context, ShopOrder order) async {
    final next = _nextStatuses(order.status);
    if (next.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('${order.displayId} is already ${_statusLabel(order.status)}')),
      );
      return;
    }
    final chosen = await showModalBottomSheet<String>(
      context: context,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (ctx) => SafeArea(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(
                'Process ${order.displayId}',
                style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w800, fontSize: 18),
              ),
              const SizedBox(height: 4),
              Text(
                'Current: ${_statusLabel(order.status)}',
                style: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong),
              ),
              const SizedBox(height: 12),
              ...next.map(
                (s) => Padding(
                  padding: const EdgeInsets.only(bottom: 8),
                  child: ListTile(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                      side: BorderSide(color: NexusColors.outline),
                    ),
                    leading: Icon(
                      s == 'DELIVERED'
                          ? Icons.check_circle_outline
                          : s == 'CANCELLED'
                              ? Icons.cancel_outlined
                              : Icons.sync_alt,
                      color: _statusColor(s),
                    ),
                    title: Text(
                      _statusLabel(s),
                      style: GoogleFonts.inter(fontWeight: FontWeight.w600),
                    ),
                    subtitle: Text(s, style: GoogleFonts.jetBrainsMono(fontSize: 11)),
                    onTap: () => Navigator.pop(ctx, s),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
    if (chosen != null) {
      await onUpdateStatus(order, chosen);
    }
  }

  @override
  Widget build(BuildContext context) {
    final pending = orders.where((o) => o.status.toUpperCase() == 'PENDING').length;
    final success = orders.where((o) => o.status.toUpperCase() == 'DELIVERED').length;

    if (orders.isEmpty) {
      return RefreshIndicator(
        onRefresh: onRefresh,
        child: ListView(
          children: [
            const SizedBox(height: 60),
            Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                children: [
                  const Icon(Icons.receipt_long_outlined, size: 40, color: NexusColors.outlineStrong),
                  const SizedBox(height: 12),
                  Text('No customer orders yet', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                  Text(
                    'When customers checkout, orders appear here so you can process them to Successful (Delivered).',
                    textAlign: TextAlign.center,
                    style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong),
                  ),
                ],
              ),
            ),
          ],
        ),
      );
    }

    final sorted = [...orders]..sort((a, b) {
      int rank(String s) {
        switch (s.toUpperCase()) {
          case 'PENDING':
            return 0;
          case 'PAID':
          case 'PROCESSING':
          case 'SHIPPED':
            return 1;
          case 'DELIVERED':
            return 2;
          default:
            return 3;
        }
      }
      final cmp = rank(a.status).compareTo(rank(b.status));
      if (cmp != 0) return cmp;
      return (b.createdAt ?? DateTime(1970)).compareTo(a.createdAt ?? DateTime(1970));
    });

    return RefreshIndicator(
      onRefresh: onRefresh,
      child: ListView.builder(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        itemCount: sorted.length + 1,
        itemBuilder: (context, index) {
          if (index == 0) {
            return Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: Text(
                '${sorted.length} orders · $pending pending · $success successful',
                style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong),
              ),
            );
          }
          final o = sorted[index - 1];
          final status = o.status.toUpperCase();
          final canProcess = _nextStatuses(status).isNotEmpty;
          final isPending = status == 'PENDING';
          return Container(
            margin: const EdgeInsets.only(bottom: 10),
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: NexusColors.outline),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Expanded(
                      child: Text(o.displayId, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                    ),
                    Text(formatMoney(o.total), style: context.monoStyle.copyWith(fontWeight: FontWeight.w700, color: NexusColors.primary)),
                  ],
                ),
                const SizedBox(height: 6),
                Row(
                  children: [
                    const Icon(Icons.person_outline, size: 16, color: NexusColors.primary),
                    const SizedBox(width: 6),
                    Expanded(
                      child: Text(
                        o.customerName ?? 'Customer',
                        style: GoogleFonts.inter(fontWeight: FontWeight.w600),
                      ),
                    ),
                  ],
                ),
                if (o.customerEmail != null)
                  Padding(
                    padding: const EdgeInsets.only(left: 22, top: 2),
                    child: Text(o.customerEmail!, style: context.monoStyle.copyWith(fontSize: 11)),
                  ),
                if (o.shippingAddress != null && o.shippingAddress!.isNotEmpty) ...[
                  const SizedBox(height: 6),
                  Text(
                    o.shippingAddress!,
                    style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong),
                  ),
                ],
                const SizedBox(height: 8),
                Text('Items', style: GoogleFonts.jetBrainsMono(fontSize: 10, color: NexusColors.outlineStrong, letterSpacing: 0.5)),
                const SizedBox(height: 4),
                ...o.items.map(
                  (line) => Padding(
                    padding: const EdgeInsets.only(bottom: 3),
                    child: Row(
                      children: [
                        Expanded(
                          child: Text(
                            '${line.productName} × ${line.quantity}',
                            style: GoogleFonts.inter(fontSize: 13),
                          ),
                        ),
                        Text(formatMoney(line.total), style: context.monoStyle.copyWith(fontSize: 12)),
                      ],
                    ),
                  ),
                ),
                const Divider(height: 16),
                Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: _statusColor(status).withValues(alpha: 0.12),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        _statusLabel(status),
                        style: GoogleFonts.inter(
                          fontSize: 11,
                          fontWeight: FontWeight.w700,
                          color: _statusColor(status),
                        ),
                      ),
                    ),
                    const Spacer(),
                    Text(formatDate(o.createdAt), style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
                  ],
                ),
                const SizedBox(height: 10),
                if (isPending) ...[
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: () => onUpdateStatus(o, 'DELIVERED'),
                          icon: const Icon(Icons.check_circle_outline, size: 18),
                          label: const Text('Confirm complete'),
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: OutlinedButton.icon(
                          onPressed: () => onUpdateStatus(o, 'CANCELLED'),
                          style: OutlinedButton.styleFrom(
                            foregroundColor: NexusColors.error,
                            side: BorderSide(color: NexusColors.error.withValues(alpha: 0.45)),
                          ),
                          icon: const Icon(Icons.cancel_outlined, size: 18),
                          label: const Text('Cancel'),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  TextButton(
                    onPressed: () => _pickStatus(context, o),
                    child: const Text('More status options'),
                  ),
                ] else if (canProcess)
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: () => _pickStatus(context, o),
                      icon: const Icon(Icons.rule_folder_outlined, size: 18),
                      label: const Text('Process order'),
                    ),
                  )
                else
                  Text(
                    status == 'DELIVERED'
                        ? 'Order completed successfully'
                        : 'Order closed — no further actions',
                    style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong),
                  ),
              ],
            ),
          );
        },
      ),
    );
  }
}

class _AuditTab extends StatelessWidget {
  const _AuditTab({required this.logs, required this.onRefresh});

  final List<AuditLogEntry> logs;
  final Future<void> Function() onRefresh;

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: onRefresh,
      child: ListView.builder(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 100),
        itemCount: logs.isEmpty ? 1 : logs.length,
        itemBuilder: (context, index) {
          if (logs.isEmpty) {
            return Padding(
              padding: const EdgeInsets.only(top: 60),
              child: Center(child: Text('No audit entries', style: GoogleFonts.inter(color: NexusColors.outlineStrong))),
            );
          }
          final log = logs[index];
          return Container(
            margin: const EdgeInsets.only(bottom: 8),
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: NexusColors.outline),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(log.action, style: GoogleFonts.inter(fontWeight: FontWeight.w600, fontSize: 13)),
                const SizedBox(height: 4),
                Row(
                  children: [
                    if (log.actor != null)
                      Text(log.actor!, style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
                    const Spacer(),
                    Text(
                      formatDate(log.timestamp),
                      style: context.monoStyle.copyWith(fontSize: 10, color: NexusColors.outlineStrong),
                    ),
                  ],
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}

class _StatTile extends StatelessWidget {
  const _StatTile({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: (MediaQuery.sizeOf(context).width - 48) / 2,
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: NexusColors.outline),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
          Text(value, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
        ],
      ),
    );
  }
}
