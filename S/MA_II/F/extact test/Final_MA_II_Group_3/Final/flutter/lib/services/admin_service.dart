import '../models/admin_stats.dart';
import '../models/brand.dart';
import '../models/cart_item.dart';
import '../models/category.dart';
import '../models/order.dart';
import '../models/product.dart';
import '../models/user.dart';
import 'api_client.dart';

class CustomerActivity {
  const CustomerActivity({
    required this.customer,
    required this.orders,
    required this.cart,
    required this.favorites,
    required this.totalSpent,
    required this.orderCount,
    required this.cartItemCount,
    required this.favoriteCount,
  });

  final User customer;
  final List<ShopOrder> orders;
  final List<CartItem> cart;
  final List<Product> favorites;
  final double totalSpent;
  final int orderCount;
  final int cartItemCount;
  final int favoriteCount;

  factory CustomerActivity.fromJson(Map<String, dynamic> json) {
    final ordersRaw = json['orders'];
    final cartRaw = json['cart'];
    final favRaw = json['favorites'];
    final orders = <ShopOrder>[];
    final cart = <CartItem>[];
    final favorites = <Product>[];

    if (ordersRaw is List) {
      for (final row in ordersRaw) {
        if (row is Map<String, dynamic>) orders.add(ShopOrder.fromJson(row));
      }
    }
    if (cartRaw is List) {
      for (final row in cartRaw) {
        if (row is Map<String, dynamic>) cart.add(CartItem.fromJson(row));
      }
    }
    if (favRaw is List) {
      for (final row in favRaw) {
        if (row is Map<String, dynamic>) {
          if (row['product'] is Map<String, dynamic>) {
            favorites.add(Product.fromJson(row['product'] as Map<String, dynamic>));
          } else if (row.containsKey('name')) {
            favorites.add(Product.fromJson(row));
          }
        }
      }
    }

    return CustomerActivity(
      customer: User.fromJson(Map<String, dynamic>.from(json['customer'] as Map? ?? {})),
      orders: orders,
      cart: cart,
      favorites: favorites,
      totalSpent: _asDouble(json['totalSpent']),
      orderCount: _asInt(json['orderCount'] ?? orders.length),
      cartItemCount: _asInt(json['cartItemCount']),
      favoriteCount: _asInt(json['favoriteCount'] ?? favorites.length),
    );
  }
}

int _asInt(dynamic v) {
  if (v is int) return v;
  if (v is num) return v.toInt();
  return int.tryParse(v?.toString() ?? '') ?? 0;
}

double _asDouble(dynamic v) {
  if (v is double) return v;
  if (v is num) return v.toDouble();
  return double.tryParse(v?.toString() ?? '') ?? 0;
}

class AdminService {
  AdminService(this._api);

  final ApiClient _api;

  Future<AdminStats> fetchStats() async {
    final data = _api.asMap(await _api.get('/admin/stats'));
    return AdminStats.fromJson(data);
  }

  Future<List<ShopOrder>> fetchOrders() async {
    final list = _api.asList(await _api.get('/admin/orders'));
    return list.map(ShopOrder.fromJson).toList();
  }

  Future<ShopOrder> updateOrderStatus(int orderId, String status) async {
    final data = _api.asMap(
      await _api.put('/admin/orders/$orderId/status', body: {'status': status}),
    );
    return ShopOrder.fromJson(data);
  }

  Future<List<AuditLogEntry>> fetchAuditLogs() async {
    final list = _api.asList(await _api.get('/admin/audit-logs'));
    return list.map(AuditLogEntry.fromJson).toList();
  }

  Future<Product> updateStock(int productId, int stock) async {
    final data = _api.asMap(
      await _api.put('/admin/products/$productId/stock', body: {'stock': stock}),
    );
    return Product.fromJson(data);
  }

  // —— Categories ——
  Future<List<ProductCategory>> fetchCategories() async {
    final list = _api.asList(await _api.get('/categories'));
    return list.map(ProductCategory.fromJson).toList();
  }

  Future<ProductCategory> createCategory(Map<String, dynamic> body) async {
    return ProductCategory.fromJson(_api.asMap(await _api.post('/categories', body: body)));
  }

  Future<ProductCategory> updateCategory(int id, Map<String, dynamic> body) async {
    return ProductCategory.fromJson(_api.asMap(await _api.put('/categories/$id', body: body)));
  }

  Future<void> deleteCategory(int id) async {
    await _api.delete('/categories/$id');
  }

  // —— Brands ——
  Future<List<Brand>> fetchBrands() async {
    final list = _api.asList(await _api.get('/brands'));
    return list.map(Brand.fromJson).toList();
  }

  Future<Brand> createBrand(Map<String, dynamic> body) async {
    return Brand.fromJson(_api.asMap(await _api.post('/brands', body: body)));
  }

  Future<Brand> updateBrand(int id, Map<String, dynamic> body) async {
    return Brand.fromJson(_api.asMap(await _api.put('/brands/$id', body: body)));
  }

  Future<void> deleteBrand(int id) async {
    await _api.delete('/brands/$id');
  }

  // —— Customers ——
  Future<List<User>> fetchCustomers() async {
    final list = _api.asList(await _api.get('/admin/customers'));
    return list.map(User.fromJson).toList();
  }

  Future<User> createCustomer(Map<String, dynamic> body) async {
    return User.fromJson(_api.asMap(await _api.post('/admin/customers', body: body)));
  }

  Future<User> updateCustomer(int id, Map<String, dynamic> body) async {
    return User.fromJson(_api.asMap(await _api.put('/admin/customers/$id', body: body)));
  }

  Future<void> deleteCustomer(int id) async {
    await _api.delete('/admin/customers/$id');
  }

  Future<User> banCustomer(int id, {String? reason}) async {
    return User.fromJson(_api.asMap(await _api.put(
      '/admin/customers/$id/ban',
      body: {'reason': reason ?? 'Banned by admin'},
    )));
  }

  Future<User> unbanCustomer(int id) async {
    return User.fromJson(_api.asMap(await _api.put('/admin/customers/$id/unban')));
  }

  Future<CustomerActivity> fetchCustomerActivity(int customerId) async {
    final data = _api.asMap(await _api.get('/admin/customers/$customerId/activity'));
    return CustomerActivity.fromJson(data);
  }
}
