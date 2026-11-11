// import 'package:flutter/material.dart';

// 1
// package Flutter
import 'package:flutter/material.dart';

void main(){
  runApp(MaterialApp(home:MyApp(),));
  // [ runApp() ] is the function that starts your Flutter application and
  // tells Flutter which widget should be displayed first.

  // [ MaterialApp() ] is a widget that provides the basic structure and Material Design features for a Flutter app.

  // for design is usually is widget
  // In Flutter, most things related to UI design are widgets.
  // UI is widget:
  // in Flutter, UI is made from widgets.
}


// // 1
// class MyApp extends StatelessWidget {
//
//   const MyApp({super.key});
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//         appBar: AppBar(title: Text("App Phone Shop")),
//         // body: Center(child: Text("SV2.6")),
//         // body: Text("SV2.61"),
//         // body: Text("data"),
//         body: Text(
//             "data",
//             style: TextStyle(
//                 fontSize: 30,
//                 color: Colors.green,
//                 fontFamily: "Cambria",
//             ),
//         ),
//     );
//   }
//
// }

//
// // 2
// class MyApp extends StatelessWidget {
//
//   const MyApp({super.key});
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: Text("App Phone Shop", style: TextStyle(color: Colors.white),),
//         backgroundColor: Colors.redAccent,
//       ),
//
//       // body: Container(height: 200, width: 200, color: Colors.blue),
//
//       // body: Row(
//       //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
//       //   children: [
//       //     Container(height: 200, width: 200, color: Colors.blue),
//       //     Container(height: 200, width: 200, color: Colors.green),
//       //   ],
//       // ),
//
//       body: Column(
//         // mainAxisAlignment: MainAxisAlignment.end,
//         // mainAxisAlignment: MainAxisAlignment.spaceAround,
//         // mainAxisAlignment: MainAxisAlignment.spaceBetween,
//         // mainAxisAlignment: MainAxisAlignment.spaceEvenly,
//         // mainAxisAlignment: MainAxisAlignment.start,
//         // crossAxisAlignment: CrossAxisAlignment.stretch,
//         children: [
//           // Container(height: 100, width: 100, color: Colors.blue),
//           // Container(height: 100, width: 100, color: Colors.green),
//           // Container(height: 100, width: 100, color: Colors.red),
//           // Container(height: 100, width: 100, color: Colors.pink),
//
//           Image.network("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFoKq6XEWseFYfGHpIylmbGhH43mqXo90Xauh9seeRnwmbsSgIMvLDXFzv&s=10", width: 100,),
//           Image.asset("assets/images/imagesaaa.jpg", width: 100),
//
//           Icon(Icons.add, size: 50, color: Colors.green),
//           IconButton(onPressed:(){
//             print("on click");
//           }, icon: Icon(Icons.shopify_rounded, size: 50)),
//
//           ElevatedButton(onPressed: (){
//               print("clicked");
//             },
//             child: Text("OK", style: TextStyle(color: Colors.red))
//           ),
//
//         ],
//       ),
//
//
//     );
//   }
//
// }


// // 3
// class MyApp extends StatelessWidget {
//
//   const MyApp({super.key});
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: Text("App Phone Shop", style: TextStyle(color: Colors.white),),
//         backgroundColor: Colors.redAccent,
//       ),
//
//       body: ListView.builder(
//         itemCount: 100,
//         itemBuilder: (context, index){
//           // return Text("data");
//           return ListTile(title: Text("Title${index}"), subtitle: Text("Sub title"),
//             leading: Image.asset("assets/images/001.png"),
//           );
//         }
//       ),
//
//     );
//   }
//
// }


