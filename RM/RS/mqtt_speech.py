import json

import speech_recognition as sr
import paho.mqtt.client as mqtt

isConnect = False


def on_connect(client, userdata, flags, rc):
    global isConnect
    if rc == 0:
        print("연결됨")
        isConnect = True
    else:
        print("Bad connection Returned code=", rc)


client = mqtt.Client()

# 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_publish(메세지 발행)
client.on_connect = on_connect

# address : localhost, port: 1883 에 연결
client.connect('localhost', 1883)
client.loop_start()

try:
    while True:
        if not isConnect:
            continue

        r = sr.Recognizer()
        speech = None
        with sr.Microphone() as source:
            print("Say Something")
            try:
                speech = r.listen(source)
            except Exception as e:
                print(e)

        # sys.stdout = open('audio_output.txt', 'w') #-- 텍스트 저장시 사용
        print("gett")
        try:
            if speech is not None:
                audio = r.recognize_google(speech, language="ko-KR")
                client.publish("/local/sound/", json.dumps({'text': audio}), 2)
                print("publish : " + audio)
        except sr.UnknownValueError:
            print("Your speech can not understand")
        except sr.RequestError as e:
            print("Request Error!; {0}".format(e))
except Exception as e:
    print(e)
