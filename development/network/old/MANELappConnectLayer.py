# appConnectLayer.py
import json
import datetime
from flask import Flask, request, jsonify
import server_configuration
import hashlib
import sqlite3

app = Flask(__name__)
# vectors = []

# -------------------------------------------------------------------------- Load Testing Structure -------------------------------------------------------------------------------
# f = open('testingStructure.json')
# vectors = json.load(f)
# f.close()
# -------------------------------------------------------------------------- Load Testing Structure -------------------------------------------------------------------------------

# Opening Page


@app.route('/')
def hello_world():
    return "Welcome to the REST based backend of your locker"

# -------------------------------------------------------------------------- Get and Return Variables -------------------------------------------------------------------------------

# Get an User Object - Args = username
@app.get("/user")
def get_user():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    username = args.get("username", default = "", type = str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    conn.close()
    return json.dumps(userObject), 200

# Get User All Usernams
@app.get("/usernames")
def get_username():
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT username FROM users")
    usernamesObject = cursor.fetchall()

    if usernamesObject is None:
        return {"error": "[AppServer API] There aren't any usernames stored in the database"}, 400

    usernames = [username[0] for username in usernamesObject]

    conn.close()
    return usernames, 200

# Get User Object Password - Args = username - Return = password
@app.get("/user/password")
def get_password():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    username = args.get("username", default = "", type = str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    conn.close()

    toSendPassword = {
        "password": userObject["password_hash"]
    }

    return toSendPassword, 200

# Try to Login with given password - Args = username, loginPassword


@app.get("/user/login")
def login():
    args = request.args

    required_fields = ['username', 'loginPassword']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    tryPassword = args.get("loginPassword", default="", type=str)
    username = args.get("username", default="", type=str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    tryHashedPassword = hashlib.sha256(tryPassword.encode()).hexdigest()
    
    conn.close()
    if (tryHashedPassword == userObject["password_hash"]):
        return {"success": "[AppServer API] Login Successful"}, 200
    else:
        return {"error": "[AppServer API] Login Failed"}, 401


# Get User Object Email Address - Args = username
@app.get("/user/email")
def get_email():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    username = args.get("username", default="", type=str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    

    toSendEmail = {
        "email": userObject["email"]
    }

    conn.close()
    return json.dumps(toSendEmail), 200

# Get User Object Active Locks Array - Args = username


@app.get("/user/active_locks")
def get_active_lock_array():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400
        
    username = args.get("username", default="", type=str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    toSendActiveLocks = {
        "active_locks": userObject["active_locks"]
    }

    conn.close()
    return toSendActiveLocks, 200

# Get Array with All Unactivated Locks


@app.get("/unactivatedLocks")
def getUnactivatedLocks():
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id FROM locks")

    results = cursor.fetchall()

    locks = [result[0] for result in results]

    unactivatedLocksObject = json.dumps(locks)

    conn.close()

    if unactivatedLocksObject is None:
        return {"error": "[AppServer API] There aren't any unactivated locks stored in the database"}, 400
    else:
        return unactivatedLocksObject, 200


# Get User Object Specific Locker - Args = lockID, username
@app.get("/lock")
def lock():
    args = request.args

    required_fields = ['username', 'lockID']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    username = args.get("username", default="", type=str)
    lockID = args.get("lockID", default="", type=str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    for lock in lock_dict:
        if lock["lock_id"] == lockID:
            #Check if lock object exists
            cursor.execute(
                "SELECT username, access_level FROM lock_users WHERE lock_id=?", (lockID,))
            users = cursor.fetchall()
            if not users:
                data_users = None
            else:
                data_users = [user[0] for user in users]

            cursor.execute("SELECT * FROM active_locks WHERE lock_id = ?", (lockID,))
            lock = cursor.fetchone()

            if lock is None:
                conn.close()
                return {"error": "[AppServer API] The requested lock doesn't exist"}, 400
            else:
                lockObject = {
                    "lockID": lock[1],
                    "Name": lock[2],
                    "Location": lock[3],
                    "State": lock[4],
                    "lock_request": lock[5],
                    "appcode": lock[7]
                }

            lockObject["active_users"] = data_users
            lockObject["lastAccess"] = lock[6]
            return lockObject, 200

    conn.close()
    return {"error": "Unknown Error while fetching the lock"}, 400


# -------------------------------------------------------------------------- Update and Create Variables ----------------------------------------------------------------------------

# Add New User to the System
@app.post("/createUser")
def add_user():
    args = request.args

    required_fields = ['username', 'password',
                       'email', 'accessLevel0', 'accessPin']
    for field in required_fields:
        if field not in args:
            print("[AppServer API] Missing required field")
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    username = args.get("username", default="", type=str)
    password = args.get("password", default="", type=str)
    email = args.get("email", default="", type=str)
    access_level0 = args.get("accessLevel0", default="", type=str)
    access_pin = args.get("accessPin", default="", type=str)
    notifications = '0'

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    password_hash = hashlib.sha256(password.encode()).hexdigest()

    user_id = ""
    #generate user ID and save

    cursor.execute("INSERT INTO users (username, password_hash, email, notifications, access_level, access_pin) VALUES (?, ?, ?, ?, ?, ?)",
                   (username, password_hash, email, notifications, access_level0, access_pin))
    cursor.lastrowid

    print("Created User " + username + "with user_id" + user_id)
    conn.commit()
    conn.close()

    return user_id, 201

# Update Username of a Specific User - Args = oldUsername, newUsername
@app.post("/user/username")
def update_username():
    args = request.args

    required_fields = ['oldUsername', 'newUsername']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    oldUsername = args.get("oldUsername", default="", type=str)
    newUsername = args.get("newUsername", default="", type=str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("UPDATE users SET username=? WHERE username=?", (newUsername, oldUsername))
    cursor.execute("UPDATE lock_users SET username=? WHERE username=?", (newUsername, oldUsername))

    conn.commit()
    print(f"Username changed from {oldUsername} to {newUsername}")
    conn.close()
    return json.dumps({"success": "[AppServer API] Username Updated Successfuly"}), 200

# Update Password of a Specific User - Args = username, oldPassword, newPassword
@app.post("/user/password")
def update_password():
    args = request.args

    required_fields = ['username', 'oldPassword', 'newPassword']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    username = args.get("username", default="", type=str)
    oldPassword = args.get("oldPassword", default="", type=str)
    newPassword = args.get("newPassword", default="", type=str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    

    if userObject["password_hash"] == hashlib.sha256(oldPassword.encode()).hexdigest():
        newPasswordHash = hashlib.sha256(newPassword.encode()).hexdigest()
        cursor.execute("UPDATE users SET password_hash=? WHERE username=?", (newPasswordHash, username))
        conn.commit()
        conn.close()
        return json.dumps({"success": "[AppServer API] Password Updated Successfuly"}), 200

    else:
        conn.close()
        return {"error": "Old Password didn't match"}, 400

# Update Email Address of a Specific User - Args = username, oldEmail, newEmail
@app.post("/user/email")
def update_email():
    args = request.args

    required_fields = ['username', 'oldEmail', 'newEmail']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    username = args.get("username", default="", type=str)
    oldEmail = args.get("oldEmail", default="", type=str)
    newEmail = args.get("newEmail", default="", type=str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if locks is None:
        lock_dict = []
    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    

    if (oldEmail == str(userObject["email"])):
        cursor.execute("UPDATE users SET email=? WHERE username=?", (newEmail, username))
        conn.commit()
        print(f"Email changed for user {username}")

        conn.close()
        return newEmail, 200
    else:
        conn.close()
        return {"error": "Old Email didn't match"}, 400
# -------------------------------------------------------------------------- Lockers ----------------------------------------------------------------------------

# Allocate Locker to User - Args = username, lockID, accessLevel
@app.post("/user/allocateLock")
def allocateLock():
    args = request.args

    required_fields = ['username', 'lockID', 'accessLevel']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    username = args.get("username", default="", type=str)
    lockID = args.get("lockID", default="", type=str)
    accessLevel = args.get("accessLevel", default="", type=str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    userLocks = json.loads(json.dumps(userObject["active_locks"]))
    if userLocks is None:
        conn.close()
        return {"error": "[AppServer API] The user doesn't have allocated locks"}, 400

    for lock in userLocks:
        if lock["lock_id"] == args.get("lockID", default="", type=str):
            conn.close()
            return {"error": "[AppServer API] The Lock is already allocated"}, 400

    lock_id = args.get("lockID", default="", type=str)
    access_level = args.get("accessLevel", default="", type=str)
    username = args.get("username", default="", type=str)

    cursor.execute("INSERT INTO lock_users (lock_id, username, access_level) VALUES (?, ?, ?)", (lock_id, username, access_level))
    conn.commit()
    print(f"Lock {lock_id} allocated to user {username} with access level {access_level}")

    conn.close()
    return {"success": "Lock {lock_id} allocated to user {username} with access level {access_level}"}, 200

# Deallocate Locker to User - Args = username, lockID
@app.post("/user/deallocateLock")
def deallocateLock():
    args = request.args

    required_fields = ['username', 'lockID']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    username = args.get("username", default="", type=str)
    lockID = args.get("lockID", default="", type=str)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    username = args.get("username", default="", type=str)
    lock_id = args.get("lockID", default="", type=str)

    cursor.execute("DELETE FROM lock_users WHERE lock_id = ? AND username = ?", (lock_id, username))
    conn.commit()
    print(f"Lock {lock_id} deallocated from user {username}")
    conn.close()
    
    return {"success": "Locker Deallocated Successfully"}, 200

# Update Lock Access Level of a specific User - Args = username, lockID, newAccessLevel
@app.post("/user/lockAccessLevel")
def updateLockAccessLevel():
    args = request.args

    required_fields = ['username', 'lockID', 'newAccessLevel']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    username = args.get("username", default="", type=str)
    new_access_level = args.get("newAccessLevel", default="", type=str)
    lock_id = args.get("lockID", default="", type=str)

    cursor.execute("UPDATE lock_users SET access_level = ? WHERE username = ? AND lock_id = ?",
                   (new_access_level, username, lock_id,))

    conn.commit()
    conn.close()
    return {"success": "Access Level updated successfully"}, 200

# Update Pin of a specified Lock - Args = lockID, oldPin, newPin
@app.post("/user/updateUserPin")
def updateUserPin():
    args = request.args

    required_fields = ['username', 'oldPin', 'newPin']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400


    username = args.get("username", default="", type=int)
    oldPin = args.get("oldPin", default="", type=int)
    newPin = args.get("newPin", default="", type=int)

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()
    
    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict
        
    if (int(userObject["access_pin"]) == oldPin):
        cursor.execute("UPDATE users SET access_pin=? WHERE username=?",
                   (newPin, username,))
        conn.commit()
        conn.close()
        return toUpdateUserPin, 200
    else:
        return {"error": "The old code doesn't match"}, 400

# Update Location of a specified Lock - Args = lockID, newLocation
@app.post("/lock/updateLockLocation")
def updateLockLocation():
    args = request.args

    required_fields = ['lockID', 'newLocation']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    lock_id = args.get("lockID", default="", type=str)
    new_location = args.get("newLocation", default="", type=str)

    cursor.execute("UPDATE active_locks SET location=? WHERE lock_id=?",
                   (new_location, lock_id))

    print(f"Location updated for lock {lock_id}")
    conn.commit()
    conn.close()
    return {"success": "Locker Location Updated"}, 200

# Update Name of a specified Lock - Args = lockID, newName
@app.post("/lock/updateLockName")
def updateLockName():
    args = request.args

    required_fields = ['lockID', 'newName']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    lock_id = args.get("lockID", default="", type=str)
    new_name = args.get("newName", default="", type=str)

    cursor.execute("UPDATE active_locks SET name=? WHERE lock_id=?",
                   (new_name, lock_id))
    conn.commit()
    print(f"Name updated for lock {lock_id}")

    conn.close()
    return {"success": "Locker Name Updated"}, 200


# First Interaction - Args = username
@app.post("/firstInteraction")
def firstInteraction():
    args = request.args

    required_fields = ['username', 'appcode']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    lock_id = "Lock1"
    username = args.get("username", default="", type=str)

    cursor.execute("SELECT appcode FROM locks WHERE lock_id = ?", (lock_id,))
    appcode = cursor.fetchone()[0]
    if appcode is None:
        conn.close()
        return {"error": "[AppServer API] The requested lock doesn't exist"}, 400

    if args.get("appcode", default="", type=int) == appcode:
        conn = sqlite3.connect('users.db')
        cursor = conn.cursor()

        cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
        locks = cursor.fetchall()

        if not locks:
            lock_dict = []

        else:
            lock_dict = [{
                "lock_id": str(lock_id),
                "access_level": str(access_level)
            } for lock_id, access_level in locks]

        cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
        user = cursor.fetchone()

        if user is None:
            conn.close()
            return {"error": "[AppServer API] The requested user doesn't exist"}, 400
        else:
            userObject = {
                "username": user[1],
                "password_hash": user[2],
                "email": user[3],
                "access_level": user[4],
                "notifications": user[5],
                "access_pin": user[6]
            }

        userObject["active_locks"] = lock_dict

        access_level = args.get("accessLevel", default="", type=str)
        username = args.get("username", default="", type=str)

        for lock_id in ["Lock1", "Lock2", "Lock3", "Lock4"]:
            #Move the Lockers from nonactivated table to activated
            cursor.execute("SELECT * FROM locks WHERE lock_id = ?", (lock_id,))
            lock = cursor.fetchone()
            
            cursor.execute("INSERT INTO active_locks (lock_id, name, location, state, lock_request, user_last_access, appcode) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        (lock[1], lock[2], lock[3], lock[4], lock[5], lock[6], lock[7]))
            conn.commit()

            cursor.execute("INSERT INTO lock_users (lock_id,username,access_level) VALUES (?,?,?)", (lock[1],"master",0))
            cursor.execute("DELETE FROM locks WHERE lock_id = ?", (lock_id,))
            conn.commit()

            #Allocate Lock for User
            cursor.execute("INSERT INTO lock_users (lock_id, username, access_level) VALUES (?, ?, ?)",
                   (lock_id, username, access_level))
            conn.commit()
            print(f"Lock {lock_id} allocated to user {username} with access level {access_level}")

        conn.close()
        return {"success": "Safe-Box Activated Successfully"}, 200

    else:
        return {"error": "The AppCodes didn't match"}, 400


# Open Locker - Args = lockID
@app.post("/lock/openLocks")
def openLock():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    username = args.get("username", default="", type=str)

    cursor.execute("SELECT lock_id, access_level FROM lock_users WHERE username=?", (username,))
    locks = cursor.fetchall()

    if not locks:
        lock_dict = []

    else:
        lock_dict = [{
            "lock_id": str(lock_id),
            "access_level": str(access_level)
        } for lock_id, access_level in locks]

    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    user = cursor.fetchone()

    if user is None:
        conn.close()
        return {"error": "[AppServer API] The requested user doesn't exist"}, 400
    else:
        userObject = {
            "username": user[1],
            "password_hash": user[2],
            "email": user[3],
            "access_level": user[4],
            "notifications": user[5],
            "access_pin": user[6]
        }

    userObject["active_locks"] = lock_dict

    userLocks = json.loads(json.dumps(userObject["active_locks"]))
    if userLocks is None:
        return {"error": "[AppServer API] The user doesn't have allocated locks"}, 400

    for lock in userLocks:
        lock_id = lock["lock_id"]

        #Check if lock object exists
        cursor.execute(
            "SELECT username, access_level FROM lock_users WHERE lock_id=?", (lock_id,))
        users = cursor.fetchall()
        if not users:
            data_users = None
        else:
            data_users = [user[0] for user in users]

        cursor.execute("SELECT * FROM active_locks WHERE lock_id = ?", (lock_id,))
        lock = cursor.fetchone()

        if lock is None:
            conn.close()
            return {"error": "[AppServer API] The requested lock doesn't exist"}, 400
        else:
            lockObject = {
                "lockID": lock[1],
                "Name": lock[2],
                "Location": lock[3],
                "State": lock[4],
                "lock_request": lock[5],
                "appcode": lock[7]
            }

        lockObject["active_users"] = data_users
        lockObject["lastAccess"] = lock[6]

        cursor.execute(
            "UPDATE active_locks SET lock_request=? WHERE lock_id=?", (1, lock_id))
        conn.commit()

        #Update last access

        timestamp = datetime.datetime.now().strftime("%d-%m-%Y %H:%M:%S")

        # Get the current last_access_user string
        cursor.execute(
            "SELECT user_last_access FROM active_locks WHERE lock_id=?", (lock_id,))
        result = cursor.fetchone()

        if result is None:
            conn.close()
            return "Error: The requested lock doesn't exist", 400
        else:
            # Otherwise, append the new access at the end of the current string
            new_last_access_user = {
                "username": username,
                "time": timestamp
            }
            
        new_last_access_user = json.dumps(new_last_access_user)
        cursor.execute("UPDATE active_locks SET user_last_access=? WHERE lock_id=?",
                    (new_last_access_user, lock_id))
        conn.commit()
        
        cursor.execute("SELECT user_last_access FROM active_locks WHERE lock_id=?", (lock_id,))
        test = cursor.fetchone()
        
    conn.close()

    return {"success": "All User Lockers Opened"}, 202

# --------------------------------------------------------------------------------- Main Function -----------------------------------------------------------------------------------


if __name__ == '__main__':
    # app.run(host='0.0.0.0', port=443, ssl_context=('cert.pem', 'key.pem'), debug = True)
    target = app.run(host=server_configuration.hostName,
                     port=server_configuration.appApiPort, debug=True)
# http://127.0.0.1:5000/user/lock?username=user2&lockerID=Lock1
# http://127.0.0.1:5000/user/active_locks?username=user1

# https://blog.miguelgrinberg.com/post/running-your-flask-application-over-https
# https://www.section.io/engineering-education/implementing-totp-2fa-using-flask/
