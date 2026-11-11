import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../theme/app_theme.dart';

class AdminCrudHeader extends StatelessWidget {
  const AdminCrudHeader({
    super.key,
    required this.title,
    required this.countLabel,
    required this.addLabel,
    required this.onAdd,
    this.onRefresh,
  });

  final String title;
  final String countLabel;
  final String addLabel;
  final VoidCallback onAdd;
  final VoidCallback? onRefresh;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.fromLTRB(16, 8, 16, 12),
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
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w800, fontSize: 18)),
                    const SizedBox(height: 2),
                    Text(countLabel, style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong)),
                  ],
                ),
              ),
              if (onRefresh != null)
                IconButton(
                  onPressed: onRefresh,
                  icon: const Icon(Icons.refresh, size: 20),
                  tooltip: 'Refresh',
                ),
            ],
          ),
          const SizedBox(height: 10),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: onAdd,
              icon: const Icon(Icons.add, size: 18),
              label: Text(addLabel),
            ),
          ),
        ],
      ),
    );
  }
}

class AdminActionBar extends StatelessWidget {
  const AdminActionBar({
    super.key,
    required this.onEdit,
    required this.onDelete,
    this.editLabel = 'Edit',
    this.deleteLabel = 'Delete',
  });

  final VoidCallback onEdit;
  final VoidCallback onDelete;
  final String editLabel;
  final String deleteLabel;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: OutlinedButton.icon(
            onPressed: onEdit,
            icon: const Icon(Icons.edit_outlined, size: 16),
            label: Text(editLabel),
          ),
        ),
        const SizedBox(width: 8),
        Expanded(
          child: OutlinedButton.icon(
            onPressed: onDelete,
            style: OutlinedButton.styleFrom(
              foregroundColor: NexusColors.error,
              side: BorderSide(color: NexusColors.error.withValues(alpha: 0.45)),
            ),
            icon: const Icon(Icons.delete_outline, size: 16),
            label: Text(deleteLabel),
          ),
        ),
      ],
    );
  }
}
