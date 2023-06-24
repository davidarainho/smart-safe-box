from end_points import *
import server_configuration
from lock_api_aux import *
import time
import threading
import json
from http.server import BaseHTTPRequestHandler, HTTPServer
import sys
sys.path.append("..")


is_to_send = False
json_data_dict = {}
opening_report_dict = {}

send_messages_last_time = 0
get_requests_from_app_last_time = 0


class MyServer(BaseHTTPRequestHandler):

    def __init__(self, request, client_address, server):
        super().__init__(request, client_address, server)
        self.client_host = "localhost"
        self.client_port = 8080

    def do_GET(self):
        status_code, data_to_send = lock_api_responses('/report_init')

        self.send_response(status_code)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        response_json = json.dumps(data_to_send)
        self.wfile.write(response_json.encode('utf-8'))

    def do_POST(self):
        global is_to_send

        if is_to_send == False:
            print("Nothing to send")
            return False

        content_length = int(self.headers['Content-Length'])
        post_data = self.rfile.read(content_length).decode('utf-8')
        json_data = json.loads(post_data)

        response_code, data_to_send = lock_api_responses(self.path, json_data)

        # Send a JSON response
        self.send_response(response_code)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        response_json = json.dumps(data_to_send)
        self.wfile.write(response_json.encode('utf-8'))
        is_to_send = False


was_open_rise_previous = False
compare_time = False


def control_refresh_rates(refresh_dict):

    global send_messages_last_time
    global last_access_token_update_time
    global get_requests_from_app_last_time
    global was_open_rise_previous
    global compare_time

    current_time = time.monotonic()

    if current_time - send_messages_last_time > server_configuration.send_messages_refresh_rate:
        refresh_dict['is_to_send'] = True
        send_messages_last_time = current_time

    was_open = check_for_open_port()

    if was_open != was_open_rise_previous:
        get_requests_from_app_last_time = current_time + \
            (server_configuration.get_requests_from_app_refresh_rate)
        was_open_rise_previous = True
        compare_time = True
        print("timer to close start")
        print("current_time" + str(current_time))
        print("time to close " + str(get_requests_from_app_last_time))

    if compare_time == True:

        if get_requests_from_app_last_time - current_time < 0:
            print(current_time)
            refresh_dict['get_requests_from_app'] = True
            # get_requests_from_app_last_time = current_time
            compare_time = False
            was_open_rise_previous = False
            print("timer stop")

    return refresh_dict


def output_control():
    global is_to_send
    global opening_report_dict
    global json_data_dict
    global secret_key

    refresh_dict = {
        'is_to_send': True,
        'get_requests_from_app': True
    }
# check_for_opening_request
    while True:
        refresh_dict = control_refresh_rates(refresh_dict)

        is_to_send = refresh_dict['is_to_send']

        if refresh_dict['get_requests_from_app']:
            close_all_doors()

            refresh_dict['get_requests_from_app'] = False


if __name__ == "__main__":

    lockWebServer = HTTPServer(
        (server_configuration.hostName, server_configuration.espApiPort), MyServer)
    print("Esp api opened at http://%s:%s" %
          (server_configuration.hostName, server_configuration.espApiPort))
    lockThread = threading.Thread(target=lockWebServer.serve_forever)

    threading.Thread(target=lockWebServer.serve_forever).start()

    api_control_thread = threading.Thread(
        target=output_control, args=())
    api_control_thread.daemon = True
    api_control_thread.start()
