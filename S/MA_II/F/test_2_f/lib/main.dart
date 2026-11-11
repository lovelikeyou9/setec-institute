import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:test_2_f/features/home_screen.dart';
import 'features/count.dart';
import 'features/slash_screen.dart';

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
  runApp(MaterialApp(home: SlashScreen()));
  // runApp(MaterialApp(home: HomeScreen()));
}