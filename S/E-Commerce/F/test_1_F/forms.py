from flask_wtf import FlaskForm
from flask_wtf.file import FileField, FileAllowed
from wtforms import (
    StringField,
    PasswordField,
    SubmitField,
    TextAreaField,
    DecimalField,
    IntegerField,
    SelectField,
    BooleanField,
    HiddenField,
)
from wtforms.validators import (
    DataRequired,
    Email,
    EqualTo,
    Length,
    NumberRange,
    Optional,
    ValidationError,
)


class RegisterForm(FlaskForm):
    name = StringField("Full Name", validators=[DataRequired(), Length(min=2, max=120)])
    email = StringField("Email", validators=[DataRequired(), Email(), Length(max=120)])
    password = PasswordField("Password", validators=[DataRequired(), Length(min=6, max=128)])
    confirm_password = PasswordField(
        "Confirm Password",
        validators=[DataRequired(), EqualTo("password", message="Passwords must match.")],
    )
    submit = SubmitField("Create Account")


class LoginForm(FlaskForm):
    email = StringField("Email", validators=[DataRequired(), Email()])
    password = PasswordField("Password", validators=[DataRequired()])
    remember = BooleanField("Remember me")
    submit = SubmitField("Sign In")


class CheckoutForm(FlaskForm):
    full_name = StringField("Full Name", validators=[DataRequired(), Length(max=120)])
    email = StringField("Email", validators=[DataRequired(), Email(), Length(max=120)])
    phone = StringField("Phone", validators=[DataRequired(), Length(max=40)])
    address = StringField("Address", validators=[DataRequired(), Length(max=255)])
    city = StringField("City", validators=[DataRequired(), Length(max=100)])
    country = StringField("Country", validators=[DataRequired(), Length(max=100)])
    payment_method = SelectField(
        "Payment Method",
        choices=[
            ("stripe", "Stripe (redirect checkout)"),
            ("stripe_qr", "Stripe QR Code"),
            ("visa", "Visa / Credit or debit card (X-Pay)"),
            ("visa_qr", "Visa QR Code"),
        ],
        default="stripe",
        validators=[DataRequired()],
    )
    card_number = StringField("Card number", validators=[Optional(), Length(max=19)])
    card_exp_month = StringField("Expiry month (MM)", validators=[Optional(), Length(max=2)])
    card_exp_year = StringField("Expiry year (YYYY)", validators=[Optional(), Length(max=4)])
    card_cvc = StringField("CVC", validators=[Optional(), Length(max=4)])
    submit = SubmitField("Pay now")

    def validate(self, extra_validators=None):
        ok = super().validate(extra_validators=extra_validators)
        if not ok:
            return False
        if self.payment_method.data != "visa":
            return True

        number = "".join(ch for ch in (self.card_number.data or "") if ch.isdigit())
        month = (self.card_exp_month.data or "").strip()
        year = (self.card_exp_year.data or "").strip()
        cvc = (self.card_cvc.data or "").strip()

        if len(number) < 13 or len(number) > 19:
            self.card_number.errors.append("Enter a valid card number.")
            return False
        if not (month.isdigit() and 1 <= int(month) <= 12):
            self.card_exp_month.errors.append("Use a month from 01–12.")
            return False
        if not (year.isdigit() and len(year) == 4):
            self.card_exp_year.errors.append("Use a 4-digit year (e.g. 2030).")
            return False
        if not (cvc.isdigit() and 3 <= len(cvc) <= 4):
            self.card_cvc.errors.append("Enter a valid CVC.")
            return False
        return True


class VisaPayForm(FlaskForm):
    """Card entry for Visa QR / mobile pay page."""

    card_number = StringField("Card number", validators=[DataRequired(), Length(max=19)])
    card_exp_month = StringField("Expiry month (MM)", validators=[DataRequired(), Length(max=2)])
    card_exp_year = StringField("Expiry year (YYYY)", validators=[DataRequired(), Length(max=4)])
    card_cvc = StringField("CVC", validators=[DataRequired(), Length(max=4)])
    submit = SubmitField("Pay with Visa")

    def validate(self, extra_validators=None):
        ok = super().validate(extra_validators=extra_validators)
        if not ok:
            return False
        number = "".join(ch for ch in (self.card_number.data or "") if ch.isdigit())
        month = (self.card_exp_month.data or "").strip()
        year = (self.card_exp_year.data or "").strip()
        cvc = (self.card_cvc.data or "").strip()
        if len(number) < 13 or len(number) > 19:
            self.card_number.errors.append("Enter a valid card number.")
            return False
        if not (month.isdigit() and 1 <= int(month) <= 12):
            self.card_exp_month.errors.append("Use a month from 01–12.")
            return False
        if not (year.isdigit() and len(year) == 4):
            self.card_exp_year.errors.append("Use a 4-digit year (e.g. 2030).")
            return False
        if not (cvc.isdigit() and 3 <= len(cvc) <= 4):
            self.card_cvc.errors.append("Enter a valid CVC.")
            return False
        return True


class ProductForm(FlaskForm):
    name = StringField("Name", validators=[DataRequired(), Length(max=200)])
    brand = StringField("Brand", validators=[DataRequired(), Length(max=100)])
    description = TextAreaField("Description", validators=[Optional()])
    specifications = TextAreaField("Specifications", validators=[Optional()])
    price = DecimalField("Price", validators=[DataRequired(), NumberRange(min=0)])
    discount_price = DecimalField("Discount Price", validators=[Optional(), NumberRange(min=0)])
    stock_quantity = IntegerField("Stock", validators=[DataRequired(), NumberRange(min=0)])
    category_id = SelectField("Category", coerce=int, validators=[DataRequired()])
    rating = DecimalField("Rating", validators=[Optional(), NumberRange(min=0, max=5)], default=4.5)
    is_featured = BooleanField("Featured")
    image = FileField("Image", validators=[FileAllowed(["jpg", "jpeg", "png", "webp", "gif", "svg"])])
    submit = SubmitField("Save Product")


class CategoryForm(FlaskForm):
    name = StringField("Name", validators=[DataRequired(), Length(max=100)])
    description = TextAreaField("Description", validators=[Optional()])
    icon = StringField("Bootstrap Icon Class", validators=[Optional(), Length(max=50)], default="bi-cpu")
    submit = SubmitField("Save Category")


class OrderStatusForm(FlaskForm):
    status = SelectField(
        "Order Status",
        choices=[
            ("Pending", "Pending"),
            ("Paid", "Paid"),
            ("Processing", "Processing"),
            ("Shipped", "Shipped"),
            ("Delivered", "Delivered"),
            ("Cancelled", "Cancelled"),
        ],
        validators=[DataRequired()],
    )
    submit = SubmitField("Update Status")


class CartUpdateForm(FlaskForm):
    item_id = HiddenField(validators=[DataRequired()])
    quantity = IntegerField(validators=[DataRequired(), NumberRange(min=1, max=99)])
    submit = SubmitField("Update")


class AddToCartForm(FlaskForm):
    product_id = HiddenField(validators=[DataRequired()])
    quantity = IntegerField(default=1, validators=[DataRequired(), NumberRange(min=1, max=99)])
    submit = SubmitField("Add to Cart")
