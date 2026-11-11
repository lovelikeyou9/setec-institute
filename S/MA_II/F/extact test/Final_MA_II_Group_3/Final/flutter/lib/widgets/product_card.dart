import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '../models/product.dart';
import '../providers/cart_provider.dart';
import '../providers/wishlist_provider.dart';
import '../theme/app_theme.dart';
import '../utils/formatters.dart';
import 'product_image.dart';
import 'spec_chip.dart';
import 'stock_badge.dart';

class ProductCard extends StatelessWidget {
  const ProductCard({
    super.key,
    required this.product,
    this.compact = false,
    this.onTap,
  });

  final Product product;
  final bool compact;
  final VoidCallback? onTap;

  List<MapEntry<String, String>> get _specEntries {
    final entries = product.specs.entries.toList();
    final take = compact ? 3 : 4;
    if (entries.length >= take) return entries.take(take).toList();
    while (entries.length < take) {
      entries.add(MapEntry('—', '—'));
    }
    return entries.take(take).toList();
  }

  Future<void> _toggleWishlist(BuildContext context, WishlistProvider wishlist) async {
    try {
      await wishlist.toggle(product);
    } catch (_) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Could not update favorites')),
        );
      }
    }
  }

  Future<void> _addToCart(BuildContext context) async {
    try {
      await context.read<CartProvider>().addProduct(product.id);
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('${product.name} added to cart')),
        );
      }
    } catch (_) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Could not add to cart')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final wishlist = context.watch<WishlistProvider>();
    final saved = wishlist.contains(product.id);

    if (compact) {
      return _CompactCard(
        product: product,
        saved: saved,
        specs: _specEntries,
        onTap: onTap,
        onFavorite: () => _toggleWishlist(context, wishlist),
        onAdd: product.inStock ? () => _addToCart(context) : null,
      );
    }

    return Card(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(8),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _BrandChip(brand: product.brand),
                        const SizedBox(height: 6),
                        Text(
                          product.name,
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                          style: GoogleFonts.spaceGrotesk(fontSize: 15, fontWeight: FontWeight.w700),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          product.sku,
                          style: context.monoStyle.copyWith(fontSize: 10, color: NexusColors.outlineStrong),
                        ),
                      ],
                    ),
                  ),
                  IconButton(
                    visualDensity: VisualDensity.compact,
                    onPressed: () => _toggleWishlist(context, wishlist),
                    icon: Icon(saved ? Icons.favorite : Icons.favorite_border, color: NexusColors.primary),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              ClipRRect(
                borderRadius: BorderRadius.circular(8),
                child: ProductImage(url: product.imageUrl, height: 120),
              ),
              const SizedBox(height: 8),
              StockBadge(product: product),
              const SizedBox(height: 8),
              Row(
                children: _specEntries
                    .map(
                      (e) => Expanded(
                        child: Padding(
                          padding: const EdgeInsets.only(right: 4),
                          child: SpecChip(label: e.key, value: e.value),
                        ),
                      ),
                    )
                    .toList(),
              ),
              const SizedBox(height: 10),
              Row(
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          formatMoney(product.price),
                          style: context.monoStyle.copyWith(
                            fontSize: 16,
                            fontWeight: FontWeight.w700,
                            color: NexusColors.primary,
                          ),
                        ),
                        if (product.originalPrice != null && product.originalPrice! > product.price)
                          Text(
                            formatMoney(product.originalPrice),
                            style: context.monoStyle.copyWith(
                              fontSize: 11,
                              decoration: TextDecoration.lineThrough,
                              color: NexusColors.outlineStrong,
                            ),
                          ),
                      ],
                    ),
                  ),
                  OutlinedButton.icon(
                    onPressed: product.inStock ? () => _addToCart(context) : null,
                    icon: const Icon(Icons.add_shopping_cart, size: 16),
                    label: const Text('Cart'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _CompactCard extends StatelessWidget {
  const _CompactCard({
    required this.product,
    required this.saved,
    required this.specs,
    required this.onFavorite,
    this.onTap,
    this.onAdd,
  });

  final Product product;
  final bool saved;
  final List<MapEntry<String, String>> specs;
  final VoidCallback? onTap;
  final VoidCallback onFavorite;
  final VoidCallback? onAdd;

  @override
  Widget build(BuildContext context) {
    return Card(
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.fromLTRB(10, 10, 10, 8),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  _BrandChip(brand: product.brand),
                  const Spacer(),
                  InkWell(
                    onTap: onFavorite,
                    borderRadius: BorderRadius.circular(16),
                    child: Padding(
                      padding: const EdgeInsets.all(4),
                      child: Icon(
                        saved ? Icons.favorite : Icons.favorite_border,
                        color: NexusColors.primary,
                        size: 20,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 4),
              Text(
                product.name,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                style: GoogleFonts.spaceGrotesk(fontSize: 14, fontWeight: FontWeight.w700),
              ),
              const SizedBox(height: 6),
              ClipRRect(
                borderRadius: BorderRadius.circular(6),
                child: ProductImage(url: product.imageUrl, height: 100, fit: BoxFit.contain),
              ),
              const SizedBox(height: 6),
              StockBadge(product: product),
              const SizedBox(height: 6),
              Row(
                children: specs
                    .map(
                      (e) => Expanded(
                        child: Padding(
                          padding: const EdgeInsets.only(right: 3),
                          child: SpecChip(label: e.key, value: e.value),
                        ),
                      ),
                    )
                    .toList(),
              ),
              const Spacer(),
              Row(
                children: [
                  Expanded(
                    child: Text(
                      formatMoney(product.price),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: context.monoStyle.copyWith(
                        fontSize: 14,
                        fontWeight: FontWeight.w700,
                        color: NexusColors.primary,
                      ),
                    ),
                  ),
                  SizedBox(
                    height: 32,
                    child: OutlinedButton(
                      onPressed: onAdd,
                      style: OutlinedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(horizontal: 10),
                        visualDensity: VisualDensity.compact,
                      ),
                      child: const Text('Cart', style: TextStyle(fontSize: 12)),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
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
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: NexusColors.secondary.withValues(alpha: 0.12),
        borderRadius: BorderRadius.circular(4),
        border: Border.all(color: NexusColors.outline),
      ),
      child: Text(
        brand.toUpperCase(),
        style: GoogleFonts.jetBrainsMono(fontSize: 9, fontWeight: FontWeight.w700),
      ),
    );
  }
}
