
"""     1
usd_amount = 13
khr_rate = 4035

khr_amount = usd_amount * khr_rate
print(khr_amount)
"""


"""     2
usd_amount = 10

def to_khr(usd_amount):
    khr_rate = 4035
    khr_amount = usd_amount * khr_rate
    return khr_amount

res = to_khr(13)
print(res)
"""


"""     3
def to_khr(usd_amount):
    khr_rate = 4035
    khr_amount = usd_amount * khr_rate
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(13)
print(res)
"""


"""     4
def to_khr(usd_amount):
    khr_rate = 4035
    khr_amount = usd_amount * khr_rate
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(13)
print(res)
"""

"""     5
def to_khr(usd_amount):
    import math
    khr_rate = 4035
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(13)
print(res)
"""


"""     6.1
def to_khr(usd_amount: float):
    import math
    khr_rate = 4035
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(1000)
print(res)
"""


"""     6.2
def to_khr(usd_amount: float):
    import math
    khr_rate = 4035
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(100)
print(res)
"""


"""     6.3
def to_khr(usd_amount: float) -> float:
    import math
    khr_rate = 4035
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(100)
print(res)
"""


"""     6.4
def to_khr(usd_amount: float) -> str:
    import math
    khr_rate = 4035
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(100)
print(res)
"""


"""     6.5
def to_khr(usd_amount: float) -> str:
    import math
    khr_rate = 4035
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(100)
res += 10   # error because khr_amount is string
print(res)
"""


"""     7
def to_khr(usd_amount: float) -> str:
    #""
This Python function is designed to convert a US Dollar (USD) amount into Cambodian Riel (KHR), with a specific focus on how cash transactions typically work in Cambodia.

Here is a breakdown of what the code is doing:

1. The Conversion Rate
It uses a fixed exchange rate of 4,035 KHR per 1 USD.

Note: While 4,000 is the common "street" rate, 4,035 to 4,100 is more common for official bank rates or modern digital exchanges.

2. The "Rounding Up" Logic
This is the most important part of the function:
math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100

The Problem: In Cambodia, the smallest commonly used bill is the 100 Riel note. Small coins or 10-riel notes are not used in daily trade.

The Solution: The code ensures the result is always rounded up to the nearest 100 Riel.

Example: If the math results in 403,500.01 Riel, it will jump to 403,600 Riel so that it can be paid with physical cash.

3. Formatting
The final line f"{khr_amount:,.2f}៛" takes the number and turns it into a readable string with:

Commas as thousands separators.

Two decimal places (though since it rounds to the nearest 100, these will always be .00).

The Riel symbol (៛) at the end.
    #""
    import math
    khr_rate = 4035
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(100)
print(res)
"""


"""     8.1
def to_khr(usd_amount: float, khr_rate = 4000 ) -> str:
    #""
    Notes:
        - Uses a fixed exchange rate of 4,035 KHR per 1 USD.
        - Always rounds up to the nearest 100៛.
    #""
    import math
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount

res = to_khr(100)
print(res)
"""


"""     8.2
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

res = to_khr(100)
res1 = to_khr(100, 4035)
print(res)
print(res1)
"""


"""     9.1     Position arguments
def greeting(name: str, gender: str, age: int):
    print(f"Hello, {name} gender: {gender} age: {age}")

greeting("dara", "male", 29)
"""


"""     9.2.1
def greeting(name: str, gender: str, age: int):
    print(f"Hello, {name} gender: {gender} age: {age}")

greeting("dara", "male", 29)
"""


"""     9.2.2
def greeting(name: str, gender: str, age: int):
    print(f"Hello, {name} gender: {gender} age: {age}")

greeting( "male", "dara", 29 )
"""


"""     9.3.1
def greeting(name: str, gender: str, age: int):
    print(f"Hello, {name} gender: {gender} age: {age}")

greeting(name="dara", gender="male", age=29)
"""


"""     9.3.2
def greeting(name: str, gender: str, age: int):
    print(f"Hello, {name} gender: {gender} age: {age}")

greeting( age=29, name="dara", gender="male")
"""


"""     10
def greeting(*, name: str, gender: str, age: int):
    print(f"Hello, {name} gender: {gender} age: {age}")

greeting(name="dara", gender="male", age=29)
"""



"""     11
def greeting(name: str, /, gender: str, age: int):
    print(f"Hello, {name} gender: {gender} age: {age}")

greeting("dara", gender="male", age=29)
"""


"""     12
def greeting(name: str, gender: str, age: int):
    print(f"Hello, {name} gender: {gender} age: {age}")

greeting( name="dara", gender="male", age=29 )
greeting( "dara", "male", 29 )
greeting( name="dara", "male", 29 ) # error
"""
