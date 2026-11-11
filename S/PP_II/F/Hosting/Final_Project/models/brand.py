from extensions import db


class Brand(db.Model):
    __tablename__ = "brand"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=False, unique=True)
    slug = db.Column(db.String(140), nullable=False, unique=True)
    logo = db.Column(db.String(255), nullable=True)
    description = db.Column(db.Text, nullable=True)

    products = db.relationship("Product", back_populates="brand", lazy="dynamic")

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "slug": self.slug,
            "logo": self.logo,
            "description": self.description or "",
        }
