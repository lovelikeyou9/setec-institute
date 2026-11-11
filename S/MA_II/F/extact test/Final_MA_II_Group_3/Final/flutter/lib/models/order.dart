import 'product.dart';

class OrderLine {
  const OrderLine({
    required this.productName,
    required this.quantity,
    required this.unitPrice,
    this.productId,
    this.sku,
    this.brand,
    this.imageUrl,
    this.lineTotal,
  });

  final int? productId;
  final String productName;
  final String? sku;
  final String? brand;
  final String? imageUrl;
  final int quantity;
  final double unitPrice;
  final double? lineTotal;

  double get total => lineTotal ?? (unitPrice * quantity);

  factory OrderLine.fromJson(Map<String, dynamic> json) {
    return OrderLine(
      productId: json['productId'] != null ? _asInt(json['productId']) : null,
      productName: json['productName']?.toString() ??
          json['product']?['name']?.toString() ??
          'Item',
      sku: json['sku']?.toString() ?? json['product']?['sku']?.toString(),
      brand: json['brand']?.toString() ?? json['product']?['brand']?.toString(),
      imageUrl: json['imageUrl']?.toString() ?? json['product']?['imageUrl']?.toString(),
      quantity: _asInt(json['quantity'] ?? 1),
      unitPrice: _asDouble(json['unitPrice'] ?? json['price'] ?? json['product']?['price']),
      lineTotal: json['lineTotal'] != null ? _asDouble(json['lineTotal']) : null,
    );
  }
}

class ShopOrder {
  const ShopOrder({
    required this.id,
    required this.status,
    required this.total,
    required this.createdAt,
    this.orderNumber,
    this.items = const [],
    this.discountCode,
    this.customerId,
    this.customerName,
    this.customerEmail,
    this.subtotal = 0,
    this.tax = 0,
    this.shipping = 0,
    this.discount = 0,
    this.shippingAddress,
  });

  final int id;
  final String? orderNumber;
  final String status;
  final double total;
  final DateTime? createdAt;
  final List<OrderLine> items;
  final String? discountCode;
  final int? customerId;
  final String? customerName;
  final String? customerEmail;
  final double subtotal;
  final double tax;
  final double shipping;
  final double discount;
  final String? shippingAddress;

  String get displayId => orderNumber ?? '#$id';

  int get totalUnits => items.fold(0, (sum, i) => sum + i.quantity);

  String get itemSummary {
    if (items.isEmpty) return 'No line items';
    if (items.length == 1) {
      return '${items.first.productName} × ${items.first.quantity}';
    }
    return '${items.first.productName} + ${items.length - 1} more';
  }

  bool get isCompleted {
    final s = status.toUpperCase();
    return s.contains('COMPLETE') || s.contains('DELIVERED') || s == 'PAID' || s == 'SHIPPED';
  }

  factory ShopOrder.fromJson(Map<String, dynamic> json) {
    final itemsRaw = json['items'] ?? json['orderItems'] ?? json['lines'];
    final items = <OrderLine>[];
    if (itemsRaw is List) {
      for (final item in itemsRaw) {
        if (item is Map<String, dynamic>) {
          items.add(OrderLine.fromJson(item));
        }
      }
    }
    return ShopOrder(
      id: _asInt(json['id']),
      orderNumber: json['orderNumber']?.toString(),
      status: json['status']?.toString() ?? 'PENDING',
      total: _asDouble(json['total'] ?? json['totalAmount'] ?? json['grandTotal']),
      createdAt: DateTime.tryParse(json['createdAt']?.toString() ?? ''),
      items: items,
      discountCode: json['discountCode']?.toString(),
      customerId: json['customerId'] != null ? _asInt(json['customerId']) : null,
      customerName: json['customerName']?.toString(),
      customerEmail: json['customerEmail']?.toString(),
      subtotal: _asDouble(json['subtotal']),
      tax: _asDouble(json['tax']),
      shipping: _asDouble(json['shipping']),
      discount: _asDouble(json['discount']),
      shippingAddress: json['shippingAddress']?.toString(),
    );
  }
}

class WishlistEntry {
  const WishlistEntry({required this.productId, this.product});

  final int productId;
  final Product? product;

  factory WishlistEntry.fromJson(Map<String, dynamic> json) {
    final pid = _asInt(json['productId'] ?? json['product']?['id']);
    Product? product;
    if (json['product'] is Map<String, dynamic>) {
      product = Product.fromJson(json['product'] as Map<String, dynamic>);
    }
    return WishlistEntry(productId: pid, product: product);
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
