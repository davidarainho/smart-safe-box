import json
from server_esp_api_aux import *
import sqlite3
import random
from userinfo import get_locks_for_user


def report_init():
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()
    
    lock_id = "Lock1"
    cursor.execute("SELECT appcode FROM locks WHERE lock_id = ?", (lock_id,))
    code_app = cursor.fetchone()[0]
    
    if code_app is None:
        conn.close()
        print(f"No locks available")
        return 400, 0, 0
    
    UnicId = "555330"    
    conn.close()
    return 200, UnicId, code_app


def get_user_update_info():

    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    cursor.execute("SELECT COUNT(*) FROM users")
    number_of_users = cursor.fetchone()[0]
    user_info = []

    cursor.execute("SELECT * FROM users")
    users = cursor.fetchall()

    users_list = []

    for user in users:
        # Get user information
        user_id = user[1]
        print(user_id)
        user_password = user[2]
        print(user_password)



        # Create user dictionary
        user_dict = {
            "user_id": user_id,
            "user_password": user_password,
            "door": {
                "N_doors": n_doors,
                "door_id": door_id
            }
        }
        users_list.append(user_dict)

    if not users_list:  # Checking if the list is empty
        data_to_send = {
            'usersCount': 0,
            'user_info': []
        }
        return data_to_send

    data_to_send = {
        "usersCount": number_of_users,
        "user_info": users_list
    }

    conn.close()

    return data_to_send


def update_lock_status(lock_id, state):
    # Create a connection to the database
    conn = sqlite3.connect('users.db')

    # Create a cursor object
    cursor = conn.cursor()

    print(lock_id)
    cursor.execute(
        "SELECT lock_id FROM active_locks WHERE lock_id=?", (lock_id,))

    lock = cursor.fetchone()

    if lock == None:
        # cursor.execute(
        #     "SELECT lock_id FROM active_locks WHERE lock_id=?", (lock_id))
        # print(cursor.fetchone())
        return 350

    try:
        ret = cursor.execute(
            "UPDATE active_locks SET state=? WHERE lock_id=?", (state, lock_id))
    except Exception as e:
        print(f"An error writing to db")
        return 300

    rows = cursor.fetchall()

    # Print the rows
    for row in rows:
        print(row)

    conn.commit()

    conn.close()
    return 200


def change_password(username, old_password ,new_password):
    # Create a connection to the database
    conn = sqlite3.connect('users.db')

    # Create a cursor object
    cursor = conn.cursor()

    cursor.execute(
        "SELECT username FROM users WHERE username=?", (username,))

    user = cursor.fetchone()
    print(user)

    cursor.execute("SELECT access_pin FROM users WHERE username=?", (username,))
    db_password = cursor.fetchone()

    if db_password == old_password:
        print("password is wrong")
        data_to_send = {
            'state_password':'NotChanged'
        } 

        return 201, data_to_send


    if user is None:
        print("user is none")
        data_to_send = {
            'state_password':'NotChanged'
        } 
        conn.close()
        return 201, data_to_send

    try:
        ret = cursor.execute(
            "UPDATE users SET access_pin=? WHERE username=?", (new_password, username))
    except Exception as e:
        print(f"An error writing to db")
        data_to_send = {
            'state_password':'NotChanged'
        } 
        conn.close()
        return 201, data_to_send

    conn.commit()

    conn.close()
    data_to_send = {
            'state_password':'NotChanged'
        } 

    return 200, data_to_send

def check_for_opening_request():

    # Create a connection to the database
    conn = sqlite3.connect('users.db')

    try:
        cursor = conn.cursor()
        cursor.execute("SELECT lock_id FROM active_locks WHERE lock_request=?", (1,))

    except sqlite3.Error as e:
        # Handle the error here
        print("An error occurred:", e)
        code = 500  # Set an appropriate response code
        data_to_send = {'message': 'Database error'}  # Provide a suitable response message
        return code, data_to_send

    code = 200

    rows = cursor.fetchall()
    door_ids = [row[0] for row in rows]
    door_ids = [int(door_id.replace("Lock", "")) for door_id in door_ids]  # Convert strings to integers
    data_to_send = {
        "N_doors": len(rows),
        "door_id": door_ids
    }

    for row in rows:
        print("row")
        print(row)
        
    cursor.execute("UPDATE active_locks SET lock_request=? WHERE lock_request=?", (0,1))
    conn.commit()
    conn.close()
    return code, data_to_send

def opening_report(lock_status, username):
    print('here')
    
    
    if lock_status == 'closed':
        json_input = {
            "username": username
        }
        json_input = json.dumps(json_input)
        locks_for_user = get_locks_for_user(json_input)
        locks_for_user =json.loads(locks_for_user)

        if locks_for_user['lock_id'] is None:
            data_to_send = {"state": "close"}
            code = 201
            return code, data_to_send
        
        lock_id = data_to_send['lock_id']

        port_numbers = []
        for it in lock_id:
            port_numbers.append(it[0])

        door = {
                "N_doors": len(port_numbers),
                "door_id": port_numbers
            }
        data_to_send = {
            "state": "open",
            "door": door
        }

        print(data_to_send)

        return 201, data_to_send

        
    else:
        data_to_send = {"Message": "Ok"}
        code = 200

    return code, data_to_send



def get_data_to_send(request_type, json_data):

    if request_type == 'opening_request':
        code, data_to_send = check_for_opening_request()

    elif request_type == 'opening_report':
        lock_status = json_data['State']
        username = json_data['user_id']
        code, data_to_send = opening_report(lock_status, username)

    elif request_type == 'change_password':
        new_password = json_data['new_password']
        old_password = json_data['password']
        username = json_data['user_id']
        
        code, data_to_send = change_password(username, old_password, new_password)

    elif request_type == 'user_update':
        data_to_send = get_user_update_info()
        code = 200

    else:
        data_to_send = {
            "Message:": "Invalid request"
        }
        code = 400

    return code, data_to_send
