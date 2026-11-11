

import 'package:flutter/cupertino.dart';
import 'package:test_5_f/features/product/product_response.dart';
import 'package:test_5_f/features/product/product_service.dart';

class ProductProvider extends ChangeNotifier {
  final ProductService _productService = ProductService();
  List<Product> _products = [];
  List<Product> products() => _products;
  Future<void> getProducts() async {
    _products = await _productService.getProducts();
    print(products().length);
    notifyListeners();
  }

}