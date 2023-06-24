import sqlite3
import json
import hashlib
import random

conn = sqlite3.connect('smart_safe_box.db')
cursor = conn.cursor()

cursor.execute('''CREATE TABLE IF NOT EXISTS users(
                    id INTEGER PRIMARY KEY,
                    username TEXT,
                    password_hash TEXT,
                    email TEXT,
                    is_admin BOOLEAN,
                    access_pin TEXT)''')

cursor.execute('''CREATE TABLE IF NOT EXISTS devices(
                    id TEXT UNIQUE PRIMARY KEY,
                    app_code INTEGER,
                    is_activated BOOLEAN)''')

cursor.execute('''CREATE TABLE IF NOT EXISTS doors(
                    id TEXT UNIQUE PRIMARY KEY,
                    lock_id TEXT,
                    name TEXT,
                    last_access DATE,
                    lock_request BOOLEAN,
                    user_last_access DATE,
                    FOREIGN KEY (lock_id) REFERENCES devices(id))''')

cursor.execute('''CREATE TABLE IF NOT EXISTS users_doors (
                    door_id TEXT,
                    user_id TEXT,
                    access_level INTEGER,
                    expiration_date DATE,
                    FOREIGN KEY (door_id) REFERENCES doors(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )''')

cursor.execute('''CREATE TABLE IF NOT EXISTS notifications (
                    door_id TEXT,
                    notify_forced_entry BOOLEAN,
                    notify_access_pin_change BOOLEAN,
                    FOREIGN KEY (door_id) REFERENCES doors(id)
                )''')

cursor.execute('''CREATE TABLE IF NOT EXISTS log (
                    door_id TEXT,
                    user_id TEXT,
                    time DATE,
                    FOREIGN KEY (door_id) REFERENCES doors(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )''')

