"""Dev tool: manual fixes applied after _convert.py (re-runnable)."""
import os
import re

ROOT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "templates")
MANUAL_RE = re.compile(r"\{# MANUAL-PHP.*?#\}", re.S)


def rd(rel):
    return open(os.path.join(ROOT, rel), encoding="utf-8").read()


def wr(rel, text):
    open(os.path.join(ROOT, rel), "w", encoding="utf-8", newline="\n").write(text)


def manual(rel, replacement, count=1):
    t = rd(rel)
    t, n = MANUAL_RE.subn(lambda m: replacement, t, count=count)
    assert n >= 1, rel
    wr(rel, t)


def replace(rel, old, new, count=0):
    t = rd(rel)
    assert old in t, (rel, old)
    wr(rel, t.replace(old, new) if not count else t.replace(old, new, count))


# layouts: Blade @yield('modals') + @stack('modals') -> one block, drop empty heading section
for name in ("store", "account", "admin", "seller"):
    rel = f"layouts/{name}.html"
    t = rd(rel)
    t = t.replace("{% block modals %}{% endblock %}\n{% block modals %}{% endblock %}",
                  "{% block modals %}{% endblock %}")
    t = re.sub(r"\s*\{% if false %\}\s*<h1[^>]*>\{% block heading %\}\{% endblock %\}</h1>\s*\{% endif %\}", "", t)
    wr(rel, t)

# sidebars
for kind in ("account", "admin", "seller"):
    manual(f"partials/{kind}-sidebar.html",
           "{% set nav = nav_for('" + kind + "', activeNav|default('')) %}")

# components
wr("components/status_pill.html",
   "{% set status = status if status is defined else '' %}\n"
   "{% set type = type if type is defined else 'order' %}\n"
   "<span class=\"status-pill {{ status_tone(status, type) }}\">{{ str_replace('_', ' ', status) }}</span>\n")

wr("components/stars.html",
   "{% set rating = rating if rating is defined else 0 %}\n"
   "{% set full = [0, [5, (rating|float|round|int)]|min]|max %}\n"
   "<span class=\"stars\" aria-label=\"Rating {{ number_format((rating|float), 1) }} out of 5\">\n"
   "  {% for i in range(1, 6) %}\n"
   "    <span class=\"{{ 'on' if (i <= full) else '' }}\">&#9733;</span>\n"
   "  {% endfor %}\n"
   "</span>\n")

replace("components/product_card.html", "(wishlisted == none)", "(wishlisted is none)")

manual("partials/product-variant-fields.html",
       "{% set prefix = prefix if prefix is defined else 'add' %}"
       "{% set specsVal = specs if specs is defined else '' %}"
       "{% set groupsJson = optionGroups if optionGroups is defined else '[]' %}"
       "{% set groupsJson = groupsJson|json_encode %}")

# pages
manual("account/order-detail.html",
       "{% set s = order.status %}{% set cancelled = s in ['CANCELLED', 'REFUNDED'] %}"
       "{% set disputed = (s == 'DISPUTED') %}{% set idx = order_step_index(s) %}"
       "{% set commission = order.commission %}")

manual("admin/sellers.html",
       "{% set statusClass = {'APPROVED': 'green', 'SUSPENDED': 'red', 'PENDING': 'yellow'}.get(s.status, 'gray') %}")

manual("store/product-detail.html",
       "{% set gallery = product.galleryList() %}"
       "{% if not gallery %}{% set gallery = [default_product_image()] %}{% endif %}"
       "{% set optionGroups = product.selectableOptionGroups() %}"
       "{% set verified = ((product.seller.level or '') in ['VERIFIED', 'TRUSTED', 'TOP']) %}"
       "{% set specRows = spec_rows(product.specifications) %}")

manual("seller/brands.html", "{% set list = brands %}")
manual("seller/categories.html", "{% set list = categories %}")

t = rd("store/products.html")
t = t.replace("collect(g['items']).pluck('id')", "(g['items']|map(attribute='id')|list)")
t = t.replace("categories.firstWhere('id', item['id'])",
              "(categories|selectattr('id', 'equalto', item['id'])|first)")
wr("store/products.html", t)

# checkout: add Stripe option, encode image urls
replace("store/checkout.html",
        """          <label class="radio-card">
            <input type="radio" name="paymentMethod" value="QR_PAYMENT"/>""",
        """          <label class="radio-card">
            <input type="radio" name="paymentMethod" value="STRIPE"/>
            <div><strong>Stripe</strong> <span style="font-size:.85rem;color:var(--text-muted)">(secure Stripe Checkout, test mode)</span></div>
          </label>
          <label class="radio-card">
            <input type="radio" name="paymentMethod" value="QR_PAYMENT"/>""")
replace("store/checkout.html", 'src="{{ item.product.image_url }}"', 'src="{{ media_url(item.product.image_url) }}"')

replace("vendor/pagination/mmt.html", "{% if is_string(element) %}", "{% if element is string %}")
replace("vendor/pagination/mmt.html", "{% if is_array(element) %}", "{% if element is not string %}")
replace("vendor/pagination/mmt.html", "{% for page, url in (element)|kv %}", "{% for page_no, page_url in element.items() %}")
replace("vendor/pagination/mmt.html", "{% if page == paginator.currentPage() %}", "{% if page_no == paginator.currentPage() %}")
replace("vendor/pagination/mmt.html", '<span class="current" aria-current="page">{{ page }}</span>', '<span class="current" aria-current="page">{{ page_no }}</span>')
replace("vendor/pagination/mmt.html", '<a href="{{ url }}">{{ page }}</a>', '<a href="{{ page_url }}">{{ page_no }}</a>')

replace("admin/applications.html", "{% if app.business_info if (app.business_info is defined and app.business_info is not none) else none %}", "{% if app.business_info %}")
replace("store/products.html", "request.boolean('inStock')", "(req_input('inStock') in ['1', 'true', 'on'])")

print("postfix ok")
