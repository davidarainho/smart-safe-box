import sqlite3
import datetime

DATABASE_FILENAME = '../smart_safe_box.db'

# TODO


def get_device_id_and_app_code():
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    cursor.execute(
        "SELECT device_id, app_code FROM devices WHERE is_active = 0 LIMIT 1")

    # print(cursor.fetchall())
    device_id, app_code = cursor.fetchall()[0]
    conn.close()

    return device_id, app_code


def validate_user_id(user_id):
    user_id_str = str(user_id)

    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    cursor.execute(
        "SELECT COUNT(*) FROM users WHERE user_id = ?", (user_id_str,))
    count = cursor.fetchone()[0]

    conn.close()

    return count > 0


def check_user_access_pin(user_id, received_access_pin):
    received_access_pin = str(received_access_pin)
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    cursor.execute(
        "SELECT access_pin FROM users WHERE user_id = ?", (user_id,))
    stored_access_pin = cursor.fetchone()

    conn.close()

    if stored_access_pin is not None and received_access_pin == stored_access_pin[0]:
        return True
    else:
        return False


def get_device_users_ids(device_id):
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    sql_query = '''
    SELECT user_id
    FROM users_doors
    JOIN doors ON users_doors.door_id = doors.door_id
    JOIN devices ON doors.device_id = devices.device_id
    WHERE devices.device_id = ?
    GROUP BY user_id
    '''

    cursor.execute(sql_query, (device_id,))
    users_ids = cursor.fetchall()
    user_ids_list = [int(row[0]) for row in users_ids]

    conn.close()

    return user_ids_list


def get_user_doors_ids_in_device(user_id, device_id):
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    sql_query = '''
    SELECT doors.real_door, doors.door_id
    FROM doors
    JOIN users_doors ON users_doors.door_id = doors.door_id
    JOIN devices ON doors.device_id = devices.device_id
    WHERE devices.device_id = ? AND user_id = ?
    '''
    # GROUP BY user_id

    cursor.execute(sql_query, (device_id, user_id,))
    users_doors = cursor.fetchall()
    user_doors_real_list = [int(row[0]) for row in users_doors]
    user_doors_ids_list = [int(row[1]) for row in users_doors]

    conn.close()

    return user_doors_real_list, user_doors_ids_list


def get_user_access_pin(user_id):

    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    sql_query = '''
    SELECT access_pin FROM users WHERE user_id = ?
    '''
    # GROUP BY user_id

    cursor.execute(sql_query, (user_id,))
    access_pin = cursor.fetchall()[0][0]

    conn.close()
    return access_pin


def change_user_access_pin(user_id, new_access_pin):
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    # Convert new_access_pin to a string if it's an integer
    if isinstance(new_access_pin, int):
        new_access_pin = str(new_access_pin)

    # Update the access_pin for the specified user_id
    cursor.execute(
        "UPDATE users SET access_pin = ? WHERE user_id = ?", (new_access_pin, user_id))

    # Commit the changes and close the connection
    conn.commit()
    conn.close()

    return


def get_doors_to_open(device_id):
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    sql_query = '''
    SELECT real_door, door_id
    FROM doors
    WHERE door_request = 1 AND device_id = ?
    '''

    cursor.execute(sql_query, (device_id,))
    ret = cursor.fetchall()
    real_doors_list = [int(row[0]) for row in ret]
    doors_list = [int(row[1]) for row in ret]

    conn.close()

    return real_doors_list, doors_list


def set_door_requests(doors_ids, door_request):
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    # Construct the SQL query
    sql_query = "UPDATE doors SET door_request = ? WHERE door_id IN ({})".format(
        ','.join(['?'] * len(doors_ids)))

    # Execute the query with the door IDs and door_request value
    cursor.execute(sql_query, [door_request] + doors_ids)

    # Commit the changes and close the connection
    conn.commit()
    conn.close()


def set_false_try_trigger(user_id, trigger_value):
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    # Create the trigger
    cursor.execute(f"""
        UPDATE users SET false_try_trigger = {trigger_value}
            WHERE users.user_id = ?;
    """, (user_id,))

    # Commit the changes and close the connection
    conn.commit()
    conn.close()

    return


