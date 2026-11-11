

class ProductResponse {
  ProductResponse({
    required this.content,
    this.pageNo,
    this.pageSize,
    this.totalElements,
    this.totalPages,
    this.last,
    this.first,
  });

  final List<Product> content;
  final int? pageNo;
  final int? pageSize;
  final int? totalElements;
  final int? totalPages;
  final bool? last;
  final bool? first;

  factory ProductResponse.fromJson(Map<String, dynamic> json) {
    return ProductResponse(
      content: json["content"] == null
          ? []
          : List<Product>.from(
        (json["content"] as List).map(
              (x) => Product.fromJson(x as Map<String, dynamic>),
        ),
      ),
      pageNo: json["pageNo"] ?? json["pageNumber"],
      pageSize: json["pageSize"],
      totalElements: json["totalElements"],
      totalPages: json["totalPages"],
      last: json["last"],
      first: json["first"],
    );
  }

  Map<String, dynamic> toJson() => {
    "content": content.map((x) => x.toJson()).toList(),
    "pageNo": pageNo,
    "pageSize": pageSize,
    "totalElements": totalElements,
    "totalPages": totalPages,
    "last": last,
    "first": first,
  };
}

class Product {
  Product({
    this.id,
    this.name,
    this.price,
    this.photo,
  });

  final int? id;
  final String? name;
  final double? price;
  final String? photo;

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: json["id"] is int
          ? json["id"]
          : int.tryParse(json["id"]?.toString() ?? ""),
      name: json["name"]?.toString(),
      price: (json["price"] as num?)?.toDouble(),
      photo: json["photo"]?.toString(),
    );
  }

  Map<String, dynamic> toJson() => {
    "id": id,
    "name": name,
    "price": price,
    "photo": photo,
  };
}

/// Alias for backward compatibility with existing card/cart code
typedef Content = Product;