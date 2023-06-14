# appConnectLayer.py
import json
from flask import Flask, request, jsonify
from OpenSSL import SSL

app = Flask(__name__)
vectors = []

# -------------------------------------------------------------------------- Load Testing Structure -------------------------------------------------------------------------------
f = open('testingStructure.json')
vectors = json.load(f)
f.close()
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

    users = vectors.get("user")

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            return jsonify(users[i])

# Get User All Usernams
@app.get("/usernames")
def get_username():
    users = vectors.get("users")

    usernamesList = [obj['username'] for obj in users]

    return jsonify(usernamesList)
    
# Get User Object Password - Args = username
@app.get("/user/password")
def get_password():
    args = request.args
    
    users = vectors.get("users")

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            return jsonify(users[i].get("password"))

# Get User Object Email Address - Args = username
@app.get("/user/email")
def get_email():
    args = request.args
    
    users = vectors.get("users")

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            return jsonify(users[i].get("email"))

# Get User Object Active Locks Array - Args = username
@app.get("/user/active_locks")
def get_active_lock_array():
    args = request.args
    
    users = vectors.get("users")

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            return jsonify(users[i].get("active_locks"))
        
# Get User Object Specific Locker - Args = lockID, accessLevel
@app.get("/lock")
def lock():
    args = request.args
    locks = vectors.get("activatedLocks")

    if args.get("accessLevel", type=int) == 0:
        for i in range(len(locks)):
            if(locks[i].get("lockID") == args.get("lockID", default = "", type = str)):
                return jsonify(locks[i])

# -------------------------------------------------------------------------- Update and Create Variables ----------------------------------------------------------------------------

# Add New User to the System
@app.post("/user")
def add_user():
    users = vectors.get("users")

    args = request.args

    if request.is_json:
        newUser = {
            "username": args.get("username", default = "", type = str),
            "password": args.get("password", default = "", type = str),
            "email": args.get("email", default = "", type = str),
            "accessLevel0": args.get("accessLevel0", default = "", type = int),
            "active_locks": []
        }

        users.append(newUser)

        return newUser, 201
    return {"error": "Request must be JSON"}, 415

# Update Username of a Specific User - Args = oldUsername, newUsername
@app.post("/user/username")
def update_username():
    args = request.args

    users = vectors.get("users")

    if request.is_json:
        newUsername = args.get("newUsername", default = "", type = str)
        for i in range(len(users)):
            if(users[i].get("username") == args.get("oldUsername", default = "", type = str)):
                users[i].update({"username": newUsername})
        return newUsername, 201
    return {"error": "Request must be JSON"}, 415

# Update Password of a Specific User - Args = username, oldPassword, newPassword
@app.post("/user/password")
def update_password():
    args = request.args

    users = vectors.get("users")

    if request.is_json:
        newPassword = args.get("newPassword", default = "", type = str)
        for i in range(len(users)):
            if(users[i].get("password") == args.get("oldPassword", default = "", type = str)):
                users[i].update({"username": newPassword})
        return newPassword, 201
    return {"error": "Request must be JSON"}, 415

# Update Email Address of a Specific User - Args = username, oldEmail, newEmail
@app.post("/user/email")
def update_email():
    args = request.args

    users = vectors.get("users")

    if request.is_json:
        newEmail = args.get("newEmail", default = "", type = str)
        for i in range(len(users)):
            if(users[i].get("email") == args.get("oldEmail", default = "", type = str)):
                users[i].update({"username": newEmail})
        return newEmail, 201
    return {"error": "Request must be JSON"}, 415

#-------------------------------------------------------------------------- Lockers ----------------------------------------------------------------------------

# Add Locker to User - Args = username, lockID
@app.post("/user/addLock")
def addLock():
    args = request.args

    users = vectors.get("users")

    toAddLock = {
                    "lockId": args.get("lockId", default = "", type = str),
                    "statusUser": args.get("accessLevel", default = "", type = str),
                }

    if request.is_json:
        for i in range(len(users)):
            if(users[i].get("username") == args.get("username", default = "", type = str)):
                users[i].get("active_locks").append(toAddLock)
        return toAddLock, 201
    return {"error": "Request must be JSON"}, 415

