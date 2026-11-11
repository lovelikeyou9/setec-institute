import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../../models/user.dart';
import '../../services/admin_service.dart';
import '../../theme/app_theme.dart';
import 'admin_customer_detail_screen.dart';

class AdminCustomersTab extends StatefulWidget {
  const AdminCustomersTab({super.key, required this.admin});

  final AdminService admin;

  @override
  State<AdminCustomersTab> createState() => _AdminCustomersTabState();
}

class _AdminCustomersTabState extends State<AdminCustomersTab> {
  List<User> _items = [];
  bool _loading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _refresh();
  }

  Future<void> _refresh() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      _items = await widget.admin.fetchCustomers();
    } catch (e) {
      _error = e.toString();
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _openForm([User? existing]) async {
    final fullName = TextEditingController(text: existing?.fullName ?? '');
    final username = TextEditingController(text: existing?.username ?? '');
    final email = TextEditingController(text: existing?.email ?? '');
    final password = TextEditingController();

    final saved = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(existing == null ? 'Add customer' : 'Edit customer'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(controller: fullName, decoration: const InputDecoration(labelText: 'Full name')),
              TextField(controller: username, decoration: const InputDecoration(labelText: 'Username')),
              TextField(controller: email, decoration: const InputDecoration(labelText: 'Email'), keyboardType: TextInputType.emailAddress),
              TextField(
                controller: password,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: existing == null ? 'Password' : 'Password (leave blank to keep)',
                ),
              ),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancel')),
          TextButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('Save')),
        ],
      ),
    );
    if (saved != true) return;

    final body = <String, dynamic>{
      'fullName': fullName.text.trim(),
      'username': username.text.trim(),
      'email': email.text.trim(),
    };
    if (password.text.trim().isNotEmpty) {
      body['password'] = password.text.trim();
    }

    try {
      if (existing == null) {
        if (password.text.trim().length < 6) {
          if (!mounted) return;
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Password must be at least 6 characters')),
          );
          return;
        }
        await widget.admin.createCustomer(body);
      } else {
        await widget.admin.updateCustomer(existing.id, body);
      }
      await _refresh();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
      }
    }
  }

  Future<void> _toggleBan(User user) async {
    if (user.banned) {
      try {
        await widget.admin.unbanCustomer(user.id);
        await _refresh();
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('${user.fullName} unbanned')));
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
        }
      }
      return;
    }

    final reason = TextEditingController(text: 'Violation of store policy');
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('Ban ${user.fullName}?'),
        content: TextField(controller: reason, decoration: const InputDecoration(labelText: 'Reason')),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancel')),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            style: TextButton.styleFrom(foregroundColor: NexusColors.error),
            child: const Text('Ban'),
          ),
        ],
      ),
    );
    if (ok != true) return;
    try {
      await widget.admin.banCustomer(user.id, reason: reason.text.trim());
      await _refresh();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
      }
    }
  }

  Future<void> _delete(User user) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete customer?'),
        content: Text('Permanently delete ${user.email}?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancel')),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            style: TextButton.styleFrom(foregroundColor: NexusColors.error),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
    if (ok != true) return;
    try {
      await widget.admin.deleteCustomer(user.id);
      await _refresh();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) return const Center(child: CircularProgressIndicator());
    return Scaffold(
      backgroundColor: Colors.transparent,
      floatingActionButton: FloatingActionButton.extended(
        heroTag: 'cust_fab',
        onPressed: () => _openForm(),
        icon: const Icon(Icons.person_add_alt_1),
        label: const Text('Add customer'),
      ),
      body: RefreshIndicator(
        onRefresh: _refresh,
        child: ListView(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 100),
          children: [
            if (_error != null) Text(_error!, style: const TextStyle(color: NexusColors.error)),
            Text(
              'POST · PUT · DELETE · BAN / UNBAN customers',
              style: GoogleFonts.jetBrainsMono(fontSize: 10, color: NexusColors.outlineStrong),
            ),
            const SizedBox(height: 8),
            if (_items.isEmpty)
              Padding(
                padding: const EdgeInsets.only(top: 40),
                child: Center(child: Text('No customers yet', style: GoogleFonts.inter(color: NexusColors.outlineStrong))),
              ),
            ..._items.map(
              (u) => Container(
                margin: const EdgeInsets.only(bottom: 8),
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(
                    color: u.banned ? NexusColors.error.withValues(alpha: 0.45) : NexusColors.outline,
                  ),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: Text(u.fullName, style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                          decoration: BoxDecoration(
                            color: (u.banned ? NexusColors.error : NexusColors.success).withValues(alpha: 0.12),
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            u.banned ? 'BANNED' : 'ACTIVE',
                            style: GoogleFonts.jetBrainsMono(
                              fontSize: 9,
                              fontWeight: FontWeight.w700,
                              color: u.banned ? NexusColors.error : NexusColors.success,
                            ),
                          ),
                        ),
                      ],
                    ),
                    Text('@${u.username}', style: GoogleFonts.inter(fontSize: 12, color: NexusColors.outlineStrong)),
                    Text(u.email, style: GoogleFonts.jetBrainsMono(fontSize: 11)),
                    if (u.banned && u.banReason != null && u.banReason!.isNotEmpty)
                      Padding(
                        padding: const EdgeInsets.only(top: 4),
                        child: Text('Reason: ${u.banReason}', style: GoogleFonts.inter(fontSize: 12, color: NexusColors.error)),
                      ),
                    const SizedBox(height: 6),
                    Wrap(
                      spacing: 4,
                      children: [
                        TextButton(
                          onPressed: () {
                            Navigator.of(context).push(
                              MaterialPageRoute(
                                builder: (_) => AdminCustomerDetailScreen(
                                  admin: widget.admin,
                                  customerId: u.id,
                                  customerName: u.fullName,
                                ),
                              ),
                            );
                          },
                          child: const Text('View activity'),
                        ),
                        TextButton(onPressed: () => _openForm(u), child: const Text('Edit')),
                        TextButton(
                          onPressed: () => _toggleBan(u),
                          child: Text(u.banned ? 'Unban' : 'Ban'),
                        ),
                        TextButton(
                          onPressed: () => _delete(u),
                          style: TextButton.styleFrom(foregroundColor: NexusColors.error),
                          child: const Text('Delete'),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
