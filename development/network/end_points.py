import json
from server_esp_api_aux import *


def opening_report(json_data):

    lock_id = json_data['locker_id']
    # access_token = json_data['access_token']
    # master_key = get_master_key(lock_id)

    # if check_access_token(lock_id, master_key, access_token) == False:
    #     print("Invalid access token")
    #     return

    data_to_send, response_code = retrieve_data(lock_id)
        
    return data_to_send, response_code

