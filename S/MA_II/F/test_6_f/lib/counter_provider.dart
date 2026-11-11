

import 'package:flutter/cupertino.dart';

class CounterProvider extends ChangeNotifier {
  int count = 1;
  void increase(){
    count++;
    notifyListeners();
  }

  void decrease(){
    count--;
    notifyListeners();
  }

}