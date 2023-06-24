# appConnectLayer.py
import json
import NEW_userinfo as userinfo
from flask import Flask, request, jsonify
import server_configuration
import hashlib
import sqlite3

app = Flask(__name__)
# vectors = []

# Opening Page

@app.route('/')
def hello_world():
    return "Welcome to the REST based backend of your System"

# -------------------------------------------------------------------------- Get and Return Variables -------------------------------------------------------------------------------

def username2user_id(username):
    to_get_user = {
        "username": username
    }

    user_id = userinfo.get_user_id(json.dumps(to_get_user))

    #Check if it's JSON
    if user_id[0] == '{':
        return None
    else:
        return user_id[0]

# Get an User Object - Args = username
@app.get("/user")
def get_user():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    to_get_user = {
        "user_id": username2user_id(args.get("username", default="", type=str))
    }
    
    try:
        user_test = json.loads(userinfo.get_user_object(json.dumps(to_get_user)))
        if user_test is None:
            return {"error": "[AppServer API] The user doesn't exist"}, 400
    except TypeError:
        print({"error": "[AppServer API] Type Error in get_user()"})
        return "Error", 400

    try:
        user_object = {
            "username": user_test["username"],
            "user_id": user_test["user_id"],
            "email": user_test["email"],
            "password": user_test["password"],
            "allow_notifications": user_test["allow_notifications"],
            "access_pin": user_test["access_pin"],
            "active_doors": [door["door_id"] for door in user_test["active_doors"]]
            #"active_doors": user_test["active_doors"]
        }
    except KeyError:
        print("[AppServer API] Key Error in get_user()")
        return "Error", 400    

    return json.dumps(user_object), 200

# Get User All Usernams
@app.get("/usernames")
def get_username():
    usernames_object = userinfo.list_all_usernames()
    if usernames_object is None:
        return {"error": "[AppServer API] There aren't any usernames stored in the database"}, 400
    return usernames_object, 200


# Try to Login with given password - Args = username, password
@app.get("/user/login")
def login():
    args = request.args

    required_fields = ['username', 'password']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    try_password = args.get("password", default="", type=str)

    to_get_user = {
        "user_id": username2user_id(args.get("username", default="", type=str))
    }

    user_test = userinfo.get_user_object(json.dumps(to_get_user))
    if user_test is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        user_object = json.loads(user_test)

    try_hashed_password = hashlib.sha256(try_password.encode()).hexdigest()

    try:
        if (try_hashed_password == user_object["password"]):
            return "True", 200
        else:
            return "False", 400
    except KeyError:
        print("[AppServer API] Key Error in login()")
        return "Error", 400 
    

# Get User Object Email Address - Args = username
@app.get("/user/email")
def get_email():
    args = request.args

    required_fields = ['email']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    conn = sqlite3.connect(userinfo.DB_NAME)

    email = args.get("email", default="", type=str)

    # Create a cursor object
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM users WHERE email = ?", (email,))
    user = cursor.fetchone()

    conn.close()

    if user == None:
        print("[AppServer API] The email doesn't exist in the database")
        return "False", 400
    else:
        return "True", 200


# Get User Object Active Doors Array - Args = username
@app.get("/user/active_doors")
def get_active_door_array():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    to_get_user = {
        "user_id": username2user_id(args.get("username", default="", type=str))
    }

    user_test = userinfo.get_user_object(json.dumps(to_get_user))
    if user_test is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        user_object = json.loads(user_test)

    try:
        to_send_active_doors = {
            "active_doors": user_object["active_doors"]
        }
    except KeyError:
        print("[AppServer API] Key Error in get_active_door_array()")
        return "Error", 400

    return json.dumps(to_send_active_doors), 200


