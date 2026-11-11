import 'package:intl/intl.dart';

final NumberFormat _currency = NumberFormat.currency(symbol: '\$', decimalDigits: 2);

String formatMoney(num? value) {
  if (value == null) return '\$0.00';
  return _currency.format(value);
}

String formatDate(DateTime? date) {
  if (date == null) return '—';
  return DateFormat.yMMMd().format(date);
}
