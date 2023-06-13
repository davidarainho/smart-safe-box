import sqlite3
import hashlib
import secrets
import smtplib
from email.mime.text import MIMEText
import datetime
#import schedule


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
                 password_hash TEXT,
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
                 start_time TIMESTAMP,
                 end_time TIMESTAMP,
                 FOREIGN KEY(lock_id) REFERENCES locks(id),
                 FOREIGN KEY(username) REFERENCES users(id))''')

# Create a table to store password reset tokens
cursor.execute('''CREATE TABLE IF NOT EXISTS password_reset_tokens
                 (token TEXT PRIMARY KEY,
                 username TEXT,
                 FOREIGN KEY(username) REFERENCES users(id))''')


# Function to add a user to the database
def add_user(username, password, email, access_level, locks,start_time=None, end_time=None):
    # Generate the password hash
    password_hash = hashlib.sha256(password.encode()).hexdigest()
    for lock_id, access_level in locks:
        cursor.execute("SELECT COUNT(*) FROM lock_users WHERE lock_id=?", (lock_id,))
        count = cursor.fetchone()[0]
        cursor.execute("SELECT max_users FROM locks WHERE lock_id=?", (lock_id,))
        max_users = cursor.fetchone()[0]
        if count >= max_users:
            print(f"Failed to add user {username} to lock {lock_id}: maximum number of users reached!")
            continue
        
        else:
            cursor.execute("INSERT INTO users (username, password_hash, email, access_level) VALUES (?, ?, ?, ?)", (username, password_hash, email, access_level))
            user_id = cursor.lastrowid
            cursor.execute("INSERT INTO lock_users (lock_id, username, access_level, start_time, end_time) VALUES (?, ?, ?, ?, ?)", (lock_id, username, access_level, start_time, end_time))
            print(f"User {username} added successfully to lock {lock_id}!")
            conn.commit()

# Function to allocate a lock to a specific user for a time duration
def allocate_lock(username, lock_id, access_level, start_time=None, end_time=None):
    if start_time is None:
        start_time = datetime.datetime.now()
    if end_time is None:
        end_time = None  # None represents an indefinite allocation

    cursor.execute("INSERT INTO lock_users (lock_id, username, access_level, start_time, end_time) VALUES (?, ?, ?, ?, ?)",
                   (lock_id, username, access_level, start_time, end_time))
    conn.commit()
    print(f"Lock {lock_id} allocated to user {username} from {start_time} to {end_time}")

# Function to deallocate a lock from a specific user   
def deallocate_lock(username, lock_id):
    cursor.execute("DELETE FROM lock_users WHERE lock_id=? AND username=?", (lock_id, username))
    conn.commit()
    print(f"Lock {lock_id} deallocated from user {username}")
    
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
    cursor.execute("SELECT username, start_time, end_time FROM lock_users WHERE lock_id=?", (lock_id,))
    users = cursor.fetchall()
    users = [user[0] for user in users]
    return users

# Function to get all locks associated with a specific user
def get_locks_for_user(username):
    cursor.execute("SELECT lock_id, start_time, end_time FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()
    locks = [lock[0] for lock in locks]
    return locks

# Function to get the state of a specific lock
def get_lock_state(lock_id):
    cursor.execute("SELECT state FROM locks WHERE lock_id=?", (lock_id,))
    state = cursor.fetchone()[0]
    return state

# Function to update the username for a user
def update_username_function(username, new_username):
    cursor.execute("UPDATE users SET username=? WHERE username=?", (new_username, username))
    cursor.execute("UPDATE lock_users SET username=? WHERE username=?", (new_username, username))
    conn.commit()
    print(f"Username changed from {username} to {new_username}")
    return

# Function to update the password for a user
def update_password_function(username):
    def update_password(new_password):
        # Generate the new password hash
        new_password_hash = hashlib.sha256(new_password.encode()).hexdigest()
        cursor.execute("UPDATE users SET password_hash=? WHERE username=?", (new_password_hash, username))
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
    
# Function to send an email
def send_email(subject, body, recipient):
    # Configure the email settings
    sender = 'your_email@example.com'
    smtp_server = 'smtp.example.com'
    smtp_port = 587
    username = 'your_email_username'
    password = 'your_email_password'

    # Create the email message
    message = MIMEText(body)
    message['Subject'] = subject
    message['From'] = sender
    message['To'] = recipient

    try:
        # Connect to the SMTP server
        server = smtplib.SMTP(smtp_server, smtp_port)
        server.starttls()
        server.login(username, password)

        # Send the email
        server.sendmail(sender, [recipient], message.as_string())
        server.quit()

        print("Email sent successfully")
    except Exception as e:
        print("Failed to send email:", str(e))
        
# Function to generate a password reset token
def generate_reset_token(username, email):
    token = secrets.token_urlsafe(32)
    cursor.execute("INSERT INTO password_reset_tokens (token, username) VALUES (?, ?)", (token, username))
    conn.commit()

    # Send the password reset email
    subject = "Password Reset"
    body = f"Please click on the following link to reset your password: https://yourwebsite.com/reset?token={token}"
    recipient = email

    send_email(subject, body, recipient)

    return token

# Function to reset the password using the reset token
def reset_password(token, new_password):
    # Hash the new password
    hashed_password = hashlib.sha256(new_password.encode()).hexdigest()
    
    # Check if the token exists in the database
    cursor.execute("SELECT username FROM password_reset_tokens WHERE token=?", (token,))
    result = cursor.fetchone()
    if result is None:
        print("Invalid or expired password reset token")
        return
    
    username = result[0]
    
    # Update the password for the user
    cursor.execute("UPDATE users SET password=? WHERE username=?", (hashed_password, username))
    
    # Delete the reset token from the table
    cursor.execute("DELETE FROM password_reset_tokens WHERE token=?", (token,))
    
    conn.commit()
    print("Password reset successfully")

# Function to update the name for a lock
def update_lock_name(lock_id, new_name, username):
    # Check if the user has access level 0
    cursor.execute("SELECT access_level FROM users WHERE username=?", (username,))
    access_level = cursor.fetchone()[0]
    if access_level != 0:
        print("Access denied. Only users with access level 0 can perform this action.")
        return

    cursor.execute("UPDATE locks SET name=? WHERE lock_id=?", (new_name, lock_id))
    conn.commit()
    print(f"Name updated for lock {lock_id}")


# Function to update the location for a lock
def update_lock_location(lock_id, new_location, username):
    # Check if the user has access level 0
    cursor.execute("SELECT access_level FROM users WHERE username=?", (username,))
    access_level = cursor.fetchone()[0]
    if access_level != 0:
        print("Access denied. Only users with access level 0 can perform this action.")
        return

    cursor.execute("UPDATE locks SET location=? WHERE lock_id=?", (new_location, lock_id))
    conn.commit()
    print(f"Location updated for lock {lock_id}")

# Function to update the max number of users for a lock
def update_lock_max_users(lock_id, new_max_users, username):
    # Check if the user has access level 0
    cursor.execute("SELECT access_level FROM users WHERE username=?", (username,))
    access_level = cursor.fetchone()[0]
    if access_level != 0:
        print("Access denied. Only users with access level 0 can perform this action.")
        return

    cursor.execute("UPDATE locks SET max_users=? WHERE lock_id=?", (new_max_users, lock_id))
    conn.commit()
    print(f"Max users updated for lock {lock_id}")

# Function to update the ID for a lock
def update_lock_id(old_lock_id, new_lock_id, username):
    # Check if the user has access level 0
    cursor.execute("SELECT access_level FROM users WHERE username=?", (username,))
    access_level = cursor.fetchone()[0]
    if access_level != 0:
        print("Access denied. Only users with access level 0 can perform this action.")
        return

    cursor.execute("UPDATE locks SET lock_id=? WHERE lock_id=?", (new_lock_id, old_lock_id))
    cursor.execute("UPDATE lock_users SET lock_id=? WHERE lock_id=?", (new_lock_id, old_lock_id))
    conn.commit()
    print(f"ID updated for lock {old_lock_id}")
    
# Function to allocate a lock for a specific user
def allocate_lock_for_user(lock_id, username, access_level):
    # Check if the user has access level 0
    cursor.execute("SELECT access_level FROM users WHERE username=?", (username,))
    access_level = cursor.fetchone()[0]
    if access_level != 0:
        print("Access denied. Only users with access level 0 can perform this action.")
        return
    cursor.execute("SELECT COUNT(*) FROM lock_users WHERE lock_id=?", (lock_id,))
    count = cursor.fetchone()[0]
    cursor.execute("SELECT max_users FROM locks WHERE lock_id=?", (lock_id,))
    max_users = cursor.fetchone()[0]
    if count >= max_users:
        print(f"Failed to allocate lock {lock_id} to user {username}: maximum number of users reached!")
    else:
        cursor.execute("INSERT INTO lock_users (lock_id, username, access_level) VALUES (?, ?, ?)", (lock_id, username, access_level))
        conn.commit()
        print(f"Lock {lock_id} allocated to user {username}")
        
# Function to allocate a lock with a specific user for a time duration
def allocate_lock_with_user(lock_id, username, duration):
    # Check if the user has access level 1
    cursor.execute("SELECT access_level FROM users WHERE username=?", (username,))
    access_level = cursor.fetchone()[0]
    if access_level != 1:
        print("Access denied. Only users with access level 1 can perform this action.")
        return

    # Check if the lock exists
    cursor.execute("SELECT COUNT(*) FROM locks WHERE lock_id=?", (lock_id,))
    count = cursor.fetchone()[0]
    if count == 0:
        print(f"Lock {lock_id} does not exist.")
        return

    # Check if the user exists
    cursor.execute("SELECT COUNT(*) FROM users WHERE username=?", (username,))
    count = cursor.fetchone()[0]
    if count == 0:
        print(f"User {username} does not exist.")
        return

    # Check if the lock already allocated to the user
    cursor.execute("SELECT COUNT(*) FROM lock_users WHERE lock_id=? AND username=?", (lock_id, username))
    count = cursor.fetchone()[0]
    if count > 0:
        print(f"Lock {lock_id} is already allocated to user {username}.")
        return

    # Check if the lock has available slots for users
    cursor.execute("SELECT COUNT(*) FROM lock_users WHERE lock_id=?", (lock_id,))
    count = cursor.fetchone()[0]
    cursor.execute("SELECT max_users FROM locks WHERE lock_id=?", (lock_id,))
    max_users = cursor.fetchone()[0]
    if count >= max_users:
        print(f"Lock {lock_id} has reached the maximum number of users.")
        return

    # Allocate the lock to the user
    current_time = datetime.datetime.now()
    end_time = current_time + datetime.timedelta(minutes=duration)
    cursor.execute("INSERT INTO lock_users (lock_id, username, access_level, start_time, end_time) VALUES (?, ?, ?, ?, ?)",
                   (lock_id, username, 2, current_time, end_time))
    conn.commit()
    print(f"Lock {lock_id} allocated to user {username} for {duration} minutes.")

# Function to deallocate expired locks
def deallocate_expired_locks():
    current_time = datetime.datetime.now()
    cursor.execute("SELECT lock_id, username FROM lock_users WHERE end_time <= ?", (current_time,))
    expired_locks = cursor.fetchall()
    for lock_id, username in expired_locks:
        cursor.execute("DELETE FROM lock_users WHERE lock_id=? AND username=?", (lock_id, username))
        print(f"Lock {lock_id} deallocated from user {username}.")
    conn.commit()
    
#Schedule the deallocation task to run every minute
#schedule.every(1).minutes.do(deallocate_expired_locks)

# Run the scheduler in the background
#while True:
#    schedule.run_pending()
#    time.sleep(1)

# Close the connection
conn.close()