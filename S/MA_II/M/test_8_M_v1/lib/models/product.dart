import 'package:flutter/material.dart';

class Product {
  final String name;
  final String category;
  final String price;
  final String size;
  final String imagePath;
  final Color bgColor;
  final Color detailColor;

  const Product({
    required this.name,
    required this.category,
    required this.price,
    required this.size,
    required this.imagePath,
    required this.bgColor,
    required this.detailColor,
  });
}

const List<Product> demoProducts = [
  Product(
    name: 'Office Code',
    category: 'Aristocratic Hand Bag',
    price: '\$234',
    size: '12 cm',
    imagePath: 'assets/coffee-shop/Espresso.jpg',
    bgColor: Color(0xFF3D82AE),
    detailColor: Color(0xFF3D82AE),
  ),
  Product(
    name: 'Belt Bag',
    category: 'Aristocratic Hand Bag',
    price: '\$234',
    size: '12 cm',
    imagePath: 'assets/coffee-shop/Cafe Latte.jpg',
    bgColor: Color(0xFFD3A984),
    detailColor: Color(0xFFD3A984),
  ),
  Product(
    name: 'Hang Top',
    category: 'Aristocratic Hand Bag',
    price: '\$234',
    size: '12 cm',
    imagePath: 'assets/coffee-shop/Cappuccino.jpg',
    bgColor: Color(0xFF989493),
    detailColor: Color(0xFF989493),
  ),
  Product(
    name: 'Old Fashion',
    category: 'Aristocratic Hand Bag',
    price: '\$234',
    size: '12 cm',
    imagePath: 'assets/coffee-shop/Caramel Latte.jpg',
    bgColor: Color(0xFFE6B398),
    detailColor: Color(0xFFE6B398),
  ),
  Product(
    name: 'Sit bag',
    category: 'Aristocratic Hand Bag',
    price: '\$234',
    size: '12 cm',
    imagePath: 'assets/coffee-shop/Mocha.jpg',
    bgColor: Color(0xFFFB7883),
    detailColor: Color(0xFFFB7883),
  ),
  Product(
    name: 'Travel Bag',
    category: 'Aristocratic Hand Bag',
    price: '\$234',
    size: '12 cm',
    imagePath: 'assets/coffee-shop/Americano.jpg',
    bgColor: Color(0xFFAEAEAE),
    detailColor: Color(0xFFAEAEAE),
  ),
];
