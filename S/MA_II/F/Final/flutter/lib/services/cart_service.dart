import '../models/cart_item.dart';
import 'api_client.dart';

class CartService {
  CartService(this._api);

  final ApiClient _api;

  Future<List<CartItem>> fetchCart() async {
    final list = _api.asList(await _api.get('/cart'));
    return list.map(CartItem.fromJson).toList();
  }

  Future<void> addToCart({required int productId, int quantity = 1}) async {
    await _api.post('/cart', body: {'productId': productId, 'quantity': quantity});
  }

  Future<void> updateQuantity({required int cartItemId, required int quantity}) async {
    await _api.put('/cart', body: {'id': cartItemId, 'quantity': quantity});
  }

  Future<void> removeItem(int cartItemId) async {
    await _api.delete('/cart/$cartItemId');
  }

  Future<void> clearCart() async {
    await _api.delete('/cart');
  }
}
