import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:flutter_final/screens/auth/login_screen.dart';
import 'package:flutter_final/theme/app_theme.dart';

void main() {
  testWidgets('Login screen shows MMT branding', (WidgetTester tester) async {
    await tester.pumpWidget(
      MaterialApp(
        theme: AppTheme.light,
        home: const LoginScreen(),
      ),
    );

    expect(find.text('MMT'), findsOneWidget);
    expect(find.text('Modern Multiple Technology'), findsOneWidget);
    expect(find.text('Sign in'), findsOneWidget);
  });
}
