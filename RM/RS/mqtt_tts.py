import time

from gtts import gTTS
import playsound
import paho.mqtt.client as mqtt
import json
import os


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("subscribe")
        client.subscribe("/local/tts/", 2)
    else:
        print("Bad connection Returned code=", rc)


def on_message(client, userdata, msg):
    try:
        if msg.topic == "/local/tts/":
            data = json.loads(msg.payload.decode("utf-8"))
            print(data["msg"])
            speak(data["msg"])
    except Exception as e:
        print(e)


def speak(text):
    tts = gTTS(text=text, lang='ko')
    filename = 'voice.mp3'
    tts.save(filename)
    playsound.playsound(filename, True)
    os.remove(filename)


client = mqtt.Client()

# 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_publish(메세지 발행)
client.on_connect = on_connect
client.on_message = on_message

# address : localhost, port: 1883 에 연결
client.connect('localhost', 1883)
client.loop_start()

while True:
    pass
