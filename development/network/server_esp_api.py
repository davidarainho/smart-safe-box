from http.server import BaseHTTPRequestHandler, HTTPServer
import json
import threading
import time
from server_esp_api_aux import *
import server_configuration
from end_points import *
import time


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
        if self.path == '/report_init': 
            data_to_send = {"UnicID": "123456", "APPCode": "111222"}

            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            response_json = json.dumps(data_to_send)
            self.wfile.write(response_json.encode('utf-8'))

        if self.path == '/duarte_easter_egg':

            self.send_response(200)  # Set the HTTP response code to 200 (OK)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            html_content = "<html><body><h1>ola duarte</h1></body></html>"
            self.wfile.write(html_content.encode('utf-8'))

        if self.path == '/ola_joel':

            self.send_response(200)  # Set the HTTP response code to 200 (OK)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            html_content = "<html><body><h1>ola joel</h1></body></html>"
            self.wfile.write(html_content.encode('utf-8'))

        if self.path == '/lobinhos':
            self.send_response(200)  # Set the HTTP response code to 200 (OK)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            html_content = '''
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Página sobre Lobinhos</title>
                </head>
                <body>
                    <h1>Lobinhos</h1>
                    <p>Os lobinhos são jovens lobos que possuem uma natureza curiosa e brincalhona. Eles são conhecidos por sua inteligência e agilidade.</p>
                    
                    <h2>Características dos Lobinhos</h2>
                    <ul>
                        <li>Pelagem fofa</li>
                        <li>Orelhas pontudas</li>
                        <li>Garras afiadas</li>
                        <li>Visão aguçada</li>
                    </ul>
                    
                    <h2>Comportamento Social</h2>
                    <p>Os lobinhos são animais sociais que vivem em matilhas. Eles se comunicam através de vocalizações e expressões corporais.</p>
                    
                    <h2>Ecologia</h2>
                    <p>Os lobinhos são importantes para o equilíbrio dos ecossistemas, ajudando a controlar a população de presas, como roedores e cervos.</p>
                </body>
                <meta http-equiv="refresh" content="5; URL=https://www.instagram.com/reel/CrT42gZLc_x/?utm_source=ig_web_copy_link&igshid=MzRlODBiNWFlZA==">
                </html>
                '''
            self.wfile.write(html_content.encode('utf-8'))


    def do_POST(self):
        global is_to_send
        print("jkdlaç")

        # error_response, check_message = check_lock_message(json_data)

        # if check_message == False:

        #     print(json_data)

        #     self.send_response(400)
        #     self.send_header('Content-type', 'application/json')
        #     self.end_headers()
        #     error_json = json.dumps(error_response)
        #     self.wfile.write(error_json.encode('utf-8'))
        #     return

        if is_to_send == False:
            print("Nothing to send")
            return False
        
        if self.path == '/opening_request':
            request_type = 'opening_request'
        elif self.path == '/user_update':
            request_type = 'user_update'
        elif self.path == '/opening_report':
            request_type = 'opening_report'
        elif self.path == '/change_password':
            request_type = 'change_password'
    

        else:
            self.send_response(404)  # Set the HTTP response code (e.g., 404 for "Not Found")
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            error_message = "Endpoint not found"
            self.wfile.write(error_message.encode('utf-8'))
            return

        # reading message from the client
        content_length = int(self.headers['Content-Length'])
        post_data = self.rfile.read(content_length).decode('utf-8')
        json_data = json.loads(post_data)
        
        response_code, data_to_send = get_data_to_send(request_type, json_data)

            

        # Send a JSON response
        self.send_response(response_code)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        response_json = json.dumps(data_to_send)
        self.wfile.write(response_json.encode('utf-8'))
        is_to_send = False
        


def control_refresh_rates(refresh_dict):
    # global send_messages_refresh_rate
    # global new_access_token_refresh_rate
    # global get_requests_from_app_refresh_rate

    global send_messages_last_time
    global last_access_token_update_time
    global get_requests_from_app_last_time

    current_time = time.monotonic()

    if current_time - send_messages_last_time > server_configuration.send_messages_refresh_rate:
        refresh_dict['is_to_send'] = True
        send_messages_last_time = current_time

    if current_time - get_requests_from_app_last_time > server_configuration.get_requests_from_app_refresh_rate:
        refresh_dict['get_requests_from_app'] = True
        get_requests_from_app_last_time = current_time

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

    while True:
        refresh_dict = control_refresh_rates(refresh_dict)

        is_to_send = refresh_dict['is_to_send']

        if refresh_dict['get_requests_from_app']:
            request_type, new_password, lock_id = get_app_request()


            # access_token = generate_access_token(
            #     get_master_key(lock_id), lock_id)
            data_to_send = create_request_json(
                request_type, new_password)
            
            if(request_type == 'open_vault'):
                opening_report_dict = insert_new_request(
                    opening_report_dict, lock_id, data_to_send)

            refresh_dict['get_requests_from_app'] = False


if __name__ == "__main__":


    lockWebServer = HTTPServer((server_configuration.hostName, server_configuration.espApiPort), MyServer)
    print("Esp api opened at http://%s:%s" % (server_configuration.hostName, server_configuration.espApiPort))
    lockThread = threading.Thread(target=lockWebServer.serve_forever)

    threading.Thread(target=lockWebServer.serve_forever).start()

    api_control_thread = threading.Thread(
        target=output_control, args=())
    api_control_thread.daemon = True
    api_control_thread.start()
