
from tabulate import tabulate

choose_option = 0

limit_input_student = 0

continue_or_not = ""

student_id = 1
student_id_list = []

student_name = ""
student_name_list = []

student_gender = ""
student_gender_list = []

count_male = 0
count_female = 0

student_age = 0
student_age_list = []

student_score = 0
student_score_list = []

all_student_information_list = []


print("1. Enter student amount number")
print("2. Enter student until you want to stop")
while True:
    choose_option = int(input("Choose ( 1 or 2 ): "))
    if choose_option == 1:
        break
    elif choose_option == 2:
        break
    else:
        print(f"Wrong input ( {choose_option} )")

# 1
if choose_option == 1:
    limit_input_student = int(input("Enter Student Amount N: "))
    for i in range(limit_input_student):

        print("---------------------------------------")
        print(f"-- Student {i + 1}")

        student_name = input("Enter Student Name: ")
        student_name_list.append(student_name)

        while True:
            student_gender = input("Enter Student Gender Male/Female: ")
            if student_gender.lower() == "male":
                student_gender_list.append(student_gender)
                count_male += 1
                break
            elif student_gender.lower() == "female":
                student_gender_list.append(student_gender)
                count_female += 1
                break
            else:
                print("!!! INVALID ENTER !!!")

        while True:
            student_age = int(input("Enter Student Age: "))
            if student_age >= 0:
                student_age_list.append(student_age)
                break
            else:
                print("!!! INVALID ENTER !!!")

        while True:
            student_score = int(input("Enter Student Score: "))
            if student_score <= -1:
                print("!!! INVALID ENTER !!!")
            else:
                student_score_list.append(student_score)
                break

        all_student_information_list.append(
            {
                "#": student_id,
                "Name": student_name,
                "Gender": student_gender,
                "Age": student_age,
                "Score": student_score
            }
        )

        # student_id_list.append(student_id)

        student_id += 1

        # print(all_student_information_list)

# 2
if choose_option == 2:

    while True:
        print("---------------------------------------")

        student_name = input("Enter Student Name: ")
        student_name_list.append(student_name)

        while True:
            student_gender = input("Enter Student Gender Male/Female: ")
            if student_gender.lower() == "male":
                student_gender_list.append(student_gender)
                count_male += 1
                break
            elif student_gender.lower() == "female":
                student_gender_list.append(student_gender)
                count_female += 1
                break
            else:
                print("!!! INVALID ENTER !!!")

        while True:
            student_age = int(input("Enter Student Age: "))
            if student_age >= 0:
                student_age_list.append(student_age)
                break
            else:
                print("!!! INVALID ENTER !!!")

        while True:
            student_score = int(input("Enter Student Score: "))
            if student_score <= -1:
                print("!!! INVALID ENTER !!!")
            else:
                student_score_list.append(student_score)
                break

        all_student_information_list.append(
            {
                "#": student_id,
                "Name": student_name,
                "Gender": student_gender,
                "Age": student_age,
                "Score": student_score
            }
        )

        # student_id_list.append(student_id)

        student_id += 1

        # print(all_student_information_list)

        while True:
            continue_or_not = input("Do you want to add more ( y or n ): ")
            if continue_or_not.lower() == "y":
                break
            elif continue_or_not == "n" or continue_or_not == "N":
                break
            else:
                print(f"Wrong input ( {continue_or_not} )")

        if continue_or_not.lower() == "n":
            break

"""
highest_score = max(scores)
index = scores.index(highest_score)
"""
top_score = student_score_list[0]
top_index = 0
for i in range(len(student_score_list)):
    if student_score_list[i] > top_score:
        top_score = student_score_list[i]
        top_index = i


top_student_list = [all_student_information_list[top_index]]

"""
top_student_list = [
    {
        "#": student_id_list[top_index],
        "Name": student_name_list[top_index],
        "Gender": student_gender_list[top_index],
        "Age": student_age_list[top_index],
        "Score": student_score_list[top_index]
    }
]
"""


print("===== Student Information =============")
"""
print("#\tName\t\tGender\tAge\tScore")
for i in range(limit_input_student):
    print(f"{ i + 1 }\t{student_name_list[i]}\t\t{student_gender_list[i]}\t{student_age_list[i]}\t{student_score_list[i]}")
"""
print(tabulate(all_student_information_list, headers="keys", tablefmt="double_outline"))



print("=======================================")

print("Total Student : ", len(student_name_list))
print(f"Total Male    : {count_male}  -  ", f"Total Female: {count_female}")


print("======--⭐⭐⭐ Top 1 Score ⭐⭐⭐--======")
#   print("Top 1 Score   : ", student_name_list[top_index], "-", student_score_list[top_index])
print(tabulate(top_student_list, headers="keys", tablefmt="double_outline"))
