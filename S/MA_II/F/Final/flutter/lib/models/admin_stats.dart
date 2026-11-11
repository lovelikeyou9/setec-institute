class AdminStats {
  const AdminStats({
    this.totalProducts = 0,
    this.activeSkus = 0,
    this.totalOrders = 0,
    this.totalRevenue = 0,
    this.valuation = 0,
    this.lowStockCount = 0,
    this.pendingOrders = 0,
    this.totalCustomers = 0,
    this.categories = 0,
  });

  final int totalProducts;
  final int activeSkus;
  final int totalOrders;
  final double totalRevenue;
  final double valuation;
  final int lowStockCount;
  final int pendingOrders;
  final int totalCustomers;
  final int categories;

  factory AdminStats.fromJson(Map<String, dynamic> json) {
    return AdminStats(
      totalProducts: _asInt(json['totalProducts'] ?? json['activeSkus']),
      activeSkus: _asInt(json['activeSkus']),
      totalOrders: _asInt(json['totalOrders']),
      totalRevenue: _asDouble(json['totalRevenue'] ?? json['revenue']),
      valuation: _asDouble(json['valuation']),
      lowStockCount: _asInt(json['lowStockCount'] ?? json['criticalSkus']),
      pendingOrders: _asInt(json['pendingOrders']),
      totalCustomers: _asInt(json['totalCustomers']),
      categories: _asInt(json['categories']),
    );
  }
}

class AuditLogEntry {
  const AuditLogEntry({
    required this.id,
    required this.action,
    required this.timestamp,
    this.actor,
    this.details,
    this.level,
  });

  final int id;
  final String action;
  final DateTime? timestamp;
  final String? actor;
  final String? details;
  final String? level;

  factory AuditLogEntry.fromJson(Map<String, dynamic> json) {
    final message = json['message']?.toString() ?? json['action']?.toString() ?? '—';
    return AuditLogEntry(
      id: _asInt(json['id']),
      action: message,
      timestamp: DateTime.tryParse(json['timestamp']?.toString() ?? json['createdAt']?.toString() ?? ''),
      actor: json['adminUsername']?.toString() ?? json['actor']?.toString(),
      details: json['details']?.toString(),
      level: json['level']?.toString(),
    );
  }
}

int _asInt(dynamic v) {
  if (v is int) return v;
  if (v is num) return v.toInt();
  return int.tryParse(v?.toString() ?? '') ?? 0;
}

double _asDouble(dynamic v) {
  if (v is double) return v;
  if (v is num) return v.toDouble();
  return double.tryParse(v?.toString() ?? '') ?? 0;
}
