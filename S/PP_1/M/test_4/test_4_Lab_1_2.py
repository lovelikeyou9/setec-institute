print("------- Welcome to Product Lab -------")
print("1. Product Lab 1")
print("2. Product Lab 2")
print("3. Product Lab 3")
lab_number = int(input("Choose Lab number (1-3): "))
print("--------------------------------------")

limit = 0
product_name = "a"
qty = 0
price = 0
discount_type = "a"
discount_price = 0
discount_percent = 0
total = 0
grand_total = 0

# -- Lab 1 --
if lab_number == 1:
    print(f"----- You are in Lab {lab_number}")
    print("Input Product Information:")

    product_name = input("1. Enter Product Name: ")
    qty = int(input("2. Enter Product Qty (1-10): "))

    if qty < 1 or qty > 10:
        print("Quantity must be between 1 to 10!!!")
    else:
        price = float(input("3. Enter Product Price (1-100): "))

        if price < 1 or price > 100:
            print("Price must be between 1 to 100!!!")
        else:
            total = price * qty
            discount_type = input("4. Enter Product Discount Type ( fix or pct ): ").lower()

            if discount_type == "fix":
                discount_price = float(input("Enter discount price ($): "))
            elif discount_type == "pct":
                discount_percent = float(input("Enter discount (% 0-100): "))
                discount_price = total * (discount_percent / 100)
            else:
                print("No Discount")
                discount_price = 0

            grand_total = total - discount_price

            print("\n-----------------------------")
            print("Discount Type  :   ", discount_type)
            print("Discount Price : $ ", discount_price)
            print("Total          : $ ", total)
            print("Grand Total    : $ ", grand_total)
            print("\n-----------------------------")

# -- Lab 2 --
if lab_number == 2:
    print(f"----- You are in Lab {lab_number}")

    product_name = input("1. Enter product Name: ")

    while True:
        qty = int(input("2. Enter product quantity (1-100): "))
        if qty < 1 or qty > 100:
            print("Quantity cannot be more than 1-100")
        else:
            break

    while True:
        price = float(input("3. Enter product price (0-200): "))
        if 0 <= price <= 200:
            break
        print("Price must be between 0-200")

    total = qty * price

    discount_type = input("4. Enter product discount type ( fix or pct ): ").lower()

    if discount_type == "fix":
        while True:
            discount_price = float(input("Enter discount price ($): "))
            if discount_price < 0:
                print("Input Discount Price/Fix cannot be negative!!!")
            else:
                discount_label = "Fixed"
                break
    elif discount_type == "pct":
        while True:
            discount_percent = float(input("Enter discount (% 0-100): "))
            if 0 <= discount_percent <= 100:
                discount_price = total * (discount_percent / 100)
                discount_label = "Percentage"
                break
            else:
                print("Discount Percentage cannot be more than (% 0-100)")
    else:
        discount_label = "No Discount"
        discount_price = 0

    grand_total = total - discount_price

    print("\n---------------------------")
    print("Discount Type  :   ", discount_label)
    print("Discount Price : $ ", discount_price)
    print("Discount Total : $ ", total)
    print("Grand Total    : $ ", grand_total)
    print("---------------------------")

# -- Lab 3 --
if lab_number == 3:
    print(f"----- You are in Lab {lab_number}")
    limit = int(input("How many times you want to input products: "))
    for i in range(limit):
        print("----- Product ", i + 1)

        product_name = input("1. Enter product Name: ")
        qty = int(input("2. Enter product quantity (1-100): "))
        price = float(input("3. Enter product price (0-200): "))
        discount_type = input("4. Enter product discount type ( fix or pct ): ").lower()

        total = qty * price

        if qty < 1 or qty > 100:
            print("Quantity cannot be more than 1-100")
            continue

        if price < 0 or price > 200:
            print("Price cannot be more than 0-200")
            continue

        if discount_type == "fix":
            discount_price = float(input("Enter discount price ($): "))
            discount_label = "Fixed"
        elif discount_type == "pct":
            discount_percent = float(input("Enter discount (% 0-100): "))
            discount_price = total * (discount_percent / 100)
            discount_label = "Percentage"
        else:
            discount_label = "No Discount"
            discount_price = 0

        grand_total = total - discount_price

        print("\n---------------------------")
        print("Discount Type  :   ", discount_label)
        print("Discount Price : $ ", discount_price)
        print("Discount Total : $ ", total)
        print("Grand Total    : $ ", grand_total)
        print("---------------------------")
