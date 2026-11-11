from extensions import db
from models.base import MysqlBit, utcnow


class Category(db.Model):
    __tablename__ = "categories"

    id = db.Column(db.BigInteger, primary_key=True)
    active = db.Column(MysqlBit, nullable=False, default=True)
    icon = db.Column(db.String(50))
    image_url = db.Column(db.String(500))
    name = db.Column(db.String(80), unique=True, nullable=False)
    slug = db.Column(db.String(80), unique=True, nullable=False)
    sort_order = db.Column(db.Integer)

    products = db.relationship("Product", back_populates="category")


class Brand(db.Model):
    __tablename__ = "brands"

    id = db.Column(db.BigInteger, primary_key=True)
    active = db.Column(MysqlBit, nullable=False, default=True)
    logo_url = db.Column(db.String(500))
    name = db.Column(db.String(80), unique=True, nullable=False)
    slug = db.Column(db.String(80), unique=True, nullable=False)

    products = db.relationship("Product", back_populates="brand")


class Product(db.Model):
    __tablename__ = "products"

    id = db.Column(db.BigInteger, primary_key=True)
    active = db.Column(MysqlBit, nullable=False, default=True)
    color_options = db.Column(db.String(500))
    condition_type = db.Column(db.String(20), default="NEW")  # NEW|REFURBISHED|USED
    created_at = db.Column(db.DateTime, default=utcnow)
    description = db.Column(db.Text)
    discount_percent = db.Column(db.Integer)
    featured = db.Column(MysqlBit, nullable=False, default=False)
    flash_deal = db.Column(MysqlBit, nullable=False, default=False)
    flash_deal_ends_at = db.Column(db.DateTime)
    gallery_urls = db.Column(db.Text)
    image_url = db.Column(db.String(500))
    name = db.Column(db.String(250), nullable=False)
    original_price = db.Column(db.Numeric(12, 2))
    price = db.Column(db.Numeric(12, 2), nullable=False)
    ram_options = db.Column(db.String(500))
    option_groups = db.Column(db.Text)
    rating = db.Column(db.Float)
    review_count = db.Column(db.Integer)
    sku = db.Column(db.String(80))
    slug = db.Column(db.String(280), unique=True, nullable=False)
    sold_count = db.Column(db.Integer)
    specifications = db.Column(db.Text)
    stock = db.Column(db.Integer, nullable=False, default=0)
    storage_options = db.Column(db.String(500))
    updated_at = db.Column(db.DateTime, default=utcnow)
    warranty = db.Column(db.String(80))
    brand_id = db.Column(db.BigInteger, db.ForeignKey("brands.id"), nullable=False)
    category_id = db.Column(db.BigInteger, db.ForeignKey("categories.id"), nullable=False)
    seller_id = db.Column(db.BigInteger, db.ForeignKey("sellers.id"), nullable=False)

    brand = db.relationship("Brand", back_populates="products")
    category = db.relationship("Category", back_populates="products")
    seller = db.relationship("Seller", back_populates="products")
    reviews = db.relationship("Review", back_populates="product", cascade="all, delete-orphan")
    cart_items = db.relationship("CartItem", back_populates="product", cascade="all, delete-orphan")
    wishlist_items = db.relationship("WishlistItem", back_populates="product", cascade="all, delete-orphan")

    @property
    def effective_price(self):
        return self.price

    def selectableOptionGroups(self):
        # Includes per-choice prices (from option_groups JSON, or auto-scaled by capacity).
        from services.product_variants import ensure_priced_groups
        return ensure_priced_groups(self)

    def galleryList(self):
        from helpers import product_gallery_urls
        return product_gallery_urls(self.image_url, self.gallery_urls)

    def primaryImageUrl(self):
        from helpers import default_product_image
        lst = self.galleryList()
        return lst[0] if lst else default_product_image()


class Review(db.Model):
    __tablename__ = "reviews"

    id = db.Column(db.BigInteger, primary_key=True)
    comment = db.Column(db.String(2000))
    created_at = db.Column(db.DateTime, default=utcnow)
    hidden = db.Column(MysqlBit, nullable=False, default=False)
    image_urls = db.Column(db.String(1000))
    rating = db.Column(db.Integer, nullable=False)
    seller_responded_at = db.Column(db.DateTime)
    seller_response = db.Column(db.String(2000))
    verified_purchase = db.Column(MysqlBit, nullable=False, default=False)
    product_id = db.Column(db.BigInteger, db.ForeignKey("products.id"), nullable=False)
    user_id = db.Column(db.BigInteger, db.ForeignKey("users.id"), nullable=False)

    product = db.relationship("Product", back_populates="reviews")
    user = db.relationship("User")