// Test_5_M classwork
//
// // 1
// class MyApp extends StatelessWidget {
//
//   const MyApp({super.key});
//
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       debugShowCheckedModeBanner: false,
//       home: CoffeeMenu(),
//     );
//   }
// }
//
// class CoffeeMenu extends StatelessWidget {
//   CoffeeMenu({super.key});
//
//   final List<Map<String, String>> cafe = [
//     {
//       "image": "assets/images/Espresso.jpg",
//       "name": "Espresso",
//       "price": "\$11",
//     },
//     {
//       "image": "assets/images/Americano.jpg",
//       "name": "Americano",
//       "price": "\$22",
//     },
//     {
//       "image": "assets/images/Cappuccino.jpg",
//       "name": "Cappuccino",
//       "price": "\$33",
//     },
//     {
//       "image": "assets/images/Cafe Latte.jpg",
//       "name": "Cafe Latte",
//       "price": "\$44",
//     },
//     {
//       "image": "assets/images/Flat White.jpg",
//       "name": "Flat White",
//       "price": "\$55",
//     },
//     {
//       "image": "assets/images/Mocha.jpg",
//       "name": "Mocha",
//       "price": "\$66",
//     },
//     {
//       "image": "assets/images/Macchiato.jpg",
//       "name": "Macchiato",
//       "price": "\$77",
//     },
//     {
//       "image": "assets/images/Long Black.jpg",
//       "name": "Long Black",
//       "price": "\$88",
//     },
//     {
//       "image": "assets/images/Caramel Latte.jpg",
//       "name": "Caramel Latte",
//       "price": "\$99",
//     },
//     {
//       "image": "assets/images/Vanilla Latte.jpg",
//       "name": "Vanilla Latte",
//       "price": "\$100",
//     },
//   ];
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//
//       appBar: AppBar(
//         title: const Text(
//           "Coffee Menu",
//           style: TextStyle(color: Colors.white),
//         ),
//         backgroundColor: Colors.redAccent,
//       ),
//
//       body: ListView.builder(
//         itemCount: cafe.length,
//
//         itemBuilder: (context, index) {
//           return Card(
//             margin: const EdgeInsets.all(10),
//
//             child: Column(
//               mainAxisAlignment: MainAxisAlignment.center,
//
//               children: [
//
//                 Text(
//                   '${index + 1}. ${cafe[index]["name"]}',
//                   textAlign: TextAlign.center,
//                   style: const TextStyle(
//                     fontSize: 16,
//                     color: Colors.red,
//                   ),
//                 ),
//
//                 Image.asset(
//                   cafe[index]["image"]!,
//                   width: 100,
//                   height: 100,
//                   fit: BoxFit.cover,
//                 ),
//
//                 const SizedBox(height: 10),
//
//                 Text(
//                   cafe[index]["price"]!,
//                   textAlign: TextAlign.center,
//                   style: const TextStyle(
//                     fontSize: 16,
//                     color: Colors.red,
//                   ),
//                 ),
//
//                 const SizedBox(height: 10),
//
//               ],
//             ),
//
//           );
//         }
//       ),
//
//     );
//   }
//
// }


// Test_5_M classwork
// 2
class MyApp extends StatelessWidget {
  // [ StatelessWidget ] is from [ import 'package:flutter/material.dart'; ]

  const MyApp({super.key});
  // [ super ] is send key to StatelessWidget

  // [ key ] is used to identify a widget.
  // It helps Flutter know which widget is which when widgets are rebuilt,
  // moved, added, or removed.

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: CoffeeMenu(),
    );
  }
  // [ Widget ] is class

  // [ build() ] is a method used to create and return the UI of a widget.
  // In Flutter, every [ StatelessWidget ] must have a [ build() ] method.

  // [ BuildContext ] is an object that tells Flutter where a widget is located
  // inside the widget tree.

  // [ debugShowCheckedModeBanner ] is a property of [ MaterialApp ] that controls
  // the "DEBUG" banner shown on the top-right corner of your app while running
  // in debug mode.
  // false means:
  // ✅ Hide the debug banner.

  // [ home ] in [ MaterialApp ] is used to define the first screen (starting page)
  // that Flutter shows when the app starts.
}

class CoffeeMenu extends StatelessWidget {
  CoffeeMenu({super.key});

