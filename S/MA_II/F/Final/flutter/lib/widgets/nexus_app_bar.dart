import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../theme/app_theme.dart';

class NexusAppBar extends StatelessWidget implements PreferredSizeWidget {
  const NexusAppBar({
    super.key,
    this.title = 'MMT',
    this.showSearch = false,
    this.searchHint,
    this.onSearchChanged,
    this.searchController,
    this.actions,
  });

  final String title;
  final bool showSearch;
  final String? searchHint;
  final ValueChanged<String>? onSearchChanged;
  final TextEditingController? searchController;
  final List<Widget>? actions;

  @override
  Size get preferredSize => Size.fromHeight(showSearch ? 104 : kToolbarHeight);

  @override
  Widget build(BuildContext context) {
    return AppBar(
      title: Row(
        children: [
          Container(
            width: 28,
            height: 28,
            decoration: BoxDecoration(
              color: NexusColors.primary,
              borderRadius: BorderRadius.circular(4),
              border: Border.all(color: NexusColors.outlineStrong),
            ),
            alignment: Alignment.center,
            child: Text(
              'M',
              style: GoogleFonts.spaceGrotesk(
                color: Colors.white,
                fontWeight: FontWeight.w700,
                fontSize: 14,
              ),
            ),
          ),
          const SizedBox(width: 8),
          Text(
            title,
            style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700),
          ),
        ],
      ),
      actions: actions,
      bottom: showSearch
          ? PreferredSize(
              preferredSize: const Size.fromHeight(56),
              child: Padding(
                padding: const EdgeInsets.fromLTRB(16, 0, 16, 12),
                child: TextField(
                  controller: searchController,
                  onChanged: onSearchChanged,
                  decoration: InputDecoration(
                    hintText: searchHint ?? 'Search tech, gadgets, SKUs…',
                    prefixIcon: const Icon(Icons.search, size: 20),
                    isDense: true,
                  ),
                ),
              ),
            )
          : null,
    );
  }
}
