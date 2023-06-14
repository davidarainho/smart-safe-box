# appConnectLayer.py
import json
import userinfo
from flask import Flask, request, jsonify
import server_configuration
import hashlib

app = Flask(__name__)
#vectors = []

# -------------------------------------------------------------------------- Load Testing Structure -------------------------------------------------------------------------------
#f = open('testingStructure.json')
#vectors = json.load(f)
#f.close()
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

    toGetUserDic = {
        "username": args.get("username", default = "", type = str)
    }

    return userinfo.user_object(json.dumps(toGetUserDic))

# Get User All Usernams
@app.get("/usernames")
def get_username():
    return userinfo.list_all_usernames()
    
# Get User Object Password - Args = username - Return = password
@app.get("/user/password")
def get_password():
    args = request.args
    
    toGetUser = {
        "username": args.get("username", default = "", type = str)
    }

    userObject = json.loads(userinfo.user_object(json.dumps(toGetUser)))

    toSendPassword = {
        "password": userObject["password_hash"]
    }

    return toSendPassword

# Try to Login with given password - Args = username, loginPassword
@app.get("/user/login")
def login():
    args = request.args

    tryPassword = args.get("loginPassword", default = "", type = str)
    
    toGetUser = {
        "username": args.get("username", default = "", type = str)
    }

    userObject = json.loads(userinfo.user_object(json.dumps(toGetUser)))

    tryHashedPassword = hashlib.sha256(tryPassword.encode()).hexdigest()

    if(tryHashedPassword == userObject["password"]):
        return {"login": "true"}, 201
    else:
        return {"login": "false"}, 405
    

# Get User Object Email Address - Args = username
@app.get("/user/email")
def get_email():
    args = request.args
    
    toGetUser = {
        "username": args.get("username", default = "", type = str)
    }

    userObject = json.loads(userinfo.user_object(json.dumps(toGetUser)))

    toSendEmail = {
        "email": userObject["email"]
    }

    return toSendEmail

# Get User Object Active Locks Array - Args = username
@app.get("/user/active_locks")
def get_active_lock_array():
    args = request.args
    
    toGetUser = {
        "username": args.get("username", default = "", type = str)
    }

    userObject = json.loads(userinfo.user_object(json.dumps(toGetUser)))

    toSendActiveLocks = {
        "active_locks": userObject["active_locks"]
    }

    return toSendActiveLocks

@app.get("/unactivatedLocks")
def getUnactivatedLocks():
    return userinfo.list_unactivated_locks();
        
# Get User Object Specific Locker - Args = lockID, username
@app.get("/lock")
def lock():
    args = request.args

    lockID = args.get("lockID", default = "", type = str)
    
    toGetUser = {
        "username": args.get("username", default = "", type = str)
    }

    userObject = json.loads(userinfo.user_object(json.dumps(toGetUser)))
    userLocks = userObject["active_locks"]

    for lock in userLocks:
        if lock == lockID:
            toGetLock = {
                "lock_id": lockID
            }
            return userinfo.lock_object(json.dumps(toGetLock))

    return {"error": "Lock not found"}, 415

    

# -------------------------------------------------------------------------- Update and Create Variables ----------------------------------------------------------------------------

# Add New User to the System
@app.post("/createUser")
def add_user():
    args = request.args

    newUser = {
        "username": args.get("username", default = "", type = str),
        "password": args.get("password", default = "", type = str),
        "email": args.get("email", default = "", type = str),
        "notifications": 0,
        "access_level": args.get("accessLevel0", default = "", type = str),
    }

    userinfo.add_user(json.dumps(newUser))

    return newUser, 201

# Update Username of a Specific User - Args = oldUsername, newUsername
@app.post("/user/username")
def update_username():
    args = request.args

    oldUsername = args.get("oldUsername", default = "", type = str)
    newUsername = args.get("newUsername", default = "", type = str)

    usernameUpdateArguments = {
        "username": oldUsername,
        "new_username": newUsername
    }

    userinfo.update_username_function(json.dumps(usernameUpdateArguments))
    return newUsername, 201

# Update Password of a Specific User - Args = username, oldPassword, newPassword
@app.post("/user/password")
def update_password():
    args = request.args

    username = args.get("username", default = "", type = str)

    oldPassword = args.get("oldPassword", default = "", type = str)
    newPassword = args.get("newPassword", default = "", type = str)

    passwordUpdateArguments = {
        "username": username,
        "oldPassword": oldPassword,
        "newPassword": newPassword
    }
    
    userinfo.update_password_function(json.dumps(passwordUpdateArguments))
    return newPassword, 201

# Update Email Address of a Specific User - Args = username, oldEmail, newEmail
@app.post("/user/email")
def update_email():
    args = request.args

    username = args.get("username", default = "", type = str)

    oldEmail = args.get("oldEmail", default = "", type = str)
    newEmail = args.get("newEmail", default = "", type = str)
    
    toGetUser = {
        "username": args.get("username", default = "", type = str)
    }

    userObject = json.loads(userinfo.user_object(json.dumps(toGetUser)))

    if(oldEmail == str(userObject["email"])):
        emailUpdateArguments = {
            "username": username,
            "new_email": newEmail
        }

        userinfo.update_email_function(json.dumps(emailUpdateArguments))
        return newEmail, 201

