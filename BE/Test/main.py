import requests
import json
import base64
import cv2

files = open('cjw.jpg', 'rb')

# 파이썬 딕셔너리 형식으로 file 설정
upload = {'imgUrlBase': files}

# request.post방식으로 파일전송.
res = requests.post('http://localhost:5555/',
                    files=upload)

print(res.json())
