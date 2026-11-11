import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '../../models/category.dart';
import '../../providers/product_provider.dart';
import '../../theme/app_theme.dart';
import '../../widgets/catalog_filter_sheet.dart';
import '../../widgets/nexus_app_bar.dart';
import '../../widgets/product_card.dart';
import '../product/product_detail_screen.dart';

class CatalogScreen extends StatefulWidget {
  const CatalogScreen({super.key, this.initialCategoryId, this.initialSearch});

  final int? initialCategoryId;
  final String? initialSearch;

  @override
  State<CatalogScreen> createState() => CatalogScreenState();
}

class CatalogScreenState extends State<CatalogScreen> {
  CatalogFilterResult _filters = const CatalogFilterResult();
  String _search = '';
  final _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    if (widget.initialCategoryId != null) {
      _filters = CatalogFilterResult(categoryId: widget.initialCategoryId);
    }
    _search = widget.initialSearch ?? '';
    _searchController.text = _search;
    WidgetsBinding.instance.addPostFrameCallback((_) => _load());
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  /// Called from [MainShell] when user taps a store category or searches.
  void applyFilters({
    int? categoryId,
    String? search,
    bool clearCategory = false,
    bool clearSearch = false,
  }) {
    setState(() {
      if (clearCategory) {
        _filters = CatalogFilterResult(
          brand: _filters.brand,
          minPrice: _filters.minPrice,
          maxPrice: _filters.maxPrice,
          inStock: _filters.inStock,
          sort: _filters.sort,
        );
      } else if (categoryId != null) {
        _filters = CatalogFilterResult(
          categoryId: categoryId,
          brand: _filters.brand,
          minPrice: _filters.minPrice,
          maxPrice: _filters.maxPrice,
          inStock: _filters.inStock,
          sort: _filters.sort,
        );
      }
      if (clearSearch) {
        _search = '';
        _searchController.clear();
      } else if (search != null) {
        _search = search;
        _searchController.text = search;
      }
    });
    _load();
  }

  Future<void> _load() async {
    await context.read<ProductProvider>().searchCatalog(
          categoryId: _filters.categoryId,
          search: _search.isEmpty ? null : _search,
          brand: _filters.brand,
          minPrice: _filters.minPrice,
          maxPrice: _filters.maxPrice,
          inStock: _filters.inStock ? true : null,
          sort: _filters.sort,
        );
  }

  Future<void> _openFilterSheet() async {
    final provider = context.read<ProductProvider>();
    final result = await showCatalogFilterSheet(
      context: context,
      current: _filters,
      categories: provider.categories,
      brands: provider.brands,
    );
    if (result == null || !mounted) return;
    setState(() => _filters = result);
    await _load();
  }

