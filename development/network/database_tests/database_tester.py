import sqlite3

def print_table_from_query(cursor, sql_query):
    cursor.execute(sql_query)
    rows = cursor.fetchall()

    # Retrieve the column names
    columns = [description[0] for description in cursor.description]

    abbreviated_rows = []
    password_index = columns.index('password') if 'password' in columns else None
    for row in rows:
        abbreviated_row = list(row)
        if password_index is not None:
            password = abbreviated_row[password_index]
            if isinstance(password, str) and len(password) > 3:
                abbreviated_row[password_index] = password[:3] + '...'
        abbreviated_rows.append(abbreviated_row)

    # Calculate the maximum width for each column
    column_widths = [max(len(str(row[i])) for row in abbreviated_rows + [columns]) for i in range(len(columns))]

    # Print the attribute names
    for column, width in zip(columns, column_widths):
        print(f"{column:{width}}", end=" ")
    print()

    # Print the data
    for index, row in enumerate(abbreviated_rows):
        for item, width in zip(row, column_widths):
            print(f"{item:{width}}", end=" ")
        print()

def update_user_password(user_id, new_password):
    conn = sqlite3.connect('smart_safe_box.db')
    cursor = conn.cursor()

    # Update the password for the specified user_id
    cursor.execute("UPDATE users SET password = ? WHERE username = ?", (new_password, 'master'))

    # Commit the changes and close the connection
    conn.commit()
    conn.close()

    return


# # Example usage
# conn = sqlite3.connect('smart_safe_box.db')
# cursor = conn.cursor()

# sql_query = '''
#         CREATE TABLE IF NOT EXISTS notifications_requests(
#             user_id TEXT,
#             login_attempt_request INTEGER,
#             FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
# '''
# print_table_from_query(cursor, sql_query)
# conn.close()

import sqlite3

def delete_notifications_table():
    conn = sqlite3.connect('smart_safe_box.db')
    cursor = conn.cursor()

    # Drop the table if it exists
    cursor.execute("DROP TABLE IF EXISTS notifications_requests")

    # Commit the changes and close the connection
    conn.commit()
    conn.close()

    return
delete_notifications_table()