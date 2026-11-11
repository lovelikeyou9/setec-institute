import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../../models/category.dart';
import '../../services/admin_service.dart';
import '../../theme/app_theme.dart';

class AdminCategoryFormScreen extends StatefulWidget {
  const AdminCategoryFormScreen({super.key, required this.admin, this.category});

  final AdminService admin;
  final ProductCategory? category;

  @override
  State<AdminCategoryFormScreen> createState() => _AdminCategoryFormScreenState();
}

class _AdminCategoryFormScreenState extends State<AdminCategoryFormScreen> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _name;
  late final TextEditingController _slug;
  late final TextEditingController _icon;
  late final TextEditingController _description;
  late final TextEditingController _sortOrder;
  bool _saving = false;

  bool get _editing => widget.category != null;

  @override
  void initState() {
    super.initState();
    final c = widget.category;
    _name = TextEditingController(text: c?.name ?? '');
    _slug = TextEditingController(text: c?.slug ?? '');
    _icon = TextEditingController(text: c?.icon ?? 'category');
    _description = TextEditingController(text: c?.description ?? '');
    _sortOrder = TextEditingController(text: '${c?.sortOrder ?? 1}');
  }

  @override
  void dispose() {
    _name.dispose();
    _slug.dispose();
    _icon.dispose();
    _description.dispose();
    _sortOrder.dispose();
    super.dispose();
  }

  String _slugify(String value) =>
      value.trim().toLowerCase().replaceAll(RegExp(r'[^a-z0-9]+'), '-').replaceAll(RegExp(r'(^-|-$)'), '');

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _saving = true);
    final body = {
      'name': _name.text.trim(),
      'slug': _slug.text.trim().isEmpty ? _slugify(_name.text) : _slug.text.trim(),
      'iconName': _icon.text.trim(),
      'description': _description.text.trim(),
      'sortOrder': int.tryParse(_sortOrder.text.trim()) ?? 0,
    };
    try {
      if (_editing) {
        await widget.admin.updateCategory(widget.category!.id, body);
      } else {
        await widget.admin.createCategory(body);
      }
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
          _editing ? 'Edit category' : 'Add category',
          style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700),
        ),
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text(
              _editing ? 'Update this category (PUT)' : 'Create a new category (POST)',
              style: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong),
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _name,
              decoration: const InputDecoration(labelText: 'Category name'),
              validator: (v) => v == null || v.trim().isEmpty ? 'Required' : null,
              onChanged: (v) {
                if (!_editing && _slug.text.isEmpty) {
                  _slug.text = _slugify(v);
                }
              },
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _slug,
              decoration: const InputDecoration(labelText: 'Slug', hintText: 'laptops-pcs'),
              validator: (v) => v == null || v.trim().isEmpty ? 'Required' : null,
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _icon,
              decoration: const InputDecoration(labelText: 'Icon name', hintText: 'laptop, wifi, tv…'),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _sortOrder,
              decoration: const InputDecoration(labelText: 'Sort order'),
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _description,
              decoration: const InputDecoration(labelText: 'Description'),
              maxLines: 3,
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _saving ? null : _save,
                child: _saving
                    ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                    : Text(_editing ? 'Save changes' : 'Create category'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
