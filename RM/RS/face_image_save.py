import cv2
import numpy as np
import mediapipe as mp

mp_face_detection = mp.solutions.face_detection
mp_drawing = mp.solutions.drawing_utils

camera = cv2.VideoCapture(0)
idx = 0
while True:
    ret, image = camera.read()
    if not ret:
        print("dont read cam")
        break
    image = cv2.resize(image, (224, 224), interpolation=cv2.INTER_AREA)
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
    cv2.imshow("dd", image)
    cv2.imwrite("image" + str(idx) + ".jpg", image)
    idx += 1
    # print("publish opencv data")
    cv2.waitKey(10)  # MQTT 성능에 따라 유도리 있게 설정
