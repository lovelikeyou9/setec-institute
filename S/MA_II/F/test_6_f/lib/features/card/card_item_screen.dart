
import 'package:flutter/material.dart';
import 'package:test_6_f/features/card/CardItemService.dart';
import 'package:test_6_f/features/card/card_item.dart';
import 'package:flutter/material.dart';
import 'package:test_6_f/features/model/product.dart';

class CardItemScreen extends StatefulWidget {
  const CardItemScreen({super.key});

  @override
  State<CardItemScreen> createState() => _CardItemState();
}

class _CardItemState extends State<CardItemScreen> {
  List<CardItem> cardItems=[];

  @override
  void initState() {
    super.initState();
    cardItems = CardItemService.getCardItem();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Order")),
      body: _myCardItem(),
      bottomNavigationBar: Container(
        padding: EdgeInsets.symmetric(
          horizontal: 20,
          vertical: 20,
        ),
        height: 300,
        decoration: BoxDecoration(
            color: Colors.white
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              "Payment Summary",
              style: TextStyle(fontSize: 20),
            ),
            SizedBox(height: 10,),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text("Total Payment", style: TextStyle(fontSize: 20)),
                Text("\$${CardItemService.calculateTotalAmount().toStringAsFixed(2)}", style: TextStyle(fontSize: 20)),
              ],
            ),
            SizedBox(height: 10,),
            Row(
              children: [
                Expanded(
                  child: SizedBox(
                    height: 50,
                    child: ElevatedButton(
                      style: ButtonStyle(backgroundColor: MaterialStatePropertyAll(Colors.orange)),
                      onPressed: () {

                      },
                      child: Text(
                        "Order Now",
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 25,
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  _myCardItem() {
    return ListView.builder(
      itemCount: cardItems.length,
      itemBuilder: (context, index) {
        var item = cardItems[index];
        return Container(
          margin: EdgeInsets.all(10),
          height: 100,
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(10),
              color: Colors.white
          ),
          child: Row(
            children: [
              Container(
                width: 100,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(10),
                  color: Colors.amber,
                  image: DecorationImage(
                    image: AssetImage("${item.photo}"),
                    fit: BoxFit.cover,
                  ),
                ),
              ),
              SizedBox(width: 10),
              Expanded(
                child: Container(
                  width: 200,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(10),
                    color: Colors.white,
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text("${item.name}", style: TextStyle(fontSize: 20),),
                      Text("\$${item.price}",  style: TextStyle(fontSize: 20),),
                    ],
                  ),
                ),
              ),
              Expanded(
                child: Container(
                  width: 200,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(10),
                    color: Colors.white,
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      IconButton(
                        onPressed: () {
                          CardItem cardItem = CardItem();
                          cardItem.id=item.id;
                          cardItem.name=item.name;
                          cardItem.price=item.price;
                          cardItem.qty= 1;
                          cardItem.photo=item.photo;

                          setState(() {
                            CardItemService.removeItemToCard(cardItem);
                          });
                        },
                        icon: Icon(
                            Icons.remove_circle_outline,
                            color: Colors.red
                        ),
                      ),
                      Text(
                        "${item.qty}",
                        style: TextStyle(
                            fontSize: 20
                        ),
                      ),
                      IconButton(
                        onPressed: () {
                          Product product = Product();
                          product.id=item.id;
                          product.name=item.name;
                          product.price=item.price;
                          product.qty= 1;
                          product.photo=item.photo;

                          setState(() {
                            CardItemService.addItemToCard(product);
                          });
                        },
                        icon: Icon(
                          Icons.add_circle_outline,
                          color: Colors.green
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        );
      }
    );

  }

}
