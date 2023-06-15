import sqlite3
import hashlib
import json
import datetime


def initializeDatabase():
    # Create a connection with a remote server
    # conn = sqlite3.connect('http://<REMOTE_HOST>/users.db')

    # Create a connection to the database
    conn = sqlite3.connect('users.db')

    # Create a cursor object
    cursor = conn.cursor()

    # Create a table to store user information if it doesn't already exist
    cursor.execute('''CREATE TABLE IF NOT EXISTS users
                    (user_id INTEGER PRIMARY KEY,
                    username TEXT,
                    password_hash TEXT,
                    email TEXT,
                    notifications INTEGER,
                    access_level INTEGER)''')

    # Create a table to store lock information if it doesn't already exist
    cursor.execute('''CREATE TABLE IF NOT EXISTS locks
                    (id INTEGER PRIMARY KEY,
                    lock_id TEXT UNIQUE,
                    name TEXT,
                    location TEXT,
                    state INTEGER,
                    lock_request INTEGER,
                    access_pin TEXT,
                    user_last_access TEXT)''')

    # Create a table to store active locks information if it doesn't already exist
    cursor.execute('''CREATE TABLE IF NOT EXISTS active_locks
                    (id INTEGER PRIMARY KEY,
                    lock_id TEXT UNIQUE,
                    name TEXT,
                    location TEXT,
                    state INTEGER,
                    lock_request INTEGER,
                    access_pin TEXT,
                    user_last_access TEXT)''')

    # Create a table to store the relationship between locks and users
    cursor.execute('''CREATE TABLE IF NOT EXISTS lock_users
                    (lock_users_id INTEGER PRIMARY KEY,
                    lock_id INTEGER,
                    username TEXT,
                    access_level INTEGER)''')

    conn.close()


# Function to add a user to the database
def add_user(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username', 'password',
                       'email', 'notifications', 'access_level']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']
    password = data['password']
    email = data['email']
    notifications = data['notifications']
    access_level = data['access_level']

    # Generate the password hash
    password_hash = hashlib.sha256(password.encode()).hexdigest()

    cursor.execute("INSERT INTO users (username, password_hash, email, notifications, access_level) VALUES (?, ?, ?, ?, ?)",
                   (username, password_hash, email, notifications, access_level))
    user_id = cursor.lastrowid

    print("Created User " + username)
    conn.commit()
    conn.close()
    return True

