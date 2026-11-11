from extensions import db


class Product(db.Model):
    __tablename__ = "product"

    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(255), nullable=False)
    price = db.Column(db.Float, nullable=False, default=0)
    description = db.Column(db.Text, nullable=True)
    image = db.Column(db.String(500), nullable=True)
    rating_rate = db.Column(db.Float, nullable=True, default=0)
    rating_count = db.Column(db.Integer, nullable=True, default=0)
    is_active = db.Column(db.Boolean, nullable=False, default=True)

    category_id = db.Column(db.Integer, db.ForeignKey("category.id"), nullable=True)
    brand_id = db.Column(db.Integer, db.ForeignKey("brand.id"), nullable=True)

    category = db.relationship("Category", back_populates="products")
    brand = db.relationship("Brand", back_populates="products")

    def to_dict(self):
        return {
            "id": self.id,
            "title": self.title,
            "price": float(self.price or 0),
            "description": self.description or "",
            "image": self.image or "",
            "category": self.category.name if self.category else "",
            "category_id": self.category_id,
            "brand": self.brand.name if self.brand else "",
            "brand_id": self.brand_id,
            "rating": {
                "rate": float(self.rating_rate or 0),
                "count": int(self.rating_count or 0),
            },
            "is_active": self.is_active,
        }
