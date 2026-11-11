import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../models/category.dart';
import '../theme/app_theme.dart';

class CategoryGrid extends StatelessWidget {
  const CategoryGrid({
    super.key,
    required this.categories,
    this.onTap,
  });

  final List<ProductCategory> categories;
  final void Function(ProductCategory category)? onTap;

  static IconData iconFor(String? slug, int index) {
    switch (slug) {
      case 'laptops-pcs':
        return Icons.laptop_mac;
      case 'components':
        return Icons.memory;
      case 'monitors-tvs':
        return Icons.tv;
      case 'networking':
        return Icons.wifi;
      case 'drones-cameras':
        return Icons.videocam_outlined;
      case 'audio-streaming':
        return Icons.mic_none;
      case 'gaming-consoles':
        return Icons.sports_esports;
      case 'storage-nas':
        return Icons.storage;
      case 'accessories':
        return Icons.cable;
      case 'gift-cards':
        return Icons.card_giftcard;
      default:
        const fallback = [
          Icons.devices_other,
          Icons.memory,
          Icons.tv,
          Icons.wifi,
          Icons.videocam_outlined,
          Icons.mic_none,
        ];
        return fallback[index % fallback.length];
    }
  }

  @override
  Widget build(BuildContext context) {
    final visible = categories.take(6).toList();
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: GridView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          mainAxisSpacing: 8,
          crossAxisSpacing: 8,
          childAspectRatio: 1.05,
        ),
        itemCount: visible.length,
        itemBuilder: (context, index) {
          final cat = visible[index];
          final icon = iconFor(cat.slug, index);
          return InkWell(
            onTap: onTap == null ? null : () => onTap!(cat),
            borderRadius: BorderRadius.circular(8),
            child: Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: NexusColors.outline),
              ),
              padding: const EdgeInsets.all(10),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(icon, color: NexusColors.primary, size: 22),
                  const SizedBox(height: 6),
                  Text(
                    cat.name,
                    textAlign: TextAlign.center,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: GoogleFonts.inter(fontSize: 11, fontWeight: FontWeight.w600),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
