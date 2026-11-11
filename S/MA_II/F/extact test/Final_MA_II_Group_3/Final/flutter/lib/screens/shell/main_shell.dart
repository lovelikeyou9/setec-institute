import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../providers/cart_provider.dart';
import '../../providers/wishlist_provider.dart';
import '../../widgets/nexus_bottom_nav.dart';
import '../cart/cart_screen.dart';
import '../catalog/catalog_screen.dart';
import '../profile/profile_screen.dart';
import '../store/store_home_screen.dart';
import '../wishlist/wishlist_screen.dart';

class MainShell extends StatefulWidget {
  const MainShell({super.key});

  @override
  State<MainShell> createState() => _MainShellState();
}

class _MainShellState extends State<MainShell> {
  int _index = 0;
  final _catalogKey = GlobalKey<CatalogScreenState>();
  final _profileKey = GlobalKey<ProfileScreenState>();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<CartProvider>().refresh();
      context.read<WishlistProvider>().refresh();
    });
  }

  void _goToCatalog({int? categoryId, String? search}) {
    setState(() => _index = 1);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final catalog = _catalogKey.currentState;
      if (catalog == null) return;
      if (categoryId != null) {
        catalog.applyFilters(categoryId: categoryId, clearSearch: true);
      } else if (search != null) {
        catalog.applyFilters(search: search, clearCategory: true);
      }
    });
  }

  void _onNavTap(int i) {
    setState(() => _index = i);
    if (i == 4) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        _profileKey.currentState?.reload();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final cartCount = context.watch<CartProvider>().itemCount;

    final pages = [
      StoreHomeScreen(
        onBrowseCategory: (cat) => _goToCatalog(categoryId: cat.id),
        onSearch: (q) => _goToCatalog(search: q),
      ),
      CatalogScreen(key: _catalogKey),
      const CartScreen(),
      const WishlistScreen(),
      ProfileScreen(key: _profileKey),
    ];

    return Scaffold(
      body: IndexedStack(index: _index, children: pages),
      bottomNavigationBar: NexusBottomNav(
        currentIndex: _index,
        cartCount: cartCount,
        onTap: _onNavTap,
      ),
    );
  }
}
