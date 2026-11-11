import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '../../config/api_config.dart';
import '../../models/order.dart';
import '../../providers/auth_provider.dart';
import '../../providers/cart_provider.dart';
import '../../providers/wishlist_provider.dart';
import '../../services/api_client.dart';
import '../../services/order_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/formatters.dart';
import '../admin/admin_dashboard_screen.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => ProfileScreenState();
}

class ProfileScreenState extends State<ProfileScreen> {
  List<ShopOrder> _orders = [];
  bool _loadingOrders = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => reload());
  }

  /// Called when customer opens Profile tab so status updates after admin confirms.
  Future<void> reload() async {
    if (!mounted) return;
    await Future.wait([
      _loadOrders(),
      context.read<CartProvider>().refresh(),
      context.read<WishlistProvider>().refresh(),
    ]);
  }

  Future<void> _loadOrders() async {
    setState(() => _loadingOrders = true);
    try {
      final api = context.read<ApiClient>();
      _orders = await OrderService(api).fetchOrders();
    } catch (_) {
      _orders = [];
    } finally {
      if (mounted) setState(() => _loadingOrders = false);
    }
  }

  Future<void> _refreshAll() => reload();

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();
    final cart = context.watch<CartProvider>();
    final wishlist = context.watch<WishlistProvider>();
    final user = auth.user;
    // Lifetime spend excludes cancelled orders.
    final totalSpent = _orders
        .where((o) => o.status.toUpperCase() != 'CANCELLED')
        .fold<double>(0, (sum, o) => sum + o.total);

    return Scaffold(
      appBar: AppBar(
        title: Text(
          user?.isAdmin == true ? 'MMT Admin profile' : 'My MMT account',
          style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700),
        ),
      ),
      body: user == null
          ? const Center(child: Text('Not signed in'))
          : RefreshIndicator(
              onRefresh: _refreshAll,
              child: ListView(
                padding: const EdgeInsets.fromLTRB(16, 8, 16, 32),
                children: [
                  _ProfileHeader(userName: user.fullName, email: user.email, username: user.username, avatarUrl: user.avatarUrl, role: user.role),
                  const SizedBox(height: 14),
                  if (user.isAdmin)
                    _AdminShortcut(
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(builder: (_) => const AdminDashboardScreen()),
                        );
                      },
                    )
                  else ...[
                    Text('Your activity', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Expanded(
                          child: _MetricTile(
                            label: 'In cart',
                            value: '${cart.itemCount}',
                            icon: Icons.shopping_cart_outlined,
                          ),
                        ),
                        const SizedBox(width: 8),
                        Expanded(
                          child: _MetricTile(
                            label: 'Favorites',
                            value: '${wishlist.items.length}',
                            icon: Icons.favorite_border,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Expanded(
                          child: _MetricTile(
                            label: 'Orders',
                            value: '${_orders.length}',
                            icon: Icons.receipt_long_outlined,
                          ),
                        ),
                        const SizedBox(width: 8),
                        Expanded(
                          child: _MetricTile(
                            label: 'Total spent',
                            value: formatMoney(totalSpent),
                            icon: Icons.payments_outlined,
                            emphasize: true,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(14),
                      decoration: BoxDecoration(
                        color: NexusColors.primary.withValues(alpha: 0.06),
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: NexusColors.primary.withValues(alpha: 0.25)),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text('LIFETIME SPEND', style: GoogleFonts.jetBrainsMono(fontSize: 10, color: NexusColors.primary, letterSpacing: 1)),
                          const SizedBox(height: 4),
                          Text(formatMoney(totalSpent), style: GoogleFonts.spaceGrotesk(fontSize: 28, fontWeight: FontWeight.w800, color: NexusColors.primaryContainer)),
                          Text('From ${_orders.length} purchase${_orders.length == 1 ? '' : 's'}', style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong)),
                        ],
                      ),
                    ),
                  ],
                  const SizedBox(height: 20),
                  Text(
                    user.isAdmin ? 'Your test orders' : 'What you bought',
                    style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    user.isAdmin
                        ? 'Open Admin Dashboard to see all customer purchases.'
                        : 'Order history with items and totals.',
                    style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong),
                  ),
                  const SizedBox(height: 10),
                  if (_loadingOrders)
                    const Padding(
                      padding: EdgeInsets.all(32),
                      child: Center(child: CircularProgressIndicator()),
                    )
                  else if (_orders.isEmpty)
                    _EmptyBlock(
                      icon: Icons.shopping_bag_outlined,
                      title: 'No purchases yet',
                      subtitle: 'Items you checkout will show up here.',
                    )
                  else
                    ..._orders.map((o) => _OrderCard(order: o)),
                  const SizedBox(height: 16),
                  OutlinedButton.icon(
                    onPressed: () => auth.logout(),
                    icon: const Icon(Icons.logout, size: 18),
                    label: const Text('Log out'),
                  ),
                ],
              ),
            ),
    );
  }
}

class _ProfileHeader extends StatelessWidget {
  const _ProfileHeader({
    required this.userName,
    required this.email,
    required this.username,
    required this.role,
    this.avatarUrl,
  });

  final String userName;
  final String email;
  final String username;
  final String role;
  final String? avatarUrl;

