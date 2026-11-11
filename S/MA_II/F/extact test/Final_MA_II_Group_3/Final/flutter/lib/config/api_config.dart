import 'dart:io' show Platform;

import 'package:flutter/foundation.dart';

/// API configuration for the Spring Boot backend on port 8080.
class ApiConfig {
  /// Override for testing (e.g. physical device LAN IP).
  /// Example: `http://192.168.1.10:8080/api`
  static String? overrideBaseUrl;

  static const String androidEmulatorHost = 'http://10.0.2.2:8080/api';
  static const String desktopHost = 'http://localhost:8080/api';

  static String get baseUrl {
    if (overrideBaseUrl != null && overrideBaseUrl!.isNotEmpty) {
      return overrideBaseUrl!;
    }
    if (!kIsWeb && Platform.isAndroid) {
      return androidEmulatorHost;
    }
    return desktopHost;
  }

  /// Origin without `/api` — used for `/uploads/...` media files.
  static String get mediaOrigin {
    final base = baseUrl;
    if (base.endsWith('/api')) {
      return base.substring(0, base.length - 4);
    }
    return base;
  }

  /// Resolve product/avatar paths from the API into full image URLs.
  static String resolveMediaUrl(String? path) {
    if (path == null || path.isEmpty) return '';
    if (path.startsWith('http://') || path.startsWith('https://')) {
      return path;
    }
    if (path.startsWith('/')) {
      return '$mediaOrigin$path';
    }
    return '$mediaOrigin/$path';
  }
}
