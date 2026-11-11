
import 'dart:io';

import 'package:test_2_m/ingeritances/people.dart';
// import 'people.dart';

class Student extends People {

  // int? id;
  // String? name;
  // String? address;
  String? subject;
  int? year;

  // void show() {

  // }

  @override
  void show() {
    // TODO: implement show
    super.show();
    stdout.write("\t${this.subject}\t${this.year}");
  }

}