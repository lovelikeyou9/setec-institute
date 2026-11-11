import '../models/order.dart';
import '../models/product.dart';
import 'api_client.dart';

class WishlistService {
  WishlistService(this._api);

  final ApiClient _api;

  Future<List<Product>> fetchWishlist() async {
    final list = _api.asList(await _api.get('/wishlist'));
    final products = <Product>[];
    for (final row in list) {
      if (row['product'] is Map) {
        products.add(Product.fromJson(Map<String, dynamic>.from(row['product'] as Map)));
      } else if (row.containsKey('name')) {
        products.add(Product.fromJson(row));
      } else {
        final entry = WishlistEntry.fromJson(row);
        if (entry.product != null) products.add(entry.product!);
      }
    }
    return products;
  }

  Future<void> add(int productId) async {
    await _api.post('/wishlist/$productId');
  }

  Future<void> remove(int productId) async {
    await _api.delete('/wishlist/$productId');
  }
}
