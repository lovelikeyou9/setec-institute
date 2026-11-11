"""     1
value_a = 10
value_b = float(value_a)
print(value_b)
"""


"""     2
value_a = '10'
value_b = float(value_a)
print(value_b)
"""

"""     3
value_a = '10'
value_b = float(value_a)
print(type(value_b))
print(value_b)
"""

"""     4
value_a = '10'
print(type(value_a))
value_b = float(value_a)
print(type(value_b))
"""


"""     5
value_a = '10a'
print(type(value_a))
value_b = float(value_a)
print(type(value_b))    #   error
"""


"""     6
value_a = 10
print(type(value_a))
value_b = float(value_a)
print(type(value_b))    #   work
"""


"""     7
a = input("Enter A: ")
print(a)
print(type(a))
"""


"""     8
a = input("Enter A: ")
b = 10
print(a + b)
print(type(a))  # error because all input is string or varchar
"""


"""     9
a = input("Enter A: ")
b = '10'
print(a + b)
print(type(a))  # error because all input is string or varchar
"""


"""     10
a = input("Enter A: ")
b = 10
print(int(a)+b)     # error if we input string or varchar. can input only number
"""


"""     11
a = int(input("Enter A: "))
b = 10
print(a+b)
"""


"""     12
if True:
    print("hello")
"""


"""     13
if True:
    print("hello")

if False:
    print("world")
"""


"""     14
if True:
    print("hello")
else:
    print("world")
"""


"""     15
if True:
    print("hello")
elif False:
    print("world")
else:
    print("a")
"""


"""     16
for i in range(10):
    print(f"{i} . Hello world!")
"""


"""     17
for index in range(10):
    print(f"{index} . Hello world!")
"""


"""     18
print(range(10))
"""


"""     19
from time import sleep
for index in range(10):
    print(f"{index+1} . Hello world!")
    sleep(1)
"""


"""     20
from time import sleep
from random import randint
for index in range(10):
    secs = randint(1, 10)
    print(f"{index+1} . Hello world!")
    sleep(secs)
"""


"""     21
std = ['dara', 'bora', 'koko', 'kaka']
for index in range(10):
    print(f"Hello world {index}")
"""

"""     22
std = ['dara', 'bora', 'koko', 'kaka']
for index in range(10):
    print(std[index])   # error because we have only 0 to 3 value in array. Total 4
"""


"""     23
std = ['dara', 'bora', 'koko', 'kaka']
for index in range(4):
    print(std[index])
"""


"""     24
std = ['dara', 'bora', 'koko', 'kaka']
for item in std:
    print(item)
"""

"""
std = 'daraborakokokaka'
for item in std:
    print(item)
"""
