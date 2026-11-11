import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../models/brand.dart';
import '../models/category.dart';
import '../theme/app_theme.dart';

class CatalogFilterResult {
  const CatalogFilterResult({
    this.categoryId,
    this.brand,
    this.minPrice,
    this.maxPrice,
    this.inStock = false,
    this.sort = 'newest',
  });

  final int? categoryId;
  final String? brand;
  final double? minPrice;
  final double? maxPrice;
  final bool inStock;
  final String sort;

  bool get hasActiveFilters =>
      categoryId != null ||
      (brand != null && brand!.isNotEmpty) ||
      minPrice != null ||
      maxPrice != null ||
      inStock ||
      (sort.isNotEmpty && sort != 'newest');

  int get activeCount {
    var n = 0;
    if (categoryId != null) n++;
    if (brand != null && brand!.isNotEmpty) n++;
    if (minPrice != null || maxPrice != null) n++;
    if (inStock) n++;
    if (sort.isNotEmpty && sort != 'newest') n++;
    return n;
  }

  CatalogFilterResult copyWith({
    int? categoryId,
    String? brand,
    double? minPrice,
    double? maxPrice,
    bool? inStock,
    String? sort,
    bool clearCategory = false,
    bool clearBrand = false,
    bool clearPrice = false,
  }) {
    return CatalogFilterResult(
      categoryId: clearCategory ? null : (categoryId ?? this.categoryId),
      brand: clearBrand ? null : (brand ?? this.brand),
      minPrice: clearPrice ? null : (minPrice ?? this.minPrice),
      maxPrice: clearPrice ? null : (maxPrice ?? this.maxPrice),
      inStock: inStock ?? this.inStock,
      sort: sort ?? this.sort,
    );
  }
}

Future<CatalogFilterResult?> showCatalogFilterSheet({
  required BuildContext context,
  required CatalogFilterResult current,
  required List<ProductCategory> categories,
  required List<Brand> brands,
}) {
  return showModalBottomSheet<CatalogFilterResult>(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.white,
    shape: const RoundedRectangleBorder(
      borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
    ),
    builder: (ctx) => _CatalogFilterSheet(
      current: current,
      categories: categories,
      brands: brands,
    ),
  );
}

class _CatalogFilterSheet extends StatefulWidget {
  const _CatalogFilterSheet({
    required this.current,
    required this.categories,
    required this.brands,
  });

  final CatalogFilterResult current;
  final List<ProductCategory> categories;
  final List<Brand> brands;

  @override
  State<_CatalogFilterSheet> createState() => _CatalogFilterSheetState();
}

class _CatalogFilterSheetState extends State<_CatalogFilterSheet> {
  late int? _categoryId;
  late String? _brand;
  late bool _inStock;
  late String _sort;
  late final TextEditingController _minPrice;
  late final TextEditingController _maxPrice;

  static const _sortOptions = <(String, String)>[
    ('newest', 'Newest'),
    ('priceAsc', 'Price: low → high'),
    ('priceDesc', 'Price: high → low'),
    ('nameAsc', 'Name: A → Z'),
    ('nameDesc', 'Name: Z → A'),
  ];

  @override
  void initState() {
    super.initState();
    _categoryId = widget.current.categoryId;
    _brand = widget.current.brand;
    _inStock = widget.current.inStock;
    _sort = widget.current.sort;
    _minPrice = TextEditingController(
      text: widget.current.minPrice?.toStringAsFixed(
            widget.current.minPrice! % 1 == 0 ? 0 : 2,
          ) ??
          '',
    );
    _maxPrice = TextEditingController(
      text: widget.current.maxPrice?.toStringAsFixed(
            widget.current.maxPrice! % 1 == 0 ? 0 : 2,
          ) ??
          '',
    );
  }

  @override
  void dispose() {
    _minPrice.dispose();
    _maxPrice.dispose();
    super.dispose();
  }

  void _clear() {
    setState(() {
      _categoryId = null;
      _brand = null;
      _inStock = false;
      _sort = 'newest';
      _minPrice.clear();
      _maxPrice.clear();
    });
  }

