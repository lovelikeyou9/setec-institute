import 'package:flutter/material.dart';
import 'package:test_4_f/features/card/CardItemService.dart';
import 'package:test_4_f/features/model/product.dart';
import 'package:test_4_f/features/model/product_service.dart';
import 'package:test_4_f/features/product_detail.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final ProductService _productService = ProductService();
  List<String> categoryItems = [
    "Hot latte",
    "Iced Latte",
    "Hot latte",
    "Iced Latte",
    "Iced Latte",
    "Hot latte",
    "Iced Latte",
  ];
  int selectedItem = 0;
  int selectedBottomNavigationItem = 0;
  final menus = [
    {'icon': Icons.home, 'destination': "Home"},
    {'icon': Icons.favorite, 'destination': "Favorite"},
    {'icon': Icons.shopping_bag, 'destination': "Shopping Card"},
    {'icon': Icons.notifications, 'destination': "Notification"}
  ];

  List<Product> get products => _productService.products;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: Colors.white,
        height: double.infinity,
        child: SingleChildScrollView(
          physics: AlwaysScrollableScrollPhysics(),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Stack(
                children: [
                  Container(
                    height: 300,
                    decoration: BoxDecoration(color: Colors.black),
                  ),
                  _headerItems(),
                ],
              ),
              _categoryItems(),
              _productItem(),
            ],
          ),
        ),
      ),
      bottomNavigationBar: _bottomNavigationBar(),
    );
  }

  BottomNavigationBar _bottomNavigationBar() {
    return BottomNavigationBar(
      backgroundColor: Colors.white,
      type: BottomNavigationBarType.fixed,
      currentIndex: selectedBottomNavigationItem,
      selectedItemColor: Colors.orange,
      unselectedItemColor: Colors.grey,
      onTap: (index) {
        setState(() {
          selectedBottomNavigationItem = index;
        });
      },
      items: List.generate(
        menus.length,
        (index) {
          final item = menus[index];
          return BottomNavigationBarItem(
            icon: Icon(item["icon"] as IconData),
            label: item["destination"] as String,
          );
        },
      ),
    );
  }

  Padding _productItem() {
    return Padding(
      padding: const EdgeInsets.all(10),
      child: SizedBox(
        height: 500,
        child: GridView.builder(
          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            crossAxisSpacing: 10,
            mainAxisSpacing: 10,
            childAspectRatio: 0.7,
          ),
          itemCount: products.length,
          itemBuilder: (context, index) {
            final product = products[index];
            return GestureDetector(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => ProductDetail(product: product),
                  ),
                );
              },
              child: Container(
                height: 300,
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(10),
                  border: Border.all(
                    color: Colors.grey.withOpacity(0.5),
                  ),
                ),
                child: Stack(
                  children: [
                    Container(
                      margin: EdgeInsets.all(10),
                      height: 180,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(20),
                        image: DecorationImage(
                          fit: BoxFit.cover,
                          image: AssetImage(product.photo ?? ""),
                        ),
                      ),
                    ),
                    Positioned(
                      right: 10,
                      top: 10,
                      child: Container(
                        height: 50,
                        width: 60,
                        decoration: BoxDecoration(
                          color: Colors.brown.withOpacity(0.5),
                          borderRadius: BorderRadius.only(
                            topRight: Radius.circular(20),
                            bottomLeft: Radius.circular(35),
                          ),
                        ),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(Icons.star, color: Colors.orange),
                            Text(
                              "4.5",
                              style: TextStyle(color: Colors.white),
                            ),
                          ],
                        ),
                      ),
                    ),
                    Positioned(
                      top: 208,
                      left: 10,
                      right: 10,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            product.name ?? "",
                            style: TextStyle(fontWeight: FontWeight.bold),
                          ),
                          SizedBox(height: 4),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text(
                                "\$${product.price?.toStringAsFixed(1) ?? "0"}",
                                style: TextStyle(fontWeight: FontWeight.bold),
                              ),
                              GestureDetector(
                                onTap: () {
                                  CardItemService.addItemToCard(product);
                                  ScaffoldMessenger.of(context).showSnackBar(
                                    SnackBar(
                                      content: Text(
                                        "${product.name} added to cart",
                                      ),
                                      duration: Duration(seconds: 1),
                                    ),
                                  );
                                },
                                child: Container(
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(5),
                                    color: Colors.orangeAccent,
                                  ),
                                  child: Icon(Icons.add, color: Colors.white),
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
  
  Padding _headerItems() {
    return Padding(
      padding: const EdgeInsets.all(10),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(height: 50),

          const Text(
            "Location",
            style: TextStyle(color: Colors.white),
          ),

          Row(
            children: const [
              Text(
                "Bilze, Khmer",
                style: TextStyle(color: Colors.white),
              ),
              Icon(
                Icons.keyboard_arrow_down,
                color: Colors.white,
              ),
            ],
          ),

          const SizedBox(height: 20),

          Row(
            children: [
              Expanded(
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10),
                  height: 50,
                  decoration: BoxDecoration(
                    color: Colors.grey.withOpacity(0.5),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Row(
                    children: [
                      Image.asset(
                        "assets/coffee-shop/ic_search.png",
                        color: Colors.white,
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: TextField(
                          style: const TextStyle(color: Colors.white),
                          decoration: const InputDecoration(
                            border: InputBorder.none,
                            hintText: "Search Coffee...",
                            hintStyle: TextStyle(color: Colors.white),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),

              const SizedBox(width: 10),

              Container(
                height: 50,
                width: 50,
                decoration: BoxDecoration(
                  color: Colors.orange,
                  borderRadius: BorderRadius.circular(10),
                ),
                child: const Icon(
                  Icons.abc_outlined,
                  color: Colors.white,
                ),
              ),
            ],
          ),

          const SizedBox(height: 10),

          ClipRRect(
            child: Image.asset(
              "assets/coffee-shop/coffee-5417663_1280 - Copy.png",
              fit: BoxFit.cover,
            ),
          ),
        ],
      ),
    );
  }

  Padding _categoryItems() {
    return Padding(
      padding: const EdgeInsets.all(10),
      child: Container(
        color: Colors.white,
        child: SizedBox(
          height: 40,
          child: ListView.builder(
            shrinkWrap: true,
            itemCount: categoryItems.length,
            scrollDirection: Axis.horizontal,
            itemBuilder: (context, index) {
              return GestureDetector(
                onTap: () {
                  setState(() {
                    selectedItem = index;
                  });
                },
                child: Container(
                  margin: const EdgeInsets.only(left: 1),
                  padding: const EdgeInsets.symmetric(
                    horizontal: 10,
                    vertical: 10,
                  ),
                  decoration: BoxDecoration(
                    color: selectedItem == index
                        ? const Color(0XFFBC7F56)
                        : Colors.white,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Text(
                    categoryItems[index],
                    style: TextStyle(
                      color: selectedItem == index
                          ? Colors.white
                          : Colors.black,
                    ),
                  ),
                ),
              );
            },
          ),
        ),
      ),
    );
  }

}


