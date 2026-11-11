class Brand {
  const Brand({
    required this.id,
    required this.name,
    required this.slug,
    this.logoUrl,
    this.description,
    this.active = true,
  });

  final int id;
  final String name;
  final String slug;
  final String? logoUrl;
  final String? description;
  final bool active;

  factory Brand.fromJson(Map<String, dynamic> json) {
    return Brand(
      id: _asInt(json['id']),
      name: json['name']?.toString() ?? '',
      slug: json['slug']?.toString() ?? '',
      logoUrl: json['logoUrl']?.toString(),
      description: json['description']?.toString(),
      active: json['active'] != false,
    );
  }

  Map<String, dynamic> toJson() => {
        'name': name,
        'slug': slug,
        'logoUrl': logoUrl,
        'description': description,
        'active': active,
      };
}

int _asInt(dynamic v) {
  if (v is int) return v;
  if (v is num) return v.toInt();
  return int.tryParse(v?.toString() ?? '') ?? 0;
}