# Get User Object Specific Door - Args = door_id, username
@app.get("/door")
def door():
    args = request.args

    required_fields = ['username', 'door_id']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    door_id = args.get("door_id", default = "", type = str)
    user_id = username2user_id(args.get("username", default="", type = str))

    to_get_user = {
        "user_id": user_id
    }

    user_test = userinfo.get_user_object(json.dumps(to_get_user))
    if user_test is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        user_object = json.loads(user_test)

    user_doors = user_object["active_doors"]
    if user_doors is None:
        return {"error": "[AppServer API] The user doesn't have allocated door"}, 400

    for door in user_doors:
        try:
            if door["door_id"] == door_id:
                to_get_door = {
                    "door_id": door_id,
                    "user_id": user_id,
                }
                got_door_object = userinfo.get_door_object(json.dumps(to_get_door))
                if got_door_object is None:
                    return {"error": "[AppServer API] The requested door doesn't exist"}, 400
                else:
                    door_object = json.loads(got_door_object)

                try:
                    aux_dict = {
                        "user_id": user_id,
                        "door_id": door_id,
                    }
                    
                    door_users = json.loads(userinfo.get_users_for_door(json.dumps(aux_dict)))["users"]
                    access_level = next((door["access_level"] for door in user_object["active_doors"] if door["door_id"] == door_id), None)

                    to_send_door = {
                        "lock_name": userinfo.get_door_name_for_user(json.dumps(aux_dict)),
                        "lock_state": door_object["door_state"],
                        "lock_id": door_object["door_id"],
                        "last_access": door_object["last_access"],
                        "user_last_access": door_object["user_last_access"],
                        "number_of_users": door_object["number_of_users"],
                        "users_with_access": [d["username"] for d in door_users],
                        "permission_level": access_level,
                        "comment": door_object["comment"],
                    }
                except KeyError as e:
                    print(f"KeyError: {e}")
                    print("[AppServer API] Key Error in door()2")
                    return "Error", 400

                return json.dumps(to_send_door), 200
        except KeyError as e:
            print("[AppServer API] Key Error in door()1")
            print(f"KeyError: {e}")
            return "Error", 400

    return {"error": "Unknown Error while fetching the door"}, 400

@app.get("/door/get_door_state")
def get_door_state():
    args = request.args

    required_fields = ['username', 'door_id']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    door_id = args.get("door_id", default = "", type = str)
    user_id = username2user_id(args.get("username", default="", type = str))

    to_get_user = {
        "user_id": user_id
    }

    user_test = userinfo.get_user_object(json.dumps(to_get_user))
    if user_test is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        user_object = json.loads(user_test)

    user_doors = user_object["active_doors"]
    if user_doors is None:
        return {"error": "[AppServer API] The user doesn't have allocated door"}, 400

    for door in user_doors:
        try:
            if door["door_id"] == door_id:
                to_get_door = {
                    "door_id": door_id,
                    "user_id": user_id,
                }
                got_door_object = userinfo.get_door_object(json.dumps(to_get_door))
                if got_door_object is None:
                    return {"error": "[AppServer API] The requested door doesn't exist"}, 400
                else:
                    door_object = json.loads(got_door_object)

                try:
                    if door_object["door_state"] == 1:
                        return "True", 200
                    elif door_object["door_state"] == 0:
                        return "False", 400
                except KeyError as e:
                    print(f"KeyError: {e}")
                    print("[AppServer API] Key Error in door()2")
                    return "Error", 400
        except KeyError as e:
            print("[AppServer API] Key Error in door()1")
            print(f"KeyError: {e}")
            return "Error", 400

    return {"error": "Unknown Error while fetching the door"}, 400


@app.get("/user/notification_status")
def get_notification_status():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    to_get_user = {
        "user_id": username2user_id(args.get("username", default="", type=str))
    }

    user_test = userinfo.get_user_object(json.dumps(to_get_user))
    if user_test is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        user_object = json.loads(user_test)

    try:
        notification_status = user_object["allow_notifications"]
        if notification_status == 1:
            return "True", 200
        elif notification_status == 0:
            return "False", 400
        else:
            return "Error", 400
    except KeyError:
        print("[AppServer API] Key Error in get_active_door_array()")
        return "Error", 400
    
    

@app.get("/user/pin_change_notification")
def get_pin_change():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    user_id = username2user_id(args.get("username", default = "", type = str))

    to_get_pin_notification = {
        "user_id": user_id
    }

    try:
        get_pin_change_notification_result = userinfo.get_pin_change_notification(json.dumps(to_get_pin_notification))

        if get_pin_change_notification_result == True:
            return "True", 200
        elif get_pin_change_notification_result == False:
            return "False", 400
        else:
            return "error", 400
    except KeyError:
        print("[AppServer API] Key Error in get_pin_change_notification()")
        return "Error", 400

