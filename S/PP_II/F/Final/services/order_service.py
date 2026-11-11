"""Port of App\\Services\\OrderService (escrow flow, 100%).

Flow:  place order  ->  admin confirms hold (FUNDS_HELD)  ->  seller accepts stock
       (SELLER_PROCESSING) -> seller ships (SHIPPED / OUT_FOR_DELIVERY) -> admin confirms
       DELIVERED (3-day buyer protection) -> payout released (COMPLETED, 3% commission).
Online payments start as PAYMENT_CONFIRMED, COD as PENDING_PAYMENT.
"""
import time
from datetime import timedelta

from flask import current_app
from sqlalchemy.orm import joinedload

from extensions import db
from models import (
    CartItem, Commission, Notification, Order, OrderItem, Product, User,
)
from models.base import utcnow
from services import cart_service


class OrderError(RuntimeError):
    """Same role as the RuntimeException thrown by OrderService.php."""


COMMISSION_PERCENT = 3.0
BUYER_PROTECTION_DAYS = 3


def now():
    # Laravel stores datetimes without microseconds (Y-m-d H:i:s)
    return utcnow().replace(microsecond=0)


def get_buyer_protection_days():
    try:
        return int(current_app.config.get("BUYER_PROTECTION_DAYS", BUYER_PROTECTION_DAYS))
    except RuntimeError:
        return BUYER_PROTECTION_DAYS


def _commission_percent():
    try:
        return float(current_app.config.get("COMMISSION_PERCENT", COMMISSION_PERCENT))
    except RuntimeError:
        return COMMISSION_PERCENT


def _get_order(order_id, *options):
    q = Order.query
    if options:
        q = q.options(*options)
    order = q.filter(Order.id == order_id).first()
    if order is None:
        raise LookupError("Order not found")
    return order


def notify(user_id, title, message, type_, link=None):
    db.session.add(Notification(
        user_id=user_id, title=title[:150], message=message[:500], type=type_,
        link=link, read_flag=False, created_at=now(),
    ))


# ---------------------------------------------------------------- placing
def place_orders(user, recipient_name, recipient_phone, shipping_address, shipping_country,
                 delivery_type, payment_method):
    """One order per seller. Returns the list of created orders."""
    try:
        selected = cart_service.get_selected(user.id)
        if not selected:
            raise OrderError("No items selected")

        by_seller = {}
        for ci in selected:
            by_seller.setdefault(ci.product.seller_id, []).append(ci)

        shipping = 5.00 if delivery_type == "EXPRESS" else 2.00
        online_pay = payment_method != "CASH_ON_DELIVERY"
        percent = _commission_percent()
        stamp = now()
        orders = []

        for seller_id, items in by_seller.items():
            seller = items[0].product.seller
            subtotal = sum(ci.line_unit_price() * ci.quantity for ci in items)
            commission_amt = round(subtotal * percent / 100, 2)
            order_number = f"KS{int(time.time() * 1000)}{seller_id}"

            order = Order(
                order_number=order_number,
                user_id=user.id,
                seller_id=seller.id,
                status="PAYMENT_CONFIRMED" if online_pay else "PENDING_PAYMENT",
                payment_method=payment_method,
                delivery_type=delivery_type,
                subtotal=round(subtotal, 2),
                shipping_fee=shipping,
                discount=0,
                platform_fee=commission_amt,
                total=round(subtotal + shipping, 2),
                recipient_name=recipient_name,
                recipient_phone=recipient_phone,
                shipping_address=shipping_address,
                shipping_country=shipping_country.strip() if shipping_country else None,
                placed_at=stamp,
                paid_at=stamp if online_pay else None,
                estimated_delivery=stamp + timedelta(days=1 if delivery_type == "EXPRESS" else 3),
            )
            db.session.add(order)
            db.session.flush()

            for ci in items:
                p = ci.product
                if (p.stock or 0) < ci.quantity:
                    raise OrderError(f"Insufficient stock for {p.name}")
                p.stock = (p.stock or 0) - ci.quantity
                p.sold_count = (p.sold_count or 0) + ci.quantity
                unit = ci.line_unit_price()
                item_name = p.name
                if ci.selected_options:
                    # Append selected variant so the order line shows RAM/Storage choice
                    item_name = f"{p.name} ({ci.selected_options.replace('|', ', ')})"
                db.session.add(OrderItem(
                    order_id=order.id, product_id=p.id, product_name=item_name[:250],
                    product_image=p.image_url, quantity=ci.quantity,
                    unit_price=unit, line_total=round(unit * ci.quantity, 2),
                    line_status="ACTIVE",
                ))

            db.session.add(Commission(
                order_id=order.id, seller_id=seller.id, sale_amount=round(subtotal, 2),
                commission_percent=percent, commission_amount=commission_amt,
                seller_earnings=round(subtotal - commission_amt, 2),
                payout_status="HELD", held_at=stamp, created_at=stamp,
            ))

            seller.total_sales = (seller.total_sales or 0) + sum(ci.quantity for ci in items)

            pay_msg = (
                "Payment successful. Waiting for MMT admin to confirm and hold your funds."
                if online_pay else
                "Order placed (Cash on Delivery). Pay when you receive the package."
            )
            notify(user.id, "Order Confirmed", f"Order {order_number}: {pay_msg}", "ORDER",
                   f"/orders/{order_number}")
            orders.append(order)
            # keep order numbers unique when several sellers are processed in the same ms
            time.sleep(0.002)

        CartItem.query.filter(CartItem.user_id == user.id, CartItem.selected == 1) \
            .delete(synchronize_session=False)
        db.session.commit()
        return orders
    except Exception:
        db.session.rollback()
        raise


