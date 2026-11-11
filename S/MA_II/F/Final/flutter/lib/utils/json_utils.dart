import 'dart:convert';

Map<String, String> parseSpecsJson(dynamic value) {
  if (value == null) return {};
  Map<String, dynamic> raw = {};
  if (value is Map) {
    raw = Map<String, dynamic>.from(value);
  } else if (value is String && value.trim().isNotEmpty) {
    try {
      final decoded = jsonDecode(value);
      if (decoded is Map) {
        raw = Map<String, dynamic>.from(decoded);
      }
    } catch (_) {
      return {};
    }
  }
  return raw.map((k, v) => MapEntry(k.toString(), v?.toString() ?? ''));
}

List<String> parseTags(dynamic value) {
  if (value == null) return [];
  if (value is List) {
    return value.map((e) => e.toString()).toList();
  }
  if (value is String && value.isNotEmpty) {
    return value.split(',').map((e) => e.trim()).where((e) => e.isNotEmpty).toList();
  }
  return [];
}
