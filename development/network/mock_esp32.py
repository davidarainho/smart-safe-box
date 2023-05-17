import http.client
import time
import json
import jwt
import datetime
import tkinter as tk
from tkinter import messagebox
from server_esp_api_aux import generate_access_token, check_access_token
from server_esp_api_config import secret_key

# Server information
server_host = "localhost"
server_port = 8080

global password
password = "safe_password"


def show_password():
    global password
    messagebox.showinfo("Current Password", password)


def check_for_server_api():
    global password

    try:
        # Connect to the server
        conn = http.client.HTTPConnection(server_host, server_port)

        lock_id = 1
        access_token = generate_access_token(secret_key, lock_id)

        data = {'locker_id': lock_id, 'access_token': access_token}

        json_data = json.dumps(data)
        headers = {'Content-type': 'application/json'}
        conn.request("POST", "/api", json_data, headers)

        # Get the response from the server
        response = conn.getresponse()

        # Check the response from the server

        if response.status == 200:
            response_data = response.read()
            response_str = response_data.decode(
                'utf-8').replace('\\', '')[1:-1]
            json_data_dict = json.loads(response_str)

            received_access_token = json_data_dict["access_token"]

            if False == check_access_token(lock_id, secret_key, received_access_token):
                print("Invalid access token")

            else:

                if json_data_dict["request_type"] == "open_vault":
                    print("Opening lock")
                    status_label.config(text="Lock is open")

                elif json_data_dict["request_type"] == "close_vault":
                    print("Closing lock")
                    status_label.config(text="Lock is closed")
                elif json_data_dict["request_type"] == "change_password":
                    print("Changing password")
                    password = json_data_dict["new_passowrd"]

        # Close the connection
        conn.close()

    except http.client.RemoteDisconnected:
        # If the connection was closed unexpectedly, wait for a bit and try again
        # print("Nothing to receive. Retrying in 5 seconds...")
        root.after(5000, check_for_server_api)

    # Schedule the function to run again after 2 seconds
    root.after(2000, check_for_server_api)


root = tk.Tk()
root.geometry("200x150")
root.title("Smart Safe Lock")

# Create a label to display the lock status
status_label = tk.Label(root, text="Lock is closed")
status_label.pack(pady=10)

# Create a button to show the current password
password_button = tk.Button(root, text="Show Password", command=show_password)
password_button.pack(pady=10)

# Schedule the check_lock_status function to run every 2 seconds
root.after(2000, check_for_server_api)

# Start the main loop
root.mainloop()