# ------------------------------------------------------------- escrow steps
def admin_confirm_hold(order_id):
    order = _get_order(order_id)
    if order.status not in ("PAYMENT_CONFIRMED", "PENDING_PAYMENT"):
        raise OrderError("Only paid (or pending COD) orders can be confirmed for escrow hold")
    stamp = now()
    if not order.paid_at:
        order.paid_at = stamp
    order.admin_held_at = stamp
    order.status = "FUNDS_HELD"
    _ensure_commission_held(order)

    notify(order.user_id, "Payment Held by MMT",
           f"Admin confirmed order {order.order_number}. Your payment is held until delivery "
           "and buyer protection finish.", "ORDER", f"/orders/{order.order_number}")
    if order.seller and order.seller.user_id:
        notify(order.seller.user_id, "Confirm Stock",
               f"Order {order.order_number} \u2014 payment is held. Please confirm you have stock, "
               "or decline if out of stock.", "ORDER", "/seller-dashboard/orders")
    db.session.commit()
    return order


def seller_accept_order(order_id, seller_id):
    order = _get_order(order_id)
    _assert_seller_owns(order, seller_id)
    if order.status != "FUNDS_HELD":
        raise OrderError("You can only accept after admin has confirmed and held payment")
    order.seller_confirmed_at = now()
    order.status = "SELLER_PROCESSING"
    order.dispute_reason = None
    order.dispute_note = None
    order.dispute_opened_at = None
    notify(order.user_id, "Seller Confirmed",
           f"Seller confirmed stock for order {order.order_number} and is preparing your package.",
           "ORDER", f"/orders/{order.order_number}")
    db.session.commit()
    return order


def seller_decline_out_of_stock(order_id, seller_id):
    """Refuse every remaining item on the order (full cancel + refund)."""
    order = _get_order(order_id, joinedload(Order.items).joinedload(OrderItem.product),
                       joinedload(Order.seller), joinedload(Order.commission))
    _assert_seller_owns(order, seller_id)
    if order.status not in ("FUNDS_HELD", "PAYMENT_CONFIRMED"):
        raise OrderError("This order can no longer be declined for stock")

    for item in order.items:
        if _item_is_active(item):
            item.line_status = "REFUSED"
            _restore_item_stock(order, item)

    _finalize_full_out_of_stock_refund(order)
    db.session.commit()
    return order


