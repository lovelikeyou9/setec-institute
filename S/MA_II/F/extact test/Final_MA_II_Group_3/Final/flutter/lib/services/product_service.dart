import '../models/brand.dart';
import '../models/category.dart';
import '../models/product.dart';
import 'api_client.dart';

class ProductService {
  ProductService(this._api);

  final ApiClient _api;

  Future<List<ProductCategory>> fetchCategories() async {
    final list = _api.asList(await _api.get('/categories', auth: false));
    return list.map(ProductCategory.fromJson).toList();
  }

  Future<List<Brand>> fetchBrands() async {
    final list = _api.asList(await _api.get('/brands', auth: false));
    return list.map(Brand.fromJson).toList();
  }

  Future<List<Product>> fetchProducts({
    int? categoryId,
    String? search,
    bool? featured,
    bool? flashDrop,
    String? brand,
    double? minPrice,
    double? maxPrice,
    bool? inStock,
    String? sort,
  }) async {
    final query = <String, String>{};
    if (categoryId != null) query['categoryId'] = categoryId.toString();
    if (search != null && search.isNotEmpty) query['search'] = search;
    if (featured == true) query['featured'] = 'true';
    if (flashDrop == true) query['flashDrop'] = 'true';
    if (brand != null && brand.isNotEmpty) query['brand'] = brand;
    if (minPrice != null) query['minPrice'] = minPrice.toString();
    if (maxPrice != null) query['maxPrice'] = maxPrice.toString();
    if (inStock == true) query['inStock'] = 'true';
    if (sort != null && sort.isNotEmpty) query['sort'] = sort;

    final list = _api.asList(await _api.get('/products', query: query, auth: false));
    return list.map(Product.fromJson).toList();
  }

  Future<Product> fetchProduct(int id) async {
    final data = _api.asMap(await _api.get('/products/$id', auth: false));
    return Product.fromJson(data);
  }

  Future<Product> createProduct(Map<String, dynamic> body) async {
    final data = _api.asMap(await _api.post('/products', body: body));
    return Product.fromJson(data);
  }

  Future<Product> updateProduct(int id, Map<String, dynamic> body) async {
    final data = _api.asMap(await _api.put('/products/$id', body: body));
    return Product.fromJson(data);
  }

  Future<void> deleteProduct(int id) async {
    await _api.delete('/products/$id');
  }
}
