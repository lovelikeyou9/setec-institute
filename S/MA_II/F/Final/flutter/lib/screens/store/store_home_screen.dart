import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '../../models/category.dart';
import '../../models/product.dart';
import '../../providers/product_provider.dart';
import '../../theme/app_theme.dart';
import '../../utils/formatters.dart';
import '../../widgets/category_grid.dart';
import '../../widgets/nexus_app_bar.dart';
import '../../widgets/product_card.dart';
import '../../widgets/product_image.dart';
import '../../widgets/section_header.dart';
import '../product/product_detail_screen.dart';

class StoreHomeScreen extends StatefulWidget {
  const StoreHomeScreen({super.key, this.onBrowseCategory, this.onSearch});

  final void Function(ProductCategory category)? onBrowseCategory;
  final void Function(String query)? onSearch;

  @override
  State<StoreHomeScreen> createState() => _StoreHomeScreenState();
}

class _StoreHomeScreenState extends State<StoreHomeScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<ProductProvider>().loadHomeData();
    });
  }

  void _openProduct(Product product) {
    Navigator.of(context).push(
      MaterialPageRoute(builder: (_) => ProductDetailScreen(productId: product.id, initialProduct: product)),
    );
  }

  @override
  Widget build(BuildContext context) {
    final products = context.watch<ProductProvider>();

    return Scaffold(
      appBar: NexusAppBar(
        showSearch: true,
        searchHint: 'Search laptops, drones, Wi‑Fi…',
        onSearchChanged: (q) {
          if (q.trim().length >= 2) {
            widget.onSearch?.call(q.trim());
          }
        },
      ),
      body: RefreshIndicator(
        onRefresh: products.loadHomeData,
        child: products.loading && products.catalog.isEmpty
            ? const Center(child: CircularProgressIndicator())
            : ListView(
                padding: const EdgeInsets.only(bottom: 24),
                children: [
                  if (products.error != null)
                    Padding(
                      padding: const EdgeInsets.all(16),
                      child: Text(products.error!, style: const TextStyle(color: NexusColors.error)),
                    ),
                  _HeroBanner(featured: products.featured.isNotEmpty ? products.featured.first : null, onTap: _openProduct),
                  const SizedBox(height: 20),
                  const SectionHeader(title: 'Shop by category', subtitle: 'Multiple technology departments'),
                  const SizedBox(height: 12),
                  CategoryGrid(
                    categories: products.categories,
                    onTap: widget.onBrowseCategory,
                  ),
                  const SizedBox(height: 24),
                  const SectionHeader(title: 'Flash Drops', subtitle: 'Limited window pricing'),
                  const SizedBox(height: 12),
                  SizedBox(
                    height: 300,
                    child: ListView.separated(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      scrollDirection: Axis.horizontal,
                      itemCount: products.flashDrops.length,
                      separatorBuilder: (context, index) => const SizedBox(width: 12),
                      itemBuilder: (context, i) {
                        final p = products.flashDrops[i];
                        return SizedBox(
                          width: 260,
                          child: ProductCard(product: p, compact: true, onTap: () => _openProduct(p)),
                        );
                      },
                    ),
                  ),
                  const SizedBox(height: 24),
                  const SectionHeader(title: 'Top picks', subtitle: 'Popular across MMT departments'),
                  const SizedBox(height: 12),
                  ...products.catalog.take(6).map(
                        (p) => Padding(
                          padding: const EdgeInsets.fromLTRB(16, 0, 16, 12),
                          child: ProductCard(product: p, onTap: () => _openProduct(p)),
                        ),
                      ),
                  const SizedBox(height: 8),
                  _ValueProps(),
                ],
              ),
      ),
    );
  }
}

class _HeroBanner extends StatelessWidget {
  const _HeroBanner({this.featured, required this.onTap});

  final Product? featured;
  final void Function(Product) onTap;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Container(
        decoration: BoxDecoration(
          color: NexusColors.primaryContainer,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: NexusColors.outline),
        ),
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Featured technology',
                    style: GoogleFonts.spaceGrotesk(color: Colors.white, fontSize: 20, fontWeight: FontWeight.w700),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    featured?.name ?? 'Explore laptops, drones, networking, and more.',
                    style: GoogleFonts.inter(color: Colors.white70, fontSize: 13),
                  ),
                  const SizedBox(height: 12),
                  if (featured != null)
                    Text(
                      formatMoney(featured!.price),
                      style: context.monoStyle.copyWith(color: NexusColors.secondary, fontSize: 18),
                    ),
                  const SizedBox(height: 12),
                  OutlinedButton(
                    style: OutlinedButton.styleFrom(
                      foregroundColor: Colors.white,
                      side: const BorderSide(color: Colors.white54),
                    ),
                    onPressed: featured == null ? null : () => onTap(featured!),
                    child: const Text('View product'),
                  ),
                ],
              ),
            ),
            const SizedBox(width: 12),
            ClipRRect(
              borderRadius: BorderRadius.circular(8),
              child: SizedBox(
                width: 120,
                child: ProductImage(url: featured?.imageUrl, height: 120, fit: BoxFit.cover),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ValueProps extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    const items = [
      ('Genuine multi-brand tech', Icons.verified_outlined),
      ('Clear specs & warranty info', Icons.table_chart_outlined),
      ('Support across every category', Icons.support_agent_outlined),
    ];
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Column(
        children: items
            .map(
              (e) => Container(
                margin: const EdgeInsets.only(bottom: 8),
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: NexusColors.outline),
                ),
                child: Row(
                  children: [
                    Icon(e.$2, color: NexusColors.primary),
                    const SizedBox(width: 12),
                    Expanded(child: Text(e.$1, style: GoogleFonts.inter(fontWeight: FontWeight.w600))),
                  ],
                ),
              ),
            )
            .toList(),
      ),
    );
  }
}