def seller_decline_item(order_id, item_id, seller_id):
    """Refuse one line item. If nothing remains, cancel the whole order."""
    order = _get_order(order_id, joinedload(Order.items).joinedload(OrderItem.product),
                       joinedload(Order.seller), joinedload(Order.commission))
    _assert_seller_owns(order, seller_id)
    if order.status not in ("FUNDS_HELD", "PAYMENT_CONFIRMED"):
        raise OrderError("This item can no longer be refused for stock")

    item = next((i for i in order.items if int(i.id) == int(item_id)), None)
    if item is None:
        raise OrderError("Item not found on this order")
    if not _item_is_active(item):
        raise OrderError("This item was already refused")

    item.line_status = "REFUSED"
    _restore_item_stock(order, item)

    remaining = [i for i in order.items if _item_is_active(i)]
    if not remaining:
        _finalize_full_out_of_stock_refund(order)
        db.session.commit()
        return order

    refund_line = float(item.line_total or 0)
    _recalc_order_after_partial_refuse(order)
    notify(
        order.user_id,
        "Item refused \u2014 Out of Stock",
        f"Seller could not fulfill \"{item.product_name}\" on order {order.order_number}. "
        f"${refund_line:.2f} for that item will be refunded. "
        f"Other items stay on the order (new total ${float(order.total):.2f}).",
        "ORDER",
        f"/orders/{order.order_number}",
    )
    db.session.commit()
    return order


def seller_update_fulfillment(order_id, seller_id, status):
    order = _get_order(order_id)
    _assert_seller_owns(order, seller_id)
    allowed = {
        "SELLER_PROCESSING": ["SHIPPED"],
        "SHIPPED": ["OUT_FOR_DELIVERY"],
    }.get(order.status, [])
    if status not in allowed:
        raise OrderError("Seller can only: Processing \u2192 Shipped \u2192 Out for delivery. "
                         "Admin confirms Delivered.")
    return update_status(order_id, status)


def admin_confirm_delivered(order_id):
    order = _get_order(order_id)
    if order.status not in ("OUT_FOR_DELIVERY", "SHIPPED"):
        raise OrderError("Confirm delivered only after seller marks Out for delivery (or Shipped).")
    return update_status(order_id, "DELIVERED")


def update_status(order_id, status):
    order = _get_order(order_id)
    order.status = status
    stamp = now()

    if status == "PAYMENT_CONFIRMED":
        order.paid_at = stamp
        _ensure_commission_held(order)
    elif status == "FUNDS_HELD":
        if not order.paid_at:
            order.paid_at = stamp
        order.admin_held_at = stamp
        _ensure_commission_held(order)
    elif status == "SELLER_PROCESSING":
        if not order.seller_confirmed_at:
            order.seller_confirmed_at = stamp
    elif status == "SHIPPED":
        order.shipped_at = stamp
        if not order.tracking_number:
            order.tracking_number = f"TRK{int(time.time() * 1000)}"
    elif status == "OUT_FOR_DELIVERY":
        if not order.shipped_at:
            order.shipped_at = stamp
        order.out_for_delivery_at = stamp
    elif status == "DELIVERED":
        order.delivered_at = stamp
        if order.payment_method == "CASH_ON_DELIVERY" and not order.paid_at:
            order.paid_at = stamp
        _start_buyer_protection(order, stamp)
    elif status == "CANCELLED":
        order.cancelled_at = stamp

    notify(order.user_id, "Order Update", _status_message(order, status), "ORDER",
           f"/orders/{order.order_number}")
    db.session.commit()
    return order


