import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';

import '../config/api_config.dart';
import '../theme/app_theme.dart';

class ProductImage extends StatelessWidget {
  const ProductImage({super.key, this.url, this.height = 120, this.fit = BoxFit.contain});

  final String? url;
  final double height;
  final BoxFit fit;

  @override
  Widget build(BuildContext context) {
    final resolved = ApiConfig.resolveMediaUrl(url);
    if (resolved.isEmpty) {
      return _placeholder();
    }
    return CachedNetworkImage(
      imageUrl: resolved,
      height: height,
      width: double.infinity,
      fit: fit,
      placeholder: (context, url) => _placeholder(loading: true),
      errorWidget: (context, url, error) => _placeholder(),
    );
  }

  Widget _placeholder({bool loading = false}) {
    return Container(
      height: height,
      width: double.infinity,
      color: Colors.white,
      alignment: Alignment.center,
      child: loading
          ? const SizedBox(
              width: 24,
              height: 24,
              child: CircularProgressIndicator(strokeWidth: 2, color: NexusColors.primary),
            )
          : Icon(Icons.devices_other, size: 40, color: NexusColors.outlineStrong.withValues(alpha: 0.6)),
    );
  }
}
