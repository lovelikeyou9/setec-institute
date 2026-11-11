class ProductCategory {
  const ProductCategory({
    required this.id,
    required this.name,
    this.slug,
    this.icon,
    this.description,
    this.sortOrder = 0,
  });

  final int id;
  final String name;
  final String? slug;
  final String? icon;
  final String? description;
  final int sortOrder;

  factory ProductCategory.fromJson(Map<String, dynamic> json) {
    return ProductCategory(
      id: _asInt(json['id']),
      name: json['name']?.toString() ?? 'Category',
      slug: json['slug']?.toString(),
      icon: (json['iconName'] ?? json['icon'])?.toString(),
      description: json['description']?.toString(),
      sortOrder: _asInt(json['sortOrder']),
    );
  }

  Map<String, dynamic> toJson() => {
        'name': name,
        'slug': slug ?? name.toLowerCase().replaceAll(RegExp(r'[^a-z0-9]+'), '-'),
        'iconName': icon,
        'description': description,
        'sortOrder': sortOrder,
      };
}

int _asInt(dynamic v) {
  if (v is int) return v;
  if (v is num) return v.toInt();
  return int.tryParse(v?.toString() ?? '') ?? 0;
}
