

import requests
from tabulate import tabulate
import accounting_package as acc

URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
if response.status_code == 200:
    data = response.json()
    headers = ["ID", "Title", "Category", "Price($)", "Price(៛)"]
    rows = []

    for product in data:    # convert to dictionary
        rows.append(
           [
               product["id"],
               f"{product["title"][:20]}{"..." if len(product["title"]) >= 20 else ""}",
               product["category"],
               f"{product["price"]}",
               f"{acc.to_khr(product["price"])}"
           ]
       )    # convert dictionary to array
        # print(rows)
    print(tabulate(headers=headers, tabular_data=rows, tablefmt="psql"))

else:
    print(f"Error: {response.status_code}")

# print(requests.get(URL))
# print(response.text)  # JSON Array (API response)
# print(response.text)  # List of Dictionaries
# print(data)   # List of Dictionaries
# print(rows)   # List of Lists (2D list)
"""
URL (string)
   ↓
requests.get()
   ↓
response (object)
   ↓
response.text  → JSON string
response.json() → Python list/dict
"""

"""
while True:
    name_or_id = input("Input [ ID or Name ]: ").lower()

    # print(f"Student { id }")

    select = None


    if name_or_id.isdigit():
        for p in rows:
            if p[0] == int(name_or_id):
                # print(p[0])
                # print(rows)
                select = p
                # print(select)
                break
    else:
        for p in rows:
            if name_or_id in p[1].lower():
                # print(p[1])
                select = p
                break

    if select:
        print(tabulate([select], headers=headers, tablefmt="psql"))
        break
    else:
        print("Product Not Found")
    
    # try:
    #     int(name_or_id)
    #     print( name_or_id + " is number" )
    # except:
    #     str(name_or_id )
    #     print( name_or_id + " is string" )
    # 
"""


while True:
    name_or_id = input("Input [ ID or Name ]: ").lower()

    # print(f"Student { id }")

    select = None

    # Check if a string contains at least one number
    # name_or_id = "12345"
    # print(name_or_id.isdigit())   # True
    # name_or_id = "123a5"
    # print(name_or_id.isdigit())   # False
    if name_or_id.isdigit():
        for p in response.json():
            if p["id"] == int(name_or_id):
                # print(p[0])
                # print(rows)
                # print(p)
                select = p
                # print(select)
                break
    else:
        for p in response.json():
            if name_or_id in p["title"].lower():
                select = p
                break

    if select:
        row = [
            select["id"],
            select["title"],
            select["price"],
            f"{select["description"][:20]}{"..." if len(select["description"]) >= 20 else ""}",
            select["category"],
            select["image"],
            select["rating"]["rate"],
            select["rating"]["count"]
        ]

        headers1 = ["id", "title", "price", "description", "category", "image", "rate", "count"]
        print(tabulate([row], headers=headers1, tablefmt="psql"))
        break
    else:
        print("Product Not Found")
""""""





