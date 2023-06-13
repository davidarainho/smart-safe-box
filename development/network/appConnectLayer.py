# appConnectLayer.py
import json
from flask import Flask, request, jsonify
from OpenSSL import SSL

app = Flask(__name__)
users = []

# -------------------------------------------------------------------------- Load Testing Structure -------------------------------------------------------------------------------
f = open('testingStructure.json')
users = json.load(f)
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

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            return jsonify(users[i])

# Get User Object Username - Args = username
@app.get("/user/username")
def get_username():
    args = request.args

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            return jsonify(users[i].get("username"))
    
# Get User Object Password - Args = username
@app.get("/user/password")
def get_password():
    args = request.args

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            return jsonify(users[i].get("password"))

# Get User Object Email Address - Args = username
@app.get("/user/email")
def get_email():
    args = request.args

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            return jsonify(users[i].get("email"))

# Get User Object Active Locks Array - Args = username
@app.get("/user/active_locks")
def get_active_lock_array():
    args = request.args

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            return jsonify(users[i].get("active_locks"))
        
# Get User Object Specific Lcoker - Args = username, lockerID
@app.get("/user/lock")
def lock():
    args = request.args

    for i in range(len(users)):
        if(users[i].get("username") == args.get("username", default = "", type = str)):
            for j in range(len(users[i].get("active_locks"))):
                if(users[i].get("active_locks")[j].get("lockID") == args.get("lockerID", default = "", type = str)):
                    return jsonify(users[i].get("active_locks")[j])

# -------------------------------------------------------------------------- Update and Create Variables ----------------------------------------------------------------------------

# Add New User to the System
@app.post("/user")
def add_user():
    if request.is_json:
        newUser = request.get_json()
        users.append(newUser)
        return newUser, 201
    return {"error": "Request must be JSON"}, 415

# Update Username of a Specific User - Args = oldUsername, newUsername
@app.post("/user/username")
def update_username():
    args = request.args

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

    if request.is_json:
        newEmail = args.get("newEmail", default = "", type = str)
        for i in range(len(users)):
            if(users[i].get("email") == args.get("oldEmail", default = "", type = str)):
                users[i].update({"username": newEmail})
        return newEmail, 201
    return {"error": "Request must be JSON"}, 415

# Add Locker to User - Args = username, lock
@app.post("/user/openLock")
def addLock():
    args = request.args

    if request.is_json:
        newLock = request.get_json()
        for i in range(len(users)):
            if(users[i].get("email") == args.get("oldEmail", default = "", type = str)):
                users[i].get("active_locks").append(newLock)
        return newLock, 201
    return {"error": "Request must be JSON"}, 415

# Open Locker - Args = username, lockID
@app.post("/user/openLock")
def openLock():
    args = request.args

    if request.is_json:
        for i in range(len(users)):
            if(users[i].get("username") == args.get("username", default = "", type = str)):
                print("I just don't want errors")
                # Call open SafeBox Function
        return "open", 201
    return {"error": "Request must be JSON"}, 415

# --------------------------------------------------------------------------------- Main Function -----------------------------------------------------------------------------------

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=443, ssl_context=('cert.pem', 'key.pem'), debug = True)

#http://127.0.0.1:5000/user/lock?username=user2&lockerID=Lock1
#http://127.0.0.1:5000/user/active_locks?username=user1

#https://blog.miguelgrinberg.com/post/running-your-flask-application-over-https
#https://www.section.io/engineering-education/implementing-totp-2fa-using-flask/