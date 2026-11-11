from extensions import db
from models.base import MysqlBit, utcnow


class User(db.Model):
    __tablename__ = "users"

    id = db.Column(db.BigInteger, primary_key=True)
    avatar_url = db.Column(db.String(500))
    cover_url = db.Column(db.String(500))
    created_at = db.Column(db.DateTime, default=utcnow)
    updated_at = db.Column(db.DateTime, default=utcnow)
    email = db.Column(db.String(100), unique=True, nullable=False)
    enabled = db.Column(MysqlBit, nullable=False, default=True)
    full_name = db.Column(db.String(120), nullable=False)
    password = db.Column(db.String(255), nullable=False)
    phone = db.Column(db.String(30))
    role = db.Column(db.String(20), nullable=False, default="CUSTOMER")  # ADMIN|CUSTOMER|SELLER
    remember_token = db.Column(db.String(100))

    seller = db.relationship("Seller", back_populates="user", uselist=False,
                             cascade="all, delete-orphan")
    addresses = db.relationship("Address", back_populates="user", cascade="all, delete-orphan")
    cart_items = db.relationship("CartItem", back_populates="user", cascade="all, delete-orphan")
    wishlist_items = db.relationship("WishlistItem", back_populates="user", cascade="all, delete-orphan")

    # ---- flask-login style flags (used by templates: current_user.is_authenticated)
    is_authenticated = True
    is_anonymous = False

    @property
    def name(self):
        return self.full_name or ""

    # ---- password helpers (bcrypt $2y$ from Laravel + werkzeug hashes)
    def set_password(self, plain):
        from auth_utils import hash_password
        self.password = hash_password(plain)

    def check_password(self, plain):
        from auth_utils import verify_password
        return verify_password(self.password, plain)

    # ---- role helpers (same names as the Laravel model)
    def isAdmin(self):
        return self.role == "ADMIN"

    def isSeller(self):
        return self.role == "SELLER"

    def isCustomer(self):
        return self.role == "CUSTOMER"

    def isApprovedSeller(self):
        return self.isSeller() and self.seller is not None and self.seller.status == "APPROVED"

    def __repr__(self):
        return f"<User {self.id} {self.email} {self.role}>"


class AnonymousUser:
    """Stand-in for Auth::user() when nobody is logged in."""

    is_authenticated = False
    is_anonymous = True
    id = None
    role = None
    seller = None
    full_name = ""
    email = ""
    phone = ""
    avatar_url = None
    cover_url = None
    enabled = False

    def isAdmin(self):
        return False

    def isSeller(self):
        return False

    def isCustomer(self):
        return False

    def isApprovedSeller(self):
        return False

    def __bool__(self):
        return False


class Seller(db.Model):
    __tablename__ = "sellers"

    id = db.Column(db.BigInteger, primary_key=True)
    address = db.Column(db.String(200))
    banner_url = db.Column(db.String(500))
    business_info = db.Column(db.String(500))
    created_at = db.Column(db.DateTime, default=utcnow)
    description = db.Column(db.String(1000))
    followers = db.Column(db.Integer, default=0)
    id_document = db.Column(db.String(100))
    joined_at = db.Column(db.DateTime)
    level = db.Column(db.String(20), nullable=False, default="NEW")  # NEW|TOP|TRUSTED|VERIFIED
    location = db.Column(db.String(100))
    logo_url = db.Column(db.String(500))
    owner_name = db.Column(db.String(100))
    phone = db.Column(db.String(30))
    rating = db.Column(db.Float, default=0)
    rejection_reason = db.Column(db.String(500))
    response_rate = db.Column(db.Float)
    status = db.Column(db.String(20), nullable=False, default="PENDING")  # APPROVED|PENDING|REJECTED|SUSPENDED
    store_name = db.Column(db.String(150), nullable=False)
    total_products = db.Column(db.Integer, default=0)
    total_sales = db.Column(db.Integer, default=0)
    user_id = db.Column(db.BigInteger, db.ForeignKey("users.id"), unique=True, nullable=False)
    country = db.Column(db.String(80))

    user = db.relationship("User", back_populates="seller")
    products = db.relationship("Product", back_populates="seller")
    orders = db.relationship("Order", back_populates="seller")
    commissions = db.relationship("Commission", back_populates="seller")

    def isApproved(self):
        return self.status == "APPROVED"


class SellerFollow(db.Model):
    __tablename__ = "seller_follows"

    id = db.Column(db.BigInteger, primary_key=True)
    created_at = db.Column(db.DateTime, default=utcnow)
    seller_id = db.Column(db.BigInteger, db.ForeignKey("sellers.id"), nullable=False)
    user_id = db.Column(db.BigInteger, db.ForeignKey("users.id"), nullable=False)

    user = db.relationship("User")
    seller = db.relationship("Seller")


class Address(db.Model):
    __tablename__ = "addresses"

    id = db.Column(db.BigInteger, primary_key=True)
    commune = db.Column(db.String(80))
    created_at = db.Column(db.DateTime, default=utcnow)
    default_address = db.Column(MysqlBit, nullable=False, default=False)
    district = db.Column(db.String(80), nullable=False)
    full_name = db.Column(db.String(120), nullable=False)
    phone = db.Column(db.String(30), nullable=False)
    province = db.Column(db.String(80), nullable=False)
    street = db.Column(db.String(250), nullable=False)
    user_id = db.Column(db.BigInteger, db.ForeignKey("users.id"), nullable=False)
    country = db.Column(db.String(80))

    user = db.relationship("User", back_populates="addresses")

    def formatted(self):
        parts = [self.street, self.commune, self.district, self.province, self.country]
        return ", ".join(p for p in parts if p)
