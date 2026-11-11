

import 'package:flutter/material.dart';
import 'package:test_3_f/features/card/CardItemService.dart';
import 'package:test_3_f/features/card/card_item_screen.dart';
import 'package:test_3_f/features/model/product.dart';

class ProductDetail extends StatefulWidget {
  const ProductDetail({super.key, required this.product});

  final Product product;

  @override
  State<ProductDetail> createState() => _ProductDetailState();

}


class _ProductDetailState extends State<ProductDetail> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Product Detail")),
      body: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              margin: EdgeInsets.all(10),
              height: 300,
              decoration: BoxDecoration(
                image: DecorationImage(
                  image: AssetImage("${widget.product.photo}"),
                  fit: BoxFit.cover,
                ),
                color: Colors.amber,
                borderRadius: BorderRadius.circular(10),
              ),
            ),
          Padding(
            padding: EdgeInsetsGeometry.all(10),
            child: Text(
              "${widget.product.name}",
              style: TextStyle(fontSize: 25),
            ),
          ),
        ],
      ),
      bottomNavigationBar: Container(
      padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
      height: 200,
      decoration: BoxDecoration(color: Colors.white),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "\$${widget.product.price?.toStringAsFixed(2)}",
                  style: TextStyle(fontSize: 25, color: Colors.orange),
                ), // Text
              ],
            ), // Column
            SizedBox(width: 20),
            Expanded(
              child: SizedBox(
                height: 65,
                child: ElevatedButton(
                  style: ButtonStyle(
                    backgroundColor: MaterialStatePropertyAll(Colors.orange),
                  ),
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context)=>CardItemScreen()),
                    );
                    CardItemService.addItemToCard(widget.product);
                  },
                  child: Text(
                    "BUY NOW",
                    style: TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.w500,
                      fontSize: 25,
                    ),
                  ),
                ),
              ),
            ),
          ],
        ), // Row
      ), // Container
    ); // Scaffold
  }
}