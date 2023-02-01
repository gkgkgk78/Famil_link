FROM python:3.8-slim

COPY . /app

RUN pip3 install opencv==4.7.0.68
RUN pip3 install flask
RUN pip3 install keras==2.11.0
RUN pip3 install tensorflow==2.11.0
RUN pip3 install paho-mqtt==1.6.1
RUN pip3 install numpy==1.24.1


WORKDIR /app

CMD ["python3", "-m", "flask", "run", "--host=0.0.0.0"]
