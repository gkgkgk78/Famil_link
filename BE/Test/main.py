import requests
import json
import base64
import cv2

image_name = 'cjw.jpg'
img = cv2.imread(image_name)
jpg_img = cv2.imencode('.jpg', img)
b64_string = base64.b64encode(jpg_img[1]).decode('utf-8')

files = {
            "img": b64_string,
        }

r = requests.post("http://localhost:5555/", json=json.dumps(files))
print(r.json())