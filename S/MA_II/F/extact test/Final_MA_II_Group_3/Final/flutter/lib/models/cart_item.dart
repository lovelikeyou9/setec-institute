import 'product.dart';

class CartItem {
  const CartItem({
    required this.id,
    required this.product,
    required this.quantity,
  });

  final int id;
  final Product product;
  final int quantity;

  double get lineTotal => product.price * quantity;

  factory CartItem.fromJson(Map<String, dynamic> json) {
    final productJson = json['product'] as Map<String, dynamic>? ?? json;
    return CartItem(
      id: _asInt(json['id']),
      product: Product.fromJson(productJson),
      quantity: _asInt(json['quantity'] ?? 1),
    );
  }
}

int _asInt(dynamic v) {
  if (v is int) return v;
  if (v is num) return v.toInt();
  return int.tryParse(v?.toString() ?? '') ?? 0;
}
