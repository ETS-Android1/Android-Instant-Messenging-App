# coding:utf8
# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.

import firebase_admin
import mysql.connector
from flask import Flask, jsonify, request
from task import sendPush
from firebase_admin import credentials, messaging

app = Flask(__name__)


# token = ["fCsdbc46RTWejoNG0U9-ED:APA91bGGufEB8o38d_TBX-15lSslgAAXZcwwFPLzaBkCjnXbPlJothrtqFrvAvj"
#          "-mlI00Rz0DxRH84vR_kJWd8F5DMAcHABIvTF-vfiuqtXtuwTw43YFZ9xUNgIK8cYk40QxaDnyF-Ft"]


class MyDB:
    conn = None
    cursor = None

    def __init__(self):
        self.connect()
        return

    def connect(self):
        self.conn = mysql.connector.connect(
            host="localhost",
            user="dbuser",
            password="password",
            database="iems5722",
        )
        self.cursor = self.conn.cursor(dictionary=True)
        return


# i dont know what is the format of chatrooms
@app.route("/api/a3/get_chatrooms")
def get_chatroom():
    myDB = MyDB()
    userid = request.args.get("userid")
    query = "SELECT id, name FROM chatrooms where userid = %s"
    params = (userid,)
    myDB.cursor.execute(query, params)
    chatrooms = myDB.cursor.fetchall()
    myDB.conn.close()
    if chatrooms:
        return jsonify(data=chatrooms, status="OK")
    else:
        return jsonify(messages="There is no chatroom", status="ERROR")


@app.route("/api/a3/get_messages")
def get_message():
    numberperpage = 5
    myDB = MyDB()
    cr_id = request.args.get("chatroom_id")
    page = int(request.args.get("page"))
    query = "SELECT message,name,message_time,user_id FROM messages WHERE chatroom_id = %s LIMIT %s , %s"
    query_count = "SELECT COUNT(*) FROM messages where chatroom_id = %s"
    params = (cr_id, (page - 1) * numberperpage, numberperpage)
    params_count = (cr_id,)
    myDB.cursor.execute(query, params)
    messages = myDB.cursor.fetchall()
    myDB.cursor.execute(query_count, params_count)
    messages_total = myDB.cursor.fetchone()
    total = int(messages_total["COUNT(*)"] / numberperpage) + 1
    myDB.conn.close()
    if total >= page:
        if messages:
            if messages_total:
                return jsonify(data={"current_page": page, "messages": messages, "total_pages": total}, status="OK")
    else:
        return jsonify(messages="Wrong Chatroom ID or Pages", status="ERROR")


@app.route("/api/a3/send_message", methods=["POST"])
def send_message():
    myDB = MyDB()
    cr_id = request.form.get("chatroom_id")
    ur_id = request.form.get("user_id")
    ur_name = request.form.get("name")
    content = request.form.get("message")
    if cr_id:
        if ur_id:
            if ur_name:
                if content:
                    query = "INSERT INTO messages (chatroom_id,user_id,name,message) VALUES (%s,%s,%s,%s)"
                    query_2 = "SELECT * FROM push_tokens "
                    query_3 = "SELECT name FROM chatrooms WHERE id = %s"
                    params = (cr_id, ur_id, ur_name, content)
                    myDB.cursor.execute(query, params)
                    myDB.conn.commit()
                    params_2 = (cr_id,)
                    myDB.cursor.execute(query_3, params_2)
                    title = myDB.cursor.fetchall()
                    bigtitle = ""
                    if len(title) != 0:
                        for row in title:
                            bigtitle = row["name"]
                    myDB.cursor.execute(query_2)
                    tokens = myDB.cursor.fetchall()
                    payload = []
                    if len(tokens) != 0:
                        for row in tokens:
                            token = row["token"]
                            payload.append(str(token))
                    sendPush.delay(bigtitle, content, payload)
                    myDB.conn.close()
                    return jsonify(status="OK")
                else:
                    return jsonify(messages="Input Error", status="ERROR")


@app.route("/api/a4/submit_push_token", methods=["POST"])
def insert_token():
    myDB = MyDB()
    user_id = request.form.get("user_id")
    token = request.form.get("token")
    if user_id:
        if token:
            query = "INSERT INTO push_tokens (user_id, token) VALUES (%s,%s)"
            params = (user_id, token)
            myDB.cursor.execute(query, params)
            myDB.conn.commit()
            myDB.conn.close()
            return jsonify(status="OK")
        else:
            return jsonify(messages="Input Error", status="ERROR")
    else:
        return jsonify(messages="Input Error", status="ERROR")


