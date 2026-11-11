import 'package:flutter/foundation.dart';

import '../models/cart_item.dart';
import '../services/cart_service.dart';

class CartProvider extends ChangeNotifier {
  CartProvider(this._cart);

  final CartService _cart;

  List<CartItem> items = [];
  bool loading = false;
  String? error;
  String discountCode = '';

  int get itemCount => items.fold(0, (sum, i) => sum + i.quantity);

  double get subtotal => items.fold(0, (sum, i) => sum + i.lineTotal);

  /// Matches Spring Boot OrderService: flat $50 off for CYBER2025.
  double get discount {
    if (discountCode.trim().toUpperCase() == 'CYBER2025') {
      return 50.0;
    }
    return 0;
  }

  double get shipping => subtotal >= 500 ? 0 : 15;

  double get tax {
    final taxable = (subtotal - discount).clamp(0, double.infinity);
    return double.parse((taxable * 0.07).toStringAsFixed(2));
  }

  double get total {
    final taxable = (subtotal - discount).clamp(0, double.infinity);
    return double.parse((taxable + tax + shipping).toStringAsFixed(2));
  }

  Future<void> refresh() async {
    loading = true;
    error = null;
    notifyListeners();
    try {
      items = await _cart.fetchCart();
    } catch (e) {
      error = e.toString();
      items = [];
    } finally {
      loading = false;
      notifyListeners();
    }
  }

  Future<void> addProduct(int productId, {int quantity = 1}) async {
    try {
      await _cart.addToCart(productId: productId, quantity: quantity);
      await refresh();
    } catch (e) {
      error = e.toString();
      notifyListeners();
      rethrow;
    }
  }

  Future<void> setQuantity(CartItem item, int quantity) async {
    if (quantity <= 0) {
      await removeItem(item);
      return;
    }
    await _cart.updateQuantity(cartItemId: item.id, quantity: quantity);
    await refresh();
  }

  Future<void> removeItem(CartItem item) async {
    await _cart.removeItem(item.id);
    await refresh();
  }

  void setDiscountCode(String code) {
    discountCode = code;
    notifyListeners();
  }
}
