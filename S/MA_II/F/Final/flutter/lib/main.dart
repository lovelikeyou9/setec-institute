import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'providers/auth_provider.dart';
import 'providers/cart_provider.dart';
import 'providers/product_provider.dart';
import 'providers/wishlist_provider.dart';
import 'screens/auth/login_screen.dart';
import 'screens/shell/main_shell.dart';
import 'services/api_client.dart';
import 'services/cart_service.dart';
import 'services/product_service.dart';
import 'services/wishlist_service.dart';
import 'theme/app_theme.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final apiClient = ApiClient();
  await apiClient.loadToken();

  runApp(MmtApp(apiClient: apiClient));
}

class MmtApp extends StatelessWidget {
  const MmtApp({super.key, required this.apiClient});

  final ApiClient apiClient;

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        Provider<ApiClient>.value(value: apiClient),
        ChangeNotifierProvider(
          create: (_) => AuthProvider(apiClient: apiClient)..restoreSession(),
        ),
        ChangeNotifierProvider(create: (_) => ProductProvider(ProductService(apiClient))),
        ChangeNotifierProvider(create: (_) => CartProvider(CartService(apiClient))),
        ChangeNotifierProvider(create: (_) => WishlistProvider(WishlistService(apiClient))),
      ],
      child: MaterialApp(
        title: 'MMT — Modern Multiple Technology',
        debugShowCheckedModeBanner: false,
        theme: AppTheme.light,
        home: const _RootGate(),
      ),
    );
  }
}

class _RootGate extends StatelessWidget {
  const _RootGate();

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();

    if (auth.status == AuthStatus.unknown || auth.loading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    if (auth.status == AuthStatus.authenticated) {
      return const MainShell();
    }

    return const LoginScreen();
  }
}
