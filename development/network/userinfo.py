import sqlite3


#Create a connection with a remote server
#conn = sqlite3.connect('http://<REMOTE_HOST>/users.db')

# Create a connection to the database
conn = sqlite3.connect('users.db')

# Create a cursor object
cursor = conn.cursor()

# Create a table to store user information if it doesn't already exist
cursor.execute('''CREATE TABLE IF NOT EXISTS users
                 (id INTEGER PRIMARY KEY,
                 username TEXT,
                 password TEXT,
                 email TEXT,
                 access_level INTEGER)''')

# Create a table to store lock information if it doesn't already exist
cursor.execute('''CREATE TABLE IF NOT EXISTS locks
                 (id INTEGER PRIMARY KEY,
                 lock_id INTEGER,
                 name TEXT,
                 location TEXT,
                 state INTEGER,
                 max_users INTEGER)''')

# Create a table to store the relationship between locks and users
cursor.execute('''CREATE TABLE IF NOT EXISTS lock_users
                 (lock_id INTEGER,
                 username TEXT,
                 access_level INTEGER,
                 FOREIGN KEY(lock_id) REFERENCES locks(id),
                 FOREIGN KEY(username) REFERENCES users(id))''')

# Function to add a user to the database
def add_user(username, password, email, access_level, locks):
    for lock_id, access_level in locks:
        cursor.execute("SELECT COUNT(*) FROM lock_users WHERE lock_id=?", (lock_id,))
        count = cursor.fetchone()[0]
        cursor.execute("SELECT max_users FROM locks WHERE lock_id=?", (lock_id,))
        max_users = cursor.fetchone()[0]
        if count >= max_users:
            print(f"Failed to add user to lock {lock_id}: maximum number of users reached!")
            continue
        
        else:
            cursor.execute("INSERT INTO users (username, password, email, access_level) VALUES (?, ?, ?, ?)", (username, password, email, access_level))
            user_id = cursor.lastrowid
            cursor.execute("INSERT INTO lock_users (lock_id, username, access_level) VALUES (?, ?, ?)", (lock_id, username, access_level))
            print("User added successfully!")
            conn.commit()
    

# Function to remove a user from the database
def remove_user(username):
    cursor.execute("DELETE FROM users WHERE username=?", (username,))
    cursor.execute("DELETE FROM lock_users WHERE username=?", (username,))
    conn.commit()
    print(f"User {username} and their relationships removed successfully!")

def remove_lock(lock_id):
    cursor.execute("DELETE FROM locks WHERE id=?", (lock_id,))
    cursor.execute("DELETE FROM lock_users WHERE lock_id=?", (lock_id,))
    conn.commit()
    print(f"Lock {lock_id} and its relationships removed successfully!")
    
# Function to get all users associated with a specific lock
def get_users_for_lock(lock_id):
    cursor.execute("SELECT username FROM lock_users WHERE lock_id=?", (lock_id,))
    users = cursor.fetchall()
    users = [user[0] for user in users]
    return users


# Function to get all locks associated with a specific user
def get_locks_for_user(username):
    cursor.execute("SELECT lock_id FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()
    locks = [lock[0] for lock in locks]
    return locks


# Function to get the state of a specific lock
def get_lock_state(lock_id):
    cursor.execute("SELECT state FROM locks WHERE lock_id=?", (lock_id,))
    state = cursor.fetchone()[0]
    return state

# Function to update the username for a user
def update_username_function(username):
    def update_username(new_username):
        cursor.execute("UPDATE users SET username=? WHERE username=?", (new_username, username))
        cursor.execute("UPDATE lock_users SET username=? WHERE username=?", (new_username, username))
        conn.commit()
        print(f"Username changed from {username} to {new_username}")
    return update_username

# Function to update the password for a user
def update_password_function(username):
    def update_password(new_password):
        cursor.execute("UPDATE users SET password=? WHERE username=?", (new_password, username))
        conn.commit()
        print(f"Password changed for user {username}")
    return update_password

# Function to update the e-mail for a user
def update_email_function(username):
    def update_email(new_email):
        cursor.execute("UPDATE users SET email=? WHERE username=?", (new_email, username))
        conn.commit()
        print(f"Email changed for user {username}")
    return update_email
        

# Test the add_user function with a lock that allows only 2 users
cursor.execute("INSERT INTO locks (lock_id, name, location, state, max_users) VALUES (?, ?, ?, ?, ?)", (123, "Lock A", "Location A", 1, 2))
lock_id = 123

add_user("user1", "password1", "user1@example.com", 1, [(lock_id, 1)]) # Should be successful
add_user("user2", "password2", "user2@example.com", 2, [(lock_id, 2)]) # Should be successful
add_user("user3", "password3", "user3@example.com", 3, [(lock_id, 3)]) # Should fail due to maximum number of users reached


# Assuming you have a user called "user1"
update_username = update_username_function("user1")
update_password = update_password_function("user1")
update_email = update_email_function("user1")

# Change the username
update_username("johnd")

# Change the password
update_password("newpassword")

# Change the email
update_email("johnd@example.com")

# Testing for fetch list users, list locks and lock state function
usern = 'johnd'
locks_total = get_locks_for_user(usern)
print(f"Locks for user with username {usern}: {locks_total}")

lock_id = 123
users_total = get_users_for_lock(lock_id)
print(f"Users for lock {lock_id}: {users_total}")

state = get_lock_state(lock_id)
if(state == 0):
    response = "Open"
else:
    response = "Closed"
print(f"The lock {lock_id} is {response}")


# Test the remove_user function
remove_user("johnd")
remove_user("user2")
remove_user("user3")

# Close the connection
conn.close()

