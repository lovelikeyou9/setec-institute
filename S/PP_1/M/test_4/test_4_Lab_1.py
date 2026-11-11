print("----- Welcome to Lab 1 -----")
print("----- Enter Product Information -----")

product_name = input("1. Enter Product Name: ")
qty = int(input("2. Enter Product Qty (1-10): "))

if qty < 1 or qty > 10:
    print("Quantity must be between 1 and 10!")
else:
    price = float(input("3. Enter Product Price (1-100): "))

    if price < 1 or price > 100:
        print("Price must be between 1 and 100!")
    else:
        total = qty * price
        discount_type = input("4. Enter Product Discount Type (fix, pct): ").lower()

        if discount_type == "fix":
            discount_price = float(input("Enter discount price ($): "))
        elif discount_type == "pct":
            discount_percent = float(input("Enter discount (% 0-100): "))
            discount_price = total * (discount_percent / 100)
        else:
            print("Invalid discount type!")
            discount_price = 0

        grand_total = total - discount_price

        print("\n------------------")
        print("Discount Price:", discount_price)
        print("Discount Type:", discount_type)
        print("Total:", total)
        print("Grand Total:", grand_total)
