import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../../models/cart_item.dart';
import '../../models/order.dart';
import '../../models/product.dart';
import '../../services/admin_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/formatters.dart';
import '../../widgets/product_image.dart';
import '../../widgets/stock_badge.dart';

class AdminCustomerDetailScreen extends StatefulWidget {
  const AdminCustomerDetailScreen({
    super.key,
    required this.admin,
    required this.customerId,
    required this.customerName,
  });

  final AdminService admin;
  final int customerId;
  final String customerName;

  @override
  State<AdminCustomerDetailScreen> createState() => _AdminCustomerDetailScreenState();
}

class _AdminCustomerDetailScreenState extends State<AdminCustomerDetailScreen>
    with SingleTickerProviderStateMixin {
  late final TabController _tabs;
  CustomerActivity? _activity;
  bool _loading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _tabs = TabController(length: 3, vsync: this);
    _load();
  }

  @override
  void dispose() {
    _tabs.dispose();
    super.dispose();
  }

  Future<void> _load() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      _activity = await widget.admin.fetchCustomerActivity(widget.customerId);
    } catch (e) {
      _error = e.toString();
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final a = _activity;
    final cartValue = a?.cart.fold<double>(0, (s, i) => s + i.lineTotal) ?? 0;

    return Scaffold(
      appBar: AppBar(
        title: Text(
          widget.customerName,
          style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700),
        ),
        bottom: TabBar(
          controller: _tabs,
          labelStyle: GoogleFonts.inter(fontWeight: FontWeight.w700, fontSize: 12),
          tabs: [
            Tab(text: 'Orders (${a?.orderCount ?? 0})'),
            Tab(text: 'Cart (${a?.cartItemCount ?? 0})'),
            Tab(text: 'Favorites (${a?.favoriteCount ?? 0})'),
          ],
        ),
        actions: [
          IconButton(onPressed: _load, icon: const Icon(Icons.refresh)),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Text(_error!, style: const TextStyle(color: NexusColors.error)))
              : a == null
                  ? const Center(child: Text('No data'))
                  : Column(
                      children: [
                        Padding(
                          padding: const EdgeInsets.fromLTRB(16, 12, 16, 8),
                          child: Container(
                            width: double.infinity,
                            padding: const EdgeInsets.all(14),
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(8),
                              border: Border.all(color: NexusColors.outline),
                            ),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  a.customer.fullName,
                                  style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16),
                                ),
                                Text('@${a.customer.username}', style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong)),
                                Text(a.customer.email, style: GoogleFonts.jetBrainsMono(fontSize: 11)),
                                if (a.customer.banned) ...[
                                  const SizedBox(height: 6),
                                  Text(
                                    'BANNED${a.customer.banReason != null ? ': ${a.customer.banReason}' : ''}',
                                    style: GoogleFonts.jetBrainsMono(fontSize: 11, color: NexusColors.error, fontWeight: FontWeight.w700),
                                  ),
                                ],
                                const SizedBox(height: 12),
                                Row(
                                  children: [
                                    Expanded(child: _MiniStat('Orders', '${a.orderCount}')),
                                    Expanded(child: _MiniStat('Cart items', '${a.cartItemCount}')),
                                    Expanded(child: _MiniStat('Favorites', '${a.favoriteCount}')),
                                  ],
                                ),
                                const SizedBox(height: 10),
                                Row(
                                  children: [
                                    Expanded(
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          Text('Total spent', style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
                                          Text(
                                            formatMoney(a.totalSpent),
                                            style: GoogleFonts.spaceGrotesk(
                                              fontSize: 20,
                                              fontWeight: FontWeight.w800,
                                              color: NexusColors.primary,
                                            ),
                                          ),
                                        ],
                                      ),
                                    ),
                                    Expanded(
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          Text('Cart value', style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
                                          Text(
                                            formatMoney(cartValue),
                                            style: GoogleFonts.spaceGrotesk(
                                              fontSize: 20,
                                              fontWeight: FontWeight.w800,
                                              color: NexusColors.primaryContainer,
                                            ),
                                          ),
                                        ],
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                        Expanded(
                          child: TabBarView(
                            controller: _tabs,
                            children: [
                              _OrdersPane(orders: a.orders, onRefresh: _load),
                              _CartPane(cart: a.cart, onRefresh: _load),
                              _FavoritesPane(favorites: a.favorites, onRefresh: _load),
                            ],
                          ),
                        ),
                      ],
                    ),
    );
  }
}

class _MiniStat extends StatelessWidget {
  const _MiniStat(this.label, this.value);

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
        Text(value, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
      ],
    );
  }
}

class _OrdersPane extends StatelessWidget {
  const _OrdersPane({required this.orders, required this.onRefresh});

  final List<ShopOrder> orders;
  final Future<void> Function() onRefresh;

