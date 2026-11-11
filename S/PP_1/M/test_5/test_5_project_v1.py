
print("--------------------------------------")
print("------- Welcome to Product -------")

limit = 0
product_name = ""
product_name_list = []
qty = 0
price = 0
total_multiply_of_price_and_qty = []
total_multiply_of_price_and_qty_III_add = sum(total_multiply_of_price_and_qty)
discount_type = ""
discount_price = 0
discount_percent = 0
total = 0
grand_total = 0
continue_input_or_not = ""


while True:

    product_name = input("1. Enter product Name: ")
    product_name_list.append(product_name)

    qty = int(input("2. Enter product quantity (1-100): "))
    while qty < 1 or qty > 100:
        qty = int(input("Invalid! Enter product quantity (1-100): "))

    while True:
        price = float(input("3. Enter product price (0-200): "))
        if 0 <= price <= 200:
            break
        print("Price must be between 0-200")

    total = qty * price
    total_multiply_of_price_and_qty.append(total)


    """
    continue_input_or_not = input("Do you want to add more product(y/n): ")
    if continue_input_or_not == "y" or continue_input_or_not == "Y":
        continue
    elif continue_input_or_not == "n" or continue_input_or_not == "N":
        break
    else:
        print("Invalid Input")
    """


    while True:
        continue_input_or_not = input("Do you want to add more product(y/n): ")
        if continue_input_or_not == "y" or continue_input_or_not == "Y":
            break
        elif continue_input_or_not == "n" or continue_input_or_not == "N":
            break
        else:
            print("Invalid Input")

    if continue_input_or_not.lower() == "n":
        break




print("--------------------------------------------")
discount_type = input("4. Enter product discount type ( fix or pct ): ").lower()

if discount_type == "fix":
    while True:
        total_multiply_of_price_and_qty_III_add = sum(total_multiply_of_price_and_qty)
        print("Total : $ ", total_multiply_of_price_and_qty_III_add)
        discount_price = float(input("Enter discount price ($): "))
        if discount_price < 0:
            print("Discount Price/Fix cannot be negative!!!")
        elif discount_price > total_multiply_of_price_and_qty_III_add:
            print("Discount Price cannot be greater than total price!!!")
        else:
            discount_label = "Fixed"
            break

elif discount_type == "pct":
    while True:
        discount_percent = float(input("Enter discount (% 0-100): "))
        if 0 <= discount_percent <= 100:
            total_multiply_of_price_and_qty_III_add = sum(total_multiply_of_price_and_qty)
            discount_price = total_multiply_of_price_and_qty_III_add * (discount_percent / 100)
            discount_label = "Percentage"
            break
        else:
            print("Discount Percentage cannot be more than (% 0-100)")
else:
    discount_label = "No Discount"
    discount_price = 0

total_multiply_of_price_and_qty_III_add = sum(total_multiply_of_price_and_qty)
grand_total = total_multiply_of_price_and_qty_III_add - discount_price

print("---------------------------")
print("Discount Type      :   ", discount_label)
print("Discount Price     : $ ", discount_price)
print("Total Price        : $ ", total_multiply_of_price_and_qty_III_add)
print("Grand Total        : $ ", grand_total)
print("Product #   Name  ",)
for i in range(len(product_name_list)):
    print(f"        { i + 1 }   {product_name_list[i]}")

print("Product Total      :  ", len(product_name_list))
print("---------------------------")
