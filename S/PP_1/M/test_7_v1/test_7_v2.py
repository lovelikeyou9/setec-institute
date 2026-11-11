from tabulate import tabulate

product_list = []

while True:
    name = input("Enter product name: ")
    qty = input("Enter product qty: ")
    price = input("Enter product price: ")
    product = {
        "name": name,
        "qty": qty,
        "price": price,
    }
    product_list.append(product)
    more = input("Do you want to add more products? (y/n): ").lower()
    while more not in ["n", "y"]:
        more = input("Do you want to add more products? (y/n): ").lower()

    if more == "n":
        break

    discount_pct = input("Enter discount percentage: ")
    print('-' * 50)

print(tabulate(product_list, headers="keys"))
print(tabulate(product_list, headers="keys", tablefmt="double_outline"))