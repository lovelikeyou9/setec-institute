import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '../../providers/cart_provider.dart';
import '../../services/api_client.dart';
import '../../services/order_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/formatters.dart';
import '../../widgets/product_image.dart';

class CartScreen extends StatefulWidget {
  const CartScreen({super.key});

  @override
  State<CartScreen> createState() => _CartScreenState();
}

class _CartScreenState extends State<CartScreen> {
  final _discountController = TextEditingController();
  final _addressController = TextEditingController(
    text: 'MMT Store, Phnom Penh, Cambodia',
  );
  bool _checkingOut = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<CartProvider>().refresh();
    });
  }

  @override
  void dispose() {
    _discountController.dispose();
    _addressController.dispose();
    super.dispose();
  }

  Future<void> _checkout(CartProvider cart) async {
    final address = _addressController.text.trim();
    if (address.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Enter a shipping address')),
      );
      return;
    }
    setState(() => _checkingOut = true);
    try {
      final api = context.read<ApiClient>();
      final orders = OrderService(api);
      final order = await orders.createOrder(
        shippingAddress: address,
        discountCode: cart.discountCode,
      );
      await cart.refresh();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              'Order ${order.displayId} placed (${formatMoney(order.total)}) — pending admin confirmation',
            ),
          ),
        );
      }
    } on ApiException catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.message)));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
      }
    } finally {
      if (mounted) setState(() => _checkingOut = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final cart = context.watch<CartProvider>();

    return Scaffold(
      appBar: AppBar(
        title: Text('Your cart', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700)),
      ),
      body: cart.loading && cart.items.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : cart.items.isEmpty
              ? ListView(
                  children: [
                    const SizedBox(height: 72),
                    Icon(Icons.shopping_cart_outlined, size: 48, color: NexusColors.outlineStrong.withValues(alpha: 0.7)),
                    const SizedBox(height: 12),
                    Center(
                      child: Text('Your cart is empty', style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700, fontSize: 16)),
                    ),
                    const SizedBox(height: 6),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 40),
                      child: Text(
                        'Browse the store or catalog and add products to checkout.',
                        textAlign: TextAlign.center,
                        style: GoogleFonts.inter(color: NexusColors.outlineStrong, fontSize: 13),
                      ),
                    ),
                  ],
                )
              : Column(
                  children: [
                    Expanded(
                      child: ListView.separated(
                        padding: const EdgeInsets.all(16),
                        itemCount: cart.items.length,
                        separatorBuilder: (context, index) => const SizedBox(height: 12),
                        itemBuilder: (context, index) {
                          final item = cart.items[index];
                          return Container(
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(8),
                              border: Border.all(color: NexusColors.outline),
                            ),
                            padding: const EdgeInsets.all(12),
                            child: Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                ClipRRect(
                                  borderRadius: BorderRadius.circular(8),
                                  child: SizedBox(
                                    width: 72,
                                    child: ProductImage(url: item.product.imageUrl, height: 72),
                                  ),
                                ),
                                const SizedBox(width: 12),
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        item.product.name,
                                        style: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w700),
                                      ),
                                      Text(item.product.sku, style: context.monoStyle.copyWith(fontSize: 10)),
                                      const SizedBox(height: 6),
                                      Text(formatMoney(item.product.price), style: context.monoStyle),
                                      Row(
                                        children: [
                                          IconButton(
                                            visualDensity: VisualDensity.compact,
                                            onPressed: () => cart.setQuantity(item, item.quantity - 1),
                                            icon: const Icon(Icons.remove_circle_outline),
                                          ),
                                          Text('${item.quantity}', style: context.monoStyle),
                                          IconButton(
                                            visualDensity: VisualDensity.compact,
                                            onPressed: () => cart.setQuantity(item, item.quantity + 1),
                                            icon: const Icon(Icons.add_circle_outline),
                                          ),
                                          const Spacer(),
                                          Text(
                                            formatMoney(item.lineTotal),
                                            style: context.monoStyle.copyWith(fontWeight: FontWeight.w700),
                                          ),
                                        ],
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: const BoxDecoration(
                        color: Colors.white,
                        border: Border(top: BorderSide(color: NexusColors.outline)),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          TextField(
                            controller: _addressController,
                            decoration: const InputDecoration(
                              labelText: 'Shipping address',
                            ),
                          ),
                          const SizedBox(height: 10),
                          TextField(
                            controller: _discountController,
                            decoration: const InputDecoration(
                              labelText: 'Discount code',
                              hintText: 'CYBER2025 (−\$50)',
                            ),
                            onChanged: cart.setDiscountCode,
                          ),
                          const SizedBox(height: 12),
                          _LedgerRow(label: 'Subtotal', value: formatMoney(cart.subtotal)),
                          _LedgerRow(label: 'Discount', value: '-${formatMoney(cart.discount)}'),
                          _LedgerRow(
                            label: 'Freight',
                            value: cart.shipping == 0 ? 'FREE' : formatMoney(cart.shipping),
                          ),
                          _LedgerRow(label: 'Tax (7%)', value: formatMoney(cart.tax)),
                          const Divider(),
                          _LedgerRow(
                            label: 'Total due',
                            value: formatMoney(cart.total),
                            bold: true,
                          ),
                          const SizedBox(height: 12),
                          ElevatedButton(
                            onPressed: _checkingOut ? null : () => _checkout(cart),
                            child: _checkingOut
                                ? const SizedBox(
                                    height: 20,
                                    width: 20,
                                    child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                                  )
                                : const Text('Proceed to secure checkout'),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
    );
  }
}

class _LedgerRow extends StatelessWidget {
  const _LedgerRow({required this.label, required this.value, this.bold = false});

  final String label;
  final String value;
  final bool bold;

  @override
  Widget build(BuildContext context) {
    final style = context.monoStyle.copyWith(
      fontWeight: bold ? FontWeight.w700 : FontWeight.w500,
      fontSize: bold ? 16 : 13,
    );
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: GoogleFonts.inter(fontWeight: bold ? FontWeight.w700 : FontWeight.w500)),
          Text(value, style: style),
        ],
      ),
    );
  }
}