# ---------------------------------------------------------------- disputes
def open_dispute(order_id, buyer, reason, note):
    order = _get_order(order_id, joinedload(Order.seller))
    _assert_buyer(order, buyer)
    if order.status != "DELIVERED":
        raise OrderError("You can only open a dispute after the order is delivered")
    if order.protection_ends_at and now() > order.protection_ends_at:
        raise OrderError("Buyer protection period has ended")

    order.status = "DISPUTED"
    order.dispute_reason = reason
    order.dispute_note = note
    order.dispute_opened_at = now()
    Commission.query.filter_by(order_id=order.id).update({"payout_status": "DISPUTED"})

    notify(order.user_id, "Dispute Opened",
           f"Your report ({reason}) for order {order.order_number} was submitted. Payment remains held.",
           "ORDER", f"/orders/{order.order_number}")
    if order.seller and order.seller.user_id:
        notify(order.seller.user_id, "Order Disputed",
               f"Buyer reported {reason} on order {order.order_number}. Payout is paused.",
               "ORDER", "/seller-dashboard/orders")
    db.session.commit()
    return order


# ----------------------------------------------------------------- payouts
def release_payout(order_id, force=False):
    order = _get_order(order_id, joinedload(Order.seller))
    commission = Commission.query.filter_by(order_id=order.id).first()
    if commission is None:
        raise LookupError("Commission not found for this order")

    if commission.payout_status == "RELEASED":
        return order
    if commission.payout_status == "REFUNDED":
        raise OrderError("This order was refunded \u2014 cannot release to seller")
    if commission.payout_status == "DISPUTED" and not force:
        raise OrderError("Resolve the dispute before releasing payout")
    if order.status not in ("DELIVERED", "COMPLETED") and not (force and order.status == "DISPUTED"):
        raise OrderError("Payout can only be released after delivery")
    if not force and order.protection_ends_at and now() < order.protection_ends_at:
        raise OrderError(f"Buyer protection is still active until {order.protection_ends_at}")

    stamp = now()
    commission.payout_status = "RELEASED"
    commission.released_at = stamp
    order.payout_released_at = stamp
    order.status = "COMPLETED"

    if order.seller and order.seller.user_id:
        notify(order.seller.user_id, "Payout Released",
               f"MMT released ${float(commission.seller_earnings):.2f} for order {order.order_number} "
               f"(commission {float(commission.commission_percent):g}% deducted).",
               "PAYOUT", "/seller-dashboard/earnings")
    notify(order.user_id, "Order Complete",
           f"Order {order.order_number} is complete. Thank you for shopping on MMT.",
           "ORDER", f"/orders/{order.order_number}")
    db.session.commit()
    return order


def force_release_payout(order_id):
    order = _get_order(order_id)
    if order.status == "DISPUTED":
        order.status = "DELIVERED"
        Commission.query.filter_by(order_id=order.id).update({"payout_status": "IN_PROTECTION"})
        db.session.commit()
    return release_payout(order_id, force=True)


def refund_order(order_id):
    order = _get_order(order_id)
    commission = Commission.query.filter_by(order_id=order.id).first()
    stamp = now()
    if commission:
        if commission.payout_status == "RELEASED":
            raise OrderError("Payout already released to seller \u2014 cannot refund automatically")
        commission.payout_status = "REFUNDED"
        commission.refunded_at = stamp
    order.status = "REFUNDED"
    order.cancelled_at = stamp
    notify(order.user_id, "Refund Issued",
           f"Order {order.order_number} was refunded. Held funds were returned.",
           "ORDER", f"/orders/{order.order_number}")
    db.session.commit()
    return order


def can_open_dispute(order):
    if order.status != "DELIVERED":
        return False
    if not order.protection_ends_at:
        return False
    return not (now() > order.protection_ends_at)


def auto_release_eligible_orders():
    due = Order.query.filter(
        Order.status == "DELIVERED",
        Order.protection_ends_at.isnot(None),
        Order.protection_ends_at <= now(),
    ).all()
    released = 0
    for o in due:
        try:
            release_payout(o.id)
            released += 1
        except Exception:
            db.session.rollback()
    return released


