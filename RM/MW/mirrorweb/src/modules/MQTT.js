import React, {useEffect, useRef, useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"

function MQTT() {
  const URL = "ws://localhost:9001";
  const client = mqtt.connect(URL);
  const [userList, setList] = useState([]);


  // 브로커에 연결되면
  client.on('connect', () => {
    client.subscribe("/local/face/result/", function (err) {
      if (err) {
        console.log(err)
      }
    })
  })
  
  client.on('message', function (topic, message) {
    let name = JSON.parse(message).name
    console.log(name)
  })
  
}

export default MQTT;