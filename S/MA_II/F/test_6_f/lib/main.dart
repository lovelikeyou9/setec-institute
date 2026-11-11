import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:test_6_f/counter_provider.dart';
import 'package:test_6_f/features/auth/login_screen.dart';
import 'package:test_6_f/features/product/home_screen.dart';
import 'package:test_6_f/features/product/product_provider.dart';
import 'features/count.dart';
import 'features/slash_screen.dart';
import 'package:provider/provider.dart';

// // 1
// void main() {
//   // runApp(const MyApp());
//   runApp(MaterialApp(home:MyApp(),));
// }

// // 1
// class MyApp extends StatelessWidget {
//
//   // const MyApp({super.key, required this.name});
//
//   String? name = "Vannizaa";
//   String? gender = "Male";
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: Text("App Phone Shop", style: TextStyle(color: Colors.white),),
//         backgroundColor: Colors.redAccent,
//       ),
//
//       body: Text("Name: ${name}${gender}"),
//
//     );
//   }
//
// }


// // 2
// void main() {
//   runApp(MaterialApp(home: Count()));
// }


// 9
void main() {
  // runApp(MaterialApp(home: SlashScreen()));
  // runApp(MaterialApp(home: HomeScreen()));
  // runApp(MaterialApp(home: LoginScreen()));
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(
          create: (_create)=> ProductProvider(),
        ),
        ChangeNotifierProvider(
          create: (_create)=> CounterProvider(),
        ),
      ],
      child: MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(home: HomeScreen());
  }
}
