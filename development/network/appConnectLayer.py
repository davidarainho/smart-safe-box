# app.py
from flask import Flask, request, jsonify

app = Flask(__name__)

user = [
    {"id": "User1", "username": "user1", "password": "pass1", "email": "email1", "active_locks": 3},
    {"id": "User2", "username": "user2", "password": "pass2", "email": "email2", "active_locks": 3},
    {"id": "User3", "username": "user3", "password": "pass3", "email": "email3", "active_locks": 3},
]

lock = [

]

@app.route('/')
def hello_world():
   return "Hello World"

@app.get("/user/username")
def get_username():
    args = request.args

    for i in range(len(user)):
        if(user[i].get("id") == args.get("name", default = "", type = str)):
            return jsonify(user[i].get("username"))
    

@app.get("/user/password")
def get_password():
    args = request.args

    for i in range(len(user)):
        if(user[i].get("id") == args.get("name", default = "", type = str)):
            return jsonify(user[i].get("password"))

@app.get("/user/email")
def get_email():
    args = request.args

    for i in range(len(user)):
        if(user[i].get("id") == args.get("name", default = "", type = str)):
            return jsonify(user[i].get("email"))

@app.get("/user/active_locks")
def get_active_lock():
    args = request.args

    for i in range(len(user)):
        if(user[i].get("id") == args.get("name", default = "", type = str)):
            return jsonify(user[i].get("active_locks"))

@app.post("/user/username")
def add_country():
    
    return {"error": "Request must be JSON"}, 415

if __name__ == '__main__':
   app.run(host = "0.0.0.0", debug = True)