  @override
  Widget build(BuildContext context) {
    if (orders.isEmpty) {
      return _Empty('No orders inside this account', Icons.receipt_long_outlined, onRefresh);
    }
    return RefreshIndicator(
      onRefresh: onRefresh,
      child: ListView.builder(
        padding: const EdgeInsets.fromLTRB(16, 0, 16, 24),
        itemCount: orders.length,
        itemBuilder: (context, index) {
          final o = orders[index];
          return Container(
            margin: const EdgeInsets.only(bottom: 10),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: NexusColors.outline),
            ),
            child: Theme(
              data: Theme.of(context).copyWith(dividerColor: Colors.transparent),
              child: ExpansionTile(
                initiallyExpanded: index == 0,
                tilePadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                childrenPadding: const EdgeInsets.fromLTRB(12, 0, 12, 12),
                title: Text(o.displayId, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                subtitle: Text(
                  '${o.status} · ${o.items.length} product(s) · ${o.totalUnits} unit(s) · ${formatDate(o.createdAt)}',
                  style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong),
                ),
                trailing: Text(
                  formatMoney(o.total),
                  style: context.monoStyle.copyWith(fontWeight: FontWeight.w700, color: NexusColors.primary),
                ),
                children: [
                  const _SectionLabel('Items inside this order'),
                  const SizedBox(height: 8),
                  ...o.items.map((line) => _OrderLineTile(line: line)),
                  const SizedBox(height: 10),
                  const _SectionLabel('Order ledger'),
                  const SizedBox(height: 6),
                  _LedgerRow('Subtotal', formatMoney(o.subtotal)),
                  _LedgerRow('Discount', '-${formatMoney(o.discount)}'),
                  if (o.discountCode != null && o.discountCode!.isNotEmpty)
                    _LedgerRow('Code', o.discountCode!),
                  _LedgerRow('Shipping', o.shipping == 0 ? 'FREE' : formatMoney(o.shipping)),
                  _LedgerRow('Tax', formatMoney(o.tax)),
                  const Divider(height: 14),
                  _LedgerRow('Total', formatMoney(o.total), bold: true),
                  if (o.shippingAddress != null && o.shippingAddress!.isNotEmpty) ...[
                    const SizedBox(height: 10),
                    const _SectionLabel('Shipping address'),
                    const SizedBox(height: 4),
                    Text(o.shippingAddress!, style: GoogleFonts.inter(fontSize: 13)),
                  ],
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}

class _OrderLineTile extends StatelessWidget {
  const _OrderLineTile({required this.line});

  final OrderLine line;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(10),
      decoration: BoxDecoration(
        color: NexusColors.background,
        borderRadius: BorderRadius.circular(6),
        border: Border.all(color: NexusColors.outline),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ClipRRect(
            borderRadius: BorderRadius.circular(6),
            child: SizedBox(
              width: 54,
              child: ProductImage(url: line.imageUrl, height: 54, fit: BoxFit.cover),
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(line.productName, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 13)),
                if (line.brand != null && line.brand!.isNotEmpty)
                  Text(line.brand!, style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
                if (line.sku != null && line.sku!.isNotEmpty)
                  Text(line.sku!, style: context.monoStyle.copyWith(fontSize: 10, color: NexusColors.outlineStrong)),
                const SizedBox(height: 4),
                Text(
                  '${formatMoney(line.unitPrice)} × ${line.quantity} = ${formatMoney(line.total)}',
                  style: context.monoStyle.copyWith(fontSize: 12, fontWeight: FontWeight.w600),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _CartPane extends StatelessWidget {
  const _CartPane({required this.cart, required this.onRefresh});

  final List<CartItem> cart;
  final Future<void> Function() onRefresh;

  @override
  Widget build(BuildContext context) {
    if (cart.isEmpty) {
      return _Empty('Nothing inside this customer cart', Icons.shopping_cart_outlined, onRefresh);
    }
    final total = cart.fold<double>(0, (s, i) => s + i.lineTotal);
    return RefreshIndicator(
      onRefresh: onRefresh,
      child: ListView(
        padding: const EdgeInsets.fromLTRB(16, 0, 16, 24),
        children: [
          Container(
            margin: const EdgeInsets.only(bottom: 10),
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: NexusColors.primary.withValues(alpha: 0.06),
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: NexusColors.primary.withValues(alpha: 0.2)),
            ),
            child: Row(
              children: [
                Expanded(
                  child: Text(
                    '${cart.length} line(s) · ${cart.fold<int>(0, (s, i) => s + i.quantity)} unit(s)',
                    style: GoogleFonts.inter(fontWeight: FontWeight.w600),
                  ),
                ),
                Text(formatMoney(total), style: context.monoStyle.copyWith(fontWeight: FontWeight.w800, color: NexusColors.primary)),
              ],
            ),
          ),
          const _SectionLabel('Items inside cart'),
          const SizedBox(height: 8),
          ...cart.map((item) => _ProductDetailCard(
                product: item.product,
                trailingLabel: 'Qty ${item.quantity}',
                trailingValue: formatMoney(item.lineTotal),
                extra: 'Unit ${formatMoney(item.product.price)}',
              )),
        ],
      ),
    );
  }
}

class _FavoritesPane extends StatelessWidget {
  const _FavoritesPane({required this.favorites, required this.onRefresh});

  final List<Product> favorites;
  final Future<void> Function() onRefresh;

  @override
  Widget build(BuildContext context) {
    if (favorites.isEmpty) {
      return _Empty('Nothing inside this customer favorites', Icons.favorite_border, onRefresh);
    }
    return RefreshIndicator(
      onRefresh: onRefresh,
      child: ListView(
        padding: const EdgeInsets.fromLTRB(16, 0, 16, 24),
        children: [
          const _SectionLabel('Products inside favorites'),
          const SizedBox(height: 8),
          ...favorites.map(
            (p) => _ProductDetailCard(
              product: p,
              trailingLabel: p.sku,
              trailingValue: formatMoney(p.price),
              showStock: true,
              showSpecs: true,
            ),
          ),
        ],
      ),
    );
  }
}

class _ProductDetailCard extends StatelessWidget {
  const _ProductDetailCard({
    required this.product,
    required this.trailingLabel,
    required this.trailingValue,
    this.extra,
    this.showStock = false,
    this.showSpecs = false,
  });

  final Product product;
  final String trailingLabel;
  final String trailingValue;
  final String? extra;
  final bool showStock;
  final bool showSpecs;

  @override
  Widget build(BuildContext context) {
    final specs = product.specs.entries.take(4).toList();
    return Container(
      margin: const EdgeInsets.only(bottom: 10),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: NexusColors.outline),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(6),
                child: SizedBox(
                  width: 72,
                  child: ProductImage(url: product.imageUrl, height: 72, fit: BoxFit.cover),
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(product.name, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                    Text('${product.brand} · ${product.sku}', style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong)),
                    if (product.architecture != null && product.architecture!.isNotEmpty)
                      Text(product.architecture!, style: GoogleFonts.jetBrainsMono(fontSize: 10, color: NexusColors.primary)),
                    if (extra != null) Text(extra!, style: context.monoStyle.copyWith(fontSize: 11)),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Expanded(child: Text(trailingLabel, style: GoogleFonts.inter(fontSize: 12))),
                        Text(trailingValue, style: context.monoStyle.copyWith(fontWeight: FontWeight.w700)),
                      ],
                    ),
                    if (showStock) ...[
                      const SizedBox(height: 6),
                      StockBadge(product: product),
                    ],
                  ],
                ),
              ),
            ],
          ),
          if (product.description != null && product.description!.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(
              product.description!,
              maxLines: 3,
              overflow: TextOverflow.ellipsis,
              style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong),
            ),
          ],
          if (showSpecs && specs.isNotEmpty) ...[
            const SizedBox(height: 8),
            Wrap(
              spacing: 6,
              runSpacing: 6,
              children: specs
                  .map(
                    (e) => Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: NexusColors.background,
                        borderRadius: BorderRadius.circular(4),
                        border: Border.all(color: NexusColors.outline),
                      ),
                      child: Text(
                        '${e.key}: ${e.value}',
                        style: GoogleFonts.jetBrainsMono(fontSize: 10),
                      ),
                    ),
                  )
                  .toList(),
            ),
          ],
        ],
      ),
    );
  }
}

