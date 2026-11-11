import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../models/product.dart';
import '../theme/app_theme.dart';

class StockBadge extends StatelessWidget {
  const StockBadge({super.key, required this.product});

  final Product product;

  @override
  Widget build(BuildContext context) {
    final Color bg;
    final Color fg;
    final String label;

    if (!product.inStock) {
      bg = NexusColors.error.withValues(alpha: 0.1);
      fg = NexusColors.error;
      label = 'Out of stock';
    } else if (product.lowStock) {
      bg = NexusColors.lowStock.withValues(alpha: 0.12);
      fg = NexusColors.lowStock;
      label = 'Low · ${product.stock}';
    } else {
      bg = NexusColors.success.withValues(alpha: 0.12);
      fg = NexusColors.success;
      label = 'In stock · ${product.stock}';
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: bg,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: fg.withValues(alpha: 0.35)),
      ),
      child: Text(
        label,
        style: GoogleFonts.jetBrainsMono(fontSize: 10, color: fg, fontWeight: FontWeight.w600),
      ),
    );
  }
}