  @override
  Widget build(BuildContext context) {
    final resolved = ApiConfig.resolveMediaUrl(avatarUrl);
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: NexusColors.outline),
      ),
      child: Row(
        children: [
          CircleAvatar(
            radius: 28,
            backgroundColor: NexusColors.primary.withValues(alpha: 0.12),
            backgroundImage: resolved.isNotEmpty ? NetworkImage(resolved) : null,
            child: resolved.isEmpty
                ? Text(
                    userName.isNotEmpty ? userName[0].toUpperCase() : 'M',
                    style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w800, color: NexusColors.primary, fontSize: 22),
                  )
                : null,
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(userName, style: GoogleFonts.spaceGrotesk(fontSize: 18, fontWeight: FontWeight.w700)),
                Text('@$username', style: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong)),
                Text(email, style: context.monoStyle.copyWith(fontSize: 11)),
                const SizedBox(height: 6),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                  decoration: BoxDecoration(
                    color: role.toUpperCase() == 'ADMIN'
                        ? NexusColors.primary.withValues(alpha: 0.1)
                        : NexusColors.success.withValues(alpha: 0.1),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    role.toUpperCase(),
                    style: GoogleFonts.jetBrainsMono(
                      fontSize: 10,
                      fontWeight: FontWeight.w700,
                      color: role.toUpperCase() == 'ADMIN' ? NexusColors.primary : NexusColors.success,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _AdminShortcut extends StatelessWidget {
  const _AdminShortcut({required this.onTap});

  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: NexusColors.primaryContainer,
      borderRadius: BorderRadius.circular(8),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(8),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              const Icon(Icons.dashboard_customize_outlined, color: Colors.white),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('Admin Dashboard', style: GoogleFonts.spaceGrotesk(color: Colors.white, fontWeight: FontWeight.w700, fontSize: 16)),
                    Text(
                      'GET / POST / PUT / DELETE products · see customer purchases',
                      style: GoogleFonts.inter(color: Colors.white70, fontSize: 12),
                    ),
                  ],
                ),
              ),
              const Icon(Icons.chevron_right, color: Colors.white),
            ],
          ),
        ),
      ),
    );
  }
}

class _MetricTile extends StatelessWidget {
  const _MetricTile({
    required this.label,
    required this.value,
    required this.icon,
    this.emphasize = false,
  });

  final String label;
  final String value;
  final IconData icon;
  final bool emphasize;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: emphasize ? NexusColors.primary.withValues(alpha: 0.35) : NexusColors.outline),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, size: 18, color: NexusColors.primary),
          const SizedBox(height: 8),
          Text(label, style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
          Text(
            value,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: emphasize ? 16 : 18),
          ),
        ],
      ),
    );
  }
}

class _OrderCard extends StatelessWidget {
  const _OrderCard({required this.order});

  final ShopOrder order;

  @override
  Widget build(BuildContext context) {
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
                child: Text(order.displayId, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
              ),
              _StatusChip(status: order.status),
            ],
          ),
          const SizedBox(height: 4),
          Text(order.itemSummary, style: GoogleFonts.inter(fontSize: 13)),
          if (order.items.length > 1) ...[
            const SizedBox(height: 6),
            ...order.items.take(4).map(
                  (line) => Padding(
                    padding: const EdgeInsets.only(bottom: 2),
                    child: Text(
                      '• ${line.productName} × ${line.quantity}',
                      style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong),
                    ),
                  ),
                ),
          ],
          const SizedBox(height: 8),
          Row(
            children: [
              Text(formatDate(order.createdAt), style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
              const Spacer(),
              Text(formatMoney(order.total), style: context.monoStyle.copyWith(fontWeight: FontWeight.w700, fontSize: 15)),
            ],
          ),
        ],
      ),
    );
  }
}

class _StatusChip extends StatelessWidget {
  const _StatusChip({required this.status});

  final String status;

  String get _label {
    switch (status.toUpperCase()) {
      case 'DELIVERED':
        return 'SUCCESSFUL';
      case 'CANCELLED':
        return 'CANCELLED';
      case 'PENDING':
        return 'PENDING';
      case 'PAID':
        return 'PAID';
      case 'PROCESSING':
        return 'PROCESSING';
      case 'SHIPPED':
        return 'SHIPPED';
      default:
        return status.toUpperCase();
    }
  }

  @override
  Widget build(BuildContext context) {
    final s = status.toUpperCase();
    Color fg = NexusColors.primary;
    if (s == 'DELIVERED' || s == 'PAID') fg = NexusColors.success;
    if (s == 'CANCELLED') fg = NexusColors.error;
    if (s == 'PENDING' || s == 'PROCESSING') fg = NexusColors.lowStock;
    if (s == 'SHIPPED') fg = const Color(0xFF0F766E);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(
        color: fg.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(_label, style: GoogleFonts.jetBrainsMono(fontSize: 10, fontWeight: FontWeight.w700, color: fg)),
    );
  }
}

class _EmptyBlock extends StatelessWidget {
  const _EmptyBlock({required this.icon, required this.title, required this.subtitle});

  final IconData icon;
  final String title;
  final String subtitle;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 36, horizontal: 16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: NexusColors.outline),
      ),
      child: Column(
        children: [
          Icon(icon, size: 36, color: NexusColors.outlineStrong),
          const SizedBox(height: 10),
          Text(title, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
          const SizedBox(height: 4),
          Text(subtitle, textAlign: TextAlign.center, style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong)),
        ],
      ),
    );
  }
}
