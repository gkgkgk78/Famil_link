import cv2
import numpy as np
import mediapipe as mp
from keras.models import load_model

labels = open('labels.txt', 'r', encoding="UTF-8").readlines()
model = load_model('keras_model.h5')

mp_face_detection = mp.solutions.face_detection
mp_drawing = mp.solutions.drawing_utils

camera = cv2.VideoCapture(0 + cv2.CAP_DSHOW)
camera.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)  # 1280
camera.set(cv2.CAP_PROP_FRAME_HEIGHT, 720)  # 720
width = camera.get(cv2.CAP_PROP_FRAME_WIDTH)
height = camera.get(cv2.CAP_PROP_FRAME_HEIGHT)
size = (int(width), int(height))  # 프레임 크기
print("size : " + str(size))
idx = 0
while True:
    ret, image = camera.read()
    if not ret:
        print("dont read cam")
        break
    # image = cv2.resize(image, (224, 224), interpolation=cv2.INTER_AREA)

    with mp_face_detection.FaceDetection(
            model_selection=0, min_detection_confidence=0.5) as face_detection:

        image.flags.writeable = False
        image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        results = face_detection.process(image)


        image.flags.writeable = True
        image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

        if results.detections:
            for detection in results.detections:

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
                np_image = np.asarray(image, dtype=np.float32).reshape(1, 224, 224, 3)


                np_image = (np_image / 127.5) - 1

                probabilities = model.predict(np_image)
                pick = np.argmax(probabilities)
                percent = probabilities[0][pick]
                name = labels[pick].strip().split(' ')[1]
                print(name, percent)
        else:
            image = np.zeros((224, 224, 3), np.uint8)
    # cv2.imshow("dd", image)
    idx += 1

    cv2.waitKey(1)  # MQTT 성능에 따라 유도리 있게 설정

