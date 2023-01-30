import threading

import cv2
import numpy as np
import paho.mqtt.client as mqtt
import json
from json import JSONEncoder


class NumpyArrayEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return JSONEncoder.default(self, obj)


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        t1 = threading.Thread(target=opencv_publish)
        t1.start()
    else:
        print("Bad connection Returned code=", rc)


def opencv_publish():
    global camera, client
    while True:
        ret, image = camera.read()
        if not ret:
            print("dont read cam")
            break
        image = cv2.resize(image, (224, 224), interpolation=cv2.INTER_AREA)
        # client.publish("/local/opencv/", json.dumps(image, cls=NumpyArrayEncoder), 2)

        # Show the image in a window
        # cv2.imshow('Webcam Image', image)
        data = {
            "image": image
        }
        client.publish("/local/opencv/", json.dumps(data, cls=NumpyArrayEncoder), 2)
        # print("publish opencv data")
        cv2.waitKey(100)  # MQTT 성능에 따라 유도리 있게 설정


camera = cv2.VideoCapture(1)

client = mqtt.Client()

# 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_publish(메세지 발행)
client.on_connect = on_connect

# address : localhost, port: 1883 에 연결
client.connect('localhost', 1883)
client.loop_start()

while True:
    pass