@app.route("/api/a4/gettoken")
def get_token():
    mydb = MyDB()
    user_id = request.args.get("user_id")
    query = "SELECT token FROM push_tokens WHERE user_id = %s"
    params = (user_id,)
    mydb.cursor.execute(query, params)
    token_a = mydb.cursor.fetchall()
    return jsonify(token=token_a)


@app.route('/api/project/registeruser', methods=['POST'])
def registeruser():
    mydb = MyDB()
    print("==========start register user=========")
    username = request.form.get("username")
    password = request.form.get("password")
    select_query = "SELECT * FROM users where username = %s"
    param = (username,)
    mydb.cursor.execute(select_query, param)
    records = mydb.cursor.fetchall()
    print("records: " + str(len(records)))
    if len(records) != 0:
        mydb.conn.close()
        return jsonify(status="ERROR")
    else:
        insert_query = "INSERT INTO users (username, password) VALUES (%s, %s)"
        insert_values = (username, password)
        mydb.cursor.execute(insert_query, insert_values)
        mydb.conn.commit()
        mydb.conn.close()
        return jsonify(status="OK")


@app.route('/api/project/loginuser', methods=['POST'])
def loginuser():
    mydb = MyDB()
    print("==========start login user=========")
    username = request.form.get("username")
    password = request.form.get("password")
    print(username + ";" + password)
    select_query = "SELECT * FROM users where username = %s and password = %s"
    params = (username, password)
    mydb.cursor.execute(select_query, params)
    records = mydb.cursor.fetchall()
    print("records: " + str(len(records)))
    query = "SELECT id FROM users where username = %s and password = %s"
    params_2 = (username, password)
    mydb.cursor.execute(query, params_2)
    userid = mydb.cursor.fetchone()
    if len(records) != 0:
        mydb.conn.close()
        return jsonify(status="OK", id=userid["id"])
    else:
        mydb.conn.close()
        return jsonify(status="ERROR")


@app.route("/api/project/get_friends_lists")
def getfriendlists():
    mydb = MyDB()
    userid = request.args.get("userid")
    query = "SELECT b.username FROM friends a INNER JOIN users b ON a.userid = %s and a.friendid=b.id"
    param = (userid,)
    mydb.cursor.execute(query, param)
    result = mydb.cursor.fetchall()
    if not result:
        return jsonify(status="ERROR")
    else:
        return jsonify(status="OK", friend=result)


@app.route("/api/project/add_user", methods=["POST"])
def adduser():
    mydb = MyDB()  # 獲取數據庫
    userid = request.form.get("userid")  # 獲取參數
    friendid = request.form.get("friendid")
    query1 = "SELECT * FROM users where id = %s"
    param1 = (friendid,)
    param = (userid,)
    mydb.cursor.execute(query1, param1)
    result1 = mydb.cursor.fetchone()
    mydb.cursor.execute(query1, param)
    result = mydb.cursor.fetchone()
    if not result1:
        mydb.conn.close()
        return jsonify(status="ERROR", type="1")
    else:
        friendname = result1["username"]
        username = result["username"]
        query2 = "SELECT * FROM friends where userid = %s and friendid = %s"
        params2 = (userid, friendid)
        mydb.cursor.execute(query2, params2)
        result2 = mydb.cursor.fetchall()
        if result2:
            mydb.conn.close()
            return jsonify(status="ERROR", type="2")
        else:
            query3 = "INSERT INTO friends (userid, friendid) VALUES (%s,%s)"
            query5 = "INSERT INTO chatrooms (name, userid) VALUES (%s,%s)"
            params3 = (userid, friendid)
            params4 = (friendid, userid)
            mydb.cursor.execute(query3, params3)
            mydb.conn.commit()
            mydb.cursor.execute(query3, params4)
            mydb.conn.commit()
            params5 = (username, friendid)
            params6 = (friendname, userid)
            mydb.cursor.execute(query5, params5)
            mydb.conn.commit()
            mydb.cursor.execute(query5, params6)
            mydb.conn.commit()
            mydb.conn.close()
            return jsonify(status="OK")


@app.route('/api/project/editpwd', methods=['POST'])
def editpwd():
    mydb = MyDB()
    print("==========start edit pwd=========")
    username = request.form.get("username")
    password = request.form.get("password")
    new_pwd = request.form.get("new_pwd")
    print(username + ";" + password + ";" + new_pwd)
    select_query = "SELECT * FROM users where username = %s and password = %s"
    params = (username, password)
    mydb.cursor.execute(select_query, params)
    records = mydb.cursor.fetchall()
    if len(records) != 0:
        update_query = "UPDATE users SET password= %s where username = %s"
        params = (new_pwd, username)
        mydb.cursor.execute(update_query, params)
        mydb.conn.commit()
        mydb.conn.close()
        return jsonify(status="OK")
    else:
        mydb.conn.close()
        return jsonify(status="ERROR")


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=8080)

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
