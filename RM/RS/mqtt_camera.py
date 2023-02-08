from __future__ import print_function, division

import subprocess
import time
import wave
import pyaudio
import threading
import os
import cv2
import numpy as np
import paho.mqtt.client as mqtt
import json
from json import JSONEncoder
import mediapipe as mp
import requests

mp_face_detection = mp.solutions.face_detection
mp_drawing = mp.solutions.drawing_utils

isRecord = False
token = None
isQr = True

from_member_uid = 4
to_member_uid = 7


class VideoRecorder():
    "Video class based on openCV"

    def __init__(self, name="temp_video.avi", fps=30):
        self.file_name = name
        self.frame_counts = 0
        self.out = cv2.VideoWriter(self.file_name, fourcc, fps, size)  # VideoWriter 객체 생성
        self.open = True
        self.start_time = time.time()

    def record(self):
        global camera
        print("start")
        while self.open:
            ret, frame = camera.read()
            if ret:
                self.out.write(frame)  # 파일 저장
                self.frame_counts += 1
                # print("write !")
            else:
                print('dont read frame')
                break
        self.out.release()
        print("video su" + str(time.time()))

    def stop(self):
        "Finishes the video recording therefore the thread too"
        if self.open:
            self.open = False

    def start(self):
        "Launches the video recording function using a thread"
        video_thread = threading.Thread(target=self.record)
        video_thread.start()


class AudioRecorder():
    "Audio class based on pyAudio and Wave"

    def __init__(self, filename="temp_audio.wav", rate=44100, fpb=1024, channels=1):
        self.open = True
        self.rate = rate
        self.frames_per_buffer = fpb
        self.channels = channels
        self.format = pyaudio.paInt16
        self.audio_filename = filename
        self.audio = pyaudio.PyAudio()
        self.stream = self.audio.open(format=self.format,
                                      channels=self.channels,
                                      rate=self.rate,
                                      input=True,
                                      frames_per_buffer=self.frames_per_buffer)
        self.audio_frames = []

    def record(self):
        "Audio starts being recorded"
        self.stream.start_stream()
        while self.open:
            data = self.stream.read(self.frames_per_buffer)
            self.audio_frames.append(data)
            if not self.open:
                break
        print("audio su: " + str(time.time()))

    def stop(self):
        "Finishes the audio recording therefore the thread too"
        if self.open:
            self.open = False
            self.stream.stop_stream()
            self.stream.close()
            self.audio.terminate()
            waveFile = wave.open(self.audio_filename, 'wb')
            waveFile.setnchannels(self.channels)
            waveFile.setsampwidth(self.audio.get_sample_size(self.format))
            waveFile.setframerate(self.rate)
            waveFile.writeframes(b''.join(self.audio_frames))
            waveFile.close()

    def start(self):
        "Launches the audio recording function using a thread"
        audio_thread = threading.Thread(target=self.record)
        audio_thread.start()


def start_AVrecording(filename="test"):
    global video_thread
    global audio_thread
    video_thread = VideoRecorder()
    audio_thread = AudioRecorder()
    audio_thread.start()
    video_thread.start()
    return filename


def start_video_recording(filename="test"):
    global video_thread
    video_thread = VideoRecorder()
    video_thread.start()
    print("video recording start th1")
    return filename


def start_audio_recording(filename="test"):
    global audio_thread
    audio_thread = AudioRecorder()
    audio_thread.start()
    print("audio recording start th1")
    return filename


def file_manager(filename="test"):
    "Required and wanted processing of final files"
    local_path = os.getcwd()
    if os.path.exists(str(local_path) + "/temp_audio.wav"):
        os.remove(str(local_path) + "/temp_audio.wav")
    if os.path.exists(str(local_path) + "/temp_video.avi"):
        os.remove(str(local_path) + "/temp_video.avi")
    if os.path.exists(str(local_path) + "/temp_video2.avi"):
        os.remove(str(local_path) + "/temp_video2.avi")


def stop_AVrecording(filename="test"):
    try:
        audio_thread.stop()
        frame_counts = video_thread.frame_counts
        elapsed_time = time.time() - video_thread.start_time
        recorded_fps = frame_counts / elapsed_time
        print("total frames " + str(frame_counts))
        print("elapsed time " + str(elapsed_time))
        print("recorded fps " + str(recorded_fps))
        video_thread.stop()

        # Makes sure the threads have finished
        while threading.active_count() > 2:
            print(threading.active_count())
            time.sleep(1)

        # Merging audio and video signal
        if abs(recorded_fps - 6) >= 0.01:  # If the fps rate was higher/lower than expected, re-encode it to the expected
            print("Re-encoding")
            cmd = "C:/Users/SSAFY/ffmpeg/bin/ffmpeg -r " + str(
                recorded_fps) + " -i temp_video.avi -pix_fmt yuv420p -r 30 temp_video2.avi"
            subprocess.call(cmd, shell=True)
            # res = os.system(cmd)
            print("Muxing")
            cmd = "C:/Users/SSAFY/ffmpeg/bin/ffmpeg -y -ac 2 -channel_layout stereo -i temp_audio.wav -i temp_video2.avi -pix_fmt yuv420p record.mp4"
            subprocess.call(cmd, shell=True)
            # res = os.system(cmd)
        else:
            print("Normal recording\nMuxing")
            cmd = "C:/Users/SSAFY/ffmpeg/bin/ffmpeg -y -ac 2 -channel_layout stereo -i temp_audio.wav -i temp_video.avi -pix_fmt yuv420p record.mp4"
            subprocess.call(cmd, shell=True)
            print("..")
            # 보내고자하는 파일을 'rb'(바이너리 리드)방식 열고

        files = open('record.mp4', 'rb')

        # 파이썬 딕셔너리 형식으로 file 설정
        upload = {'imgUrlBase': files}

        # request.post방식으로 파일전송.
        res = requests.post(
            'http://i8a208.p.ssafy.io:3000/movie?from_member_uid=' + str(from_member_uid) + '&to_member_uid=' + str(
                to_member_uid),
            files=upload)
        # headers={
        #     "Authorization": "Bearer " + str(token)
        # }
        # )
        # TODO: 추후 token 추가하여 전송 지금은 테스트단계
    except Exception as e:
        print(e)
    finally:
        file_manager()


class NumpyArrayEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return JSONEncoder.default(self, obj)


def on_connect(client, userdata, flags, rc):
    global t1, isRecord, token

    if rc == 0:
        t1 = threading.Thread(target=opencv_publish)
        t1.start()
        client.subscribe("/local/record/", 2)
        client.subscribe("/local/token/", 2)
    else:
        print("Bad connection Returned code=", rc)


def on_message(client, userdata, msg):
    global model, labels, isRecord, token, from_member_uid, to_member_uid, isQr
    if msg.topic == "/local/record/":
        try:
            temp = bool(int(msg.payload.decode("utf-8")))

            if temp == isRecord:
                return

            isRecord = temp

            if isRecord:
                print("record on msg")
                start_AVrecording()
            else:
                print("record on stop")
                stop_AVrecording()

                print("restart pub")
                t1 = threading.Thread(target=opencv_publish)
                t1.start()
        except Exception as e:
            print(e)
    elif msg.topic == "/local/token/":
        data = json.loads(msg.payload.decode("utf-8"))
        token = data['token']
        from_member_uid = data['from']
        to_member_uid = data['to']
    elif msg.topic == "/local/qr/":
        temp = bool(int(msg.payload.decode("utf-8")))
        isQr = temp


def opencv_publish():
    global camera, client, isRecord, isQr
    idx = 0
    if isQr:
        while True:
            ret, image = camera.read()
            if not ret:
                print("dont read cam")
                break
            # image = cv2.resize(image, None, fx=0.2, fy=0.2, interpolation=cv2.INTER_AREA)
            qr = cv2.QRCodeDetector()
            data, box, straight_qrcode = qr.detectAndDecode(image)
            if data:
                print('QR코드 데이터: {}'.format(data))
                client.publish("/local/qrtoken/", json.dumps(data), 2)
                print("publish qr data")
    else:
        while True:
            if isRecord:
                break
            ret, image = camera.read()
            if not ret:
                print("dont read cam")
                break
            # client.publish("/local/opencv/", json.dumps(image, cls=NumpyArrayEncoder), 2)

            # Show the image in a window
            # cv2.imshow('Webcam Image', image)

            ############################
            #                          #
            # To improve performance, optionally mark the image as not writeable to
            # pass by reference.
            with mp_face_detection.FaceDetection(
                    model_selection=0, min_detection_confidence=0.5) as face_detection:

                image.flags.writeable = False
                image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
                results = face_detection.process(image)

                # Draw the face detection annotations on the image.
                image.flags.writeable = True
                image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

                if results.detections:
                    for detection in results.detections:
                        # mp_drawing.draw_detection(image, detection)
                        # bbox_drawing_spec = mp_drawing.DrawingSpec()
                        # mp_drawing.draw_detection(image, detection)
                        image_rows, image_cols, _ = image.shape
                        location = detection.location_data
                        relative_bounding_box = location.relative_bounding_box
                        rect_start_point = mp_drawing._normalized_to_pixel_coordinates(
                            relative_bounding_box.xmin, relative_bounding_box.ymin, image_cols,
                            image_rows)
                        rect_end_point = mp_drawing._normalized_to_pixel_coordinates(
                            relative_bounding_box.xmin + relative_bounding_box.width,
                            relative_bounding_box.ymin + relative_bounding_box.height, image_cols,
                            image_rows)

                        if rect_start_point is None or rect_end_point is None:
                            image = np.zeros((224, 224, 3), np.uint8)
                            break
                        if None in rect_start_point or None in rect_end_point:
                            image = np.zeros((224, 224, 3), np.uint8)
                            break

                        image = image[rect_start_point[1]:rect_end_point[1], rect_start_point[0]:rect_end_point[0]]
                        image = cv2.resize(image, (224, 224), interpolation=cv2.INTER_AREA)
                else:
                    image = np.zeros((224, 224, 3), np.uint8)
                # Flip the image horizontally for a selfie-view display.
            # cv2.imshow("dd", image)
            ############################
            data = {
                "image": image
            }
            client.publish("/local/opencv/", json.dumps(data, cls=NumpyArrayEncoder), 2)  # TODO: 해제
            print("publish opencv data " + str(idx))
            idx += 1
            cv2.waitKey(150)  # MQTT 성능에 따라 유도리 있게 설정


print("1")
camera = cv2.VideoCapture(0)
print("2")
fourcc = cv2.VideoWriter_fourcc(*"MJPG")  # 인코딩 포맷 문자
camera.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)  # 1280
camera.set(cv2.CAP_PROP_FRAME_HEIGHT, 720)  # 720
print("3")
width = camera.get(cv2.CAP_PROP_FRAME_WIDTH)
height = camera.get(cv2.CAP_PROP_FRAME_HEIGHT)
size = (int(width), int(height))  # 프레임 크기
print("size : " + str(size))

t1 = threading.Thread(target=opencv_publish)

client = mqtt.Client()

# 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_publish(메세지 발행)
client.on_connect = on_connect
client.on_message = on_message

# address : localhost, port: 1883 에 연결
client.connect('localhost', 1883)
client.loop_start()

while True:
    pass
