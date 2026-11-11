
from tabulate import tabulate
import re

"""
text = "qwerty"
print (len(text))
print (text)
"""

table_data = []
students = []
student_id = 0


while True:
    student_id = student_id + 1
    print(f"Student { student_id }")

    # print(student_id)

    # Name Input
    while True:
        name = input("Enter Name: ")
        name = " ".join(name.split())

        # print(len(name))

        if len(name) <= 50 and re.fullmatch(r"[A-Za-z ]+", name):
            break
        print("😑 Cannot be $%^& - 123 - àéô - empty - space - limit(50) 😑")

    # print(name)

    # if len(name) <= 50 and re.fullmatch(r"[A-Za-z ]+", name) and name.strip() != "":

    # if len(name) <= 50 and name.isalpha() and name.isascii():

    # name = name.strip()

    """
    while True:
        name = input("Enter Name: ")
        if name == "":
            print("😑 Name cannot be blank 😑")
        else:
            break
    """

    # Gender Input
    while True:
        gender = input("Enter Gender: ")
        if gender.lower() == "m":
            break
        elif gender == "f" or gender == "F":
            break
        else:
            print("😑 Gender must be M/F 😑")

    # Group Input
    group = input("Enter Group: ")

    # Subject Amount Input

    while True:
        try:
            subject_amount = int(input("Enter Subject Amount: "))
            if subject_amount >= 0:
                break
            else:
                print("😑 Subject Amount cannot be negative 😑")
        except ValueError:
            print("😑 Please enter a valid number 😑")

    """
    while True:
        try:
            subject_amount = int(input("Enter Subject Amount: "))
        except ValueError:
            print("Please enter a valid number")
            continue

        if subject_amount < 0:
            print("Subject Amount cannot be negative")
            continue

        break
    """

    subjects = {}
    total_score = 0
    for i in range(subject_amount):
        print("-------------------------------")
        print(f"Subject { i + 1 }")

        while True:
            subject_name = input("Enter Subject Name: ")
            subject_name = " ".join(subject_name.split())

            if subject_name != "":
                break
            print("😑 Cannot be empty 😑")

        while True:
            try:
                subject_score = int(input("Enter Subject Score: "))
                if subject_score >= 0:
                    break
                print("😑 Subject Score cannot be negative 😑")
            except ValueError:
                print("Please enter a valid number")

        subjects[subject_name] = subject_score
        total_score += subject_score

    students.append(
        {
            "student_id": student_id,
            "name": name,
            "gender": gender,
            "group": group,
            "subject_amount": subject_amount,
            "subjects": subjects,
            "total_score": total_score
        }
    )
    # print(students)


    while True:
        input_more = input("Do you want to add more? ( Y/N ): ")
        if input_more == "y" or input_more == "Y":
            break
        elif input_more.lower() == "n":
            break
        else:
            print("😑 Please enter Y/N 😑")

    if input_more.lower() == "n":
        break

    print("==========================")


for i in students:
    row = {
        "student_id": i["student_id"],
        "name": i["name"],
        "gender": i["gender"],
        "group": i["group"],
        "subject_amount": i["subject_amount"],
        "total_score": i["total_score"],

    }
    table_data.append(row)

print(tabulate(table_data, headers="keys", tablefmt="double_outline"))
# print(students)


top3 = sorted(students, key=lambda s: s["total_score"], reverse=True)[:3]

for s in top3:
    print(s["name"], s["total_score"])

