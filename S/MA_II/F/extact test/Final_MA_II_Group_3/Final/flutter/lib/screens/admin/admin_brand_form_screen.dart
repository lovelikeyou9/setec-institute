import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../../models/brand.dart';
import '../../services/admin_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/image_pick_field.dart';

class AdminBrandFormScreen extends StatefulWidget {
  const AdminBrandFormScreen({super.key, required this.admin, this.brand});

  final AdminService admin;
  final Brand? brand;

  @override
  State<AdminBrandFormScreen> createState() => _AdminBrandFormScreenState();
}

class _AdminBrandFormScreenState extends State<AdminBrandFormScreen> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _name;
  late final TextEditingController _slug;
  late final TextEditingController _logoUrl;
  late final TextEditingController _description;
  late bool _active;
  bool _saving = false;

  bool get _editing => widget.brand != null;

  @override
  void initState() {
    super.initState();
    final b = widget.brand;
    _name = TextEditingController(text: b?.name ?? '');
    _slug = TextEditingController(text: b?.slug ?? '');
    _logoUrl = TextEditingController(text: b?.logoUrl ?? '');
    _description = TextEditingController(text: b?.description ?? '');
    _active = b?.active ?? true;
  }

  @override
  void dispose() {
    _name.dispose();
    _slug.dispose();
    _logoUrl.dispose();
    _description.dispose();
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
      'logoUrl': _logoUrl.text.trim().isEmpty ? null : _logoUrl.text.trim(),
      'description': _description.text.trim(),
      'active': _active,
    };
    try {
      if (_editing) {
        await widget.admin.updateBrand(widget.brand!.id, body);
      } else {
        await widget.admin.createBrand(body);
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
          _editing ? 'Edit brand' : 'Add brand',
          style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700),
        ),
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text(
              _editing ? 'Update this brand (PUT)' : 'Create a new brand (POST)',
              style: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong),
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _name,
              decoration: const InputDecoration(labelText: 'Brand name'),
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
              decoration: const InputDecoration(labelText: 'Slug', hintText: 'asus'),
              validator: (v) => v == null || v.trim().isEmpty ? 'Required' : null,
            ),
            const SizedBox(height: 12),
            ImagePickField(
              controller: _logoUrl,
              folder: 'brands',
              label: 'Brand logo',
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _description,
              decoration: const InputDecoration(labelText: 'Description'),
              maxLines: 3,
            ),
            const SizedBox(height: 8),
            SwitchListTile(
              contentPadding: EdgeInsets.zero,
              title: const Text('Active'),
              subtitle: const Text('Inactive brands stay hidden from active use'),
              value: _active,
              onChanged: (v) => setState(() => _active = v),
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _saving ? null : _save,
                child: _saving
                    ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                    : Text(_editing ? 'Save changes' : 'Create brand'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
