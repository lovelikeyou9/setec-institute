import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '../../models/product.dart';
import '../../providers/cart_provider.dart';
import '../../providers/product_provider.dart';
import '../../providers/wishlist_provider.dart';
import '../../theme/app_theme.dart';
import '../../utils/formatters.dart';
import '../../widgets/product_image.dart';
import '../../widgets/spec_chip.dart';
import '../../widgets/stock_badge.dart';

class ProductDetailScreen extends StatefulWidget {
  const ProductDetailScreen({super.key, required this.productId, this.initialProduct});

  final int productId;
  final Product? initialProduct;

  @override
  State<ProductDetailScreen> createState() => _ProductDetailScreenState();
}

class _ProductDetailScreenState extends State<ProductDetailScreen> {
  Product? _product;
  int _qty = 1;
  bool _loading = false;
  bool _adding = false;

  @override
  void initState() {
    super.initState();
    _product = widget.initialProduct;
    WidgetsBinding.instance.addPostFrameCallback((_) => _load());
  }

  Future<void> _load() async {
    final hasLocal = _product != null;
    if (!hasLocal) setState(() => _loading = true);
    final p = await context.read<ProductProvider>().fetchById(widget.productId);
    if (!mounted) return;
    setState(() {
      if (p != null) {
        _product = p;
        if (_qty > p.stock && p.stock > 0) _qty = p.stock;
        if (!p.inStock) _qty = 1;
      }
      _loading = false;
    });
  }

  Future<void> _addToCart() async {
    final product = _product;
    if (product == null || !product.inStock) return;
    setState(() => _adding = true);
    final messenger = ScaffoldMessenger.of(context);
    try {
      await context.read<CartProvider>().addProduct(product.id, quantity: _qty);
      messenger.showSnackBar(SnackBar(content: Text('Added $_qty × ${product.name} to cart')));
    } catch (_) {
      messenger.showSnackBar(const SnackBar(content: Text('Could not add to cart')));
    } finally {
      if (mounted) setState(() => _adding = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final product = _product;
    final wishlist = context.watch<WishlistProvider>();
    final saved = product != null && wishlist.contains(product.id);

    return Scaffold(
      appBar: AppBar(
        title: Text(
          product?.name ?? 'Product detail',
          style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700),
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
        actions: [
          if (product != null)
            IconButton(
              tooltip: saved ? 'Remove favorite' : 'Add favorite',
              onPressed: () => wishlist.toggle(product),
              icon: Icon(
                saved ? Icons.favorite : Icons.favorite_border,
                color: NexusColors.primary,
              ),
            ),
        ],
      ),
      bottomNavigationBar: product == null
          ? null
          : SafeArea(
              child: Container(
                padding: const EdgeInsets.fromLTRB(16, 10, 16, 12),
                decoration: const BoxDecoration(
                  color: Colors.white,
                  border: Border(top: BorderSide(color: NexusColors.outline)),
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            formatMoney(product.price * _qty),
                            style: context.monoStyle.copyWith(
                              fontSize: 18,
                              fontWeight: FontWeight.w700,
                              color: NexusColors.primary,
                            ),
                          ),
                          Text(
                            _qty > 1 ? '$_qty items' : 'Unit ${formatMoney(product.price)}',
                            style: GoogleFonts.inter(fontSize: 11, color: NexusColors.outlineStrong),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      flex: 2,
                      child: ElevatedButton.icon(
                        onPressed: product.inStock && !_adding ? _addToCart : null,
                        icon: _adding
                            ? const SizedBox(
                                width: 16,
                                height: 16,
                                child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                              )
                            : const Icon(Icons.shopping_cart_outlined, size: 18),
                        label: Text(product.inStock ? 'Add to cart' : 'Out of stock'),
                      ),
                    ),
                  ],
                ),
              ),
            ),
      body: _loading && product == null
          ? const Center(child: CircularProgressIndicator())
          : product == null
              ? Center(
                  child: Text(
                    'Product not found',
                    style: GoogleFonts.inter(color: NexusColors.outlineStrong),
                  ),
                )
              : RefreshIndicator(
                  onRefresh: _load,
                  child: SingleChildScrollView(
                    physics: const AlwaysScrollableScrollPhysics(),
                    padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Container(
                          width: double.infinity,
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(8),
                            border: Border.all(color: NexusColors.outline),
                          ),
                          clipBehavior: Clip.antiAlias,
                          child: ProductImage(url: product.imageUrl, height: 240, fit: BoxFit.contain),
                        ),
                        const SizedBox(height: 14),
                        Row(
                          children: [
                            _BrandChip(brand: product.brand),
                            const Spacer(),
                            StockBadge(product: product),
                          ],
                        ),
                        const SizedBox(height: 10),
                        Wrap(
                          spacing: 6,
                          runSpacing: 6,
                          children: [
                            if (product.featured)
                              const _InfoBadge(label: 'Featured', tone: _BadgeTone.primary),
                            if (product.flashDrop)
                              const _InfoBadge(label: 'Flash Drop', tone: _BadgeTone.warning),
                            if (product.architecture != null && product.architecture!.trim().isNotEmpty)
                              _InfoBadge(label: product.architecture!, tone: _BadgeTone.neutral),
                            if (product.discountPercent != null)
                              _InfoBadge(
                                label: '-${product.discountPercent!.round()}%',
                                tone: _BadgeTone.success,
                              ),
                          ],
                        ),
                        const SizedBox(height: 10),
                        Text(
                          product.name,
                          style: GoogleFonts.spaceGrotesk(fontSize: 22, fontWeight: FontWeight.w800),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          product.sku,
                          style: context.monoStyle.copyWith(fontSize: 12, color: NexusColors.outlineStrong),
                        ),
                        const SizedBox(height: 14),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              formatMoney(product.price),
                              style: context.monoStyle.copyWith(
                                fontSize: 26,
                                fontWeight: FontWeight.w700,
                                color: NexusColors.primary,
                              ),
                            ),
                            if (product.originalPrice != null && product.originalPrice! > product.price) ...[
                              const SizedBox(width: 10),
                              Padding(
                                padding: const EdgeInsets.only(bottom: 3),
                                child: Text(
                                  formatMoney(product.originalPrice),
                                  style: context.monoStyle.copyWith(
                                    fontSize: 14,
                                    decoration: TextDecoration.lineThrough,
                                    color: NexusColors.outlineStrong,
                                  ),
                                ),
                              ),
                            ],
                          ],
                        ),
                        const SizedBox(height: 18),
                        Text('About', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
                        const SizedBox(height: 6),
                        Text(
                          (product.description == null || product.description!.trim().isEmpty)
                              ? 'No description provided.'
                              : product.description!,
                          style: GoogleFonts.inter(fontSize: 14, height: 1.45, color: NexusColors.onSurface),
                        ),
                        const SizedBox(height: 20),
                        Text('Specifications', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
                        const SizedBox(height: 8),
                        if (product.specs.isEmpty)
                          Text(
                            'No specifications listed.',
                            style: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong),
                          )
                        else
                          Wrap(
                            spacing: 8,
                            runSpacing: 8,
                            children: product.specs.entries
                                .map(
                                  (e) => SizedBox(
                                    width: (MediaQuery.sizeOf(context).width - 48) / 2,
                                    child: SpecChip(label: e.key, value: e.value),
                                  ),
                                )
                                .toList(),
                          ),
                        const SizedBox(height: 22),
                        Text('Quantity', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
                        const SizedBox(height: 8),
                        _QtyStepper(
                          qty: _qty,
                          max: product.stock,
                          enabled: product.inStock,
                          onChanged: (v) => setState(() => _qty = v),
                        ),
                        if (product.inStock && product.lowStock) ...[
                          const SizedBox(height: 8),
                          Text(
                            'Only ${product.stock} left in stock',
                            style: GoogleFonts.inter(fontSize: 12, color: NexusColors.lowStock, fontWeight: FontWeight.w600),
                          ),
                        ],
                      ],
                    ),
                  ),
                ),
    );
  }
}

