import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../../models/category.dart';
import '../../services/admin_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/admin_crud_header.dart';
import 'admin_category_form_screen.dart';

class AdminCategoriesTab extends StatefulWidget {
  const AdminCategoriesTab({super.key, required this.admin});

  final AdminService admin;

  @override
  State<AdminCategoriesTab> createState() => _AdminCategoriesTabState();
}

class _AdminCategoriesTabState extends State<AdminCategoriesTab> {
  List<ProductCategory> _items = [];
  bool _loading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _refresh();
  }

  Future<void> _refresh() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      _items = await widget.admin.fetchCategories();
    } catch (e) {
      _error = e.toString();
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _openForm([ProductCategory? existing]) async {
    final changed = await Navigator.of(context).push<bool>(
      MaterialPageRoute(
        builder: (_) => AdminCategoryFormScreen(admin: widget.admin, category: existing),
      ),
    );
    if (changed == true) await _refresh();
  }

  Future<void> _delete(ProductCategory c) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete category?'),
        content: Text('Delete "${c.name}"? Products using this category may break.'),
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
    if (ok != true) return;
    try {
      await widget.admin.deleteCategory(c.id);
      await _refresh();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Category deleted')));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) return const Center(child: CircularProgressIndicator());

    return RefreshIndicator(
      onRefresh: _refresh,
      child: ListView(
        padding: const EdgeInsets.only(bottom: 24),
        children: [
          AdminCrudHeader(
            title: 'Categories',
            countLabel: '${_items.length} categor${_items.length == 1 ? 'y' : 'ies'}',
            addLabel: 'Add category',
            onAdd: () => _openForm(),
            onRefresh: _refresh,
          ),
          if (_error != null)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Text(_error!, style: const TextStyle(color: NexusColors.error)),
            ),
          if (_items.isEmpty)
            Padding(
              padding: const EdgeInsets.only(top: 40),
              child: Center(
                child: Text('No categories yet. Tap Add category.', style: GoogleFonts.inter(color: NexusColors.outlineStrong)),
              ),
            ),
          ..._items.map(
            (c) => Padding(
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
                    Text(c.name, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
                    const SizedBox(height: 4),
                    Text('Slug: ${c.slug ?? '—'}', style: GoogleFonts.jetBrainsMono(fontSize: 11, color: NexusColors.outlineStrong)),
                    Text('Sort: ${c.sortOrder} · Icon: ${c.icon ?? '—'}', style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong)),
                    if (c.description != null && c.description!.isNotEmpty) ...[
                      const SizedBox(height: 6),
                      Text(c.description!, maxLines: 2, overflow: TextOverflow.ellipsis, style: GoogleFonts.inter(fontSize: 13)),
                    ],
                    const SizedBox(height: 12),
                    AdminActionBar(
                      onEdit: () => _openForm(c),
                      onDelete: () => _delete(c),
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