# ----------------------------------------------------------------- private
def _start_buyer_protection(order, stamp):
    order.protection_ends_at = stamp + timedelta(days=max(1, get_buyer_protection_days()))
    c = Commission.query.filter_by(order_id=order.id).first()
    if c and c.payout_status in ("HELD", None):
        c.payout_status = "IN_PROTECTION"
        c.protection_started_at = stamp


def _ensure_commission_held(order):
    c = Commission.query.filter_by(order_id=order.id).first()
    if c and c.payout_status in (None, "DISPUTED", "HELD"):
        c.payout_status = "HELD"
        if not c.held_at:
            c.held_at = now()


def _assert_seller_owns(order, seller_id):
    if int(order.seller_id) != int(seller_id):
        raise OrderError("You can only manage orders for your own store")


def _item_is_active(item):
    return (getattr(item, "line_status", None) or "ACTIVE") == "ACTIVE"


def _restore_item_stock(order, item):
    if item.product:
        item.product.stock = (item.product.stock or 0) + item.quantity
        item.product.sold_count = max(0, (item.product.sold_count or 0) - item.quantity)
    if order.seller:
        order.seller.total_sales = max(0, (order.seller.total_sales or 0) - item.quantity)


def _recalc_order_after_partial_refuse(order):
    active = [i for i in order.items if _item_is_active(i)]
    subtotal = round(sum(float(i.line_total or 0) for i in active), 2)
    shipping = float(order.shipping_fee or 0)
    discount = float(order.discount or 0)
    percent = _commission_percent()
    commission_amt = round(subtotal * percent / 100, 2)
    order.subtotal = subtotal
    order.platform_fee = commission_amt
    order.total = round(subtotal + shipping - discount, 2)

    commission = order.commission or Commission.query.filter_by(order_id=order.id).first()
    if commission and commission.payout_status != "RELEASED":
        commission.sale_amount = subtotal
        commission.commission_percent = percent
        commission.commission_amount = commission_amt
        commission.seller_earnings = round(subtotal - commission_amt, 2)


def _finalize_full_out_of_stock_refund(order):
    order.cancel_reason = "OUT_OF_STOCK"
    order.cancelled_at = now()
    order.status = "REFUNDED"
    order.subtotal = 0
    order.platform_fee = 0
    order.total = 0

    commission = order.commission or Commission.query.filter_by(order_id=order.id).first()
    if commission and commission.payout_status != "RELEASED":
        commission.payout_status = "REFUNDED"
        commission.refunded_at = now()
        commission.sale_amount = 0
        commission.commission_amount = 0
        commission.seller_earnings = 0

    notify(
        order.user_id,
        "Order Cancelled \u2014 Out of Stock",
        f"Seller could not fulfill order {order.order_number} (out of stock). "
        "Your payment has been refunded.",
        "ORDER",
        f"/orders/{order.order_number}",
    )


def _assert_buyer(order, buyer):
    if int(order.user_id) != int(buyer.id):
        raise OrderError("You can only manage your own orders")


def _status_message(order, status):
    days = get_buyer_protection_days()
    n = order.order_number
    if status == "FUNDS_HELD":
        return f"MMT admin confirmed payment for order {n}. Funds are held until you receive the product."
    if status == "SELLER_PROCESSING":
        return f"Seller confirmed stock and is processing order {n}."
    if status == "SHIPPED":
        return f"Order {n} has been shipped" + (f" \u00b7 Tracking: {order.tracking_number}" if order.tracking_number else "")
    if status == "OUT_FOR_DELIVERY":
        return f"Order {n} is out for delivery."
    if status == "DELIVERED":
        return f"Order {n} delivered. Buyer protection is open for {days} day(s)."
    if status == "DISPUTED":
        return f"A dispute was opened on order {n}. Funds remain held."
    if status == "COMPLETED":
        return f"Order {n} completed. Admin deducted commission and paid the seller."
    return f"Order {n} is now: " + status.replace("_", " ")
