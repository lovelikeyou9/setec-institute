import 'dart:io';

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';

import '../services/api_client.dart';
import '../services/upload_service.dart';
import '../theme/app_theme.dart';
import 'product_image.dart';

/// Lets admin pick a gallery/camera image (uploaded to Spring) or paste a URL.
class ImagePickField extends StatefulWidget {
  const ImagePickField({
    super.key,
    required this.controller,
    required this.folder,
    this.label = 'Image',
    this.required = false,
  });

  final TextEditingController controller;
  final String folder;
  final String label;
  final bool required;

  @override
  State<ImagePickField> createState() => _ImagePickFieldState();
}

class _ImagePickFieldState extends State<ImagePickField> {
  final _picker = ImagePicker();
  bool _uploading = false;
  String? _localPreviewPath;

  Future<void> _pick(ImageSource source) async {
    final api = context.read<ApiClient>();
    final messenger = ScaffoldMessenger.of(context);
    try {
      final picked = await _picker.pickImage(
        source: source,
        maxWidth: 1600,
        maxHeight: 1600,
        imageQuality: 85,
      );
      if (picked == null) return;

      setState(() {
        _uploading = true;
        _localPreviewPath = picked.path;
      });

      final url = await UploadService(api).uploadImage(File(picked.path), folder: widget.folder);
      if (!mounted) return;
      widget.controller.text = url;
      setState(() => _localPreviewPath = null);
      messenger.showSnackBar(const SnackBar(content: Text('Image uploaded')));
    } catch (e) {
      messenger.showSnackBar(SnackBar(content: Text(e.toString())));
    } finally {
      if (mounted) setState(() => _uploading = false);
    }
  }

  void _showSourceSheet() {
    showModalBottomSheet<void>(
      context: context,
      builder: (ctx) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.photo_library_outlined),
              title: const Text('Choose from gallery'),
              onTap: () {
                Navigator.pop(ctx);
                _pick(ImageSource.gallery);
              },
            ),
            ListTile(
              leading: const Icon(Icons.photo_camera_outlined),
              title: const Text('Take photo'),
              onTap: () {
                Navigator.pop(ctx);
                _pick(ImageSource.camera);
              },
            ),
            ListTile(
              leading: const Icon(Icons.close),
              title: const Text('Cancel'),
              onTap: () => Navigator.pop(ctx),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final url = widget.controller.text.trim();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Text(widget.label, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
        const SizedBox(height: 8),
        Container(
          height: 140,
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(8),
            border: Border.all(color: NexusColors.outline),
          ),
          clipBehavior: Clip.antiAlias,
          child: _uploading
              ? const Center(child: CircularProgressIndicator())
              : _localPreviewPath != null
                  ? Image.file(File(_localPreviewPath!), fit: BoxFit.cover, width: double.infinity)
                  : url.isNotEmpty
                      ? ProductImage(url: url, height: 140, fit: BoxFit.cover)
                      : Center(
                          child: Text(
                            widget.required ? 'No image selected' : 'No image (optional)',
                            style: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong),
                          ),
                        ),
        ),
        const SizedBox(height: 10),
        Row(
          children: [
            Expanded(
              child: ElevatedButton.icon(
                onPressed: _uploading ? null : _showSourceSheet,
                icon: const Icon(Icons.image_outlined, size: 18),
                label: Text(_uploading ? 'Uploading…' : 'Choose image'),
              ),
            ),
            if (url.isNotEmpty || _localPreviewPath != null) ...[
              const SizedBox(width: 8),
              OutlinedButton(
                onPressed: _uploading
                    ? null
                    : () {
                        widget.controller.clear();
                        setState(() => _localPreviewPath = null);
                      },
                child: const Text('Clear'),
              ),
            ],
          ],
        ),
        const SizedBox(height: 8),
        TextFormField(
          controller: widget.controller,
          decoration: const InputDecoration(
            labelText: 'Or paste image URL',
            hintText: '/uploads/products/… or https://…',
          ),
          onChanged: (_) => setState(() {}),
          validator: widget.required
              ? (v) => v == null || v.trim().isEmpty ? 'Image required' : null
              : null,
        ),
      ],
    );
  }
}
