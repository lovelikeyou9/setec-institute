import 'package:flutter/material.dart';

// // 2
// class Count extends StatefulWidget {
//   const Count({super.key});
//
//   @override
//   State<Count> createState() => _CountState();
// }
//
// class _CountState extends State<Count> {
//
//   int count = 1;
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(title: Text("App")),
//       body: Column(
//         children: [
//           Text("data"),
//           ElevatedButton(onPressed: () {}, child: Text("Count"))
//         ],
//       ),
//     );
//   }
//
// }


// // 3
// class Count extends StatefulWidget {
//   const Count({super.key});
//
//   @override
//   State<Count> createState() => _CountState();
// }
//
// class _CountState extends State<Count> {
//
//   int count = 1;
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(title: Text("App")),
//       body: Column(
//         children: [
//           Text("Count: ${count}"),
//           ElevatedButton(
//               onPressed: () {
//                 setState(() {
//                   count++;
//                 });
//               },
//               child: Text("Count")
//           )
//         ],
//       ),
//     );
//   }
//
// }

//
// // 4
// class Count extends StatefulWidget {
//   const Count({super.key});
//
//   @override
//   State<Count> createState() => _CountState();
// }
//
// class _CountState extends State<Count> {
//
//   int count = 1;
//
//
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(title: Text("App")),
//       body: Column(
//         children: [
//           Text("Count: ${count}"),
//           ElevatedButton(onPressed: _count, child: Text("Count")),
//         ],
//       ),
//     );
//   }
//
//   void _count() {
//     setState(() {
//       count++;
//     });
//   }
//
// }


// 5
class Count extends StatefulWidget {
  const Count({super.key});

  @override
  State<Count> createState() => _CountState();
}

class _CountState extends State<Count> {

  int count = 0;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    count = 1;
  }

  // // 6
  // @override
  // Widget build(BuildContext context) {
  //   return Scaffold(
  //     appBar: AppBar(title: Text("App")),
  //     body: Center(child: Text("data")),
  //   );
  // }


  // 7
  // @override
  // Widget build(BuildContext context) {
  //   return Scaffold(
  //     appBar: AppBar(title: Text("App")),
  //     body: Container(
  //       height: 300,
  //       width: 200,
  //       decoration: BoxDecoration(color: Colors.orange),
  //       child: Stack(
  //         children: [
  //           Container(
  //             height: 200,
  //             decoration: BoxDecoration(color: Colors.green),
  //           ),
  //           Positioned(
  //             top: 100,
  //             right: 0,
  //             child: Text("data")
  //           ),
  //         ],
  //       ),
  //     ),
  //   );
  // }


  // 8
  // @override
  // Widget build(BuildContext context) {
  //   return Scaffold(
  //     appBar: AppBar(title: Text("App")),
  //     body: Container(
  //       height: 300,
  //       width: 200,
  //       decoration: BoxDecoration(color: Colors.orange),
  //       child: Stack(
  //         children: [
  //           Container(
  //             height: 200,
  //             decoration: BoxDecoration(color: Colors.green),
  //           ),
  //           Positioned(
  //             top: 100,
  //             right: 0,
  //             child: Text("data")
  //           ),
  //         ],
  //       ),
  //     ),
  //   );
  // }


  // 9
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("App")),
      body: Container(
        height: 300,
        width: 200,
        decoration: BoxDecoration(color: Colors.orange),
        child: Stack(
          children: [
            Container(
              height: 200,
              decoration: BoxDecoration(color: Colors.green),
            ),
            Positioned(
                top: 100,
                right: 0,
                child: Text("data")
            ),
          ],
        ),
      ),
    );
  }



}
