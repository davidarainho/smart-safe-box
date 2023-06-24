import sys
from NEW_userinfo import *
import random
from faker import Faker
import string
sys.path.append("..")
# needed to import new userinfo


def generate_random_user_info():
    fake = Faker()

    user_info = {}

    # Generate random username
    username = fake.user_name()
    user_info['username'] = username

    # Generate random password
    password = fake.password()
    # password = ''.join(random.choices(string.ascii_letters + string.digits, k=10))
    user_info['password'] = password

    user_info['false_try_trigger'] = 0

    # Generate random access_pin
    access_pin = ''.join(random.choices(string.digits, k=4))
    user_info['access_pin'] = access_pin

    # Generate random email
    email = fake.email()
    user_info['email'] = email

    # Generate random allow_notifications
    allow_notifications = random.randint(0, 1)
    user_info['allow_notifications'] = allow_notifications

    # Set is_admin to 0
    user_info['is_admin'] = 0

    return user_info


admin_info = {
    'username': 'master',
    'password': 'admin',
    'false_try_trigger': 0,
    'access_pin': '1234',
    'email': 'admin@smart_safe_box.pt',
    'allow_notifications': 0,
    'is_admin': 1
}

initialize_db()
add_user(json.dumps(admin_info))

add_user(json.dumps(generate_random_user_info()))
add_user(json.dumps(generate_random_user_info()))
add_user(json.dumps(generate_random_user_info()))
add_user(json.dumps(generate_random_user_info()))

create_device()
create_device()
create_device()

conn = sqlite3.connect('smart_safe_box.db')
cursor = conn.cursor()

sql_query = '''
SELECT device_id FROM devices WHERE is_active = 0;
'''

cursor.execute(sql_query)

print(cursor.fetchone()[0])
device_id = cursor.fetchone()[0]


conn.close()
device_info = {
    'device_id': device_id
}

activate_device(json.dumps(device_info))

conn = sqlite3.connect('smart_safe_box.db')
cursor = conn.cursor()

sql_query = '''
SELECT user_id FROM users WHERE is_admin = 0;
'''
cursor.execute(sql_query)

users_ids = cursor.fetchall()

sql_query = '''
SELECT door_id FROM doors
JOIN devices ON devices.device_id = doors.device_id
WHERE devices.is_active = 1
'''
cursor.execute(sql_query)

doors_ids = cursor.fetchall()

conn.close()

print(users_ids)
print(doors_ids)

for i, user in enumerate(users_ids):
    allocate_info = {
        'user_id': user[0],
        'door_id': doors_ids[i][0],
        'access_level': 0
    }
    print(allocate_info)
    allocate_door_to_user(json.dumps(allocate_info))
