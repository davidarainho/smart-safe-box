import sqlite3
import json

# Create a connection to the database
conn = sqlite3.connect('users.db')

# Create a self.cursor object
cursor = conn.cursor()

# Create a table to store lock information if it doesn't already exist
cursor.execute('''CREATE TABLE IF NOT EXISTS locks
                        (id INTEGER PRIMARY KEY,
                        lock_id INTEGER UNIQUE,
                        name TEXT,
                        location TEXT,
                        state INTEGER,
                        lock_request INTEGER,
                        access_pin TEXT,
                        user_last_access TEXT)''')


lock_dict = {
    "lockID": "Lock1",
    "name": "Lock1Name",
            "location": "Porto",
            "state": 0,
            "lock_request": 0,
            "access_pin": "1234",
            "lastAccess": None
}

lock1 = json.dumps(lock_dict)

lock_dict = {
    "lockID": "Lock2",
    "name": "Lock2Name",
            "location": "Viseu",
            "state": 0,
            "lock_request": 0,
            "access_pin": "6716",
            "lastAccess": None
}

lock2 = json.dumps(lock_dict)

lock_dict = {
    "lockID": "Lock3",
    "name": "Lock3Name",
            "location": "Compostela",
            "state": 0,
            "lock_request": 0,
            "access_pin": "1927",
            "lastAccess": None
}

lock3 = json.dumps(lock_dict)

print(lock1)
print(lock2)
print(lock3)


def add_lock(json_input):

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lockID', 'name', 'location',
                       'state', 'lock_request', 'access_pin', 'lastAccess']
    for field in required_fields:
        if field not in data:
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lockID']
    name = data['name']
    location = data['location']
    state = data['state']
    lock_request = data['lock_request']
    access_pin = data['access_pin']
    lastAccess = data['lastAccess']

    cursor.execute("INSERT INTO locks (lock_id, name, location, state, lock_request, access_pin, user_last_access) VALUES (?, ?, ?, ?, ?, ?, ?)",
                   (lock_id, name, location, state, lock_request, access_pin, lastAccess))
    conn.commit()
    print(f"Lock {name} added successfully!")


add_lock(lock1)
add_lock(lock2)
add_lock(lock3)

print("Initialization successful")

conn.close()