class _SectionLabel extends StatelessWidget {
  const _SectionLabel(this.text);

  final String text;

  @override
  Widget build(BuildContext context) {
    return Text(
      text.toUpperCase(),
      style: GoogleFonts.jetBrainsMono(fontSize: 10, letterSpacing: 0.6, color: NexusColors.outlineStrong, fontWeight: FontWeight.w700),
    );
  }
}

class _LedgerRow extends StatelessWidget {
  const _LedgerRow(this.label, this.value, {this.bold = false});

  final String label;
  final String value;
  final bool bold;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        children: [
          Expanded(
            child: Text(
              label,
              style: GoogleFonts.inter(fontSize: 12, fontWeight: bold ? FontWeight.w700 : FontWeight.w500),
            ),
          ),
          Text(
            value,
            style: context.monoStyle.copyWith(fontSize: bold ? 14 : 12, fontWeight: bold ? FontWeight.w800 : FontWeight.w500),
          ),
        ],
      ),
    );
  }
}

class _Empty extends StatelessWidget {
  const _Empty(this.title, this.icon, this.onRefresh);

  final String title;
  final IconData icon;
  final Future<void> Function() onRefresh;

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: onRefresh,
      child: ListView(
        children: [
          const SizedBox(height: 60),
          Icon(icon, size: 40, color: NexusColors.outlineStrong),
          const SizedBox(height: 10),
          Center(child: Text(title, style: GoogleFonts.inter(color: NexusColors.outlineStrong))),
        ],
      ),
    );
  }
}
