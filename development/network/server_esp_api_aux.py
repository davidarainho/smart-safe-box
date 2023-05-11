import jwt
import datetime
import json


def retrieve_data(json_dict, lock_id):
    if lock_id in json_dict:
        response_code = 200
        request_data = remove_and_return_data(json_dict, lock_id)
    else:
        response_code = 400
        request_data = {
            "error_message": "invalid id " + str(lock_id)
        }

    if request_data is None:
        response_code = 400
        request_data = {
            "error_message": "Nothing to send"
        }

    return request_data, response_code


def check_lock_message(json_data):
    if not ('locker_id' in json_data):
        print("No locker id")
        request_data = {
            "error_message": "No locker id"
        }

        return False, request_data

    if not ('access_token' in json_data):
        print("access_token")
        request_data = {
            "error_message": "No access_token"
        }

        return False, request_data

    return True, None


def check_access_token(lock_id, master_key, received_access_token):

    correct_token = generate_access_token(master_key, lock_id)
    return received_access_token == correct_token


def get_master_key(lock_id):
    return "smart_safe_lock"


def get_app_request():
    # TODO
    request_type = 'open_vault'
    new_password = None
    lock_id = 1

    return request_type, new_password, lock_id


def create_request_json(request_type, new_password, access_token):
    if 'change_password' == request_type:
        request_data = {
            'request_type': request_type,
            'new_password': new_password,
            'access_token': access_token
        }
    else:
        request_data = {
            'request_type': request_type,
            'access_token': access_token
        }

    return json.dumps(request_data)


def insert_new_request(json_data_dict, lock_id, data_to_send):

    if lock_id not in json_data_dict:
        json_data_dict[lock_id] = []
    json_data_dict[lock_id].append(data_to_send)

    return json_data_dict


def remove_and_return_data(json_data_dict, lock_id):
    if lock_id in json_data_dict and len(json_data_dict[lock_id]) > 0:
        return json_data_dict[lock_id].pop(0)
    else:
        return None


def generate_access_token(secret_key, lock_id):

    payload = {'lock_id': str(lock_id)}
    # set the expiration time for the token (in seconds)
    now = datetime.datetime.utcnow()
    next_hour = now.replace(second=0, microsecond=0,
                            minute=0) + datetime.timedelta(hours=1)
    expiration_time = int(next_hour.timestamp())

    # # add the expiration time to the payload
    payload['exp'] = expiration_time

    token = jwt.encode(payload, secret_key, algorithm='HS256')

    return token