@app.get("/user/user_general_notification")
def get_user_general_notification():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    user_id = username2user_id(args.get("username", default = "", type = str))

    to_get_false_try_trigger = {
        "user_id": user_id
    }

    try:
        false_try_trigger_result = userinfo.check_false_try_trigger(json.dumps(to_get_false_try_trigger))

        if isinstance(int(false_try_trigger_result), int):
            if false_try_trigger_result == 1:
                return "True", 200
            else:
                return "False", 400

            return str(false_try_trigger_result), 200
        else:
            return "error", 400
    except KeyError:
        print("[AppServer API] Key Error in get_user_general_notification()")
        return "Error", 400


# -------------------------------------------------------------------------- Update and Create Variables ----------------------------------------------------------------------------

# Add New User to the System
@app.post("/add_user")
def add_user():
    args = request.args

    required_fields = ['username', 'password', 'pincode', "email"]
    for field in required_fields:
        if field not in args:
            print("[AppServer API] Missing required field")
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    newUser = {
        "username": args.get("username", default="", type=str),
        "password": args.get("password", default="", type=str),
        "false_try_trigger": 0,
        "access_pin": args.get("pincode", default="", type=str),
        "email": args.get("email", default="", type=str),
        "allow_notifications": 1,
        "is_admin": 0,
    }

    if userinfo.add_user(json.dumps(newUser)) == True:
        return "True", 201
    else:
        print("Unknown Error while creating user")
        return "False", 400

@app.post("/delete_user")
def delete_user():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            print("[AppServer API] Missing required field")
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    to_remove_user = {
        "user_id": username2user_id(args.get("username", default="", type=str))
    }
    if userinfo.remove_user(json.dumps(to_remove_user)) == True:
        return "True", 200
    else:
        print("Unknown Error while deleting user")
        return "False", 400

