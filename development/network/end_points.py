import json
from server_esp_api_aux import *
import sqlite3


def register_lock():
    code = 200
    code_app = None
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()

    # Get the lock details from the locks table
   # Check if the table is empty
    cursor.execute("SELECT COUNT(*) FROM locks")
    count = cursor.fetchone()[0]

    if count == 0:
        print("Table is empty")
        return 350
    else:
        # If not empty, select a value from a column
        cursor.execute("SELECT * FROM locks LIMIT 1")
        lock = cursor.fetchone()

    # Add the lock to the active_locks table
    cursor.execute("INSERT INTO active_locks (lock_id, name, location, state, access_pin, user_last_access) VALUES (?, ?, ?, ?, ?, ?)",
                   (lock[1], lock[2], lock[3], lock[4], lock[5], lock[6]))
    conn.commit()

    lock_id = lock[1]
    # Remove the lock from the locks table
    cursor.execute("DELETE FROM locks WHERE lock_id = ?", (lock_id,))
    conn.commit()

    conn.close()

    return code, lock_id, code_app


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


def change_password(lock_id, new_password):
    # Create a connection to the database
    conn = sqlite3.connect('users.db')

    # Create a cursor object
    cursor = conn.cursor()

    cursor.execute(
        "SELECT lock_id FROM active_locks WHERE lock_id=?", (lock_id,))

    lock_ = cursor.fetchone()

    if lock_ is None:
        return 350

    try:
        ret = cursor.execute(
            "UPDATE active_locks SET access_pin=? WHERE lock_id=?", (new_password, lock_id))
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


def check_for_opening_request(lock_id):

    # Create a connection to the database
    conn = sqlite3.connect('users.db')

    # Create a cursor object
    cursor = conn.cursor()

    cursor.execute(
        "SELECT lock_id FROM active_locks WHERE lock_id=?", (lock_id,))

    lock_ = cursor.fetchone()

    if lock_ is None:
        return 350, 0

    cursor.execute(
        "SELECT lock_request FROM active_locks WHERE lock_id=?", (lock_id,))

    lock_request = cursor.fetchone()[0]
    print("lock_request =" + str(lock_request))

    if (lock_request == 0):
        return 200, 0

    ret = cursor.execute(
        "UPDATE active_locks SET lock_request=? WHERE lock_id=?", (0, lock_id))

    return 200, 1


def get_data_to_send(request_type, json_data):
    lock_id = json_data['UnicID']
    error_json = {
        "Message:": "Invalid request"
    }

    if request_type == 'opening_request':

        code = 200
        code, state = check_for_opening_request(lock_id)

        if state == 1:
            data_to_send = {
                "N_doors": 1,
                "door_id": [
                    1
                ]
            }
        else:
            data_to_send = {
                'Message': "Nothing to open"
            }

    # elif request_type == 'user_update':
    #     request_type = 'user_update'

    elif request_type == 'opening_report':
        lock_status = json_data['State']
        code = update_lock_status(lock_id, lock_status)
        if code == 300:
            data_to_send = {"Message": "Error writing to db"}
        elif code == 350:
            data_to_send = {"Message": 'Invalid lock id'}
        else:
            data_to_send = {"Message": 'Success'}
            code = 200

    elif request_type == 'change_password':
        new_password = json_data['new_password']
        if change_password(lock_id, new_password) == 300:
            data_to_send = {
                "Message:": "Error updating the pin"
            }
            code = 300
        else:
            data_to_send = {"state_password": "Changed"}
            code = 200

    elif request_type == 'user_update':
        data_to_send = {
            'Message': 'Not implemented'
        }
        code = 300

    else:
        data_to_send = error_json
        code = 400

    return code, data_to_send
