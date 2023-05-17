from http.server import BaseHTTPRequestHandler, HTTPServer
import json
import threading
import time
from server_esp_api_aux import *
from server_esp_api_config import *
import time


is_to_send = False
json_data_dict = {}


class MyServer(BaseHTTPRequestHandler):

    def __init__(self, request, client_address, server):
        super().__init__(request, client_address, server)
        self.client_host = "localhost"
        self.client_port = 8080

    def do_POST(self):
        global is_to_send

        if is_to_send == False:
            print("Nothing to send")
            return
        # reading message from the client
        content_length = int(self.headers['Content-Length'])
        post_data = self.rfile.read(content_length).decode('utf-8')
        json_data = json.loads(post_data)

        error_response, check_message = check_lock_message(json_data)

        if check_message == False:

            print(json_data)

            self.send_response(400)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            error_json = json.dumps(error_response)
            self.wfile.write(error_json.encode('utf-8'))
            return

        lock_id = json_data['locker_id']
        access_token = json_data['access_token']
        master_key = get_master_key(lock_id)

        if check_access_token(lock_id, master_key, access_token) == False:
            print("Invalid access token")
            return

        data_to_send, response_code = retrieve_data(
            json_data_dict, lock_id)

        # Send a JSON response
        self.send_response(response_code)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        response_json = json.dumps(data_to_send)
        self.wfile.write(response_json.encode('utf-8'))
        is_to_send = False


def control_refresh_rates(refresh_dict):
    global send_messages_refresh_rate
    global new_access_token_refresh_rate
    global get_requests_from_app_refresh_rate

    global send_messages_last_time
    global last_access_token_update_time
    global get_requests_from_app_last_time

    current_time = time.monotonic()

    if current_time - send_messages_last_time > send_messages_refresh_rate:
        refresh_dict['is_to_send'] = True
        send_messages_last_time = current_time

    if current_time - get_requests_from_app_last_time > get_requests_from_app_refresh_rate:
        refresh_dict['get_requests_from_app'] = True
        get_requests_from_app_last_time = current_time

    return refresh_dict


def output_control():
    global is_to_send
    global json_data_dict
    global secret_key

    refresh_dict = {
        'is_to_send': True,
        'get_requests_from_app': True
    }

    while True:
        refresh_dict = control_refresh_rates(refresh_dict)

        is_to_send = refresh_dict['is_to_send']

        if refresh_dict['get_requests_from_app']:
            request_type, new_password, lock_id = get_app_request()
            access_token = generate_access_token(
                get_master_key(lock_id), lock_id)
            data_to_send = create_request_json(
                request_type, new_password, access_token)

            json_data_dict = insert_new_request(
                json_data_dict, lock_id, data_to_send)

            refresh_dict['get_requests_from_app'] = False


if __name__ == "__main__":
    send_messages_last_time = time.monotonic()
    get_requests_from_app_last_time = time.monotonic()

    webServer = HTTPServer((hostName, serverPort), MyServer)
    print("Server started http://%s:%s" % (hostName, serverPort))

    threading.Thread(target=webServer.serve_forever).start()

    api_control_thread = threading.Thread(
        target=output_control, args=())
    api_control_thread.daemon = True
    api_control_thread.start()