# Function to deallocate a lock from a specific user
def deallocate_lock(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username', 'lock_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']
    lock_id = data['lock_id']

    cursor.execute(
        "DELETE FROM lock_users WHERE lock_id=? AND username=?", (lock_id, username))
    conn.commit()
    print(f"Lock {lock_id} deallocated from user {username}")
    conn.close()
    return True

# Function to remove a user from the database


def remove_user(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']

    cursor.execute("DELETE FROM users WHERE username=?", (username,))
    cursor.execute("DELETE FROM lock_users WHERE username=?", (username,))
    conn.commit()
    print(f"User {username} and their relationships removed successfully!")
    conn.close()
    return True


""" def remove_lock(self, json_input):
    try:
    # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id']
    for field in required_fields:
        if field not in data:
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lock_id']

    cursor.execute("DELETE FROM locks WHERE id=?", (lock_id,))
    cursor.execute("DELETE FROM lock_users WHERE lock_id=?", (lock_id,))
    conn.commit()
    print(f"Lock {lock_id} and its relationships removed successfully!")

    return True """

# Function to get all users associated with a specific lock


def get_users_for_lock(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lock_id']

    cursor.execute(
        "SELECT username, access_level FROM lock_users WHERE lock_id=?", (lock_id,))
    users = cursor.fetchall()
    users = [user[0] for user in users]
    json_users = json.dumps(users)
    conn.close()
    return json_users

# Function to get all locks associated with a specific user


def get_locks_for_user(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']

    cursor.execute(
        "SELECT lock_id FROM lock_users WHERE username=?", (username,))
    lock_id = cursor.fetchone()[0]
    cursor.execute(
        "SELECT access_level FROM lock_users WHERE username=?", (username,))
    access_level = cursor.fetchone()[0]
    # locks = [lock[0] for lock in locks]

    lock_dict = {
        "lock_id": lock_id,
        "access_level": access_level
    }
    json_locks = json.dumps(lock_dict)
    conn.close()
    return json_locks

# Function to get the state of a specific lock


def get_lock_state(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lock_id']

    cursor.execute("SELECT state FROM locks WHERE lock_id=?", (lock_id,))
    state = cursor.fetchone()[0]
    conn.close()
    return state

# Function to update the username for a user


def update_username_function(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username', 'new_username']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']
    new_username = data['new_username']

    cursor.execute("UPDATE users SET username=? WHERE username=?",
                   (new_username, username))
    cursor.execute(
        "UPDATE lock_users SET username=? WHERE username=?", (new_username, username))
    conn.commit()
    print(f"Username changed from {username} to {new_username}")
    conn.close()
    return True

# Function to update the password for a user


def update_password_function(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username', 'new_password']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']
    new_password = data['new_password']

    # Generate the new password hash
    new_password_hash = hashlib.sha256(new_password.encode()).hexdigest()
    cursor.execute("UPDATE users SET password_hash=? WHERE username=?",
                   (new_password_hash, username))
    conn.commit()
    print(f"Password changed for user {username}")
    conn.close()
    return True

# Function to update the e-mail for a user


def update_email_function(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username', 'new_email']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']
    new_email = data['new_email']

    cursor.execute("UPDATE users SET email=? WHERE username=?",
                   (new_email, username))
    conn.commit()
    print(f"Email changed for user {username}")

    conn.close()
    return True

# Function to update the name for a lock


def update_lock_name(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id', 'new_name']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    new_name = data['new_name']
    lock_id = data['lock_id']

    cursor.execute("UPDATE active_locks SET name=? WHERE lock_id=?",
                   (new_name, lock_id))
    conn.commit()
    print(f"Name updated for lock {lock_id}")

    conn.close()
    return True


# Function to update the location for a lock
def update_lock_location(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id', 'new_location']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    new_location = data['new_location']
    lock_id = data['lock_id']

    cursor.execute("UPDATE active_locks SET location=? WHERE lock_id=?",
                   (new_location, lock_id))
    conn.commit()
    print(f"Location updated for lock {lock_id}")

    conn.close()
    return True


# Function to allocate a lock for a specific user


def allocate_lock_for_user(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id', 'access_level', 'username']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lock_id']
    access_level = data['access_level']
    username = data['username']

    cursor.execute("INSERT INTO lock_users (lock_id, username, access_level) VALUES (?, ?, ?)",
                   (lock_id, username, access_level))
    conn.commit()
    print(
        f"Lock {lock_id} allocated to user {username} with access level {access_level}")

    conn.close()
    return True

# Function to list all usernames


def list_all_usernames():
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    # Execute a SQL query to get all usernames from the users table
    cursor.execute("SELECT username FROM users")

    # Fetch all results from the executed SQL query
    results = cursor.fetchall()

    # Extract usernames from the results and return them as a list
    usernames = [result[0] for result in results]

    json_usernames = json.dumps(usernames)

    conn.close()
    return json_usernames


def user_object(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']

    data_locks = get_locks_for_user(json.dumps({"username": username}))

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    user_dict = {
        "username": user[1],
        "password_hash": user[2],
        "email": user[3],
        "access_level": user[4]
    }

    user_dict["active_locks"] = json.loads(data_locks)

    # print(user_dict)
    user_ret = json.dumps(user_dict)
    # print(user_ret)

    conn.close()
    return user_ret


def update_access_level(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['username', 'new_access_level', 'lock_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    username = data['username']
    new_access_level = data['new_access_level']
    lock_id = data['lock_id']

    cursor.execute("UPDATE lock_users SET access_level = ? WHERE username = ? AND lock_id = ?",
                   (new_access_level, username, lock_id,))

    conn.commit()

    conn.close()
    return True


def lock_object(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lock_id']

    cursor.execute("SELECT * FROM active_locks WHERE lock_id = ?", (lock_id,))
    lock = cursor.fetchone()

    lock_dict = {
        "lockID": lock[1],
        "Name": lock[2],
        "Location": lock[3],
        "State": lock[4],
        "lock_request": lock[5],
        "pinLock": lock[6],
        "lastAccess": lock[7]
    }

    lock_ret = json.dumps(lock_dict)

    conn.close()
    return lock_ret


def activate_lock(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lock_id']

    # Get the lock details from the locks table
    cursor.execute("SELECT * FROM locks WHERE lock_id = ?", (lock_id,))
    lock = cursor.fetchone()

    # Add the lock to the active_locks table
    cursor.execute("INSERT INTO active_locks (lock_id, name, location, state, lock_request, access_pin, user_last_access) VALUES (?, ?, ?, ?, ?, ?, ?)",
                   (lock[1], lock[2], lock[3], lock[4], lock[5], lock[6], lock[7]))
    conn.commit()

    # Remove the lock from the locks table
    cursor.execute("DELETE FROM locks WHERE lock_id = ?", (lock_id,))
    conn.commit()

    conn.close()
    return True


def deactivate_lock(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lock_id']

    # Get the lock details from the active_locks table
    cursor.execute("SELECT * FROM active_locks WHERE lock_id = ?", (lock_id,))
    lock = cursor.fetchone()

    # Add the lock to the locks table
    cursor.execute("INSERT INTO locks (lock_id, name, location, state, lock_request, access_pin, user_last_access) VALUES (?, ?, ?, ?, ?, ?, ?)",
                   (lock[1], lock[2], lock[3], lock[4], lock[5], lock[6], lock[7]))
    conn.commit()

    # Remove the lock from the active_locks table
    cursor.execute("DELETE FROM active_locks WHERE lock_id = ?", (lock_id,))
    cursor.execute("DELETE FROM lock_users WHERE lock_id=?", (lock_id,))
    conn.commit()

    conn.close()
    return True


def update_pin_app2server(json_input):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id', 'new_access_pin']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lock_id']
    new_access_pin = data['new_access_pin']

    # Update the access_pin in the active_locks table
    cursor.execute("UPDATE active_locks SET access_pin=? WHERE lock_id=?",
                   (new_access_pin, lock_id,))
    conn.commit()

    conn.close()
    print("Updated Lock Pin")
    return True


def list_unactivated_locks():
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    # Execute a SQL query to get all usernames from the users table
    cursor.execute("SELECT lock_id FROM locks")

    # Fetch all results from the executed SQL query
    results = cursor.fetchall()

    # Extract usernames from the results and return them as a list
    locks = [result[0] for result in results]

    json_locks = json.dumps(locks)

    conn.close()
    return json_locks


def pin2esp(lock_id):

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute(
        "SELECT access_pin FROM active_locks WHERE lock_id=?", (lock_id,))

    pin = cursor.fetchone()[0]

    return pin


def update_lock_request(lock_id):

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute(
        "UPDATE active_locks SET lock_request=? WHERE lock_id=?", (1, lock_id["lock_id"]))
    conn.commit()
    conn.close()

    return True

def last_acess_update(json_input):

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['lock_id', 'username']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    lock_id = data['lock_id']
    username = data['username']

    # Create timestamp
    timestamp = datetime.datetime.now().strftime("%d-%m-%Y %H:%M:%S")

    # Create timestamped username
    timestamped_username = f"{timestamp}: {username}"

    # Get the current last_access_user string
    cursor.execute("SELECT user_last_access FROM locks WHERE lock_id=?", (lock_id,))
    result = cursor.fetchone()

    if result is None:
        return "Error: No matching lock found."

    # If the lock was never accessed before, initialize the string
    if result[0] is None:
        new_last_access_user = timestamped_username
    else:
    # Otherwise, append the new access at the end of the current string
        new_last_access_user = result[0] + ', ' + timestamped_username

    # Update the last_access_user string for the lock
    cursor.execute("UPDATE locks SET user_last_access=? WHERE lock_id=?", (new_last_access_user, lock_id))

    conn.commit()
    conn.close()
