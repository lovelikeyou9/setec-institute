print("----- Welcome to Lab 2 -----")
print("----- Enter Product Information -----")

for i in range(3):

    print("\n===== Product", i+1, "=====")

    product_name = input("Enter product name: ")
    qty = int(input("Enter product quantity (1-100): "))
    price = float(input("Enter product price (0-200): "))
    discount_type = input("Enter product discount type (fix, pct): ").lower()

    # Validate quantity
    if qty < 1 or qty > 100:
        print("Quantity cannot be more than 1-100")
        continue

    # Validate price
    if price < 0 or price > 200:
        print("Price cannot be more than 0-200")
        continue

    total = qty * price

    # Discount calculation
    if discount_type == "fix":
        discount_price = float(input("Enter fixed discount amount: "))
        discount_label = "Fixed"

    elif discount_type == "pct":
        percent = float(input("Enter discount percentage: "))
        discount_price = (total * percent) / 100
        discount_label = "Percentage"

    else:
        discount_price = 0
        discount_label = "No Discount"

    grand_total = total - discount_price

    print("\n-----------------------------")
    print("Discount Type:", discount_label)
    print("Discount Price:", discount_price, "$")
    print("Total:", total, "$")
    print("Grand Total:", grand_total, "$")
    print("-----------------------------")
