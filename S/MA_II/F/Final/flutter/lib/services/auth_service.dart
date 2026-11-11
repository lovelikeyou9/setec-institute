import '../models/user.dart';
import 'api_client.dart';

class AuthService {
  AuthService(this._api);

  final ApiClient _api;

  Future<User> register({
    required String fullName,
    required String username,
    required String email,
    required String password,
  }) async {
    await _api.post(
      '/auth/register',
      auth: false,
      body: {
        'fullName': fullName,
        'username': username,
        'email': email,
        'password': password,
      },
    );
    return login(email: email, password: password);
  }

  Future<User> login({required String email, required String password}) async {
    final data = _api.asMap(await _api.post(
      '/auth/login',
      auth: false,
      body: {'email': email, 'password': password},
    ));
    final token = data['token']?.toString();
    if (token == null || token.isEmpty) {
      throw ApiException('No token returned from server');
    }
    await _api.setToken(token);
    final userJson = data['user'];
    if (userJson is Map) {
      return User.fromJson(Map<String, dynamic>.from(userJson));
    }
    return fetchMe();
  }

  Future<User> fetchMe() async {
    final data = _api.asMap(await _api.get('/auth/me'));
    return User.fromJson(data);
  }

  Future<void> logout() async {
    await _api.setToken(null);
  }
}
