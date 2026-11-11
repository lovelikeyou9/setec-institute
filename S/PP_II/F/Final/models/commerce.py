from extensions import db
from models.base import MysqlBit, utcnow


class CartItem(db.Model):
    __tablename__ = "cart_items"

    id = db.Column(db.BigInteger, primary_key=True)
    created_at = db.Column(db.DateTime, default=utcnow)
    quantity = db.Column(db.Integer, nullable=False, default=1)
    selected = db.Column(MysqlBit, nullable=False, default=True)
    # Variant selection (e.g. "Color:Black|RAM:32GB") + locked unit price at add-to-cart time
    selected_options = db.Column(db.String(255))
    unit_price = db.Column(db.Numeric(12, 2))
    product_id = db.Column(db.BigInteger, db.ForeignKey("products.id"), nullable=False)
    user_id = db.Column(db.BigInteger, db.ForeignKey("users.id"), nullable=False)

    user = db.relationship("User", back_populates="cart_items")
    product = db.relationship("Product", back_populates="cart_items")

    def line_unit_price(self):
        if self.unit_price is not None:
            return float(self.unit_price)
        return float(self.product.price) if self.product else 0.0


class WishlistItem(db.Model):
    __tablename__ = "wishlist_items"

    id = db.Column(db.BigInteger, primary_key=True)
    created_at = db.Column(db.DateTime, default=utcnow)
    product_id = db.Column(db.BigInteger, db.ForeignKey("products.id"), nullable=False)
    user_id = db.Column(db.BigInteger, db.ForeignKey("users.id"), nullable=False)

    user = db.relationship("User", back_populates="wishlist_items")
    product = db.relationship("Product", back_populates="wishlist_items")


class Notification(db.Model):
    __tablename__ = "notifications"

    id = db.Column(db.BigInteger, primary_key=True)
    created_at = db.Column(db.DateTime, default=utcnow)
    link = db.Column(db.String(300))
    message = db.Column(db.String(500), nullable=False)
    read_flag = db.Column(MysqlBit, nullable=False, default=False)
    title = db.Column(db.String(150), nullable=False)
    type = db.Column(db.String(50))
    user_id = db.Column(db.BigInteger, db.ForeignKey("users.id"), nullable=False)

    user = db.relationship("User")


class Order(db.Model):
    __tablename__ = "orders"

    id = db.Column(db.BigInteger, primary_key=True)
    cancelled_at = db.Column(db.DateTime)
    delivered_at = db.Column(db.DateTime)
    delivery_type = db.Column(db.String(20))      # EXPRESS|STANDARD
    discount = db.Column(db.Numeric(12, 2))
    estimated_delivery = db.Column(db.DateTime)
    order_number = db.Column(db.String(40), unique=True, nullable=False)
    paid_at = db.Column(db.DateTime)
    payment_method = db.Column(db.String(30))     # CASH_ON_DELIVERY|CREDIT_CARD|QR_PAYMENT|STRIPE|VISA
    placed_at = db.Column(db.DateTime)
    platform_fee = db.Column(db.Numeric(12, 2))
    recipient_name = db.Column(db.String(120))
    recipient_phone = db.Column(db.String(30))
    shipped_at = db.Column(db.DateTime)
    shipping_address = db.Column(db.String(400))
    shipping_fee = db.Column(db.Numeric(12, 2))
    status = db.Column(db.String(30), nullable=False)
    subtotal = db.Column(db.Numeric(12, 2), nullable=False)
    total = db.Column(db.Numeric(12, 2), nullable=False)
    tracking_number = db.Column(db.String(80))
    seller_id = db.Column(db.BigInteger, db.ForeignKey("sellers.id"), nullable=False)
    user_id = db.Column(db.BigInteger, db.ForeignKey("users.id"), nullable=False)
    dispute_note = db.Column(db.String(500))
    dispute_opened_at = db.Column(db.DateTime)
    dispute_reason = db.Column(db.String(20))     # DAMAGED|INCORRECT|MISSING|OTHER
    payout_released_at = db.Column(db.DateTime)
    protection_ends_at = db.Column(db.DateTime)
    admin_held_at = db.Column(db.DateTime)
    cancel_reason = db.Column(db.String(40))
    seller_confirmed_at = db.Column(db.DateTime)
    out_for_delivery_at = db.Column(db.DateTime)
    shipping_country = db.Column(db.String(80))

    user = db.relationship("User")
    seller = db.relationship("Seller", back_populates="orders")
    items = db.relationship("OrderItem", back_populates="order", cascade="all, delete-orphan")
    commission = db.relationship("Commission", back_populates="order", uselist=False,
                                 cascade="all, delete-orphan")

    def statusLabel(self):
        return (self.status or "").replace("_", " ")


class OrderItem(db.Model):
    __tablename__ = "order_items"

    id = db.Column(db.BigInteger, primary_key=True)
    line_total = db.Column(db.Numeric(12, 2), nullable=False)
    product_image = db.Column(db.String(500))
    product_name = db.Column(db.String(250), nullable=False)
    quantity = db.Column(db.Integer, nullable=False)
    unit_price = db.Column(db.Numeric(12, 2), nullable=False)
    line_status = db.Column(db.String(20), nullable=False, default="ACTIVE")
    order_id = db.Column(db.BigInteger, db.ForeignKey("orders.id"), nullable=False)
    product_id = db.Column(db.BigInteger, db.ForeignKey("products.id"), nullable=False)

    order = db.relationship("Order", back_populates="items")
    product = db.relationship("Product")

    def is_active(self):
        return (self.line_status or "ACTIVE") == "ACTIVE"

    def is_refused(self):
        return (self.line_status or "ACTIVE") == "REFUSED"


class Commission(db.Model):
    __tablename__ = "commissions"

    id = db.Column(db.BigInteger, primary_key=True)
    commission_amount = db.Column(db.Numeric(12, 2), nullable=False)
    commission_percent = db.Column(db.Numeric(5, 2), nullable=False)
    created_at = db.Column(db.DateTime, default=utcnow)
    sale_amount = db.Column(db.Numeric(12, 2), nullable=False)
    seller_earnings = db.Column(db.Numeric(12, 2), nullable=False)
    order_id = db.Column(db.BigInteger, db.ForeignKey("orders.id"), nullable=False)
    seller_id = db.Column(db.BigInteger, db.ForeignKey("sellers.id"), nullable=False)
    held_at = db.Column(db.DateTime)
    payout_status = db.Column(db.String(20), nullable=False, default="HELD")
    protection_started_at = db.Column(db.DateTime)
    refunded_at = db.Column(db.DateTime)
    released_at = db.Column(db.DateTime)

    order = db.relationship("Order", back_populates="commission")
    seller = db.relationship("Seller", back_populates="commissions")
