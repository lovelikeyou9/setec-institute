
""" 1
# string name_list[3]={};
name_list = ["dara", "thida"]

print(name_list[0])
print(name_list[1])
"""


""" 2
name_list = ["dara", "thida"]

print(name_list[0])
print(name_list[1])
print(name_list[2]) # error
"""


""" 3
name_list = ["dara", "thida","vireak"]

index = 0
for name in name_list:
    print(name)
"""


""" 4
name_list = ["dara", "thida","vireak"]

index = 0
for name in name_list:
    print(f"{ index + 1 }. { name }")
"""


""" 5
name_list = ["dara", "thida","vireak"]

index = 0
for name in name_list:
    print(f"{ index + 1 }. { name }")
    index += 1
    print(index)
"""


""" 6
name_list = ["dara", "thida","vireak"]

for i in range(len(name_list)):
    print(name_list[i])
"""


""" 7
name_list = ["dara", "thida","vireak"]

for i in range(len(name_list)):
    print(f"{ i + 1 }. {name_list[i]}")
"""


""" 8
name_list = []
name_list.append("thida")
name_list.append("bora")

for i in range(len(name_list)):
    print(f"{ i + 1 }. {name_list[i]}")
"""


""" 9       1 to 99     because array start from 0 value
name_list = []
name_list.append("thida")
for i in range(1, 100):
    name_list.append("bora")

for i in range(len(name_list)):
    print(f"{ i + 1 }. {name_list[i]}")
"""


""" 10      1 to 100
name_list = []
name_list.append("thida")
for i in range(0, 100):
    name_list.append("bora")

for i in range(len(name_list)):
    print(f"{ i + 1 }. {name_list[i]}")
"""


""" 11
name_list = []
n = input("Enter how many want to input names: ")

for i in range(int(n)):
    name = input("Enter the name: ")
    name_list.append(name)
print(name_list)
"""

