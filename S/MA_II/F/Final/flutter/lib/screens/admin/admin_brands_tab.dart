import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../../models/brand.dart';
import '../../services/admin_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/admin_crud_header.dart';
import 'admin_brand_form_screen.dart';

class AdminBrandsTab extends StatefulWidget {
  const AdminBrandsTab({super.key, required this.admin});

  final AdminService admin;

  @override
  State<AdminBrandsTab> createState() => _AdminBrandsTabState();
}

class _AdminBrandsTabState extends State<AdminBrandsTab> {
  List<Brand> _items = [];
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
      _items = await widget.admin.fetchBrands();
    } catch (e) {
      _error = e.toString();
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _openForm([Brand? existing]) async {
    final changed = await Navigator.of(context).push<bool>(
      MaterialPageRoute(
        builder: (_) => AdminBrandFormScreen(admin: widget.admin, brand: existing),
      ),
    );
    if (changed == true) await _refresh();
  }

  Future<void> _delete(Brand b) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete brand?'),
        content: Text('Delete "${b.name}" from brands?'),
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
      await widget.admin.deleteBrand(b.id);
      await _refresh();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Brand deleted')));
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
            title: 'Brands',
            countLabel: '${_items.length} brand${_items.length == 1 ? '' : 's'}',
            addLabel: 'Add brand',
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
                child: Text('No brands yet. Tap Add brand.', style: GoogleFonts.inter(color: NexusColors.outlineStrong)),
              ),
            ),
          ..._items.map(
            (b) => Padding(
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
                    Row(
                      children: [
                        Expanded(
                          child: Text(b.name, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                          decoration: BoxDecoration(
                            color: (b.active ? NexusColors.success : NexusColors.outlineStrong).withValues(alpha: 0.12),
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            b.active ? 'Active' : 'Inactive',
                            style: GoogleFonts.inter(
                              fontSize: 11,
                              fontWeight: FontWeight.w700,
                              color: b.active ? NexusColors.success : NexusColors.outlineStrong,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text('Slug: ${b.slug}', style: GoogleFonts.jetBrainsMono(fontSize: 11, color: NexusColors.outlineStrong)),
                    if (b.description != null && b.description!.isNotEmpty) ...[
                      const SizedBox(height: 6),
                      Text(b.description!, maxLines: 2, overflow: TextOverflow.ellipsis, style: GoogleFonts.inter(fontSize: 13)),
                    ],
                    const SizedBox(height: 12),
                    AdminActionBar(
                      onEdit: () => _openForm(b),
                      onDelete: () => _delete(b),
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
