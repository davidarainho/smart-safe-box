# appConnectLayer.py
import json
import userinfo
from flask import Flask, request, jsonify
import server_configuration
import hashlib

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

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)
    return userObject, 200

# Get User All Usernams


@app.get("/usernames")
def get_username():
    usernamesObject = userinfo.list_all_usernames()
    if usernamesObject is None:
        return {"error": "[AppServer API] There aren't any usernames stored in the database"}, 400
    return usernamesObject, 200

# Get User Object Password - Args = username - Return = password


@app.get("/user/password")
def get_password():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)

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

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)

    tryHashedPassword = hashlib.sha256(tryPassword.encode()).hexdigest()
    print(tryHashedPassword)
    print(userObject["password_hash"])

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

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)

    toSendEmail = {
        "email": userObject["email"]
    }

    return json.dumps(toSendEmail), 200

# Get User Object Active Locks Array - Args = username


@app.get("/user/active_locks")
def get_active_lock_array():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)

    toSendActiveLocks = {
        "active_locks": userObject["active_locks"]
    }

    return toSendActiveLocks, 200

# Get Array with All Unactivated Locks


@app.get("/unactivatedLocks")
def getUnactivatedLocks():
    unactivatedLocksObject = userinfo.list_unactivated_locks()
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

    lockID = args.get("lockID", default="", type=str)

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)

    lockTest = json.dumps(userObject["active_locks"])
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't have allocated locks"}, 400
    else:
        userLocks = json.loads(lockTest)

    for lock in userLocks:
        if lock["lock_id"] == lockID:
            toGetLock = {
                "lock_id": lockID
            }
            lockObject = userinfo.lock_object(json.dumps(toGetLock))
            if lockObject is None:
                return {"error": "[AppServer API] The requested lock doesn't exist"}, 400
            else:
                return lockObject, 200

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

    newUser = {
        "username": args.get("username", default="", type=str),
        "password_hash": args.get("password", default="", type=str),
        "email": args.get("email", default="", type=str),
        "notifications": 0,
        "access_level": args.get("accessLevel0", default="", type=str),
        "access_pin": args.get("accessPin", default="", type=str),
    }

    if userinfo.add_user(json.dumps(newUser)) == True:
        return newUser, 201
    else:
        print("Unknown Error while creating user")
        return {"error": "Unknown Error while creating user"}, 400

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

    usernameUpdateArguments = {
        "username": oldUsername,
        "new_username": newUsername
    }

    if userinfo.update_username_function(json.dumps(usernameUpdateArguments)) == True:
        return json.dumps({"success": "[AppServer API] Username Updated Successfuly"}), 200
        #return newUsername, 200
    else:
        return {"error": "Unknown Error while updating username"}, 400


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

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)

    if userObject["password_hash"] == hashlib.sha256(oldPassword.encode()).hexdigest():
        passwordUpdateArguments = {
            "username": username,
            "new_password": newPassword
        }

        if userinfo.update_password_function(json.dumps(passwordUpdateArguments)) == True:
            return json.dumps({"success": "[AppServer API] Password Updated Successfuly"}), 200
            #return newPassword, 200
        else:
            return {"error": "Unknown Error while updating password"}, 400

    else:
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

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)

    if (oldEmail == str(userObject["email"])):
        emailUpdateArguments = {
            "username": username,
            "new_email": newEmail
        }

        if userinfo.update_email_function(json.dumps(emailUpdateArguments)) == True:
            return newEmail, 200
        else:
            return {"error": "Unknown Error while updating email"}, 400
    else:
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

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)

    userLocks = json.loads(json.dumps(userObject["active_locks"]))
    if userLocks is None:
        return {"error": "[AppServer API] The user doesn't have allocated locks"}, 400

    for lock in userLocks:
        if lock["lock_id"] == args.get("lockID", default="", type=str):
            return {"error": "[AppServer API] The Lock is already allocated"}, 400

    toAllocateLock = {
        "lock_id": args.get("lockID", default="", type=str),
        "access_level": args.get("accessLevel", default="", type=str),
        "username": args.get("username", default="", type=str),
    }

    if userinfo.allocate_lock_for_user(json.dumps(toAllocateLock)) == True:
        return toAllocateLock, 200
    else:
        return {"error": "Unknown Error while allocating lock"}, 400

# Deallocate Locker to User - Args = username, lockID
@app.post("/user/deallocateLock")
def deallocateLock():
    args = request.args

    required_fields = ['username', 'lockID']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    toDeallocateLock = {
        "username": args.get("username", default="", type=str),
        "lock_id": args.get("lockID", default="", type=str),
    }
    
    if userinfo.deallocate_lock(json.dumps(toDeallocateLock)) == True:
        return {"success": "Locker Deallocated Successfully"}, 200
    else:
        return {"error": "Unknown Error while deallocating lock"}, 400

# Update Lock Access Level of a specific User - Args = username, lockID, newAccessLevel
@app.post("/user/lockAccessLevel")
def updateLockAccessLevel():
    args = request.args

    required_fields = ['username', 'lockID', 'newAccessLevel']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    toUpdateAccessLevel = {
        "username": args.get("username", default="", type=str),
        "new_access_level": args.get("newAccessLevel", default="", type=str),
        "lock_id": args.get("lockID", default="", type=str),
    }

    if userinfo.update_access_level(json.dumps(toUpdateAccessLevel)) == True:
        return toUpdateAccessLevel, 200
    else:
        return {"error": "Unknown Error while updating user specific access level for the specified lock"}, 400

