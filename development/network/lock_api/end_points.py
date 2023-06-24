from database_aux import *


def report_init():
    device_id, app_code = get_device_id_and_app_code()

    if device_id is None:
        status_code = 403
        data_to_send = {
            "message": "Access denied",
            "details": "No locks to be allocated."
        }
    elif app_code is None:
        status_code = 500
        data_to_send = {
            "message": "Internal Server Error",
            "details": "Error retriving the app code."
        }
    else:
        status_code = 200
        data_to_send = {
            "UnicID": device_id,
            "APPCode": app_code
        }

    return status_code, data_to_send


def opening_report(device_id, state, user_id, access_pin):
    status_code = 201
    if state == 'open':
        user_doors_real_list, user_doors_ids_list = get_user_doors_ids_in_device(
            user_id, device_id)
        print('user_id: ' + user_id)
        print('for ports: ' + str(user_doors_ids_list))

        for door_id in user_doors_ids_list:
            update_last_access(user_id, door_id)
        data_to_send = {
            "state": "open"
        }
        print('ok status when opening doors')
        set_false_try_trigger(user_id, 2)
        change_door_state(user_doors_ids_list, 1)

        # TODO send to app
    elif validate_user_id(user_id) == False:
        data_to_send = {
            "state": "close",
        }
        error_message = {
            'message': 'Bad request',
            'details': 'Invalid user_id'
        }
        print(error_message)
    elif check_user_access_pin(user_id, access_pin) == False:
        data_to_send = {
            "state": "close",
        }
        error_message = {
            'message': 'Unauthorized',
            'details': 'Wrong access pin'
        }
        set_false_try_trigger(user_id, 1)
        print(error_message)
    else:
        user_doors_real_list, user_doors_ids_list = get_user_doors_ids_in_device(
            user_id, device_id)

        if len(user_doors_real_list) == 0:
            data_to_send = {
                "state": "close",
            }
            error_message = {
                'message': 'Forbidden',
                'details': "The user don't have permissions to open doors"
            }
            print(error_message)
        else:
            door = {
                'N_doors': len(user_doors_real_list),
                'door_id': user_doors_real_list
            }
            data_to_send = {
                "state": "open",
                'door': door
            }
            for door_id in user_doors_ids_list:
                update_last_access(user_id, door_id)

    return status_code, data_to_send


def user_update(device_id):

    users_ids = get_device_users_ids(device_id)

    users_update_info_list = []

    for user_id in users_ids:
        # Get user information
        doors_ids, _ = get_user_doors_ids_in_device(user_id, device_id)
        access_pin = get_user_access_pin(user_id)

        user_dict = {
            "user_id": str(user_id),
            "user_password": str(access_pin),
            "door": {
                "N_doors": len(doors_ids),
                "door_id": doors_ids
            }
        }
        users_update_info_list.append(user_dict)

    data_to_send = {
        'usersCount': len(users_update_info_list),
        'user_info': users_update_info_list
    }

    return 201, data_to_send


def change_access_pin(user_id, recieved_access_pin, new_access_pin):

    if validate_user_id(user_id) == False:
        data_to_send = {
            'state_password': 'NotChanged',

        }
        error_message = {
            'message': 'Bad request',
            'details': 'Invalid user_id'
        }
        print(error_message)
    elif check_user_access_pin(user_id, recieved_access_pin) == False:
        data_to_send = {
            'state_password': 'NotChanged',
        }
        error_message = {
            'message': 'Unauthorized',
            'details': 'Wrong access pin'
        }
        print(error_message)

    else:
        change_user_access_pin(user_id, new_access_pin)
        data_to_send = {
            'state_password': 'Changed'
        }
        set_pin_change_notification(user_id, 1)

    return 201, data_to_send


def opening_request(device_id):

    real_doors_ids, doors_ids = get_doors_to_open(device_id)
    code = 201
    data_to_send = {
        "N_doors": len(real_doors_ids),
        "door_id": real_doors_ids
    }
    set_door_requests(doors_ids, 0)
    print(doors_ids)
    change_door_state(doors_ids, 1)

    # add_to_opening_to_log(doors,)

    return code, data_to_send


def lock_api_responses(request_type, json_data=None):

    if request_type == '/report_init':
        status_code, data_to_send = report_init()

    elif request_type == '/opening_report':
        lock_status = json_data['state']
        username = json_data['user_id']
        password = json_data['password']
        device_id = json_data['UnicID']

        status_code, data_to_send = opening_report(
            device_id, lock_status, username, password)

    elif request_type == '/opening_request':
        device_id = json_data['UnicID']
        status_code, data_to_send = opening_request(device_id)

    elif request_type == '/change_password':
        new_password = json_data['new_password']
        old_password = json_data['password']
        username = json_data['user_id']

        status_code, data_to_send = change_access_pin(
            username, old_password, new_password)

    elif request_type == '/user_update':
        device_id = json_data['UnicID']
        status_code, data_to_send = user_update(device_id)

    else:
        data_to_send = {
            "Message:": "Invalid request"
        }
        status_code = 400

    return status_code, data_to_send
