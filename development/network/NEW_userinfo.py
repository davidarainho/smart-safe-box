import sqlite3
import hashlib
import json
import datetime
import random

DB_NAME = 'smart_safe_box.db'

def initialize_db():

    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)

    # Create a cursor object
    cursor = conn.cursor()

    # Create 'users' table
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS users (
            user_id TEXT PRIMARY KEY,
            username TEXT UNIQUE,
            password TEXT,
            false_try_trigger INTEGER,
            access_pin TEXT NOT NULL,
            email TEXT UNIQUE,
            allow_notifications INTEGER,
            is_admin INTEGER
        )
    """)

    # Create 'doors' table
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS doors (
            door_id TEXT PRIMARY KEY,
            real_door TEXT,
            device_id TEXT,
            door_state INT,
            door_request INTEGER,
            last_access TEXT,
            user_last_access TEXT,
            number_of_users INT,
            FOREIGN KEY(device_id) REFERENCES devices(device_id) ON DELETE CASCADE
        )
    """)

    # Create 'devices' table
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS devices (
            device_id TEXT PRIMARY KEY,
            app_code TEXT UNIQUE,
            is_active INTEGER
        )
    """)

    # Create 'users_doors' relationship table
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS users_doors (
            user_id TEXT,
            door_id TEXT,
            door_name TEXT,
            access_level INTEGER,
            comment TEXT,
            FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
            FOREIGN KEY(door_id) REFERENCES doors(door_id) ON DELETE CASCADE
        )
    """)
    
    #Create 'notifications_requests' relationship table
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS notifications_requests(
            user_id TEXT,
            pin_change_notification INTEGER,
            FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
        )
    """)

    # Save (commit) the changes and close the connection
    conn.commit()
    conn.close()

# Function to add a user to the database
def add_user(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)

    # Create a cursor object
    cursor = conn.cursor()
    
    # Turn on foreign key constraint enforcement
    cursor.execute("PRAGMA foreign_keys = ON")

    # Parse JSON input
    try:
        data = json.loads(json_input)
    except json.JSONDecodeError:
        print("Error: Invalid JSON input")
        return

    # Check that all necessary fields are in the data
    required_fields = ['username', 'password', 'false_try_trigger', 'access_pin', 'email', 'allow_notifications', 'is_admin']
    for field in required_fields:
        if field not in data:
            print(f"Error: Missing required field '{field}'")
            return
    
    # Extract the data
    username = data['username']
    password = data['password']
    false_try_trigger = data['false_try_trigger']
    access_pin = data['access_pin']
    email = data['email']
    allow_notifications = data['allow_notifications']
    is_admin = data['is_admin']

    while True:
        # Generate a random integer between 1 and 999999 (6 digits max)
        unique_id = random.randint(1, 999999)
        
        # Check if the generated number already exists in the database
        cursor.execute("SELECT COUNT(*) FROM users WHERE user_id = ?", (unique_id,))
        count = cursor.fetchone()[0]
        
        if count is None:
            break
        
        # If the number doesn't exist, proceed
        elif count == 0:
            break
    
    user_id = str(unique_id)
    
    # Generate the password hash
    password = hashlib.sha256(password.encode()).hexdigest()

    # Insert the data into the 'users' table
    try:
        cursor.execute("INSERT INTO users (user_id, username, password, false_try_trigger, access_pin, email, allow_notifications, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                       (user_id, username, password, false_try_trigger, access_pin, email, allow_notifications, is_admin))
    except sqlite3.IntegrityError:
        print(f"Error: User '{username}' already exists")
        return

    # Commit the changes and close the connection
    conn.commit()
    conn.close()

    print(f"User '{username}' added successfully with user_id: {user_id}")
    
    return True
    
# Function to deallocate a door from a specific user    
def deallocate_door(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)

    # Create a cursor object
    cursor = conn.cursor()

    # Parse JSON input
    try:
        data = json.loads(json_input)
    except json.JSONDecodeError:
        print("Error: Invalid JSON input")
        return

    # Check that all necessary fields are in the data
    required_fields = ['user_id', 'door_id']
    for field in required_fields:
        if field not in data:
            print(f"Error: Missing required field '{field}'")
            return

    # Extract the data
    user_id = data['user_id']
    door_id = data['door_id']

    # Delete the corresponding row from the 'users_doors' table
    cursor.execute("DELETE FROM users_doors WHERE user_id=? AND door_id=?", (user_id, door_id))

    # Check if the operation was successful
    if cursor.rowcount == 0:
        print("Error: No matching row found")
        return False
    else:
        print(f"Door '{door_id}' deallocated from user '{user_id}' successfully")

    # Commit the changes and close the connection
    conn.commit()
    conn.close()
    
    return True

# Function to remove a user from the database
def remove_user(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            print(f"Error: Missing required field '{field}'")
            return f"Error: Missing required field '{field}'"

    # Extract the username from the JSON data
    user_id = data['user_id']

    # Delete the user from the 'users' table
    cursor.execute("DELETE FROM users WHERE user_id=?", (user_id,))

    # Commit the changes and close the connection
    conn.commit()
    conn.close()

    print(f"User {user_id} removed from database")
    return True

# Function to get all users associated with a specific door
def get_users_for_door(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['door_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            print(f"Error: Missing required field '{field}'")
            return f"Error: Missing required field '{field}'"

    # Extract the door_id from the JSON data
    door_id = data['door_id']

    # Get the user_id for all users associated with this door
    cursor.execute("""
        SELECT users.username
        FROM users
        JOIN users_doors ON users.user_id = users_doors.user_id
        WHERE users_doors.door_id = ?
    """, (door_id,))

    users = cursor.fetchall()
    
    if not users:
        return None

    # Create a dictionary to hold the results
    users_for_door = {'door_id': door_id, 'users': [], 'number_of_users': 0}

    # Add each user to the results
    for user in users:
        users_for_door['users'].append({'username': user[0]})
   
    #Add the number of users to the result     
    users_for_door['number_of_users'] = len(users_for_door['users'])

    # Close the database connection
    conn.close()

    # Return the results as a JSON string
    return json.dumps(users_for_door)

# Function to get all doors associated with a specific user
def get_doors_for_user(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            print(f"Error: Missing required field '{field}'")
            return f"Error: Missing required field '{field}'"

    # Extract the user_id from the JSON data
    user_id = data['user_id']

    # Get the door_id and access_level for all doors associated with this user
    cursor.execute("""
        SELECT doors.door_id, users_doors.access_level
        FROM doors
        JOIN users_doors ON doors.door_id = users_doors.door_id
        WHERE users_doors.user_id = ?
    """, (user_id,))

    doors = cursor.fetchall()

    # Create a dictionary to hold the results
    doors_for_user = {'user_id': user_id, 'doors': []}

    # Add each door to the results
    for door in doors:
        doors_for_user['doors'].append({'door_id': door[0], 'access_level': door[1]})

    # Close the database connection
    conn.close()

    # Return the results as a JSON string
    return json.dumps(doors_for_user)

# Function to get the state of a specific door
def get_door_state(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['door_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            print(f"Error: Missing required field '{field}'")
            return f"Error: Missing required field '{field}'"

    # Extract the door_id from the JSON data
    door_id = data['door_id']

    # Get the door state
    cursor.execute("SELECT door_state FROM doors WHERE door_id = ?", (door_id,))
    door_state = cursor.fetchone()

    if door_state is None:
        conn.close()
        print("Error: Invalid door_id")
        return "Error: Invalid door_id"

    # Close the database connection
    conn.close()

    # Return the door state
    return door_state[0]

# Function to update the username for a user
def update_username(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id', 'new_username']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the user_id and new username from the JSON data
    user_id = data['user_id']
    new_username = data['new_username']

    # Check if the new username already exists in the database
    cursor.execute("SELECT username FROM users WHERE username=?", (new_username,))
    if cursor.fetchone() is not None:
        return "Error: Username already exists"

    # Update the username
    cursor.execute("UPDATE users SET username = ? WHERE user_id = ?", (new_username, user_id))
    conn.commit()

    # Close the database connection
    conn.close()

    print(f"Username successfully updated to {new_username}")
    # Return a success message
    return True

# Function to update the password for a user
def update_password(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id', 'new_password']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the user_id and new password from the JSON data
    user_id = data['user_id']
    new_password = data['new_password']

    # Update the password
    cursor.execute("UPDATE users SET password = ? WHERE user_id = ?", (new_password, user_id))
    conn.commit()

    # Close the database connection
    conn.close()

    print(f"Password successfully updated")
    
    # Return a success message
    return True

# Function to update the e-mail for a user
def update_email(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id', 'new_email']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the user_id and new email from the JSON data
    user_id = data['user_id']
    new_email = data['new_email']

    # Check if the new email is already in use
    cursor.execute("SELECT * FROM users WHERE email = ?", (new_email,))
    if cursor.fetchone() is not None:
        # The new email is already in use, return an error
        conn.close()
        return f"Error: Email '{new_email}' is already in use"

    # Update the email
    cursor.execute("UPDATE users SET email = ? WHERE user_id = ?", (new_email, user_id))
    conn.commit()

    # Close the database connection
    conn.close()

    print(f"Email successfully updated")
    
    # Return a success message
    return True 

# Function to update the name for a door
def update_door_name(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['door_id', 'new_door_name', 'user_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the door_id and new door name from the JSON data
    door_id = data['door_id']
    new_door_name = data['new_door_name']
    user_id = data['user_id']

    # Update the door name
    cursor.execute("UPDATE users_doors SET door_name = ? WHERE door_id = ? AND user_id = ?", (new_door_name, door_id, user_id))
    conn.commit()

    # Close the database connection
    conn.close()
    
    print(f"Door name for door with id {door_id} for user {user_id} successfully updated to {new_door_name}")

    # Return a success message
    return True

# Function to update the comment for a lock
def update_door_comment(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['door_id', 'user_id', 'new_comment']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the door_id and new comment from the JSON data
    door_id = data['door_id']
    user_id = data['user_id']
    new_comment = data['new_comment']

    # Update the door comment
    cursor.execute("UPDATE users_doors SET comment = ? WHERE door_id = ? AND user_id = ?", (new_comment, door_id, user_id))
    conn.commit()

    # Close the database connection
    conn.close()

    print(f"Comment for user {user_id} for door {door_id} successfully updated to {new_comment}")

    # Return a success message
    return True

# Function to allocate a door for a specific user
def allocate_door_to_user(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    # conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id', 'door_id', 'access_level']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the user_id, door_id, and access_level from the JSON data
    user_id = data['user_id']
    door_id = data['door_id']
    access_level = data['access_level']
    
    #Check if door already associated with user
    cursor.execute("SELECT user_id, door_id FROM users_doors WHERE user_id=? AND door_id=?", (user_id, door_id))
    
    if len(cursor.fetchall()) > 0:
        conn.close()
        return f"Error: User already associated with door"

    # Add the user-door relationship to the 'users_doors' table
    cursor.execute("INSERT INTO users_doors (user_id, door_id, access_level, door_name, comment) VALUES (?, ?, ?, 'MyDoor', '')", 
                   (user_id, door_id, access_level))
    conn.commit()

    # Close the database connection
    conn.close()

    print(f"Door of id {door_id} successfully allocated to user {user_id} with access_level {access_level}")

    # Return a success message
    return True

# Function to list all usernames
def list_all_usernames():
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint
    
    # Create a cursor object
    cursor = conn.cursor()

    # Execute a SQL query to get all usernames from the users table
    cursor.execute("SELECT username FROM users")

    # Fetch all results from the executed SQL query
    results = cursor.fetchall()

    # Extract usernames from the results and return them as a list
    usernames = [result[0] for result in results]

    #print(usernames)
    
    conn.close()
    
    return json.dumps(usernames)

#Function to fetch an user object
def get_user_object(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()
    
    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the user_id from the JSON data
    user_id = data['user_id']

    # Fetch the user data
    cursor.execute("SELECT * FROM users WHERE user_id=?", (user_id,))
    user = cursor.fetchone()

    if user is None:
        print("No such user exists")
        return None

    # Fetch the list of active doors for this user
    cursor.execute("""
        SELECT doors.door_id, users_doors.access_level
        FROM doors
        INNER JOIN users_doors ON doors.door_id = users_doors.door_id
        WHERE users_doors.user_id=?
    """, (user_id,))
    active_doors = cursor.fetchall()


    # Prepare the user data and active doors list for JSON conversion
    user_data = {
        "user_id": user[0],
        "username": user[1],
        "password": user[2],
        "false_try_trigger": user[3],
        "access_pin": user[4],
        "email": user[5],
        "allow_notifications": user[6],
        "is_admin": user[7],
        "active_doors": [{"door_id": door[0], "access_level": door[1]} for door in active_doors]
    }
 
    # Close the database connection
    conn.close()
 
    return json.dumps(user_data)

#Function to assign a real door to a door_id
def assign_real_door(door_id):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    # Get the current real_door numbers in use
    cursor.execute("SELECT real_door FROM doors WHERE real_door IS NOT NULL ORDER BY real_door")
    real_doors_in_use = [row[0] for row in cursor.fetchall()]

    # Find the first available real_door number
    for real_door in range(1, 7):
        if real_door not in real_doors_in_use:
            # Update the door with the new real_door number
            cursor.execute("UPDATE doors SET real_door = ? WHERE door_id = ?", (real_door, door_id))
            conn.commit()
            break
    else:
        print("Error: All real_door numbers are in use")
        return False

    conn.close()
    return True

#Function to update last access fields in the case of an access
def update_last_access(json_input):
    
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()
    
    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id', 'door_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            return f"Error: Missing required field '{field}'"

    # Extract the user_id from the JSON data
    user_id = data['user_id']
    door_id = data['door_id']
    
    cursor.execute("SELECT username FROM users WHERE user_id = ?", (user_id,))
    username = cursor.fetchone()[0]

    # Get the current date and time
    now = datetime.datetime.now()
    # Format it as a string
    now_str = now.strftime("%Y-%m-%d")

    try:
        # Fetch the current last_access and user_last_access fields for the door
        cursor.execute("SELECT last_access, user_last_access FROM doors WHERE door_id = ?", (door_id,))
        result = cursor.fetchone()
        if result is None:
            print("No such door exists.")
            return False

        last_access_current, user_last_access_current = result

        # Append new access data to existing data
        if last_access_current is not None and last_access_current is not "":
            last_access_new = last_access_current + ',' + now_str
        else:
            last_access_new = now_str
        
        if user_last_access_current is not None and user_last_access_current is not "":
            user_last_access_new = user_last_access_current + ',' + username
        else:
            user_last_access_new = username

        # Update the last_access and user_last_access fields with new data
        cursor.execute(
            "UPDATE doors SET last_access = ?, user_last_access = ? WHERE door_id = ?",
            (last_access_new, user_last_access_new, door_id)
        )
        conn.commit()

    except sqlite3.Error as error:
        print(f"Failed to update data in sqlite table", error)
        return False

    finally:
        # Close the database connection
        conn.close()

    return True

#Function to fetch a door object
def get_door_object(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        print("Error: Invalid JSON input")
        return json.dumps({"error": "Invalid JSON input"})

    # Check that all necessary fields are in the data
    required_fields = ['door_id', 'user_id']
    for field in required_fields:
        if field not in data:
            print(f"Error: Missing required field '{field}'")
            return json.dumps({"error": f"Missing required field '{field}'"})

    # Extract the door_id
    door_id = data['door_id']
    user_id = data['user_id']

    # Get the door details from the doors table
    cursor.execute("SELECT * FROM doors WHERE door_id = ?", (door_id,))
    door = cursor.fetchone()

    if door is None:
        print("Error: No such door exists")
        return None
    
    # Get the comment for this user and door from the users_doors table
    cursor.execute("SELECT comment FROM users_doors WHERE user_id = ? AND door_id = ?", (user_id, door_id,))
    comment_result = cursor.fetchone()[0]

    # Construct the door object
    door_object = {
        "door_id": door[0], 
        "real_door": door[1], 
        "device_id": door[2],
        "door_state": door[3],
        "door_request": door[4],
        "last_access": door[5],
        "user_last_access": door[6],
        "number_of_users": json.loads(get_users_for_door(json.dumps({"door_id":door[0]})))['number_of_users'],
        "comment": comment_result
    }

    # Close the database connection
    conn.close()

    return json.dumps(door_object)

#Function to activate an ESP
def activate_device(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        print("Error: Invalid JSON input")
        return json.dumps({"error": "Invalid JSON input"})

    # Check that all necessary fields are in the data
    required_fields = ['device_id']
    for field in required_fields:
        if field not in data:
            print(f"Error: Missing required field '{field}'")
            return json.dumps({"error": f"Missing required field '{field}'"})

    # Extract the device_id
    device_id = data['device_id']

    # Activate the device
    cursor.execute("UPDATE devices SET is_active = 1 WHERE device_id = ?", (device_id,))
    conn.commit()

    #conn.close()


    # Add four entries to the doors table for this device
    for i in range(1, 5):
        
        while True:
        # Generate a random integer between 1 and 999999 (6 digits max)
            unique_id_door = random.randint(1, 999999)
            
            # Check if the generated number already exists in the database

            #conn = sqlite3.connect(DB_NAME)
            #conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint
            #cursor = conn.cursor()

            cursor.execute("SELECT COUNT(*) FROM users WHERE user_id = ?", (unique_id_door,))
            count = cursor.fetchone()[0]

            #conn.close()
            
            if count is None:
                break
            
            # If the number doesn't exist, proceed
            elif count == 0:
                break
        
        door_id = str(unique_id_door)

        #conn = sqlite3.connect(DB_NAME)
        #conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint
        #cursor = conn.cursor()
            
        cursor.execute("""
            INSERT INTO doors (door_id, real_door, device_id, door_state, door_request, last_access, user_last_access, number_of_users)
            VALUES (?, ?, ?, 0, 0, '', '', 0)
        """, (door_id, i, device_id))
        conn.commit()
        
        cursor.execute("SELECT user_id FROM users WHERE username='master'")
        master_id = cursor.fetchone()[0]
        master_dict ={
            "user_id": master_id,
            "door_id": door_id,
            "access_level": 0,
        }
        #conn.close()

        allocate_door_to_user(json.dumps(master_dict))
        #if(assign_real_door(door_id)):
        #    continue
        
        #else:
        #    print("Error: All real doors already assigned")
        #    return False
    # conn.commit()
    conn.close()

    print(f"Device of id {device_id} activated successfully")

    return True

#Function to deactivate an ESP
def deactivate_device(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        print("Error: Invalid JSON input")
        return json.dumps({"error": "Invalid JSON input"})

    # Check that all necessary fields are in the data
    required_fields = ['device_id']
    for field in required_fields:
        if field not in data:
            print(f"Error: Missing required field '{field}'")
            return json.dumps({"error": f"Missing required field '{field}'"})

    # Extract the device_id
    device_id = data['device_id']

    # Deactivate the device
    cursor.execute("UPDATE devices SET activated = 0 WHERE device_id = ?", (device_id,))
    conn.commit()

    # Remove entries from the doors table for this device
    cursor.execute("DELETE FROM doors WHERE device_id = ?", (device_id,))
    conn.commit()

    # Remove entries from the users_doors table for this device
    cursor.execute("""
        DELETE FROM users_doors WHERE door_id IN (
            SELECT door_id FROM doors WHERE device_id = ?
        )
    """, (device_id,))
    conn.commit()

    # Close the database connection
    conn.close()

    print(f"Device with id {device_id} deactivated successfully")
    return True

#Function to get an app_code for an ESP
def get_app_code(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()
    
    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['device_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    device_id = data['device_id']

    cursor.execute("SELECT app_code FROM devices WHERE device_id = ?", (device_id,))
    app_code = cursor.fetchone()[0]
    
    json_app_code = json.dumps({"app_code": app_code})
    
    return json_app_code

#Function to update the door_request field to control opening requests
def update_door_request(json_input):
    # Create a connection to the database  
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()
    
    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['door_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    door_id = data['door_id']

    cursor.execute(
        "UPDATE doors SET door_request=? WHERE door_id=?", (1, door_id))
    conn.commit()
    conn.close()

    return True

#Function to get a door's door_request field 
def get_door_request(json_input):
    # Create a connection to the database  
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()
    
    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['door_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    door_id = data['door_id']

    cursor.execute("SELECT door_request FROM doors WHERE door_id=?", (door_id,))
    door_request = cursor.fetchone()[0]
    conn.close()

    return json.dumps(door_request)

#Function to list ESP that are inactive
def list_inactive_devices():
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    # Execute a SQL query to get all usernames from the users table
    cursor.execute("SELECT device_id FROM devices")

    # Fetch all results from the executed SQL query
    results = cursor.fetchall()

    # Extract usernames from the results and return them as a list
    devices = [result[0] for result in results]

    json_devices = json.dumps(devices)

    conn.close()
    return json_devices

#Function to update the access pin for an user
def update_access_pin(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id', 'new_access_pin']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    user_id = data['user_id']
    new_access_pin = data['new_access_pin']

    # Update the access_pin in the active_locks table
    cursor.execute("UPDATE users SET access_pin=? WHERE user_id=?",
                   (new_access_pin, user_id,))
    conn.commit()

    conn.close()
    print(f"Updated Door Pin for user {user_id}")
    return True

#Function to get the pin of a user
def get_access_pin(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()
    
    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        conn.close()
        print("Error: Invalid JSON input")
        return "Error: Invalid JSON input"

    # Check that all necessary fields are in the data
    required_fields = ['user_id']
    for field in required_fields:
        if field not in data:
            conn.close()
            print("Error: Missing required field")
            return f"Error: Missing required field '{field}'"

    # Extract the data
    user_id = data['user_id']

    cursor.execute(
        "SELECT access_pin FROM users WHERE user_id=?", (user_id,))

    pin = cursor.fetchone()[0]

    return json.dumps(pin)

#Function to alter notification preferences
def update_notifications(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        print("Error: Invalid JSON input")
        return json.dumps({"error": "Invalid JSON input"})

    # Check that all necessary fields are in the data
    required_fields = ['user_id', 'allow_notifications']
    for field in required_fields:
        if field not in data:
            print(f"Error: Missing required field '{field}'")
            return json.dumps({"error": f"Missing required field '{field}'"})

    # Extract the user_id and the new notification status
    user_id = data['user_id']
    allow_notifications = data['allow_notifications']

    # Alter the allow_notifications field for this user
    cursor.execute(
        "UPDATE users SET allow_notifications = ? WHERE user_id = ?",
        (allow_notifications, user_id))
    conn.commit()

    # Close the database connection
    conn.close()

    print(f"Notification status for user {user_id} updated successfully")
    
    return True

#Function to fetch and update the false_try_trigger flag
def check_false_try_trigger(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        print("Error: Invalid JSON input")
        return json.dumps({"error": "Invalid JSON input"})

    # Check that all necessary fields are in the data
    required_fields = ['user_id']
    for field in required_fields:
        if field not in data:
            print(f"Error: Missing required field '{field}'")
            return json.dumps({"error": f"Missing required field '{field}'"})

    # Extract the user_id
    user_id = data['user_id']

    # Fetch the false_try_trigger field for this user
    cursor.execute(
        "SELECT false_try_trigger FROM users WHERE user_id = ?",
        (user_id,))
    result = cursor.fetchone()

    if result is None:
        conn.close()
        return json.dumps({"error": f"No such user_id '{user_id}'"})

    false_try_trigger = result[0]

    # Reset the false_try_trigger to 0
    cursor.execute(
        "UPDATE users SET false_try_trigger = 0 WHERE user_id = ?",
        (user_id,))
    conn.commit()

    # Close the database connection
    conn.close()

    return false_try_trigger

#Function to update access_level for a specific lock
def update_access_level(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()
    
    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        print("Error: Invalid JSON input")
        return json.dumps({"error": "Invalid JSON input"})

    # Check that all necessary fields are in the data
    required_fields = ['user_id', 'door_id', 'new_access_level']
    for field in required_fields:
        if field not in data:
            print(f"Error: Missing required field '{field}'")
            return json.dumps({"error": f"Missing required field '{field}'"})

    # Extract the user_id
    user_id = data['user_id']
    door_id = data['door_id']
    new_access_level = data['new_access_level']

    # Check if user and door exist
    cursor.execute("SELECT * FROM users WHERE user_id=?", (user_id,))
    user = cursor.fetchone()

    cursor.execute("SELECT * FROM doors WHERE door_id=?", (door_id,))
    door = cursor.fetchone()

    if user is None or door is None:
        conn.close()
        return None

    try:
        # Update access level
        cursor.execute("UPDATE users_doors SET access_level=? WHERE user_id=? AND door_id=?", (new_access_level, user_id, door_id))

        conn.commit()
        conn.close()
        
        print(f"Access level for user {user_id} for door {door_id} updated successfully to {new_access_level}")

        return True
    except Exception as e:
        conn.close()
        print(f"An error occurred: {e}")
        return False

#Function to update a door state
def update_door_state(json_input):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()
    
    try:
        # Attempt to load the JSON data
        data = json.loads(json_input)
    except json.JSONDecodeError:
        print("Error: Invalid JSON input")
        return json.dumps({"error": "Invalid JSON input"})

    # Check that all necessary fields are in the data
    required_fields = ['door_id', 'new_state']
    for field in required_fields:
        if field not in data:
            print(f"Error: Missing required field '{field}'")
            return json.dumps({"error": f"Missing required field '{field}'"})

    # Extract the user_id
    door_id = data['door_id']
    new_state = data['new_state']

    # Check if door exist
    cursor.execute("SELECT * FROM doors WHERE door_id=?", (door_id,))
    door = cursor.fetchone()

    if door is None:
        conn.close()
        return None

    try:
        # Update door state
        cursor.execute("UPDATE doors SET door_state=? WHERE door_id=?", (new_state, door_id))

        conn.commit()
        conn.close()

        print(f"Door state updated successfully to {new_state}")
        return True

    except Exception as e:
        conn.close()
        print(f"An error occurred: {e}")
        return False

#Function to get a new available device_id
def get_device_id():
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        # Fetch the device_id of the first available device
        cursor.execute("SELECT device_id FROM devices WHERE is_active=0 LIMIT 1")

        device = cursor.fetchone()
        conn.close()

        if device is None:
            print("No available device found")
            return None

        print(f"Device with device_id {device[0]} found")
        return {device[0]}

    except Exception as e:
        conn.close()
        print(f"An error occurred: {e}")
        return False
    
def create_device():
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    try:
        while True:
            # Generate a random device_id and app_code
            device_id = str(random.randint(1, 999999))
            app_code = str(random.randint(1, 999999))

            # Check if they are unique in the table
            cursor.execute("SELECT * FROM devices WHERE device_id=? OR app_code=?", (device_id, app_code))

            if cursor.fetchone() is None:
                break

        # Insert the new device into the table
        cursor.execute("INSERT INTO devices (device_id, app_code, is_active) VALUES (?, ?, 0)", (device_id, app_code))

        conn.commit()
        conn.close()

        print(f"Device with device_id: {device_id} and app_code: {app_code} has been created")
        return True

    except Exception as e:
        conn.close()
        print(f"An error occurred: {e}")
        return False
    
#Function to update pin change notification to 1
def update_pin_change_notification(json_input):
    # Establish a connection to the database
    with sqlite3.connect(DB_NAME) as conn:
        conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

        # Create a cursor object
        cursor = conn.cursor()

        try:
            # Load the JSON data
            data = json.loads(json_input)
        except json.JSONDecodeError:
            print("Error: Invalid JSON input")
            return False

        # Check that all necessary fields are in the data
        if 'user_id' not in data:
            print("Error: Missing 'user_id' in JSON input")
            return False

        # Extract the user_id and pin_change_notification
        user_id = data['user_id']

        try:
            # Update the pin_change_notification
            cursor.execute(
                "UPDATE notifications_requests SET pin_change_notification = 1 WHERE user_id = ?",
                (user_id,)
            )
            # Commit the changes
            conn.commit()

        except sqlite3.Error as e:
            print(f"Database error: {e}")
            return False

    # If no errors were encountered, return True
    return True

#Function to get the pin_change_notification value
def get_pin_change_notification(json_input):
    # Connect to the SQLite database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint
    cursor = conn.cursor()

    # Try to decode the JSON input
    try:
        data = json.loads(json_input)
    except json.JSONDecodeError:
        return json.dumps({"error": "Invalid JSON input"})

    # Check if necessary fields are in the data
    if 'user_id' not in data:
        return json.dumps({"error": "Missing 'user_id' field in input JSON"})

    user_id = data['user_id']

    # Fetch the pin_change_notification for this user
    cursor.execute("SELECT pin_change_notification FROM notifications_requests WHERE user_id = ?", (user_id,))
    result = cursor.fetchone()

    if result is None:
        return json.dumps({"error": "User not found"})

    pin_change_notification = result[0]

    # Update the flag to zero
    cursor.execute("UPDATE notifications_requests SET pin_change_notification = 0 WHERE user_id = ?", (user_id,))
    conn.commit()
    conn.close()

    # If pin_change_notification was 1, return True, else return False
    return pin_change_notification == 1

#Function to get the user_id for a specific username
def get_user_id(json_input):
    # Connect to the SQLite database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint
    cursor = conn.cursor()

    # Try to decode the JSON input
    try:
        data = json.loads(json_input)
    except json.JSONDecodeError:
        return json.dumps({"error": "Invalid JSON input"})

    # Check if necessary fields are in the data
    if 'username' not in data:
        return json.dumps({"error": "Missing 'username' field in input JSON"})

    username = data['username']

    # Fetch the user_id for this user
    cursor.execute("SELECT user_id FROM users WHERE username = ?", (username,))
    result = cursor.fetchone()

    if result is None:
        return json.dumps({"error": "User not found"})

    conn.close()

    # If user_id was found, return it
    return result


def get_door_name_for_user(json_input):
     # Connect to the SQLite database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint
    cursor = conn.cursor()

    # Try to decode the JSON input
    try:
        data = json.loads(json_input)
    except json.JSONDecodeError:
        return json.dumps({"error": "Invalid JSON input"})

    # Check if necessary fields are in the data
    if 'user_id' not in data or 'door_id' not in data:
        return json.dumps({"error": "Missing 'user_id' or 'door_id field in input JSON"})

    user_id = data['user_id']
    door_id = data['door_id']
    
    # Fetch the door_name for this user and door relationship
    cursor.execute("SELECT door_name FROM users_doors WHERE user_id = ? AND door_id = ?", (user_id, door_id))
    result = cursor.fetchone()[0]

    if result is None:
        return None

    conn.close()

    # If door_name was found, return it
    return result

# Function to list all door_ids from an active device
def list_doors_by_device(device_id):
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    # Execute the SQL command
    cursor.execute("SELECT door_id FROM doors WHERE device_id = ?", (device_id,))

    # Fetch all the rows from the executed SQL command
    rows = cursor.fetchall()

    # Prepare a list to hold the door_ids
    door_ids = [row[0] for row in rows]

    # Close the connection to the database
    conn.close()

    return door_ids

# Function to fetch a list of all active devices
def list_active_devices():
    # Create a connection to the database
    conn = sqlite3.connect(DB_NAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    # Execute the SQL command
    cursor.execute("SELECT device_id FROM devices WHERE is_active = 1")

    # Fetch all the rows from the executed SQL command
    rows = cursor.fetchall()

    # Prepare a list to hold the device_ids
    device_ids = [row[0] for row in rows]

    # Close the connection to the database
    conn.close()

    return device_ids

