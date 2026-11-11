import 'dart:io';

import 'api_client.dart';

class UploadService {
  UploadService(this._api);

  final ApiClient _api;

  /// Uploads an image for admin use. [folder] is `products` or `brands`.
  /// Returns a server path like `/uploads/products/....jpg`.
  Future<String> uploadImage(File file, {String folder = 'products'}) async {
    final data = await _api.uploadFile(
      '/admin/uploads',
      file: file,
      fields: {'folder': folder},
    );
    final url = data['url']?.toString();
    if (url == null || url.isEmpty) {
      throw ApiException('Upload succeeded but no URL was returned');
    }
    return url;
  }
}
