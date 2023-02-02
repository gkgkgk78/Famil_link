import numpy as np
import paho.mqtt.client as mqtt
import json
from keras.models import load_model
import time
import os
from json import JSONEncoder
import threading

import requests

isFile = False
folder_path = os.path.dirname(os.path.realpath(__file__))


class NumpyArrayEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return JSONEncoder.default(self, obj)


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("subscribe")
        client.subscribe("/local/opencv/", 2)
    else:
        print("Bad connection Returned code=", rc)


ones = True


def on_message(client, userdata, msg):
    global model, labels, ones
    if not ones:
        return
    if msg.topic == "/local/opencv/":
        data = json.loads(msg.payload.decode("utf-8"))
        # print(data)
        image = np.asarray(data["image"])
        np_image = np.asarray(image, dtype=np.float32).reshape(1, 224, 224, 3)

        # Normalize the image array
        np_image = (np_image / 127.5) - 1

        probabilities = model.predict(np_image)
        pick = np.argmax(probabilities)
        percent = probabilities[0][pick]
        name = labels[pick].strip().split(' ')[1]
        # print(name)
        if percent > 0.90:
            data = {
                "name": name,
                "percent": str(percent),
                "image": image
            }
            client.publish("/local/face/result/", json.dumps(data, cls=NumpyArrayEncoder), 2)
            print(name)
            requests.post("http://localhost:9999/member/login",
                          headers={
                              "Content-type": "application/json",
                              "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImxldmVsIjoiYWNjb3VudCIsImlhdCI6MTY3NTMxMjA5MywiZXhwIjoxNjg1MzEyMDkzfQ._EZkmbK3vj2BkLAQ2mTReq2lajuhpVifWDrtIZhuaj0"
                          },
                          data=json.dumps(data["image"], cls=NumpyArrayEncoder))
            ones = False
            # print("result publish")


def file_check():
    global isFile, folder_path
    while True:
        isFile = os.path.isfile(folder_path + '\\keras_model.h5') and os.path.isfile(folder_path + '\\labels.txt')


file_thread = threading.Thread(target=file_check)
file_thread.start()

while not isFile:
    print('dont exist AI model')
    time.sleep(1)

print('load model')
labels = open(folder_path + '\\labels.txt', 'r', encoding="UTF-8").readlines()
model = load_model(folder_path + '\\keras_model.h5')
print('success load model')

client = mqtt.Client()

# 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_publish(메세지 발행)
client.on_connect = on_connect
client.on_message = on_message

# address : localhost, port: 1883 에 연결
client.connect('localhost', 1883)
client.loop_start()
