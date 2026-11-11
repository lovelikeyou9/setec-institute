import '../models/order.dart';
import 'api_client.dart';

class OrderService {
  OrderService(this._api);

  final ApiClient _api;

  Future<List<ShopOrder>> fetchOrders() async {
    final list = _api.asList(await _api.get('/orders'));
    return list.map(ShopOrder.fromJson).toList();
  }

  Future<ShopOrder> createOrder({
    required String shippingAddress,
    String? discountCode,
  }) async {
    final body = <String, dynamic>{
      'shippingAddress': shippingAddress,
    };
    if (discountCode != null && discountCode.isNotEmpty) {
      body['discountCode'] = discountCode;
    }
    final data = _api.asMap(await _api.post('/orders', body: body));
    return ShopOrder.fromJson(data);
  }
}
