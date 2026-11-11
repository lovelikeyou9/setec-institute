
from tabulate import tabulate
import re
import heapq
import pandas as pd

"""
text = "qwerty"
print (len(text))
print (text)
"""

# table_data = []
students = []
student_id = 0


while True:
    student_id = student_id + 1
    print(f"    Student { student_id }")

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
            "total_score": total_score,
            "subjects": subjects

        }
    )
    # print(students[0]["subjects"].values())
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


df = pd.DataFrame([
    {
        "student_id": i["student_id"],
        "name": i["name"],
        "gender": i["gender"],
        "group": i["group"],
        "subject_amount": i["subject_amount"],
        "total_score": i["total_score"],
        **i["subjects"]
    }
    for i in students
])

print(tabulate(df, headers="keys", tablefmt="double_outline", showindex=False))
# print(students)
# print(df)
"""
for i in students:
    row = {
        "student_id": i["student_id"],
        "name": i["name"],
        "gender": i["gender"],
        "group": i["group"],
        "subject_amount": i["subject_amount"],
        "total_score": i["total_score"],
        **i["subjects"]
    }
    table_data.append(row)

print(tabulate(table_data, headers="keys", tablefmt="double_outline"))
"""



show_rank = int(input("Which rank do you want to see?: "))
top_rank = heapq.nlargest(show_rank, students, key=lambda s: s["total_score"])

df_top_rank = pd.DataFrame([
    {
        "student_id": i["student_id"],
        "name": i["name"],
        "gender": i["gender"],
        "group": i["group"],
        "subject_amount": i["subject_amount"],
        "total_score": i["total_score"],
        **i["subjects"]
    }
    for i in top_rank
])

print(f"⭐⭐⭐== Top Scores { show_rank }🏆 ==⭐⭐⭐")

print(tabulate(df_top_rank, headers="keys", tablefmt="double_outline", showindex=False))

print("------------------------")
"""
i = 0
for s in top_rank:
    print(f"{ i + 1 }." ,s["name"], s["total_score"])
    i = i + 1
"""

"""
top_3 = sorted(students, key=lambda s: s["total_score"], reverse=True)[:3]

table = []
for rank, s in enumerate(top_3, 1):
    table.append([rank, s["name"], s["gender"], s["group"], s["total_score"]])
"""





students_below_50 = []

for i in students:
    for score in i["subjects"].values():
        if int(score) < 50:
            students_below_50.append(i)
            break


df_students_below_50 = pd.DataFrame([
    {
        "student_id": i["student_id"],
        "name": i["name"],
        "gender": i["gender"],
        "group": i["group"],
        "subject_amount": i["subject_amount"],
        "total_score": i["total_score"],
        **i["subjects"]
    }
    for i in students_below_50
])

# 4️⃣ Identify subject columns dynamically
subject_cols = [col for col in df_students_below_50.columns if col not in ["student_id", "name", "gender", "group", "subject_amount", "total_score"]]
# 5️⃣ Replace passing subjects with "-" for display
for col in subject_cols:
    df_students_below_50[col] = df_students_below_50[col].apply(lambda x: x if x < 50 else "-")

"""
subject_cols = list(students[0]["subjects"].keys())
for col in subject_cols:
    df_students_below_50[col] = df_students_below_50[col].apply(lambda x: x if x < 50 else "-")
"""

print("😑==== Failed Subjects ====😑")
print(tabulate(df_students_below_50, headers="keys", tablefmt="double_outline", showindex=False))