  final List<Map<String, String>> cafe = [
    {
      "image": "assets/images/Espresso.jpg",
      "name": "Espresso",
      "price": "\$11",
    },
    {
      "image": "assets/images/Americano.jpg",
      "name": "Americano",
      "price": "\$22",
    },
    {
      "image": "assets/images/Cappuccino.jpg",
      "name": "Cappuccino",
      "price": "\$33",
    },
    {
      "image": "assets/images/Cafe Latte.jpg",
      "name": "Cafe Latte",
      "price": "\$44",
    },
    {
      "image": "assets/images/Flat White.jpg",
      "name": "Flat White",
      "price": "\$55",
    },
    {
      "image": "assets/images/Mocha.jpg",
      "name": "Mocha",
      "price": "\$66",
    },
    {
      "image": "assets/images/Macchiato.jpg",
      "name": "Macchiato",
      "price": "\$77",
    },
    {
      "image": "assets/images/Long Black.jpg",
      "name": "Long Black",
      "price": "\$88",
    },
    {
      "image": "assets/images/Caramel Latte.jpg",
      "name": "Caramel Latte",
      "price": "\$99",
    },
    {
      "image": "assets/images/Vanilla Latte.jpg",
      "name": "Vanilla Latte",
      "price": "\$100",
    },
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // [ Scaffold() ] is a Flutter widget that provides the basic structure
      // (layout) of a screen/page.

      appBar: AppBar(
        title: const Text(
          "Coffee Menu",
          style: TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
        ),
        // backgroundColor: Colors.redAccent,
        backgroundColor: Colors.transparent,
        flexibleSpace: Stack(
          fit: StackFit.expand,
          children: [
            Image.asset(
              'assets/images/coffee-5417663_1280.png',
              fit: BoxFit.cover,
            ),
            Container(
              color: Colors.black.withOpacity(0.4),
            ),
          ],
        ),
      ),
      // [ appBar: ]  is used to add a top bar (header) to a [ Scaffold ] screen.
      // It usually contains:
      // - Page title
      // - Back button
      // - Menu button
      // - Action icons (search, settings, cart, etc.)

      // [ AppBar() ] is a Flutter widget that creates the top bar of a screen.
      // It is usually used for:
      // - Showing the page title
      // - Navigation (back button)
      // - Action buttons (search, cart, settings)
      // - Changing the top bar color/background

      body: GridView.builder(
        // [ GridView ] is a Flutter widget that displays items in a grid
        // builder

        // [ builder() ] is a function that creates widgets on demand.
        // Instead of creating all widgets at once, Flutter calls the builder
        // function whenever it needs an item.

        padding: const EdgeInsets.all(10),

        itemCount: cafe.length,
        // [ itemCount ] tells Flutter how many items to create in a
        // [ ListView.builder ] or [ GridView.builder ].

        gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
          maxCrossAxisExtent: 250,  // auto responsive
          crossAxisSpacing: 10,
          mainAxisSpacing: 10,
          childAspectRatio: 0.75,
        ),
        // [ gridDelegate ] is a property of [ GridView ] that tells Flutter how
        // to lay out the items in the grid.

        itemBuilder: (context, index) {
        // [ itemBuilder ] is a callback function that tells Flutter how to create
        // one item in a [ ListView.builder ] or [ GridView.builder ].

          return Card(
            // [ Card ] is a Flutter widget that displays content inside a
            // Material Design card.

            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              // [ mainAxisAlignment ] controls how children are positioned along
              // the main axis of a [ Row ] or [ Column ].

              children: [

                Text(
                  '${index + 1}. ${cafe[index]["name"]}',
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontSize: 18,
                    color: Colors.brown,
                    fontWeight: FontWeight.bold,
                  ),
                ),

                Image.asset(
                  cafe[index]["image"]!,
                  width: 100,
                  height: 100,
                  fit: BoxFit.cover,
                ),

                const SizedBox(height: 10),

                Text(
                  cafe[index]["price"]!,
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontSize: 20,
                    color: Colors.brown,
                    fontWeight: FontWeight.bold,
                  ),
                ),

                const SizedBox(height: 10),

              ],

            )

          );
        }
      ),

    );
  }

}