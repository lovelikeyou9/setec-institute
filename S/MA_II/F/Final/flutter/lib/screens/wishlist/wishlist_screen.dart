import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '../../providers/wishlist_provider.dart';
import '../../theme/app_theme.dart';
import '../../widgets/product_card.dart';
import '../product/product_detail_screen.dart';

class WishlistScreen extends StatefulWidget {
  const WishlistScreen({super.key});

  @override
  State<WishlistScreen> createState() => _WishlistScreenState();
}

class _WishlistScreenState extends State<WishlistScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<WishlistProvider>().refresh();
    });
  }

  @override
  Widget build(BuildContext context) {
    final wishlist = context.watch<WishlistProvider>();

    return Scaffold(
      appBar: AppBar(
        title: Text('Favorites', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
      ),
      body: wishlist.loading && wishlist.items.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: wishlist.refresh,
              child: wishlist.items.isEmpty
                  ? ListView(
                      children: [
                        const SizedBox(height: 72),
                        Icon(Icons.favorite_border, size: 48, color: NexusColors.outlineStrong.withValues(alpha: 0.7)),
                        const SizedBox(height: 12),
                        Center(
                          child: Text('No favorites yet', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
                        ),
                        const SizedBox(height: 6),
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 40),
                          child: Text(
                            'Tap the heart on any product to save it here.',
                            textAlign: TextAlign.center,
                            style: GoogleFonts.inter(color: NexusColors.outlineStrong, fontSize: 13),
                          ),
                        ),
                      ],
                    )
                  : ListView.builder(
                      padding: const EdgeInsets.all(16),
                      itemCount: wishlist.items.length + 1,
                      itemBuilder: (context, index) {
                        if (index == 0) {
                          return Padding(
                            padding: const EdgeInsets.only(bottom: 12),
                            child: Text(
                              '${wishlist.items.length} saved item${wishlist.items.length == 1 ? '' : 's'}',
                              style: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong),
                            ),
                          );
                        }
                        final product = wishlist.items[index - 1];
                        return Padding(
                          padding: const EdgeInsets.only(bottom: 12),
                          child: ProductCard(
                            product: product,
                            onTap: () {
                              Navigator.of(context).push(
                                MaterialPageRoute(
                                  builder: (_) => ProductDetailScreen(
                                    productId: product.id,
                                    initialProduct: product,
                                  ),
                                ),
                              );
                            },
                          ),
                        );
                      },
                    ),
            ),
    );
  }
}
