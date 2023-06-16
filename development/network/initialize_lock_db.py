import sqlite3
import json
import hashlib
import random

# Create a connection to the database
conn = sqlite3.connect('users.db')

# Create a self.cursor object
cursor = conn.cursor()

# Create appcode
appcode = str(random.randint(0, 999999))

# Create a table to store lock information if it doesn't already exist
cursor.execute('''CREATE TABLE IF NOT EXISTS locks
                        (id INTEGER PRIMARY KEY,
                        lock_id INTEGER UNIQUE,
                        name TEXT,
                        location TEXT,
                        state INTEGER,
                        lock_request INTEGER,
                        user_last_access TEXT,
                        appcode TEXT)''')


# Create a table to store user information if it doesn't already exist
cursor.execute('''CREATE TABLE IF NOT EXISTS users
                    (user_id INTEGER PRIMARY KEY,
                    username TEXT,
                    password_hash TEXT,
                    email TEXT,
                    notifications INTEGER,
                    access_level INTEGER,
                    access_pin TEXT)''')


lock_dict = {
    "lockID": "Lock1",
    "name": "Lock1Name",
    "location": "Porto",
    "state": 0,
    "lock_request": 0,
    "lastAccess": [],
    "appcode": appcode
}

lock1 = json.dumps(lock_dict)

lock_dict = {
    "lockID": "Lock2",
    "name": "Lock2Name",
    "location": "Viseu",
    "state": 0,
    "lock_request": 0,
    "lastAccess": [],
    "appcode": appcode
}

lock2 = json.dumps(lock_dict)

lock_dict = {
    "lockID": "Lock3",
    "name": "Lock3Name",
    "location": "Compostela",
    "state": 0,
    "lock_request": 0,
    "lastAccess": [],
    "appcode": appcode
}

lock3 = json.dumps(lock_dict)

lock_dict = {
    "lockID": "Lock4",
    "name": "Lock4Name",
    "location": "",
    "state": 0,
    "lock_request": 0,
    "lastAccess": [],
    "appcode": appcode
}

lock4 = json.dumps(lock_dict)

print(lock1)
print(lock2)
print(lock3)
print(lock4)

# Function to add a user to the database


def add_user(json_input):

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username', 'password_hash',
                       'email', 'notifications', 'access_level0', 'access_pin']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']
    password = data['password_hash']
    email = data['email']
    notifications = data['notifications']
    access_level = data['access_level0']
    access_pin = data['access_pin']

    # Generate the password hash
    password_hash = hashlib.sha256(password.encode()).hexdigest()

    cursor.execute("INSERT INTO users (username, password_hash, email, notifications, access_level, access_pin) VALUES (?, ?, ?, ?, ?, ?)", (username, password_hash, email, notifications, access_level, access_pin))
    user_id = cursor.lastrowid

    print("Created User " + username)
    conn.commit()


def add_lock(json_input):

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lockID', 'name', 'location', 'state', 'lock_request', 'lastAccess', 'appcode']
    for field in required_fields:
        if field not in data:
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lockID']
    name = data['name']
    location = data['location']
    state = data['state']
    lock_request = data['lock_request']
    #lastAccess = data['lastAccess']
    lastAccess = json.dumps(data['lastAccess'])
    appcode = data['appcode']

    cursor.execute("INSERT INTO locks (lock_id, name, location, state, lock_request, user_last_access, appcode) VALUES (?, ?, ?, ?, ?, ?, ?)",
                   (lock_id, name, location, state, lock_request, lastAccess, appcode))
    conn.commit()
    print(f"Lock {name} added successfully!")


add_lock(lock1)
add_lock(lock2)
add_lock(lock3)
add_lock(lock4)


user_dict = {
    "username": "master",
    "password_hash": hashlib.sha256(str(123456).encode()).hexdigest(),
    "email": "master@supersmartbox.com",
    "access_level0": 1,
    "notifications": 0,
    "access_pin": "000000"
}

super_user = json.dumps(user_dict)

print(super_user)

add_user(super_user)

print("Initialization successful")

conn.close()