enum _BadgeTone { primary, warning, success, neutral }

class _InfoBadge extends StatelessWidget {
  const _InfoBadge({required this.label, required this.tone});

  final String label;
  final _BadgeTone tone;

  @override
  Widget build(BuildContext context) {
    final Color bg;
    final Color fg;
    switch (tone) {
      case _BadgeTone.primary:
        bg = NexusColors.primary;
        fg = Colors.white;
      case _BadgeTone.warning:
        bg = NexusColors.lowStock;
        fg = Colors.white;
      case _BadgeTone.success:
        bg = NexusColors.success;
        fg = Colors.white;
      case _BadgeTone.neutral:
        bg = const Color(0xFFD6E7F8);
        fg = const Color(0xFF003A75);
    }
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: bg,
        borderRadius: BorderRadius.circular(6),
      ),
      child: Text(
        label,
        style: GoogleFonts.inter(fontSize: 11, fontWeight: FontWeight.w700, color: fg),
      ),
    );
  }
}

class _BrandChip extends StatelessWidget {
  const _BrandChip({required this.brand});

  final String brand;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: const Color(0xFFD6E7F8),
        borderRadius: BorderRadius.circular(4),
        border: Border.all(color: NexusColors.outline),
      ),
      child: Text(
        brand.toUpperCase(),
        style: GoogleFonts.jetBrainsMono(
          fontSize: 11,
          fontWeight: FontWeight.w700,
          color: const Color(0xFF003A75),
        ),
      ),
    );
  }
}

class _QtyStepper extends StatelessWidget {
  const _QtyStepper({
    required this.qty,
    required this.max,
    required this.enabled,
    required this.onChanged,
  });

  final int qty;
  final int max;
  final bool enabled;
  final ValueChanged<int> onChanged;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: NexusColors.outline),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            onPressed: enabled && qty > 1 ? () => onChanged(qty - 1) : null,
            icon: const Icon(Icons.remove),
          ),
          SizedBox(
            width: 36,
            child: Text(
              '$qty',
              textAlign: TextAlign.center,
              style: GoogleFonts.jetBrainsMono(fontSize: 16, fontWeight: FontWeight.w700),
            ),
          ),
          IconButton(
            onPressed: enabled && qty < max ? () => onChanged(qty + 1) : null,
            icon: const Icon(Icons.add),
          ),
        ],
      ),
    );
  }
}
