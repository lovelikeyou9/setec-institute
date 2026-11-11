import 'package:flutter/foundation.dart';

import '../models/product.dart';
import '../services/wishlist_service.dart';

class WishlistProvider extends ChangeNotifier {
  WishlistProvider(this._wishlist);

  final WishlistService _wishlist;

  List<Product> items = [];
  bool loading = false;
  String? error;

  bool contains(int productId) => items.any((p) => p.id == productId);

  Future<void> refresh() async {
    loading = true;
    error = null;
    notifyListeners();
    try {
      items = await _wishlist.fetchWishlist();
    } catch (e) {
      error = e.toString();
      items = [];
    } finally {
      loading = false;
      notifyListeners();
    }
  }

  Future<void> toggle(Product product) async {
    try {
      if (contains(product.id)) {
        await _wishlist.remove(product.id);
        items.removeWhere((p) => p.id == product.id);
      } else {
        await _wishlist.add(product.id);
        items = [...items, product];
      }
      notifyListeners();
    } catch (e) {
      error = e.toString();
      notifyListeners();
      rethrow;
    }
  }
}
