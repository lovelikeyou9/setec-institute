
"""     1
student_list = [
    {
        "name": "mono",
        "gender": "male",
        "group": 'sv2.6',
        "subject": 'PP',
        "score": 30
    }
]

print(student_list)
"""


"""     2
student_list = [
    {
        "name": "mono",
        "gender": "male",
        "group": 'sv2.6',
        "subject": 'PP',
        "score": 30
    },
    {
        "name": "mono",
        "gender": "male",
        "group": 'sv2.6',
        "subject": 'PP',
        "score": 30
    }
]

print(student_list)
"""


"""     3
student_list = {
    "name": "mono",
    "gender": "male",
    "group": 'sv2.6',
    "subject": 'PP',
    "score": 30
}

print(student_list)
"""


"""     4
student_list = {
    "name": "mono",
    "gender": "male",
    "group": 'sv2.6',
    "subject": 'PP',
    "score": 30
}

print(student_list['name'])
"""


"""     5
student_list = {
    "name": "mono",
    "gender": "male",
    "group": 'sv2.6',
    "subject": 'PP',
    "score": 30
}

print(f"Name: {student_list['name']}")
"""


"""     6
student_list = {
    "name": "mono",
    "gender": "male",
    "group": 'sv2.6',
    "subject": 'PP',
    "score": 30
}

print(f"Name: {student_list['name']}")
print(f"Gender: {student_list['gender']}")
print(f"Group: {student_list['group']}")
print(f"Subject: {student_list['subject']}")
print(f"Score: {student_list['score']}")
"""


"""     7
student_list = {
    "name": "mono",
    "gender": "male",
    "group": 'sv2.6',
    "subject": 'PP',
    "score": 30
}

print(f"Name: {student_list['name']}")
print(f"Gender: {student_list['gender']}")
print(f"Group: {student_list['group']}")
print(f"Subject: {student_list['subject']}")
print(f"Score: {student_list['score1']}")  # error
"""


"""     8
student_list = {
    "name": "mono",
    "gender": "male",
    "group": 'sv2.6',
    "subject": 'PP',
    "score": 30
}

print("============================")
print("Old information")
print(f"Name: {student_list['name']}")
print(f"Gender: {student_list['gender']}")
print(f"Group: {student_list['group']}")
print(f"Subject: {student_list['subject']}")
print(f"Score: {student_list['score']}")

student_list['name'] = 'mono168'

print("============================")
print("New information")
print(f"Name: {student_list['name']}")
print(f"Gender: {student_list['gender']}")
print(f"Group: {student_list['group']}")
print(f"Subject: {student_list['subject']}")
print(f"Score: {student_list['score']}")
"""


"""     9
student_list = {
    "name": "mono",
    "gender": "male",
    "group": 'sv2.6',
    "subject": 'PP',
    "score": 30
}

print("============================")
print("Old information")
print(f"Name: {student_list['name']}")
print(f"Gender: {student_list['gender']}")
print(f"Group: {student_list['group']}")
print(f"Subject: {student_list['subject']}")
print(f"Score: {student_list['score']}")

student_list['shift'] = 'morning'   # work
student_list['name'] = 'mono168'

print("============================")
print("New information")
print(f"Name: {student_list['name']}")
print(f"Gender: {student_list['gender']}")
print(f"Group: {student_list['group']}")
print(f"Subject: {student_list['subject']}")
print(f"Score: {student_list['score']}")
print(f"Score: {student_list['shift']}")
"""


"""     10
student = {
    "name": "mono",
    "gender": "male",
    "group": 'sv2.6',
    "subject": 'PP',
    "score": 30
}

print("============================")
print("Old information")
print(student)


student['shift'] = 'morning'   # work

print("============================")
print("New information")
print(student)
"""


"""     11
student = {
    "name": "mono",
    "gender": "male",
    "group": 'sv2.6',
    "subject": 'PP',
    "score": 30
}

print("============================")
print("Old information")
print(student)


student['shift'] = 'morning'   # work
student.pop('gender')   # pop is for Removing Items

print("============================")
print("New information")
print(student)
"""
