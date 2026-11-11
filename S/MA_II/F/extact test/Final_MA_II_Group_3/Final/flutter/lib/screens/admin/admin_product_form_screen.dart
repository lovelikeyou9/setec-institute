import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '../../models/brand.dart';
import '../../models/category.dart';
import '../../models/product.dart';
import '../../providers/product_provider.dart';
import '../../services/admin_service.dart';
import '../../services/api_client.dart';
import '../../services/product_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/image_pick_field.dart';

class AdminProductFormScreen extends StatefulWidget {
  const AdminProductFormScreen({super.key, this.product});

  final Product? product;

  @override
  State<AdminProductFormScreen> createState() => _AdminProductFormScreenState();
}

class _AdminProductFormScreenState extends State<AdminProductFormScreen> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _name;
  late final TextEditingController _sku;
  late final TextEditingController _description;
  late final TextEditingController _price;
  late final TextEditingController _originalPrice;
  late final TextEditingController _stock;
  late final TextEditingController _warnMin;
  late final TextEditingController _binLocation;
  late final TextEditingController _imageUrl;
  late final TextEditingController _architecture;
  late final TextEditingController _costPrice;

  final List<_SpecRow> _specRows = [];
  bool _featured = false;
  bool _flashDrop = false;
  bool _saving = false;
  bool _loadingLookups = true;

  List<ProductCategory> _categories = [];
  List<Brand> _brands = [];
  int? _categoryId;
  String? _brandName;

  bool get _editing => widget.product != null;

  @override
  void initState() {
    super.initState();
    final p = widget.product;
    _name = TextEditingController(text: p?.name ?? '');
    _sku = TextEditingController(text: p?.sku ?? '');
    _description = TextEditingController(text: p?.description ?? '');
    _price = TextEditingController(text: p != null ? '${p.price}' : '');
    _originalPrice = TextEditingController(text: p?.originalPrice?.toString() ?? '');
    _stock = TextEditingController(text: '${p?.stock ?? 0}');
    _warnMin = TextEditingController(text: '${p?.warnMin ?? 5}');
    _binLocation = TextEditingController(text: p?.binLocation ?? '');
    _imageUrl = TextEditingController(text: p?.imageUrl ?? '');
    _architecture = TextEditingController(text: p?.architecture ?? '');
    _costPrice = TextEditingController(text: p?.costPrice?.toString() ?? '');
    _featured = p?.featured ?? false;
    _flashDrop = p?.flashDrop ?? false;
    _categoryId = p?.categoryId;
    _brandName = p?.brand;

    if (p != null && p.specs.isNotEmpty) {
      for (final e in p.specs.entries) {
        _specRows.add(_SpecRow(key: e.key, value: e.value));
      }
    } else {
      _specRows.add(_SpecRow());
    }

    WidgetsBinding.instance.addPostFrameCallback((_) => _loadLookups());
  }

  Future<void> _loadLookups() async {
    final admin = AdminService(context.read<ApiClient>());
    try {
      final results = await Future.wait([
        admin.fetchCategories(),
        admin.fetchBrands(),
      ]);
      _categories = results[0] as List<ProductCategory>;
      _brands = (results[1] as List<Brand>).where((b) => b.active).toList();

      if (_categoryId == null && _categories.isNotEmpty) {
        _categoryId = _categories.first.id;
      } else if (_categoryId != null && !_categories.any((c) => c.id == _categoryId)) {
        _categoryId = _categories.isNotEmpty ? _categories.first.id : null;
      }

      if (_brandName == null || _brandName!.isEmpty) {
        _brandName = _brands.isNotEmpty ? _brands.first.name : null;
      } else if (!_brands.any((b) => b.name == _brandName)) {
        // Keep existing brand name even if not in active list (edit case)
        _brands = [..._brands, Brand(id: 0, name: _brandName!, slug: '')];
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Could not load categories/brands: $e')));
      }
    } finally {
      if (mounted) setState(() => _loadingLookups = false);
    }
  }

  @override
  void dispose() {
    _name.dispose();
    _sku.dispose();
    _description.dispose();
    _price.dispose();
    _originalPrice.dispose();
    _stock.dispose();
    _warnMin.dispose();
    _binLocation.dispose();
    _imageUrl.dispose();
    _architecture.dispose();
    _costPrice.dispose();
    for (final row in _specRows) {
      row.dispose();
    }
    super.dispose();
  }

  Map<String, dynamic> _buildBody() {
    final specs = <String, String>{};
    for (final row in _specRows) {
      final k = row.keyController.text.trim();
      final v = row.valueController.text.trim();
      if (k.isNotEmpty) specs[k] = v;
    }
    final original = double.tryParse(_originalPrice.text.trim());
    final cost = double.tryParse(_costPrice.text.trim());
    return {
      'name': _name.text.trim(),
      'brand': _brandName ?? '',
      'sku': _sku.text.trim(),
      'categoryId': _categoryId ?? 1,
      'description': _description.text.trim(),
      'price': double.tryParse(_price.text.trim()) ?? 0,
      if (original != null) 'originalPrice': original,
      'stock': int.tryParse(_stock.text.trim()) ?? 0,
      'warnMin': int.tryParse(_warnMin.text.trim()) ?? 5,
      'binLocation': _binLocation.text.trim(),
      'imageUrl': _imageUrl.text.trim(),
      'architecture': _architecture.text.trim(),
      if (cost != null) 'costPrice': cost,
      // Backend stores specs as a JSON *string*, not an object.
      'specsJson': jsonEncode(specs),
      'featured': _featured,
      'flashDrop': _flashDrop,
    };
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Fill required fields: name, category, brand, SKU, price')),
      );
      return;
    }
    if (_categoryId == null) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Select a category')));
      return;
    }
    if (_brandName == null || _brandName!.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Select a brand')));
      return;
    }
    if (_imageUrl.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Choose or paste a product image')));
      return;
    }
    setState(() => _saving = true);
    try {
      final service = ProductService(context.read<ApiClient>());
      final body = _buildBody();
      if (widget.product == null) {
        await service.createProduct(body);
      } else {
        await service.updateProduct(widget.product!.id, body);
      }
      if (!mounted) return;
      await context.read<ProductProvider>().loadHomeData();
      if (!mounted) return;
      Navigator.pop(context, true);
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
      }
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          _editing ? 'Edit product' : 'Add product',
          style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700),
        ),
      ),
      body: _loadingLookups
          ? const Center(child: CircularProgressIndicator())
          : Form(
              key: _formKey,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  Text(
                    _editing ? 'Update this product (PUT)' : 'Create a new product (POST)',
                    style: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong),
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _name,
                    decoration: const InputDecoration(labelText: 'Product name'),
                    validator: _req,
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<int>(
                    key: ValueKey('category-$_categoryId'),
                    initialValue: _categoryId,
                    decoration: const InputDecoration(labelText: 'Category'),
                    items: _categories
                        .map((c) => DropdownMenuItem(value: c.id, child: Text(c.name)))
                        .toList(),
                    onChanged: (v) => setState(() => _categoryId = v),
                    validator: (v) => v == null ? 'Required' : null,
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<String>(
                    key: ValueKey('brand-$_brandName'),
                    initialValue: _brandName,
                    decoration: const InputDecoration(labelText: 'Brand'),
                    items: _brands
                        .map((b) => DropdownMenuItem(value: b.name, child: Text(b.name)))
                        .toList(),
                    onChanged: (v) => setState(() => _brandName = v),
                    validator: (v) => v == null || v.isEmpty ? 'Required' : null,
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: _sku,
                    decoration: const InputDecoration(
                      labelText: 'SKU (required)',
                      hintText: 'MMT-GPU-4090',
                    ),
                    validator: _req,
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: TextFormField(
                          controller: _stock,
                          decoration: const InputDecoration(labelText: 'Stock'),
                          keyboardType: TextInputType.number,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: TextFormField(
                          controller: _warnMin,
                          decoration: const InputDecoration(labelText: 'Low-stock warn'),
                          keyboardType: TextInputType.number,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: TextFormField(
                          controller: _price,
                          decoration: const InputDecoration(labelText: 'Price'),
                          keyboardType: TextInputType.number,
                          validator: _req,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: TextFormField(
                          controller: _originalPrice,
                          decoration: const InputDecoration(labelText: 'Original price'),
                          keyboardType: TextInputType.number,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: _costPrice,
                    decoration: const InputDecoration(labelText: 'Cost price'),
                    keyboardType: TextInputType.number,
                  ),
                  const SizedBox(height: 12),
                  ImagePickField(
                    controller: _imageUrl,
                    folder: 'products',
                    label: 'Product image',
                    required: true,
                  ),
                  const SizedBox(height: 12),
                  TextFormField(controller: _architecture, decoration: const InputDecoration(labelText: 'Architecture')),
                  const SizedBox(height: 12),
                  TextFormField(controller: _binLocation, decoration: const InputDecoration(labelText: 'Bin location')),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: _description,
                    decoration: const InputDecoration(labelText: 'Description'),
                    maxLines: 3,
                  ),
                  SwitchListTile(
                    contentPadding: EdgeInsets.zero,
                    title: const Text('Featured'),
                    value: _featured,
                    onChanged: (v) => setState(() => _featured = v),
                  ),
                  SwitchListTile(
                    contentPadding: EdgeInsets.zero,
                    title: const Text('Flash drop'),
                    value: _flashDrop,
                    onChanged: (v) => setState(() => _flashDrop = v),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      Expanded(
                        child: Text('Specs', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                      ),
                      TextButton.icon(
                        onPressed: () => setState(() => _specRows.add(_SpecRow())),
                        icon: const Icon(Icons.add, size: 16),
                        label: const Text('Add row'),
                      ),
                    ],
                  ),
                  ..._specRows.map(
                    (row) => Padding(
                      padding: const EdgeInsets.only(bottom: 8),
                      child: Row(
                        children: [
                          Expanded(
                            child: TextField(
                              controller: row.keyController,
                              decoration: const InputDecoration(labelText: 'Key'),
                            ),
                          ),
                          const SizedBox(width: 8),
                          Expanded(
                            child: TextField(
                              controller: row.valueController,
                              decoration: const InputDecoration(labelText: 'Value'),
                            ),
                          ),
                          IconButton(
                            onPressed: _specRows.length <= 1
                                ? null
                                : () => setState(() {
                                      row.dispose();
                                      _specRows.remove(row);
                                    }),
                            icon: const Icon(Icons.close),
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 24),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: _saving ? null : _save,
                      child: _saving
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                            )
                          : Text(_editing ? 'Save changes' : 'Create product'),
                    ),
                  ),
                ],
              ),
            ),
    );
  }

  String? _req(String? v) => v == null || v.trim().isEmpty ? 'Required' : null;
}

class _SpecRow {
  _SpecRow({String key = '', String value = ''})
      : keyController = TextEditingController(text: key),
        valueController = TextEditingController(text: value);

  final TextEditingController keyController;
  final TextEditingController valueController;

  void dispose() {
    keyController.dispose();
    valueController.dispose();
  }
}
