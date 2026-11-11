

import 'package:test_2_f/features/model/product.dart';

class ProductService {
  List<Product> products = [
    Product(
      id: 1,
      name: "Americano",
      price: 1.5,
      photo: "assets/coffee-shop/images.jpg"
    ),
    Product(
        id: 2,
        name: "Sukuna",
        price: 2.5,
        photo: "assets/coffee-shop/001.png"
    ),
    Product(
        id: 3,
        name: "Americano",
        price: 3.5,
        photo: "assets/coffee-shop/Americano.jpg"
    ),
    Product(
        id: 4,
        name: "Mocha",
        price: 4.5,
        photo: "assets/coffee-shop/Mocha.jpg"
    ),
  ];

}