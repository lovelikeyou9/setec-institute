class User {
  const User({
    required this.id,
    required this.fullName,
    required this.username,
    required this.email,
    required this.role,
    this.avatarUrl,
    this.memberSince,
    this.banned = false,
    this.banReason,
  });

  final int id;
  final String fullName;
  final String username;
  final String email;
  final String role;
  final String? avatarUrl;
  final DateTime? memberSince;
  final bool banned;
  final String? banReason;

  bool get isAdmin => role.toUpperCase() == 'ADMIN';

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: _asInt(json['id']),
      fullName: json['fullName']?.toString() ?? '',
      username: json['username']?.toString() ?? '',
      email: json['email']?.toString() ?? '',
      role: json['role']?.toString() ?? 'CUSTOMER',
      avatarUrl: json['avatarUrl']?.toString(),
      memberSince: _parseDate(json['memberSince']),
      banned: json['banned'] == true,
      banReason: json['banReason']?.toString(),
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'fullName': fullName,
        'username': username,
        'email': email,
        'role': role,
        'avatarUrl': avatarUrl,
        'memberSince': memberSince?.toIso8601String(),
        'banned': banned,
        'banReason': banReason,
      };
}

int _asInt(dynamic v) {
  if (v is int) return v;
  if (v is num) return v.toInt();
  return int.tryParse(v?.toString() ?? '') ?? 0;
}

DateTime? _parseDate(dynamic v) {
  if (v == null) return null;
  return DateTime.tryParse(v.toString());
}
