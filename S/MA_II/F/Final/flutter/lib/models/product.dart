import '../utils/json_utils.dart';

class Product {
  const Product({
    required this.id,
    required this.name,
    required this.brand,
    required this.sku,
    required this.categoryId,
    this.description,
    required this.price,
    this.originalPrice,
    required this.stock,
    this.warnMin = 5,
    this.binLocation,
    this.imageUrl,
    this.architecture,
    this.costPrice,
    this.specs = const {},
    this.tags = const [],
    this.featured = false,
    this.flashDrop = false,
  });

  final int id;
  final String name;
  final String brand;
  final String sku;
  final int categoryId;
  final String? description;
  final double price;
  final double? originalPrice;
  final int stock;
  final int warnMin;
  final String? binLocation;
  final String? imageUrl;
  final String? architecture;
  final double? costPrice;
  final Map<String, String> specs;
  final List<String> tags;
  final bool featured;
  final bool flashDrop;

  bool get inStock => stock > 0;
  bool get lowStock => stock > 0 && stock <= warnMin;
  double? get discountPercent {
    if (originalPrice == null || originalPrice! <= price) return null;
    return ((originalPrice! - price) / originalPrice!) * 100;
  }

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: _asInt(json['id']),
      name: json['name']?.toString() ?? '',
      brand: json['brand']?.toString() ?? '',
      sku: json['sku']?.toString() ?? '',
      categoryId: _asInt(json['categoryId'] ?? json['category']?['id']),
      description: json['description']?.toString(),
      price: _asDouble(json['price']),
      originalPrice: json['originalPrice'] != null ? _asDouble(json['originalPrice']) : null,
      stock: _asInt(json['stock']),
      warnMin: _asInt(json['warnMin'] ?? 5),
      binLocation: json['binLocation']?.toString(),
      imageUrl: json['imageUrl']?.toString(),
      architecture: json['architecture']?.toString(),
      costPrice: json['costPrice'] != null ? _asDouble(json['costPrice']) : null,
      specs: parseSpecsJson(json['specsJson'] ?? json['specs']),
      tags: parseTags(json['tags']),
      featured: json['featured'] == true || json['featured']?.toString() == 'true',
      flashDrop: json['flashDrop'] == true || json['flashDrop']?.toString() == 'true',
    );
  }

  Map<String, dynamic> toJson() => {
        if (id > 0) 'id': id,
        'name': name,
        'brand': brand,
        'sku': sku,
        'categoryId': categoryId,
        'description': description,
        'price': price,
        'originalPrice': originalPrice,
        'stock': stock,
        'warnMin': warnMin,
        'binLocation': binLocation,
        'imageUrl': imageUrl,
        'architecture': architecture,
        'costPrice': costPrice,
        'specsJson': specs,
        'tags': tags,
        'featured': featured,
        'flashDrop': flashDrop,
      };

  Product copyWith({
    int? id,
    String? name,
    String? brand,
    String? sku,
    int? categoryId,
    String? description,
    double? price,
    double? originalPrice,
    int? stock,
    int? warnMin,
    String? binLocation,
    String? imageUrl,
    String? architecture,
    double? costPrice,
    Map<String, String>? specs,
    List<String>? tags,
    bool? featured,
    bool? flashDrop,
  }) {
    return Product(
      id: id ?? this.id,
      name: name ?? this.name,
      brand: brand ?? this.brand,
      sku: sku ?? this.sku,
      categoryId: categoryId ?? this.categoryId,
      description: description ?? this.description,
      price: price ?? this.price,
      originalPrice: originalPrice ?? this.originalPrice,
      stock: stock ?? this.stock,
      warnMin: warnMin ?? this.warnMin,
      binLocation: binLocation ?? this.binLocation,
      imageUrl: imageUrl ?? this.imageUrl,
      architecture: architecture ?? this.architecture,
      costPrice: costPrice ?? this.costPrice,
      specs: specs ?? this.specs,
      tags: tags ?? this.tags,
      featured: featured ?? this.featured,
      flashDrop: flashDrop ?? this.flashDrop,
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
