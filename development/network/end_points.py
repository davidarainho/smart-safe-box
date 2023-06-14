import json
from server_esp_api_aux import *
import sqlite3

def update_lock_status(state,lock_id):
    # Create a connection to the database
    conn = sqlite3.connect('users.db')

    # Create a cursor object
    cursor = conn.cursor()
    
    try:
        ret = cursor.execute("UPDATE active_locks SET state=? WHERE lock_id=?", (state, lock_id))
    except Exception as e:
        print(f"An error writing to db")
        return False

    
    rows = cursor.fetchall()

    # Print the rows
    for row in rows:
        print(row)

    conn.commit()

    conn.close()
    return ret

def change_password(lock_id, new_password):
    return 300

def check_for_opening_request(lock_id):
    return True
    

def get_data_to_send(request_type, json_data):
    lock_id = json_data['UnicID']
    error_json = {
        "Message:": "Invalid request"
    }

    if request_type == 'opening_request':

        code = 200
        
        if check_for_opening_request(lock_id) == True:
            
            data_to_send = {
                "N_doors": 1,
                "door_id": [
                    1
                ]
            }
        else:
            data_to_send = {
                "N_doors": 0,
                "door_id": [
                    1
                ]
            }

    # elif request_type == 'user_update':
    #     request_type = 'user_update'
    
    elif request_type == 'opening_report':
        lock_status = json_data['State']
        success = update_lock_status(lock_id, lock_status)
        if success == False:
            data_to_send =  {"Message": "Error writing to db"}
            code = 400
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
            data_to_send =  {"state_password": "Changed"}

    elif request_type == 'user_update':
        data_to_send = {
            'Message': 'Not implemented'
        }
        code = 300
      
    
    else:
        data_to_send = error_json
        code = 400

    return code, data_to_send

        
# {
#     "usersCount": 2,
#     "user_info": [
#         {
#             "user_id": "456789",
#             "user_password": "123789",    
#             "door": {
#                 "n_doors": 1,
#                 "door_id": [
#                     1
#                 ]
#             }
#         },
#         {
#             "user_id": "456788",
#             "user_password": "123788",
#             "door": {
#                 "n_doors": 2,
#                 "door_id": [
#                     2,
#                     3
#                 ]
#             }
#         }
#     ]
# }