# Update Pin of a specified Lock - Args = lockID, oldPin, newPin
@app.post("/user/updateUserPin")
def updateUserPin():
    args = request.args

    required_fields = ['username', 'oldPin', 'newPin']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    oldPin = args.get("oldPin", default="", type=int)

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)
    print(userObject)
    if (int(userObject["access_pin"]) == oldPin):
        toUpdateUserPin = {
            "username": args.get("username", default="", type=str),
            "new_access_pin": args.get("newPin", default="", type=str),
        }

        if userinfo.update_pin_app2server(json.dumps(toUpdateUserPin)) == True:
            return toUpdateUserPin, 200
        else:
            return {"error": "Unknown Error while updating lock Pin"}, 400
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

    toUpdateLocation = {
        "lock_id": args.get("lockID", default="", type=str),
        "new_location": args.get("newLocation", default="", type=str),
    }

    if userinfo.update_lock_location(json.dumps(toUpdateLocation)) == True:
        return {"success": "Locker Location Updated"}, 200
    else:
        return {"error": "Unknown Error while updating lock Location"}, 400

# Update Name of a specified Lock - Args = lockID, newName
@app.post("/lock/updateLockName")
def updateLockName():
    args = request.args

    required_fields = ['lockID', 'newName']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    toUpdateLockName = {
        "lock_id": args.get("lockID", default="", type=str),
        "new_name": args.get("newName", default="", type=str),
    }

    if userinfo.update_lock_name(json.dumps(toUpdateLockName)) == True:
        return toUpdateLockName, 200
    else:
        return {"error": "Unknown Error while updating lock name"}, 400


# First Interaction - Args = username
@app.post("/firstInteraction")
def firstInteraction():
    args = request.args

    required_fields = ['username', 'appcode']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    toGetAppcode = {
        "lock_id": "Lock1",
    }

    appcodeTest = userinfo.get_appcode(json.dumps(toGetAppcode))
    if appcodeTest is None:
        return {"error": "[AppServer API] The requested lock doesn't exist"}, 400
    else:
        appcodeObject = json.loads(appcodeTest)

    if args.get("appcode", default="", type=int) == appcodeObject["appcode"]:
        toGetUser = {
            "username": args.get("username", default="", type=str)
        }
        userTest = userinfo.user_object(json.dumps(toGetUser))
        if userTest is None:
            return {"error": "[AppServer API] The user doesn't exist"}, 400
        else:
            userObject = json.loads(userTest)

        for i in ["Lock1", "Lock2", "Lock3", "Lock4"]:
            toActivateLock = {
                "lock_id": i,
            }

            if userinfo.activate_lock(json.dumps(toActivateLock)) != True:
                return {"error": "Unknown Error while activating lock"}, 400

            toAllocateLock = {
                "lock_id": i,
                "access_level": "1",
                "username": args.get("username", default="", type=str),
            }

            if userinfo.allocate_lock_for_user(json.dumps(toAllocateLock)) != True:
                return {"error": "Unknown Error while allocating activated lock to user"}, 400

        return {"success": "Locker Activated Successfully"}, 200

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

    toGetUser = {
        "username": args.get("username", default="", type=str)
    }

    userTest = userinfo.user_object(json.dumps(toGetUser))
    if userTest is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        userObject = json.loads(userTest)

    userLocks = json.loads(json.dumps(userObject["active_locks"]))
    if userLocks is None:
        return {"error": "[AppServer API] The user doesn't have allocated locks"}, 400

    for lock in userLocks:
        toGetLock = {
            "lock_id": lock["lock_id"],
        }

        lockTest = userinfo.lock_object(json.dumps(toGetLock))
        if lockTest is None:
            return {"error": "[AppServer API] The requested lock doesn't exist"}, 400
        else:
            lockObject = json.loads(lockTest)

        toActivateLock = {
            "lock_id": lockObject["lockID"]
        }
        
        if userinfo.update_lock_request(toActivateLock) != True:
            return {"error": "[AppServer API] Error activating lock"}, 400

        toUpdateLastAccess = {
            "lock_id": lockObject["lockID"],
            "username": args.get("username", default="", type=str)
        }
        if userinfo.last_access_update(json.dumps(toUpdateLastAccess)) != True:
            return {"error": "[AppServer API] Error updating last access"}, 400

    return {"success": "All User Lockers Opened"}, 202

# --------------------------------------------------------------------------------- Main Function -----------------------------------------------------------------------------------


if __name__ == '__main__':
    # app.run(host='0.0.0.0', port=443, ssl_context=('cert.pem', 'key.pem'), debug = True)
    userinfo.initializeDatabase()
    target = app.run(host=server_configuration.hostName,
                     port=server_configuration.appApiPort, debug=True)
# http://127.0.0.1:5000/user/lock?username=user2&lockerID=Lock1
# http://127.0.0.1:5000/user/active_locks?username=user1

# https://blog.miguelgrinberg.com/post/running-your-flask-application-over-https
# https://www.section.io/engineering-education/implementing-totp-2fa-using-flask/
