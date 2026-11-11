import 'dart:convert';
import 'dart:io';

import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../config/api_config.dart';

class ApiException implements Exception {
  ApiException(this.message, {this.statusCode});

  final String message;
  final int? statusCode;

  @override
  String toString() => message;
}

class ApiClient {
  ApiClient({http.Client? client}) : _client = client ?? http.Client();

  static const _tokenKey = 'mmt_jwt';

  final http.Client _client;
  String? _token;

  String? get token => _token;

  Future<void> loadToken() async {
    final prefs = await SharedPreferences.getInstance();
    _token = prefs.getString(_tokenKey);
  }

  Future<void> setToken(String? token) async {
    _token = token;
    final prefs = await SharedPreferences.getInstance();
    if (token == null || token.isEmpty) {
      await prefs.remove(_tokenKey);
    } else {
      await prefs.setString(_tokenKey, token);
    }
  }

  Uri _uri(String path, [Map<String, String>? query]) {
    final normalized = path.startsWith('/') ? path : '/$path';
    return Uri.parse('${ApiConfig.baseUrl}$normalized').replace(queryParameters: query);
  }

  Map<String, String> _headers({bool auth = true, bool json = true}) {
    final headers = <String, String>{};
    if (json) headers['Content-Type'] = 'application/json';
    if (auth && _token != null && _token!.isNotEmpty) {
      headers['Authorization'] = 'Bearer $_token';
    }
    return headers;
  }

  dynamic _decodeBody(http.Response response) {
    if (response.body.isEmpty) return null;
    try {
      return jsonDecode(response.body);
    } catch (_) {
      return response.body;
    }
  }

  void _throwIfError(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) return;
    final body = _decodeBody(response);
    String message = 'Request failed (${response.statusCode})';
    if (body is Map && body['message'] != null) {
      message = body['message'].toString();
    } else if (body is Map && body['error'] != null) {
      message = body['error'].toString();
    } else if (body is String && body.isNotEmpty) {
      message = body;
    }
    throw ApiException(message, statusCode: response.statusCode);
  }

  Future<dynamic> get(String path, {Map<String, String>? query, bool auth = true}) async {
    final response = await _client.get(_uri(path, query), headers: _headers(auth: auth));
    _throwIfError(response);
    return _decodeBody(response);
  }

  Future<dynamic> post(String path, {Object? body, bool auth = true}) async {
    final response = await _client.post(
      _uri(path),
      headers: _headers(auth: auth),
      body: body == null ? null : jsonEncode(body),
    );
    _throwIfError(response);
    return _decodeBody(response);
  }

  Future<dynamic> put(String path, {Object? body, bool auth = true}) async {
    final response = await _client.put(
      _uri(path),
      headers: _headers(auth: auth),
      body: body == null ? null : jsonEncode(body),
    );
    _throwIfError(response);
    return _decodeBody(response);
  }

  Future<dynamic> delete(String path, {bool auth = true}) async {
    final response = await _client.delete(_uri(path), headers: _headers(auth: auth));
    _throwIfError(response);
    return _decodeBody(response);
  }

  Future<Map<String, dynamic>> uploadFile(
    String path, {
    required File file,
    String fieldName = 'file',
    Map<String, String>? fields,
    bool auth = true,
  }) async {
    final request = http.MultipartRequest('POST', _uri(path));
    if (auth && _token != null && _token!.isNotEmpty) {
      request.headers['Authorization'] = 'Bearer $_token';
    }
    if (fields != null) {
      request.fields.addAll(fields);
    }
    final fileName = file.path.split(RegExp(r'[\\/]')).last;
    final mediaType = _guessMediaType(fileName);
    request.files.add(
      await http.MultipartFile.fromPath(
        fieldName,
        file.path,
        filename: fileName,
        contentType: mediaType,
      ),
    );
    final streamed = await request.send();
    final response = await http.Response.fromStream(streamed);
    _throwIfError(response);
    return asMap(_decodeBody(response));
  }

  MediaType _guessMediaType(String fileName) {
    final lower = fileName.toLowerCase();
    if (lower.endsWith('.png')) return MediaType('image', 'png');
    if (lower.endsWith('.webp')) return MediaType('image', 'webp');
    if (lower.endsWith('.gif')) return MediaType('image', 'gif');
    return MediaType('image', 'jpeg');
  }

  List<Map<String, dynamic>> asList(dynamic data) {
    if (data is List) {
      return data.whereType<Map>().map((e) => Map<String, dynamic>.from(e)).toList();
    }
    if (data is Map) {
      for (final key in ['content', 'data', 'items', 'results']) {
        final inner = data[key];
        if (inner is List) {
          return inner.whereType<Map>().map((e) => Map<String, dynamic>.from(e)).toList();
        }
      }
    }
    return [];
  }

  Map<String, dynamic> asMap(dynamic data) {
    if (data is Map<String, dynamic>) return data;
    if (data is Map) return Map<String, dynamic>.from(data);
    return {};
  }
}
