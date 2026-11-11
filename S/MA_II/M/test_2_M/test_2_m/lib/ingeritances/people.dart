
import 'dart:io';

class People {

  int? id;
  String? name;
  String? address;

  void show () {
    // print("${this.id}\t${this.name}\t${this.address}");
    stdout.write("${this.id}\t${this.name}\t${this.address}");
  }

}