#-------------------------------------------------------------------------- Lockers ----------------------------------------------------------------------------

# Allocate Locker to User - Args = username, lockID, accessLevel
@app.post("/user/allocateLock")
def allocateLock():
    args = request.args

    toAllocateLock = {
        "lock_id": args.get("lockId", default = "", type = str),
        "access_level": args.get("accessLevel", default = "", type = str),
        "username": args.get("username", default = "", type = str),
    }
    
    userinfo.allocate_lock_for_user(json.dumps(toAllocateLock))
    return toAllocateLock, 201

# Deallocate Locker to User - Args = username, lockID
@app.post("/user/deallocateLock")
def deallocateLock():
    args = request.args

    toDeallocateLock = {
        "username": args.get("username", default = "", type = str),
        "lock_id": args.get("lockId", default = "", type = str),
    }

    userinfo.deallocate_lock(json.dumps(toDeallocateLock))
    return {"success": "Locker Deallocated Successfully"}, 201

# Update Lock Access Level of a specific User - Args = username, lockID, newAccessLevel
@app.post("/user/lockAccessLevel")
def updateLockAccessLevel():
    args = request.args

    toUpdateAccessLevel = {
        "username": args.get("username", default = "", type = str),
        "new_acess_level": args.get("newAccessLevel", default = "", type = str),
        "lock_id": args.get("lockId", default = "", type = str),
    }

    userinfo.update_access_level(json.dumps(toUpdateAccessLevel))
    return toUpdateAccessLevel, 201

# Update Pin of a specified Lock - Args = lockID, oldPin, newPin
@app.post("/lock/updateLockPin")
def updateLockPin():
    args = request.args

    oldPin = args.get("oldPin", default = "", type = int)

    toGetLock = {
        "lock_id": args.get("lockID", default = "", type = str),
    }

    checkLock = userinfo.lock_object(json.dumps(toGetLock))

    if(int(checkLock["pinLock"]) == oldPin):
        toUpdateLockPin = {
            "lock_id": args.get("lockId", default = "", type = str),
            "new_access_pin": args.get("newPin", default = "", type = str),
        }

        userinfo.update_access_level(json.dumps(toUpdateLockPin))
        return toUpdateLockPin, 201

# Update Location of a specified Lock - Args = lockID, newLocation
@app.post("/lock/updateLockLocation")
def updateLockLocation():
    args = request.args

    toUpdateLocation = {
        "lock_id": args.get("lockId", default = "", type = str),
        "new_location": args.get("newLocation", default = "", type = str),
    }
    
    userinfo.update_lock_location(json.dumps(toUpdateLocation))
    return {"success": "Locker Location Updated"}, 201

# Update Name of a specified Lock - Args = lockID, newName
@app.post("/lock/updateLockName")
def updateLockName():
    args = request.args

    toUpdateLockName = {
        "lock_id": args.get("lockID", default = "", type = str),
        "new_name": args.get("newName", default = "", type = str),
    }
    
    userinfo.update_lock_name(json.dumps(toUpdateLockName))
    return toUpdateLockName, 201

# First Interaction - Args = username, lockID, accessLevel
@app.post("/firstInteraction")
def firstInteraction():
    args = request.args

    toActivateLock = {
        "lock_id": args.get("lockID", default = "", type = str),
    }
    userinfo.activate_lock(json.dumps(toActivateLock))

    toAllocateLock = {
                    "lock_id": args.get("lockID", default = "", type = str),
                    "access_level": args.get("accessLevel", default = "", type = str),
                    "username": args.get("username", default = "", type = str),
    }

    userinfo.allocate_lock_for_user(json.dumps(toAllocateLock))
    return {"success": "Locker Activated Successfully"}, 201
    


# Open Locker - Args = lockID
@app.post("/lock/openLock")
def openLock():
    args = request.args

    locks = vectors.get("activatedLocks")

    if request.is_json:
        for i in range(len(locks)):
            if(locks[i].get("lockID") == args.get("lockID", default = "", type = str)):
                locks[i].update({"State": 1})
                #Function to Open Lock
                return {"success": "Locker Opened Successfully"}, 201
    return {"error": "Request must be JSON"}, 415

# --------------------------------------------------------------------------------- Main Function -----------------------------------------------------------------------------------

if __name__ == '__main__':
    # app.run(host='0.0.0.0', port=443, ssl_context=('cert.pem', 'key.pem'), debug = True)
    userinfo.initializeDatabase()
    target=app.run(host=server_configuration.hostName, port=server_configuration.appApiPort, debug = True)
#http://127.0.0.1:5000/user/lock?username=user2&lockerID=Lock1
#http://127.0.0.1:5000/user/active_locks?username=user1

#https://blog.miguelgrinberg.com/post/running-your-flask-application-over-https
#https://www.section.io/engineering-education/implementing-totp-2fa-using-flask/