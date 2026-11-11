import 'package:flutter/foundation.dart';

import '../models/user.dart';
import '../services/api_client.dart';
import '../services/auth_service.dart';

enum AuthStatus { unknown, authenticated, unauthenticated }

class AuthProvider extends ChangeNotifier {
  AuthProvider({required ApiClient apiClient}) : _auth = AuthService(apiClient);

  final AuthService _auth;

  AuthStatus status = AuthStatus.unknown;
  User? user;
  String? error;
  bool loading = false;

  Future<void> restoreSession() async {
    loading = true;
    notifyListeners();
    try {
      user = await _auth.fetchMe();
      status = AuthStatus.authenticated;
      error = null;
    } catch (_) {
      user = null;
      status = AuthStatus.unauthenticated;
    } finally {
      loading = false;
      notifyListeners();
    }
  }

  Future<bool> login(String email, String password) async {
    loading = true;
    error = null;
    notifyListeners();
    try {
      user = await _auth.login(email: email, password: password);
      status = AuthStatus.authenticated;
      return true;
    } on ApiException catch (e) {
      error = e.message;
      status = AuthStatus.unauthenticated;
      return false;
    } catch (e) {
      error = e.toString();
      status = AuthStatus.unauthenticated;
      return false;
    } finally {
      loading = false;
      notifyListeners();
    }
  }

  Future<bool> register({
    required String fullName,
    required String username,
    required String email,
    required String password,
  }) async {
    loading = true;
    error = null;
    notifyListeners();
    try {
      user = await _auth.register(
        fullName: fullName,
        username: username,
        email: email,
        password: password,
      );
      status = AuthStatus.authenticated;
      return true;
    } on ApiException catch (e) {
      error = e.message;
      return false;
    } catch (e) {
      error = e.toString();
      return false;
    } finally {
      loading = false;
      notifyListeners();
    }
  }

  Future<void> logout() async {
    await _auth.logout();
    user = null;
    status = AuthStatus.unauthenticated;
    notifyListeners();
  }
}
