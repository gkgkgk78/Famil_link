import numpy as np
import paho.mqtt.client as mqtt
import json
import time
import os
from json import JSONEncoder


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("subscribe")
        client.subscribe("/local/face/result/", 2)
    else:
        print("Bad connection Returned code=", rc)


def on_message(client, userdata, msg):
    if msg.topic == "/local/face/result/":
        data = json.loads(msg.payload.decode("utf-8"))
        print(data["name"], data["percent"])


client = mqtt.Client()

# 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_publish(메세지 발행)
client.on_connect = on_connect
client.on_message = on_message

# address : localhost, port: 1883 에 연결
client.connect('localhost', 1883)
client.loop_start()

while True:
    pass