def set_pin_change_notification(user_id, value):
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    # Check if the user_id already exists in the table
    cursor.execute(
        "SELECT COUNT(*) FROM notifications_requests WHERE user_id = ?", (user_id,))
    count = cursor.fetchone()[0]

    if count > 0:
        # Update the existing row
        cursor.execute(
            "UPDATE notifications_requests SET pin_change_notification = ? WHERE user_id = ?", (value, user_id))
    else:
        # Insert a new row
        cursor.execute(
            "INSERT INTO notifications_requests (user_id, pin_change_notification) VALUES (?, ?)", (user_id, value))

    # Commit the changes and close the connection
    conn.commit()
    conn.close()


def close_all_doors():
    door_state = 0
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    # Construct the SQL query
    sql_query = "UPDATE doors SET door_state = ?"

    # Execute the query with the state value
    cursor.execute(sql_query, (door_state,))

    # Commit the changes and close the connection
    conn.commit()
    conn.close()


def check_for_open_port():
    door_state = 0
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    # Construct the SQL query
    sql_query = "SELECT * FROM doors WHERE door_state = 1"

    # Execute the query with the state value
    cursor.execute(sql_query)

    if len(cursor.fetchall()) == 0:
        ret = False
    else:
        ret = True

    # Commit the changes and close the connection
    conn.commit()
    conn.close()

    return ret


def change_door_state(door_ids, state):
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()

    # Construct the SQL query
    sql_query = "UPDATE doors SET door_state = ? WHERE door_id IN ({})".format(
        ','.join(['?'] * len(door_ids)))

    # Execute the query with the state value and door IDs
    cursor.execute(sql_query, [state] + door_ids)

    # Commit the changes and close the connection
    conn.commit()
    conn.close()


def update_last_access(user_id, door_id):

    # Create a connection to the database
    conn = sqlite3.connect(DATABASE_FILENAME)
    conn.execute("PRAGMA foreign_keys = ON")  # Enable foreign key constraint

    # Create a cursor object
    cursor = conn.cursor()

    cursor.execute("SELECT username FROM users WHERE user_id = ?", (user_id,))
    username = cursor.fetchone()[0]

    # Get the current date and time
    now = datetime.datetime.now()
    # Format it as a string
    now_str = now.strftime("%Y-%m-%d")

    try:
        # Fetch the current last_access and user_last_access fields for the door
        cursor.execute(
            "SELECT last_access, user_last_access FROM doors WHERE door_id = ?", (door_id,))
        result = cursor.fetchone()
        if result is None:
            print("No such door exists.")
            return False

        last_access_current, user_last_access_current = result

        # Append new access data to existing data
        if last_access_current is not None and last_access_current != "":
            last_access_new = last_access_current + ',' + now_str
        else:
            last_access_new = now_str

        if user_last_access_current is not None and user_last_access_current != "":
            user_last_access_new = user_last_access_current + ',' + username
        else:
            user_last_access_new = username

        # Update the last_access and user_last_access fields with new data
        cursor.execute(
            "UPDATE doors SET last_access = ?, user_last_access = ? WHERE door_id = ?",
            (last_access_new, user_last_access_new, door_id)
        )
        conn.commit()

    except sqlite3.Error as error:
        print(f"Failed to update data in sqlite table", error)
        return False

    finally:
        # Close the database connection
        conn.close()

    return True


if __name__ == "__main__":
    # get_device_id_and_app_code()
    # print(get_device_users_ids(404582))
    # print(get_user_doors_ids_in_device(815534, 404582))
    # print(get_user_access_pin(815534))
    # print(validate_user_id(1234567))
    # print(check_user_access_pin('815534', 1224))
    # set_door_requests([712730], 1)
    # print(get_doors_to_open(404582))
    # change_user_access_pin(283404, 5678)
    # set_false_try_trigger(283404, 1)
    # set_pin_change_notification(283404, 1)
    change_door_state([18817], 1)