  void _apply() {
    Navigator.pop(
      context,
      CatalogFilterResult(
        categoryId: _categoryId,
        brand: _brand,
        minPrice: double.tryParse(_minPrice.text.trim()),
        maxPrice: double.tryParse(_maxPrice.text.trim()),
        inStock: _inStock,
        sort: _sort,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final bottom = MediaQuery.viewInsetsOf(context).bottom;
    return Padding(
      padding: EdgeInsets.only(bottom: bottom),
      child: SafeArea(
        child: ConstrainedBox(
          constraints: BoxConstraints(
            maxHeight: MediaQuery.sizeOf(context).height * 0.88,
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const SizedBox(height: 8),
              Container(
                width: 40,
                height: 4,
                decoration: BoxDecoration(
                  color: NexusColors.outline,
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(16, 14, 8, 8),
                child: Row(
                  children: [
                    Expanded(
                      child: Text(
                        'Filters',
                        style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w800, fontSize: 20),
                      ),
                    ),
                    TextButton(onPressed: _clear, child: const Text('Reset')),
                    IconButton(
                      onPressed: () => Navigator.pop(context),
                      icon: const Icon(Icons.close),
                    ),
                  ],
                ),
              ),
              const Divider(height: 1),
              Flexible(
                child: ListView(
                  padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
                  children: [
                    Text('Category', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: [
                        _FilterChoiceChip(
                          label: 'All',
                          selected: _categoryId == null,
                          onSelected: (_) => setState(() => _categoryId = null),
                        ),
                        ...widget.categories.map(
                          (c) => _FilterChoiceChip(
                            label: c.name,
                            selected: _categoryId == c.id,
                            onSelected: (_) => setState(() => _categoryId = c.id),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 20),
                    Text('Brand', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                    const SizedBox(height: 8),
                    if (widget.brands.isEmpty)
                      Text(
                        'No brands available',
                        style: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong),
                      )
                    else
                      Wrap(
                        spacing: 8,
                        runSpacing: 8,
                        children: [
                          _FilterChoiceChip(
                            label: 'All brands',
                            selected: _brand == null,
                            onSelected: (_) => setState(() => _brand = null),
                          ),
                          ...widget.brands.map(
                            (b) => _FilterChoiceChip(
                              label: b.name,
                              selected: _brand == b.name,
                              onSelected: (_) => setState(() => _brand = b.name),
                            ),
                          ),
                        ],
                      ),
                    const SizedBox(height: 20),
                    Text('Price range', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Expanded(
                          child: TextField(
                            controller: _minPrice,
                            keyboardType: const TextInputType.numberWithOptions(decimal: true),
                            decoration: const InputDecoration(
                              labelText: 'Min',
                              prefixText: '\$ ',
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: TextField(
                            controller: _maxPrice,
                            keyboardType: const TextInputType.numberWithOptions(decimal: true),
                            decoration: const InputDecoration(
                              labelText: 'Max',
                              prefixText: '\$ ',
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 20),
                    Text('Sort by', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _sortOptions
                          .map(
                            (opt) => _FilterChoiceChip(
                              label: opt.$2,
                              selected: _sort == opt.$1,
                              onSelected: (_) => setState(() => _sort = opt.$1),
                            ),
                          )
                          .toList(),
                    ),
                    const SizedBox(height: 12),
                    SwitchListTile(
                      contentPadding: EdgeInsets.zero,
                      title: const Text('In stock only'),
                      subtitle: const Text('Hide sold-out items'),
                      value: _inStock,
                      onChanged: (v) => setState(() => _inStock = v),
                    ),
                  ],
                ),
              ),
              const Divider(height: 1),
              Padding(
                padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
                child: SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: _apply,
                    child: const Text('Apply filters'),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _FilterChoiceChip extends StatelessWidget {
  const _FilterChoiceChip({
    required this.label,
    required this.selected,
    required this.onSelected,
  });

  final String label;
  final bool selected;
  final ValueChanged<bool> onSelected;

  @override
  Widget build(BuildContext context) {
    return ChoiceChip(
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
      selectedColor: NexusColors.primary,
      backgroundColor: Colors.white,
      side: BorderSide(
        color: selected ? NexusColors.primary : NexusColors.outline,
      ),
      showCheckmark: false,
    );
  }
}