# Remove Locker to User - Args = username, lockID
@app.post("/user/removeLock")
def addLock():
    args = request.args

    users = vectors.get("users")

    if request.is_json:
        for i in range(len(users)):
            if(users[i].get("username") == args.get("username", default = "", type = str)):
                users[i].get("active_locks").remove(args.get("lockID", default = "", type = str))
        return {"success": "Locker Removed Successfully"}, 201
    return {"error": "Request must be JSON"}, 415

# Update Lock Access Level of a specific User - Args = username, lockID, newAccessLevel
@app.post("/user/lockAccessLevel")
def updateLockAccessLevel():
    args = request.args

    users = vectors.get("users")

    if request.is_json:
        newAccessLevel = args.get("newAccessLevel", default = "", type = int)
        for i in range(len(users)):
            if(users[i].get("username") == args.get("username", default = "", type = str)):
                for j in range(len(users[i].get("active_locks"))):
                    if(users[i].get("active_locks")[j].get("lockID") == args.get("lockID", default = "", type = str)):
                        users[i].get("active_locks")[j].update({"accessLevel": newAccessLevel})
        return newAccessLevel, 201
    return {"error": "Request must be JSON"}, 415

# Update Pin of a specified Lock - Args = lockID, oldPin, newPin
@app.post("/lockPin")
def updateLockPin():
    args = request.args

    locks = vectors.get("activatedLocks")

    if request.is_json:
        newPin = args.get("newPin", default = "", type = int)
        for i in range(len(locks)):
            if(locks[i].get("lockID") == args.get("lockID", default = "", type = str)):
                if(locks[i].get("pinLock") == args.get("oldPin", default = "", type = int)):
                    locks[i].update({"pin": newPin})
        return newPin, 201
    return {"error": "Request must be JSON"}, 415

# Update Location of a specified Lock - Args = lockID, newLocation
@app.post("/lockLocation")
def updateLockLocation():
    args = request.args

    locks = vectors.get("activatedLocks")

    if request.is_json:
        newLocation = args.get("newLocation", default = "", type = str)
        for i in range(len(locks)):
            if(locks[i].get("lockID") == args.get("lockID", default = "", type = str)):
                locks[i].update({"location": newLocation})
        return newLocation, 201
    return {"error": "Request must be JSON"}, 415

# Update Name of a specified Lock - Args = lockID, newName
@app.post("/lockName")
def updateLockName():
    args = request.args

    locks = vectors.get("activatedLocks")

    if request.is_json:
        newName = args.get("newName", default = "", type = str)
        for i in range(len(locks)):
            if(locks[i].get("lockID") == args.get("lockID", default = "", type = str)):
                locks[i].update({"name": newName})
        return newName, 201
    return {"error": "Request must be JSON"}, 415

# First Interaction - Args = username, lockID, accessLevel
@app.post("/firstInteraction")
def firstInteraction():
    args = request.args

    unactivatedLocks = vectors.get("activatedLocks")
    activatedLocks = vectors.get("activatedLocks")
    users = vectors.get("users")

    if request.is_json:
        for i in range(len(unactivatedLocks)):
            if(unactivatedLocks[i].get("lockID") == args.get("lockID", default = "", type = str)):
                toActivateLock = unactivatedLocks[i]
                activatedLocks.append(toActivateLock)
                unactivatedLocks.remove(toActivateLock)

    toAddLock = {
                    "lockId": toActivateLock.get("lockID"),
                    "statusUser": args.get("accessLevel", default = "", type = str),
                }

    if request.is_json:
        for i in range(len(users)):
            if(users[i].get("username") == args.get("username", default = "", type = str)):
                users[i].get("active_locks").append(toAddLock)

    return {"success": "Locker Activated Successfully"}, 201


# Open Locker - Args = lockID
@app.post("/user/openLock")
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
    app.run(host='0.0.0.0', port=443, ssl_context=('cert.pem', 'key.pem'), debug = True)

#http://127.0.0.1:5000/user/lock?username=user2&lockerID=Lock1
#http://127.0.0.1:5000/user/active_locks?username=user1

#https://blog.miguelgrinberg.com/post/running-your-flask-application-over-https
#https://www.section.io/engineering-education/implementing-totp-2fa-using-flask/