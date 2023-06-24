import sqlite3
import hashlib
import json
import datetime
import random
from NEW_userinfo import *

initialize_db()

for i in range(1,3):
    create_device()
    
user_dict = {
    "username": "master",
    "password": hashlib.sha256(str(123456).encode()).hexdigest(),
    "email": "master@supersmartbox.com",
    "is_admin": 1,
    "allow_notifications": 0,
    "access_pin": "000000",
    "false_try_trigger": 0,
}

master_user = json.dumps(user_dict)

#print(master_user)

add_user(master_user)

