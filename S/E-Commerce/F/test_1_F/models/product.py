from datetime import datetime

from extensions import db


class Product(db.Model):
    __tablename__ = "products"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    brand = db.Column(db.String(100), nullable=False)
    price = db.Column(db.Numeric(10, 2), nullable=False)
    discount_price = db.Column(db.Numeric(10, 2))
    stock_quantity = db.Column(db.Integer, default=0, nullable=False)
    image = db.Column(db.String(255), default="placeholder.svg")
    specifications = db.Column(db.Text)
    rating = db.Column(db.Float, default=4.5)
    sales_count = db.Column(db.Integer, default=0)
    is_featured = db.Column(db.Boolean, default=False)
    category_id = db.Column(db.Integer, db.ForeignKey("categories.id"), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    category = db.relationship("Category", back_populates="products")
    cart_items = db.relationship("CartItem", back_populates="product", cascade="all, delete-orphan")
    order_items = db.relationship("OrderItem", back_populates="product")

    @property
    def effective_price(self):
        if self.discount_price is not None and self.discount_price < self.price:
            return float(self.discount_price)
        return float(self.price)

    @property
    def has_discount(self):
        return (
            self.discount_price is not None
            and float(self.discount_price) < float(self.price)
        )

    @property
    def discount_percent(self):
        if not self.has_discount:
            return 0
        return int(round((1 - float(self.discount_price) / float(self.price)) * 100))

    @property
    def in_stock(self):
        return self.stock_quantity > 0

    @property
    def image_url(self):
        if not self.image:
            return "/static/images/products/placeholder.svg"
        if self.image.startswith("http"):
            return self.image
        if self.image.startswith("/static/"):
            return self.image
        if self.image.startswith("../uploads/") or self.image.startswith("uploads/"):
            name = self.image.split("/")[-1]
            return f"/static/uploads/{name}"
        return f"/static/images/products/{self.image}"

    def __repr__(self):
        return f"<Product {self.name}>"
