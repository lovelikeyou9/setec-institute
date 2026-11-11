

import 'package:dio/dio.dart';
import 'package:test_6_f/core/api_contants/api_constant.dart';
import 'package:test_6_f/features/product/product_response.dart';

class ProductService {


  Future<List<Product>> getProducts() async {
    var dio = Dio();
    var response = await dio.get(
      "${ApiConstant.urlBase}${ApiConstant.productListEndPoint}",);
    if(response.statusCode==200){
      final List data = response.data["content"] ?? [];
      return data.map((json) => Product.fromJson(json)).toList();
    } else {
      throw Exception("Error: ${response.statusCode}");
    }
  }

}