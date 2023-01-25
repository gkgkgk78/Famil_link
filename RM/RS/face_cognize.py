import cv2
import numpy as np
import paho.mqtt.client as mqtt
import json
import threading
from json import JSONEncoder
from keras.models import load_model
from multiprocessing import Process
import time

model = None
labels = None
client = None


class NumpyArrayEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return JSONEncoder.default(self, obj)


def on_message(client, userdata, msg):
    global isModel, model, labels

    if msg.topic == "/local/setModel/":
        tmp = bool(int(msg.payload.decode("utf-8")))
        if tmp:
            model = None
            labels = None

            print("loading model...")
            labels = open('labels.txt', 'r', encoding="UTF-8").readlines()
            model = load_model('keras_model.h5')
            print("loaded model!")
        else:
            model = None
            labels = None
        print("success model")


def on_connect(client, userdata, flags, rc):
    global pub_thread
    if rc == 0:
        client.subscribe("/local/setModel/", 2)
        # print("thread start")
        # if not pub_thread.is_alive():
        #     pub_thread.start()
    else:
        print("Bad connection Returned code=", rc)


def publish():
    global client, camera, model, labels
    while True:
        if model is None:
            continue
        try:
            copy_model = model
            copy_labels = labels

            # Grab the webcameras image.
            ret, image = camera.read()
            if not ret:
                print("dont read cam")
                break
            image = cv2.resize(image, (224, 224), interpolation=cv2.INTER_AREA)
            # client.publish("/local/opencv/", json.dumps(image, cls=NumpyArrayEncoder), 2)

            # Show the image in a window
            cv2.imshow('Webcam Image', image)
            # Make the image a numpy array and reshape it to the models input shape.
            np_image = np.asarray(image, dtype=np.float32).reshape(1, 224, 224, 3)

            # Normalize the image array
            np_image = (np_image / 127.5) - 1

            probabilities = copy_model.predict(np_image)
            pick = np.argmax(probabilities)
            percent = probabilities[0][pick]
            name = copy_labels[pick].strip().split(' ')[1]
            print(name)
            if percent > 0.70:
                data = {
                    "name": name,
                    "percent": str(percent),
                    "image": image
                }
                client.publish("/local/face/result/", json.dumps(data, cls=NumpyArrayEncoder), 2)

                print("result publish")
            cv2.waitKey(1)
        except Exception as e:
            print(e)


def mqtt_func():
    global client
    # 새로운 클라이언트 생성
    client = mqtt.Client()

    # 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_publish(메세지 발행)
    client.on_connect = on_connect

    # client.on_disconnect = on_disconnect
    client.on_message = on_message

    # address : localhost, port: 1883 에 연결
    client.connect('localhost', 1883)
    client.loop_start()


if __name__ == '__main__':
    try:
        # CAMERA can be 0 or 1 based on default camera of your computer.
        camera = cv2.VideoCapture(1)

        pub_thread = threading.Thread(target=publish, daemon=True)
        mqtt_thread = threading.Thread(target=mqtt_func)
        mqtt_thread.start()

        # mqtt_process = Process(target=mqtt_func)
        # mqtt_process.start()
        print("start")
        publish()
        while True:
            pass
    except Exception as e:
        print(e)

    print("exit")
