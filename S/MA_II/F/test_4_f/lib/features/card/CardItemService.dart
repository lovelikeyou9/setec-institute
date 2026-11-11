

import 'package:test_4_f/features/card/card_item.dart';
import 'package:test_4_f/features/model/product.dart';

class CardItemService {
  static List<CardItem> _cardItems = [];
  static List<CardItem> getCardItem() => _cardItems;

  static double calculateTotalAmount() {
    double sum = 0;
    for(var item in getCardItem()) {
      sum+=item.qty! * item.price!;
    }
    return sum;
  }

  static addItemToCard(Product product) {
    var existIndex = _cardItems.indexWhere((item1) => item1.id == product.id);

    if(existIndex !=- 1) {
      int? qty = _cardItems[existIndex].qty;
      _cardItems[existIndex].qty = qty! + 1;
      calculateTotalAmount();
    } else {
      CardItem cardItem = CardItem();
      cardItem.id=product.id;
      cardItem.name=product.name;
      cardItem.price=product.price;
      cardItem.qty= 1;
      cardItem.photo=product.photo;

      _cardItems.add(cardItem);
      calculateTotalAmount();
    }
  }

  static removeItemToCard(CardItem cardItem) {
    var existIndex = _cardItems.indexWhere((item1) => item1.id == cardItem.id);

    if(existIndex !=- 1) {
      int? qty = _cardItems[existIndex].qty;
      if (qty! > 1){
        _cardItems[existIndex].qty = qty! - 1;
      }else{
        _cardItems.removeAt(existIndex);
      }
    }
  }

}