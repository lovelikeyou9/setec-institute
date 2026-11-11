import 'package:flutter/material.dart';
import 'home_screen.dart';

class SlashScreen extends StatelessWidget {
  const SlashScreen({super.key});

  // 9
  // @override
  // Widget build(BuildContext context) {
  //   return Scaffold(body: Text("data"));
  // }


  // 10
  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    return Scaffold(
      body: Container(
        height: double.infinity,
        decoration: BoxDecoration(color: Colors.white),
        child: Stack(
          children: [
            Image.asset(
                "assets/coffee-shop/coffee-5417663_1280.png",
                height: size.height,
                fit: BoxFit.cover
            ),
            Positioned(
              top: 660,
              left: 40  ,
              child: Column(
                children: [
                  Text(
                    textAlign: TextAlign.center,
                    "Fall in Love with",
                    style: TextStyle(color: Colors.white, fontSize: 30),
                  ),
                  Text(
                    textAlign: TextAlign.center,
                    "Coffee in Blissful",
                    style: TextStyle(color: Colors.white, fontSize: 30),
                  ),
                  Text(
                    textAlign: TextAlign.center,
                    "Delight!",
                    style: TextStyle(color: Colors.white, fontSize: 30),
                  ),
                  Text(
                    "Welcome to Coffee",
                    style: TextStyle(color: Colors.white, fontSize: 20),
                  ),
                  SizedBox(height: 20,),
                  SizedBox(
                    width: 350,
                    height: 60,
                    child: ElevatedButton(
                      style: ButtonStyle(
                          backgroundColor: WidgetStatePropertyAll(Color(0XFFBC7F56))
                      ),
                      onPressed: () {
                        Navigator.push(context, MaterialPageRoute(builder: (context)=>HomeScreen()));
                      },
                      child: Text(
                        "Get Started",
                        style: TextStyle(color: Colors.white, fontSize: 20),
                      )
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }



}
