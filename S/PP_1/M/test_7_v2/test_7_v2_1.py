from tabulate import tabulate

print("--------------------------------------")
print("------- Welcome to Product -------")

product_id = 1
name = ""
price = 0
qty = 0
product_list = []

continue_input_or_not = ""

discount_type = ""

discount_percent = 0

discount_price = 0
total = 0
sum_total_all_product = 0
grand_total = 0

while True:

    name = input("Enter product name: ")

    while True:
        price = float(input("Enter product price 0-200: "))
        if 0 <= price <= 200:
            break
        print("Price must be between 0-200")

    qty = int(input("Enter product quantity 1-100: "))
    while qty < 1 or qty > 100:
        qty = int(input("Invalid! Enter product quantity 1-100: "))

    total = qty * price

    product_list.append(
        {
            "ID": product_id,
            "Name": name,
            "QTY": qty,
            "Price": price,
            "Total": total
        }
    )

    product_id += 1

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
        for product in product_list:
            sum_total_all_product += product["Total"]
        print("--->>> Total Price      : $ ", sum_total_all_product)

        while True:
            discount_price = float(input("Enter discount price ($): "))
            if discount_price < 0:
                print("Discount Price/Fix cannot be negative!!!")
            elif discount_price > sum_total_all_product:
                print("Discount Price cannot be greater than total price!!!")
            else:
                discount_label = "Fixed"
                break

        if discount_label == "Fixed":
            break


elif discount_type == "pct":
    while True:
        for product in product_list:
            sum_total_all_product += product["Total"]

        while True:
            discount_percent = float(input("Enter discount (% 0-100): "))
            if 0 <= discount_percent <= 100:
                discount_price = sum_total_all_product * (discount_percent / 100)
                discount_label = "Percentage"
                break
            else:
                print("Discount Percentage cannot be more than (% 0-100)")

        if discount_label == "Percentage":
            break

else:
    discount_label = "No Discount"
    discount_price = 0


grand_total = sum_total_all_product - discount_price

print("---------------------------")
print("Discount Type      :   ", discount_label)
print("Discount Price     : $ ", discount_price)
print("Total Price        : $ ", sum_total_all_product)
print("Grand Total        : $ ", grand_total)

print(tabulate(product_list, headers="keys", tablefmt="double_outline"))

print("                 Product Total   :  ", len(product_list))
print("---------------------------")