# Update Username of a Specific User - Args = old_username, new_username
@app.post("/user/username")
def update_username():
    args = request.args

    required_fields = ['old_username', 'new_username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    old_username = args.get("old_username", default="", type=str)
    new_username = args.get("new_username", default="", type=str)

    username_update_arguments = {
        "user_id": username2user_id(old_username),
        "new_username": new_username
    }

    if userinfo.update_username(json.dumps(username_update_arguments)) == True:
        print("[AppServer API] Username Updated Successfuly")
        return "True", 200
    else:
        return "False", 400


# Update Password of a Specific User - Args = username, old_password, new_password
@app.post("/user/password")
def update_password():
    args = request.args

    required_fields = ['username', 'old_password', 'new_password']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    user_id = username2user_id(args.get("username", default="", type=str))
    old_password = args.get("old_password", default="", type=str)
    new_password = args.get("new_password", default="", type=str)

    to_get_user = {
        "user_id": user_id,
    }
    
    user_test = userinfo.get_user_object(json.dumps(to_get_user))
    if user_test is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        user_object = json.loads(user_test)

    if user_object["password"] == hashlib.sha256(old_password.encode()).hexdigest():
        password_update_arguments = {
            "user_id": user_id,
            "new_password": new_password,
        }

        if userinfo.update_password(json.dumps(password_update_arguments)) == True:
            return "True", 200
        else:
            return "False", 400

    else:
        return {"error": "Old Password didn't match"}, 400


# Update Email Address of a Specific User - Args = username, new_email
@app.post("/user/email")
def update_email():
    args = request.args

    required_fields = ['username', 'new_email']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    user_id = username2user_id(args.get("username", default="", type=str))
    new_email = args.get("new_email", default="", type=str)

    emailUpdateArguments = {
        "user_id": user_id,
        "new_email": new_email
    }

    if userinfo.update_email(json.dumps(emailUpdateArguments)) == True:
        return "True", 200
    else:
        return "False", 400


# -------------------------------------------------------------------------- Lockers ----------------------------------------------------------------------------
# Update Pin of a specified User - Args = username, oldPin, newPin
@app.post("/user/update_pin")
def update_user_pin():
    args = request.args

    required_fields = ['username', 'old_pin', 'new_pin']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    old_pin = args.get("old_pin", default="", type=int)
    new_pin = args.get("new_pin", default="", type=int)
    user_id = username2user_id(args.get("username", default="", type=str))

    to_get_user = {
        "user_id": user_id
    }

    user_test = userinfo.get_user_object(json.dumps(to_get_user))
    if user_test is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        user_object = json.loads(user_test)
    
    try:
        if (int(user_object["access_pin"]) == old_pin):
            to_update_user_pin = {
                "user_id": user_id,
                "new_access_pin": new_pin,
            }

            if userinfo.update_access_pin(json.dumps(to_update_user_pin)) == True:
                return "True", 200
            else:
                return {"error": "Unknown Error while updating User Pin"}, 400
        else:
            return "False", 400 
    except KeyError:
        print("[AppServer API] Key Error in updateUserPin()")
        return "Error", 400


# Update Name of a specified Door - Args = door_id, new_door_name, user_id
@app.post("/door/update_door_name")
def update_door_name():
    args = request.args

    required_fields = ['username', 'door_id', 'new_door_name']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    to_update_door_name = {
        "user_id": username2user_id(args.get("username", default="", type=str)),
        "door_id": args.get("door_id", default="", type=str),
        "new_door_name": args.get("new_door_name", default="", type=str),
    }

    if userinfo.update_door_name(json.dumps(to_update_door_name)) == True:
        return "True", 200
    else:
        return "False", 400


# Allocate Door to User - Args = username, door_id, accessLevel
@app.post("/user/share_door")
def share_door():
    args = request.args

    required_fields = ['username', 'user_to_share', 'door_id']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400
    
    user_id = username2user_id(args.get("username", default="", type=str))
    user_to_share_id = username2user_id(args.get("user_to_share", default="", type=str))
    door_id = args.get("door_id", default="", type=str)

    to_get_request_user = {
        "user_id": user_id
    }

    to_get_user_to_share = {
        "user_id": user_to_share_id
    }
    
    try:
        request_user_test = userinfo.get_user_object(json.dumps(to_get_request_user))
        if request_user_test is None:
            return {"error": "[AppServer API] The user doesn't exist"}, 400
        else:
            request_user_object = json.loads(request_user_test)

        request_user_access_level = next((door["access_level"] for door in request_user_object["active_doors"] if door["door_id"] == door_id), None)

        if request_user_access_level == 0:
            user_to_share_access_level = 1
        elif request_user_access_level == 1:
            user_to_share_access_level = 2
        elif request_user_access_level == 2:
            user_to_share_access_level = 3
        elif request_user_access_level == 3:
            return "False", 400

        user_to_share_test = userinfo.get_user_object(json.dumps(to_get_user_to_share))
        if user_to_share_test is None:
            return {"error": "[AppServer API] The user doesn't exist"}, 400
        else:
            user_to_share_object = json.loads(user_to_share_test)

        user_to_share_doors = user_to_share_object["active_doors"]
        if user_to_share_doors is None:
            return {"error": "[AppServer API] The user doesn't have allocated doors"}, 400
        else:
            for actual_door in user_to_share_doors:
                if actual_door["door_id"] == door_id:  
                    return {"error": "[AppServer API] The Door is already allocated"}, 400

        to_allocate_door = {
            "door_id": door_id,
            "access_level": user_to_share_access_level,
            "user_id": user_to_share_id,
        }

        if userinfo.allocate_door_to_user(json.dumps(to_allocate_door)) != True:
            return "False", 400

        to_update_door_name = {
            "door_id": door_id,
            "user_id": user_to_share_id,
            "new_door_name": door_id
        }
        
        if userinfo.update_door_name(json.dumps(to_update_door_name)) != True:
            return {"error": "[AppServer API] Error renomeating door"}, 400
        else:
            return "True", 200

    except KeyError:
        print("[AppServer API] Key Error in allocateLock()")
        return "Error", 400

# Deallocate Locker to User - Args = username, door_id
@app.post("/user/remove_share_door")
def deallocateLock():
    args = request.args

    required_fields = ['username', 'user_to_deshare', 'door_id']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    user_id = username2user_id(args.get("username", default="", type=str))
    user_to_deshare_id = username2user_id(args.get("user_to_deshare", default="", type=str))
    door_id = args.get("door_id", default="", type=str)

    to_get_request_user = {
        "user_id": user_id
    }

    to_get_user_to_deshare = {
        "user_id": user_to_deshare_id
    }

    try:
        request_user_test = userinfo.get_user_object(json.dumps(to_get_request_user))
        if request_user_test is None:
            return {"error": "[AppServer API] The user doesn't exist"}, 400
        else:
            request_user_object = json.loads(request_user_test)

        request_user_access_level = next((door["access_level"] for door in request_user_object["active_doors"] if door["door_id"] == door_id), None)

        user_to_deshare_test = userinfo.get_user_object(json.dumps(to_get_user_to_deshare))
        if user_to_deshare_test is None:
            return {"error": "[AppServer API] The user doesn't exist"}, 400
        else:
            user_to_deshare_object = json.loads(user_to_deshare_test)

        user_to_deshare_access_level = next((door["access_level"] for door in user_to_deshare_object["active_doors"] if door["door_id"] == door_id), None)

        if request_user_access_level >= user_to_deshare_access_level:
            return {"error": "[AppServer API] The user doesn't have enough privileges"}, 400

    except KeyError:
        print("[AppServer API] Key Error in allocateLock()")
        return "Error", 400
    
    to_deallocate_lock = {
        "user_id": username2user_id(args.get("user_to_deshare", default="", type=str)),
        "door_id": args.get("door_id", default="", type=str),
    }
    
    if userinfo.deallocate_door(json.dumps(to_deallocate_lock)) == True:
        return "True", 200
        #return {"success": "Locker Deallocated Successfully"}, 200
    else:
        return {"error": "Unknown Error while deallocating lock"}, 400


# Update Door Access Level of a specific User - Args = username, door_id, newAccessLevel
@app.post("/user/door_access_level")
def update_door_access_level():
    args = request.args

    required_fields = ['username', 'door_id', 'new_access_level']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    to_update_access_level = {
        "username": username2user_id(args.get("username", default="", type=str)),
        "new_access_level": args.get("new_access_level", default="", type=str),
        "door_id": args.get("door_id", default="", type=str),
    }

    if userinfo.update_access_level(json.dumps(to_update_access_level)) == True:
        return "True", 200
    else:
        return "False", 400

@app.post("/door/update_comment")
def update_door_comment():
    args = request.args

    required_fields = ['username', 'door_id', 'new_comment']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    to_update_comment = {
        "user_id": username2user_id(args.get("username", default="", type=str)),
        "door_id": args.get("door_id", default="", type=str),
        "new_comment": args.get("new_comment", default="", type=str),
    }

    if userinfo.update_door_comment(json.dumps(to_update_comment)) == True:
        return "True", 200
    else:
        return "False", 400


# First Interaction - Args = username
@app.post("/add_new_device")
def add_new_device():
    args = request.args

    required_fields = ['username', 'app_code']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    user_id = username2user_id(args.get("username", default="", type=str))
    app_code = args.get("app_code", default="", type=int)

    to_get_app_code = {
        "device_id": ''.join(filter(str.isdigit, userinfo.get_device_id())) #Select only the numbers returned from the SELECT to the database
    }

    app_code_test = userinfo.get_app_code(json.dumps(to_get_app_code))
    if app_code_test is None:
        return {"error": "[AppServer API] The requested device doesn't exist"}, 400
    else:
        app_code_object = json.loads(app_code_test)

    if app_code == int(app_code_object["app_code"]):
        to_activate_device = {
            "device_id": ''.join(filter(str.isdigit, userinfo.get_device_id())) #Select only the numbers returned from the SELECT to the database
        }

        userinfo.activate_device(json.dumps(to_activate_device))

        to_allocate_doors_id = userinfo.list_doors_by_device(''.join(filter(str.isdigit, userinfo.list_active_devices()[0])))

        try:
            
            for door in to_allocate_doors_id:
                to_allocate_door = {
                    "door_id": door,
                    "access_level": 1,
                    "user_id": user_id
                }
                if userinfo.allocate_door_to_user(json.dumps(to_allocate_door)) != True:
                    return {"error": "[AppServer API] Error allocating door"}, 400

                to_update_door_name = {
                    "door_id": door,
                    "user_id": user_id,
                    "new_door_name": door
                }
                
                if userinfo.update_door_name(json.dumps(to_update_door_name)) != True:
                    return {"error": "[AppServer API] Error renomeating door"}, 400

            to_get_user = {
                "user_id": username2user_id(args.get("username", default="", type=str))
            }
            
            user_test = json.loads(userinfo.get_user_object(json.dumps(to_get_user)))
            if user_test is None:
                return {"error": "[AppServer API] The user doesn't exist"}, 400

            try:
                to_send_active_doors = [door["door_id"] for door in user_test["active_doors"]]
            except KeyError:
                print("[AppServer API] Key Error in get_user()")
                return "Error", 400 

            return to_send_active_doors, 200
        except KeyError:
            print("[AppServer API] Key Error in addNewDevice()")
            return "Error", 400

    else:
        print("[AppServer API] The app code doesn't match")
        return "False", 400


# Open Door - Args = doord_id
@app.post("/door/open_door")
def open_door():
    args = request.args

    required_fields = ['username', 'door_id']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    user_id = username2user_id(args.get("username", default="", type=str))
    door_id = args.get("door_id", default="", type=str)

    to_get_user = {
        "user_id": user_id
    }

    user_test = userinfo.get_user_object(json.dumps(to_get_user))
    if user_test is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        user_object = json.loads(user_test)

    try:
        active_doors = user_object["active_doors"]
    except KeyError:
        print("[AppServer API] Key Error in get_active_door_array()")
        return "Error", 400

    for door in active_doors:
        if door["door_id"] == door_id:
            to_open_door = {
                "door_id": door_id,
            }
            
            if userinfo.update_door_request(json.dumps(to_open_door)) != True:
                return {"error": "[AppServer API] Error activating lock"}, 400

            to_update_last_access = {
                "door_id": door_id,
                "user_id": user_id,
            }
            if userinfo.update_last_access(json.dumps(to_update_last_access)) != True:
                return {"error": "[AppServer API] Error updating last access"}, 400
            
            return "True", 202

    
    return json.dumps({"error": "[AppServer API] The user doesn't have access to this door"}), 400

@app.post("/door/update_last_access")
def update_last_access():
    args = request.args

    required_fields = ['username', 'door_id']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    to_update_last_access = {
        "door_id": args.get("door_id", default="", type=str),
        "user_id": username2user_id(args.get("username", default="", type=str))
    }

    if userinfo.update_last_access(json.dumps(to_update_last_access)) == True:
        return "True", 200
    else:
        return "False", 400

@app.post("/user/change_notification_preference")
def change_notification_preference():
    args = request.args

    required_fields = ['username']
    for field in required_fields:
        if field not in args:
            return {"error": f"[AppServer API] Missing required field '{field}'"}, 400

    to_get_user = {
        "user_id": username2user_id(args.get("username", default="", type=str))
    }

    user_test = userinfo.get_user_object(json.dumps(to_get_user))
    if user_test is None:
        return {"error": "[AppServer API] The user doesn't exist"}, 400
    else:
        user_object = json.loads(user_test)

    if user_object["allow_notifications"] == 1:
        new_notification_preference = 0
    else:
        new_notification_preference = 1

    to_update_notification_preference = {
        "user_id": username2user_id(args.get("username", default="", type=str)),
        "allow_notifications": new_notification_preference
    }

    if userinfo.update_notifications(json.dumps(to_update_notification_preference)) == True:
        return "True", 200
    else:
        return "False", 400

# --------------------------------------------------------------------------------- Main Function -----------------------------------------------------------------------------------


if __name__ == '__main__':
    #userinfo.initializeDatabase()
    target = app.run(host=server_configuration.hostName, port=server_configuration.appApiPort, debug=True)