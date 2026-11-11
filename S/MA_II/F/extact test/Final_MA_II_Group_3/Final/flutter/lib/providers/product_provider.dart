import 'package:flutter/foundation.dart';

import '../models/brand.dart';
import '../models/category.dart';
import '../models/product.dart';
import '../services/product_service.dart';

class ProductProvider extends ChangeNotifier {
  ProductProvider(this._products);

  final ProductService _products;

  List<ProductCategory> categories = [];
  List<Brand> brands = [];
  List<Product> catalog = [];
  List<Product> featured = [];
  List<Product> flashDrops = [];
  bool loading = false;
  String? error;

  Future<void> loadHomeData() async {
    loading = true;
    error = null;
    notifyListeners();
    try {
      final results = await Future.wait([
        _products.fetchCategories(),
        _products.fetchBrands(),
        _products.fetchProducts(featured: true),
        _products.fetchProducts(flashDrop: true),
        _products.fetchProducts(),
      ]);
      categories = results[0] as List<ProductCategory>;
      brands = (results[1] as List<Brand>).where((b) => b.active).toList();
      featured = results[2] as List<Product>;
      flashDrops = results[3] as List<Product>;
      catalog = results[4] as List<Product>;
    } catch (e) {
      error = e.toString();
    } finally {
      loading = false;
      notifyListeners();
    }
  }

  Future<List<Product>> searchCatalog({
    int? categoryId,
    String? search,
    String? brand,
    double? minPrice,
    double? maxPrice,
    bool? inStock,
    String? sort,
  }) async {
    loading = true;
    error = null;
    notifyListeners();
    try {
      catalog = await _products.fetchProducts(
        categoryId: categoryId,
        search: search,
        brand: brand,
        minPrice: minPrice,
        maxPrice: maxPrice,
        inStock: inStock,
        sort: sort,
      );
      return catalog;
    } catch (e) {
      error = e.toString();
      return [];
    } finally {
      loading = false;
      notifyListeners();
    }
  }

  Future<Product?> fetchById(int id) async {
    try {
      return await _products.fetchProduct(id);
    } catch (e) {
      error = e.toString();
      notifyListeners();
      return null;
    }
  }
}
