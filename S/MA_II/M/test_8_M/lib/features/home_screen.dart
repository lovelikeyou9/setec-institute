import 'package:flutter/material.dart';



class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  List<String> categoryItems=[
    "Hot latte",
    "Iced Latte",
    "Hot latte",
    "Iced Latte",
    "Iced Latte",
    "Hot latte",
    "Iced Latte",
  ];
  int selectedItem = 0;
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
              // SizedBox(height: 10),
              _categoryItems(),
              Padding(
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
                    itemCount: 10,
                    itemBuilder: (context, index) {
                      return Container(
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
                                  image: AssetImage("assets/coffee-shop/images.jpg"),
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
                                    Icon(Icons.star, color: Colors.orange,),
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
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    "Hot Latte",
                                    style: TextStyle(fontWeight: FontWeight.bold),
                                  ),
                                  Text("Iced"),
                                  Row(
                                    children: [
                                      Text("\$4.7", style: TextStyle(fontWeight: FontWeight.bold),),
                                      SizedBox(width: 125,),
                                      Container(
                                        decoration: BoxDecoration(
                                          borderRadius: BorderRadius.circular(
                                            5,
                                          ),
                                          color: Colors.orangeAccent,
                                        ),
                                        child: Icon(Icons.add, color: Colors.white,),
                                      ),
                                    ],
                                  ),
                                ],
                              )
                            ),
                          ],
                        ),
                      );
                    },
                  ),
                ),
              ),
            ],
          ),
        ),
      )
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


