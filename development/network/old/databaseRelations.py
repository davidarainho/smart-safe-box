def save_to_database(json_data):
    # Parse the JSON data to a Python dictionary
    data = json.loads(json_data)

    # Connect to the SQLite database (create a new one if it doesn't exist)
    conn = sqlite3.connect('database.db')
    cursor = conn.cursor()

    # Create a table for users
    cursor.execute('''CREATE TABLE IF NOT EXISTS users
                      (username TEXT PRIMARY KEY,
                      password TEXT,
                      email TEXT,
                      accessLevel0 INTEGER)''')

    # Create a table for active locks
    cursor.execute('''CREATE TABLE IF NOT EXISTS active_locks
                      (username TEXT,
                      lockId TEXT,
                      accessLevel INTEGER)''')

    # Create a table for activated locks
    cursor.execute('''CREATE TABLE IF NOT EXISTS activated_locks
                      (lockID TEXT PRIMARY KEY,
                      Name TEXT,
                      Location TEXT,
                      State INTEGER,
                      pinLock TEXT,
                      maxNumUsers INTEGER)''')

    # Insert data into the users table
    for user in data['users']:
        cursor.execute('''INSERT INTO users (username, password, email, accessLevel0)
                          VALUES (?, ?, ?, ?)''',
                       (user['username'], user['password'], user['email'], user['accessLevel0']))

        # Insert data into the active_locks table
        for active_lock in user['active_locks']:
            cursor.execute('''INSERT INTO active_locks (username, lockId, accessLevel)
                              VALUES (?, ?, ?)''',
                           (user['username'], active_lock['lockId'], active_lock['accessLevel']))

    # Insert data into the activated_locks table
    for activated_lock in data['unactivatedLocks']:
        cursor.execute('''INSERT INTO activated_locks (lockID, Name, Location, State, pinLock, maxNumUsers)
                          VALUES (?, ?, ?, ?, ?, ?)''',
                       (activated_lock['lockID'], activated_lock['Name'], activated_lock['Location'],
                        activated_lock['State'], activated_lock['pinLock'], activated_lock['maxNumUsers']))

    # Commit the changes and close the connection
    conn.commit()
    conn.close()

def read_from_database():
    # Connect to the SQLite database
    conn = sqlite3.connect('database.db')
    cursor = conn.cursor()

    # Fetch users data
    cursor.execute('SELECT * FROM users')
    users = cursor.fetchall()

    # Fetch active locks data
    cursor.execute('SELECT * FROM active_locks')
    active_locks = cursor.fetchall()

    # Fetch activated locks data
    cursor.execute('SELECT * FROM activated_locks')
    activated_locks = cursor.fetchall()

    # Create a dictionary to store the retrieved data
    data = {
        'users': [],
        'activatedLocks': [],
        'unactivatedLocks': []
    }

    # Process users data
    for user in users:
        user_dict = {
            'username': user[0],
            'password': user[1],
            'email': user[2],
            'accessLevel0': user[3],
            'active_locks': []
        }
        # Append the active locks data for each user
        for active_lock in active_locks:
            if active_lock[0] == user[0]:
                user_dict['active_locks'].append({
                    'lockId': active_lock[1],
                    'accessLevel': active_lock[2]
                })
        data['users'].append(user_dict)

    # Process activated