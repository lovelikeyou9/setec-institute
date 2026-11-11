

import 'package:flutter/material.dart';

class _ProductDetailState extends State<ProductDetail> {
  Widget build(BuildContext context) {




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
                    Navigator.push(builder: (context)=>CardItem()),
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