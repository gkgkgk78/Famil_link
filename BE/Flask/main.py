import cv2
import numpy as np
import base64

from keras.models import load_model
from flask import Flask, jsonify, request
from PIL import Image
import json
from io import BytesIO

app = Flask(__name__)

# Load the model
model = load_model('C:\\Users\\SSAFY\\Desktop\\flask & java\\flask & java\\Flask\\keras_model.h5')

# CAMERA can be 0 or 1 based on default camera of your computer.
# camera = cv2.VideoCapture(0, cv2.CAP_DSHOW)

# Grab the labels from the labels.txt file. This will be used later.
labels = open('C:\\Users\\SSAFY\\Desktop\\flask & java\\flask & java\\Flask\\labels.txt', 'r', encoding='UTF8').readlines()

@app.route("/", methods=["GET", "POST"])
def index():
    print(request)
    print(request.data)
    json_data = request.get_json()
    print(type(json_data),json_data["img"])


    # dict_data = json.loads(json_data)
    # img = dict_data['img']

    img=json_data["img"]
    img = base64.b64decode(img)
    img = BytesIO(img)
    img = Image.open(img)

    # use numpy to convert the pil_image into a numpy array
    numpy_image = np.array(img)

    # convert to a openCV2 image and convert from RGB to BGR format
    opencv_image = cv2.cvtColor(numpy_image, cv2.COLOR_RGB2BGR)

    # Resize the raw image into (224-height,224-width) pixels.
    image = cv2.resize(opencv_image, (224, 224), interpolation=cv2.INTER_AREA)

    # Make the image a numpy array and reshape it to the models input shape.
    image = np.asarray(image, dtype=np.float32).reshape(1, 224, 224, 3)
    # Normalize the image array
    image = (image / 127.5) - 1

    # Have the model predict what the current image is. Model.predict
    # returns an array of percentages. Example:[0.2,0.8] meaning its 20% sure
    # it is the first label and 80% sure its the second label.
    probabilities = model.predict(image)
    # Print what the highest value probabilitie label

    np.set_printoptions(precision=3, suppress=True)
    print(probabilities)

    #제일 높은 확률이 80%이상이면 출력
    if (max(map(max, probabilities)) > 0.8):
        return json.dumps(1, ensure_ascii=False)
    else:
        return json.dumps(0, ensure_ascii=False)


idx = 0
if __name__ == "__main__":
    app.debug = True
    app.run(host="localhost", port=5555)
