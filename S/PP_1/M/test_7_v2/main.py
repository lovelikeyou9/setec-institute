
from tabulate import tabulate

product_list = []

while True:
    name = input("Enter product name: ")
    qty = input("Enter product qty: ")
    price = input("Enter product price: ")
    product = {
        "name": name,
        "qty": qty,
        "price": price
    }
    product_list.append(product)
    more = input("Do you want to add more products? (y/n): ").lower()
    while more not in ["n", "y"]:
        more = input("Do you want to add more products? (y/n): ").lower()

    if more == "n":
        break
    print('-' * 50)

discount_pct = input("Enter discount percentage: ")

total = 0
for product in product_list:
    total += float(product["price"]) * float(product["qty"])

discount_price = total * float(discount_pct) / 100

grand_total = total + discount_price

print(tabulate(product_list, headers="keys", tablefmt="double_outline"))

print("=======================")
print(f"Total: {total}")
print(f"Discount: {discount_price}")
print(f"Grand Total: {grand_total}")
