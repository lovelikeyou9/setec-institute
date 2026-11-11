import 'package:flutter/material.dart';

class Product {
  final String name;
  final String price;
  final String category;
  final Color cardColor;
  final Color detailColor;
  final String size;
  final String imagePath;

  const Product({
    required this.name,
    required this.price,
    required this.category,
    required this.cardColor,
    required this.detailColor,
    required this.imagePath,
    this.size = '12 cm',
  });
}

const List<Product> products = [
  Product(
    name: 'Office Code',
    price: '\$234',
    category: 'Aristocratic Hand Bag',
    cardColor: Color(0xFF5B8EC4),
    detailColor: Color(0xFF4A90B8),
    imagePath: 'assets/coffee-shop/Americano.jpg',
  ),
  Product(
    name: 'Belt Bag',
    price: '\$234',
    category: 'Aristocratic Hand Bag',
    cardColor: Color(0xFFE8C9A0),
    detailColor: Color(0xFFD4A574),
    imagePath: 'assets/coffee-shop/Cafe Latte.jpg',
  ),
  Product(
    name: 'Hang Top',
    price: '\$234',
    category: 'Aristocratic Hand Bag',
    cardColor: Color(0xFF9E9E9E),
    detailColor: Color(0xFF757575),
    imagePath: 'assets/coffee-shop/Cappuccino.jpg',
  ),
  Product(
    name: 'Old Fashion',
    price: '\$234',
    category: 'Aristocratic Hand Bag',
    cardColor: Color(0xFFF5C4A8),
    detailColor: Color(0xFFE8A882),
    imagePath: 'assets/coffee-shop/Caramel Latte.jpg',
  ),
  Product(
    name: 'Red Charm',
    price: '\$234',
    category: 'Aristocratic Hand Bag',
    cardColor: Color(0xFFE57373),
    detailColor: Color(0xFFD32F2F),
    imagePath: 'assets/coffee-shop/Espresso.jpg',
  ),
  Product(
    name: 'Classic Case',
    price: '\$234',
    category: 'Aristocratic Hand Bag',
    cardColor: Color(0xFFBDBDBD),
    detailColor: Color(0xFF616161),
    imagePath: 'assets/coffee-shop/Mocha.jpg',
  ),
];

const List<String> categories = [
  'Hand bag',
  'Jewellery',
  'Footwear',
  'Dresses',
];