  void _clearFilters() {
    setState(() {
      _filters = const CatalogFilterResult();
      _search = '';
      _searchController.clear();
    });
    _load();
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<ProductProvider>();
    final categories = provider.categories;
    final activeCount = _filters.activeCount + (_search.isNotEmpty ? 1 : 0);

    return Scaffold(
      appBar: NexusAppBar(
        title: 'Catalog',
        showSearch: true,
        searchHint: 'Search name, brand, or SKU',
        searchController: _searchController,
        onSearchChanged: (v) {
          _search = v;
          _load();
        },
        actions: [
          IconButton(
            tooltip: 'Filters',
            onPressed: _openFilterSheet,
            icon: Badge(
              isLabelVisible: _filters.activeCount > 0,
              label: Text('${_filters.activeCount}'),
              child: const Icon(Icons.tune),
            ),
          ),
        ],
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          SizedBox(
            height: 44,
            child: ListView(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
              children: [
                Padding(
                  padding: const EdgeInsets.only(right: 6),
                  child: _CategoryChip(
                    label: 'All',
                    selected: _filters.categoryId == null,
                    onSelected: (_) {
                      setState(() {
                        _filters = CatalogFilterResult(
                          brand: _filters.brand,
                          minPrice: _filters.minPrice,
                          maxPrice: _filters.maxPrice,
                          inStock: _filters.inStock,
                          sort: _filters.sort,
                        );
                      });
                      _load();
                    },
                  ),
                ),
                ...categories.map(
                  (ProductCategory c) => Padding(
                    padding: const EdgeInsets.only(right: 6),
                    child: _CategoryChip(
                      label: c.name,
                      selected: _filters.categoryId == c.id,
                      onSelected: (_) {
                        setState(() {
                          _filters = CatalogFilterResult(
                            categoryId: c.id,
                            brand: _filters.brand,
                            minPrice: _filters.minPrice,
                            maxPrice: _filters.maxPrice,
                            inStock: _filters.inStock,
                            sort: _filters.sort,
                          );
                        });
                        _load();
                      },
                    ),
                  ),
                ),
              ],
            ),
          ),
          if (_filters.hasActiveFilters || _search.isNotEmpty)
            Padding(
              padding: const EdgeInsets.fromLTRB(12, 0, 12, 8),
              child: Row(
                children: [
                  Expanded(
                    child: Text(
                      _summaryLabel(provider.catalog.length, activeCount),
                      style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong),
                    ),
                  ),
                  TextButton(
                    onPressed: _clearFilters,
                    child: const Text('Clear all'),
                  ),
                ],
              ),
            ),
          if (_filters.brand != null || _filters.minPrice != null || _filters.maxPrice != null || _filters.inStock)
            SizedBox(
              height: 36,
              child: ListView(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 12),
                children: [
                  if (_filters.brand != null)
                    Padding(
                      padding: const EdgeInsets.only(right: 6),
                      child: _ActiveFilterChip(
                        label: _filters.brand!,
                        onDeleted: () {
                          setState(() {
                            _filters = CatalogFilterResult(
                              categoryId: _filters.categoryId,
                              minPrice: _filters.minPrice,
                              maxPrice: _filters.maxPrice,
                              inStock: _filters.inStock,
                              sort: _filters.sort,
                            );
                          });
                          _load();
                        },
                      ),
                    ),
                  if (_filters.minPrice != null || _filters.maxPrice != null)
                    Padding(
                      padding: const EdgeInsets.only(right: 6),
                      child: _ActiveFilterChip(
                        label: _priceLabel(),
                        onDeleted: () {
                          setState(() {
                            _filters = CatalogFilterResult(
                              categoryId: _filters.categoryId,
                              brand: _filters.brand,
                              inStock: _filters.inStock,
                              sort: _filters.sort,
                            );
                          });
                          _load();
                        },
                      ),
                    ),
                  if (_filters.inStock)
                    Padding(
                      padding: const EdgeInsets.only(right: 6),
                      child: _ActiveFilterChip(
                        label: 'In stock',
                        onDeleted: () {
                          setState(() {
                            _filters = CatalogFilterResult(
                              categoryId: _filters.categoryId,
                              brand: _filters.brand,
                              minPrice: _filters.minPrice,
                              maxPrice: _filters.maxPrice,
                              sort: _filters.sort,
                            );
                          });
                          _load();
                        },
                      ),
                    ),
                ],
              ),
            ),
          const Divider(height: 1),
          Expanded(
            child: provider.loading
                ? const Center(child: CircularProgressIndicator())
                : RefreshIndicator(
                    onRefresh: _load,
                    child: provider.catalog.isEmpty
                        ? ListView(
                            children: [
                              const SizedBox(height: 80),
                              Center(
                                child: Text(
                                  'No products match your filters',
                                  style: GoogleFonts.inter(color: NexusColors.outlineStrong),
                                ),
                              ),
                              if (activeCount > 0)
                                Center(
                                  child: TextButton(onPressed: _clearFilters, child: const Text('Clear filters')),
                                ),
                            ],
                          )
                        : ListView.builder(
                            padding: const EdgeInsets.all(16),
                            itemCount: provider.catalog.length,
                            itemBuilder: (context, index) {
                              final product = provider.catalog[index];
                              return Padding(
                                padding: const EdgeInsets.only(bottom: 12),
                                child: ProductCard(
                                  product: product,
                                  onTap: () {
                                    Navigator.of(context).push(
                                      MaterialPageRoute(
                                        builder: (_) => ProductDetailScreen(
                                          productId: product.id,
                                          initialProduct: product,
                                        ),
                                      ),
                                    );
                                  },
                                ),
                              );
                            },
                          ),
                  ),
          ),
        ],
      ),
    );
  }

  String _summaryLabel(int count, int activeCount) {
    final noun = count == 1 ? 'product' : 'products';
    if (activeCount == 0) return '$count $noun';
    return '$count $noun · $activeCount filter${activeCount == 1 ? '' : 's'}';
  }

  String _priceLabel() {
    final min = _filters.minPrice;
    final max = _filters.maxPrice;
    if (min != null && max != null) return '\$${min.toStringAsFixed(0)}–\$${max.toStringAsFixed(0)}';
    if (min != null) return 'From \$${min.toStringAsFixed(0)}';
    return 'Up to \$${max!.toStringAsFixed(0)}';
  }
}

class _CategoryChip extends StatelessWidget {
  const _CategoryChip({
    required this.label,
    required this.selected,
    required this.onSelected,
  });

  final String label;
  final bool selected;
  final ValueChanged<bool> onSelected;

  @override
  Widget build(BuildContext context) {
    return FilterChip(
      label: Text(
        label,
        style: GoogleFonts.inter(
          fontSize: 12,
          fontWeight: FontWeight.w600,
          color: selected ? Colors.white : NexusColors.onSurface,
        ),
      ),
      selected: selected,
      onSelected: onSelected,
      showCheckmark: true,
      checkmarkColor: Colors.white,
      selectedColor: NexusColors.primary,
      backgroundColor: Colors.white,
      side: BorderSide(
        color: selected ? NexusColors.primary : NexusColors.outline,
      ),
      materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
      visualDensity: VisualDensity.compact,
    );
  }
}

class _ActiveFilterChip extends StatelessWidget {
  const _ActiveFilterChip({required this.label, required this.onDeleted});

  final String label;
  final VoidCallback onDeleted;

  @override
  Widget build(BuildContext context) {
    return InputChip(
      label: Text(
        label,
        style: GoogleFonts.inter(
          fontSize: 12,
          fontWeight: FontWeight.w600,
          color: NexusColors.onSurface,
        ),
      ),
      onDeleted: onDeleted,
      deleteIconColor: NexusColors.outlineStrong,
      backgroundColor: Colors.white,
      side: const BorderSide(color: NexusColors.outline),
      materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
      visualDensity: VisualDensity.compact,
    );
  }
}
