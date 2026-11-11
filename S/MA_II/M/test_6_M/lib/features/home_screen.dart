import 'package:flutter/material.dart';



class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        height: 300,
        width: double.infinity,
        decoration: BoxDecoration(
          color: Colors.black.withOpacity(0.8)
        ),
        child: Container(
          padding: EdgeInsets.all(10),
          child: Stack(
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SizedBox(
                    height: 50,
                  ),
                  Text("Location", style: TextStyle(color: Colors.white),),
                  Row(
                    children: [
                      Text("Bilze, Tanjung", style: TextStyle(color: Colors.white),),
                      Icon(Icons.keyboard_arrow_down, color: Colors.white),
                    ],
                  ),
                  SizedBox(height: 20),
                  Row(
                    children: [
                      Container(
                        height: 50,
                        width: 335,
                        decoration: BoxDecoration(
                          color: Colors.grey,
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: Image.asset("assets/coffee-shop/ic_search.png"),
                      ),
                      SizedBox(width: 10),
                      Container(
                        height: 50,
                        width: 50,
                        decoration: BoxDecoration(
                          color: Colors.orange, borderRadius: BorderRadius.circular(10),
                        ),
                        child: Icon(
                          Icons.abc_outlined, color: Colors.white,
                        ),
                      ),
                    ],
                  ),
                ],
              )
            ],
          ),
        ),
      )
    );
  }
}


