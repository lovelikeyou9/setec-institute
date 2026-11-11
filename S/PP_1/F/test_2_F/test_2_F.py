
"""
def to_khr(usd_amount: float, khr_rate:float = 4000 ) -> str:
    #""
    Notes:
        - Uses a fixed exchange rate of 4,035 KHR per 1 USD.
        - Always rounds up to the nearest 100៛.
    #""
    import math
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount
"""


"""     1
# Module
# from accounting_helpers import *
# from accounting_helpers import to_khr
# from accounting.accounting_helpers import to_khr
# from F.test_2_F.accounting_package.accounting_helpers import to_khr

# Package
# import accounting_package as acc

res = to_khr(100)
res1 = to_khr(100, 4035)
print(res)
print(res1)
"""


"""     2
# Package
from accounting_package.accounting_helpers import to_khr

res = to_khr(100)
res1 = to_khr(100, 4035)
print(res)
print(res1)
"""


"""     3
import qrcode

img = qrcode.make('hello world')
img = qrcode.make('https://i.pinimg.com/originals/28/27/77/2827771068d374a49d2c0fa49d30967d.gif')
img.save("some_file.png")
"""


"""     4
import requests

# URL = 'https://fakestoreapi.com/product'
URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
print(response.status_code)
"""

"""
# 1xx — Informational (rare in real apps)
👉 The request is received, but not finished yet.

Examples:

100 Continue → server says “keep going, I got your request”
101 Switching Protocols → switching to another protocol (like WebSocket)

📌 Meaning:

“We are still processing…”


# 2xx — Success (everything OK) ✅
👉 The request worked successfully.

Examples:

200 OK → request successful (most common)
201 Created → new data created (like new user)
204 No Content → success but no response body

📌 Meaning:

“Everything worked fine”


# 3xx — Redirection 🔄

👉 You are being sent somewhere else.

Examples:

301 Moved Permanently → old URL permanently changed
302 Found → temporary redirect
304 Not Modified → use cached version (no change)

📌 Meaning:

“Go to another location”


# 4xx — Client Error ❌ (your fault / request problem)
Something wrong with the request from user side.

Examples:

400 Bad Request → invalid request format
401 Unauthorized → login required
403 Forbidden → you are not allowed
404 Not Found → page doesn’t exist

📌 Meaning:

“You did something wrong (request issue)”


# 5xx — Server Error 💥 (server fault)
👉 Server failed to process request.

Examples:

500 Internal Server Error → general server crash/error
502 Bad Gateway → bad response from another server
503 Service Unavailable → server down or overloaded

📌 Meaning:

“Server has a problem”



| Code | Meaning                |
| ---- | ---------------------- |
| 1xx  | Thinking…              |
| 2xx  | Success ✔              |
| 3xx  | Move somewhere else 🔄 |
| 4xx  | Your mistake ❌        |
| 5xx  | Server mistake 💥      |

"""


"""     5
import requests

URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
print(response.json())
"""


"""     6
import requests

# URL = 'https://fakestoreapi.com/product'
URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
if response.status_code == 200:
    print(response.json())
else:
    print(f"Error: {response.status_code}")
"""


"""     7
import requests

URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
if response.status_code == 200:
    data = response.json()
    for product in data:    # convert to dictionary
        print(product)
else:
    print(f"Error: {response.status_code}")
"""



"""     8.1
import requests
from tabulate import tabulate

URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
if response.status_code == 200:
    data = response.json()
    headers = ["ID", "Title", "Category", "Price"]
    print(tabulate(data, headers=headers))  # error because data is dictionary then we have to convert to array
#    for product in data:    # convert to dictionary
#        print(product["id"])
#        print(product["title"])
#        print(product["category"])
#        print(product["price"])
#else:
#    print(f"Error: {response.status_code}")
"""


"""     8.2
import requests
from tabulate import tabulate

URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
if response.status_code == 200:
    data = response.json()
    headers = ["ID", "Title", "Category", "Price"]
    rows = []
    print(tabulate(rows, headers=headers))
#    for product in data:    # convert to dictionary
#        print(product["id"])
#        print(product["title"])
#        print(product["category"])
#        print(product["price"])
#else:
#    print(f"Error: {response.status_code}")
"""


"""     8.3
import requests
from tabulate import tabulate

URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
if response.status_code == 200:
    data = response.json()
    headers = ["ID", "Title", "Category", "Price"]
    rows = []
    for product in data:    # convert to dictionary
        rows.append(
           [
               product["id"],
               product["title"],
               product["category"],
               product["price"]
           ]
       )    # convert dictionary to array
    print(tabulate(rows, headers=headers))

else:
    print(f"Error: {response.status_code}")
"""


"""     8.4
import requests
from tabulate import tabulate

URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
if response.status_code == 200:
    data = response.json()
    headers = ["ID", "Title", "Category", "Price"]
    rows = []
    for product in data:    # convert to dictionary
        rows.append(
           [
               product["id"],
               product["title"][:15],
               product["category"],
               product["price"]
           ]
       )    # convert dictionary to array
    print(tabulate(rows, headers=headers))

else:
    print(f"Error: {response.status_code}")
"""


"""     8.5
import requests
from tabulate import tabulate

URL = 'https://fakestoreapi.com/products'
response = requests.get(URL)
if response.status_code == 200:
    data = response.json()
    headers = ["ID", "Title", "Category", "Price"]
    rows = []
    for product in data:    # convert to dictionary
        rows.append(
           [
               product["id"],
               f"{product["title"][:20]}{"..." if len(product["title"]) >= 20 else ""}",
               product["category"],
               product["price"]
           ]
       )    # convert dictionary to array
    print(tabulate(rows, headers=headers))

else:
    print(f"Error: {response.status_code}")
"""


"""     8.6
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
    print(tabulate(rows, headers=headers))

else:
    print(f"Error: {response.status_code}")
"""



"""     8.6
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
    print(tabulate(rows, headers=headers, tablefmt="psql"))

else:
    print(f"Error: {response.status_code}")
"""


"""     8.7
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
    print(tabulate(headers=headers, tabular_data=rows, tablefmt="psql"))
else:
    print(f"Error: {response.status_code}")
"""
