import React, {useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"

function MQTT() {
  const URL = "ws://localhost:9001";
  const client = mqtt.connect(URL);
  const [cogResult, setcogResult] = useState({})

  // 브로커에 연결되면
  client.on('connect', () => {
    // /local/face/result/ 라는 토픽을 구독
    client.subscribe("/local/face/result/", function (err) {
      if (err) {
        console.log(err)
      }
    })
  })

  // 메세지를 받으면
  client.on('message', function (topic, message) {
    // 이름을 선언
    let name = JSON.parse(message)["name"]

    // 인식 결과를 객체에 계속 저장할 것이다.
    // 만약 객체에 존재하지 않으면
    console.log(Object.keys(cogResult))
    if (!(Object.keys(cogResult).includes(name))) {
      // 키를 이름으로, 값을 0으로 객체에 추가
      console.log(`${name} 가 객체에 존재하지 않네`)
      setcogResult({
        ...cogResult,
        [name] : 0
      })
      console.log(JSON.stringify(cogResult))
      // 이름이 객체에 키로 이미 존재하면
    } else {
      console.log(`${name}은 이미 ${cogResult[name]}번 도착했어`)
      // 만약 이름이 10번 미만 도착했으면
      if (cogResult[name] < 10) {
        setcogResult({
          ...cogResult,
          // 값에 1을 더해준다
          [name] : cogResult[name]+1
        })
        console.log(JSON.stringify(cogResult))
      // 그게 아니라 이미 10번 도착했으면
      } else {
        console.log("이미 10번 도착했어")
        // 만약 리덕스에 저장된 이름이 없으면
        // 이름을 저장하고 동시에 백에 요청을 보냄
        // 그게 아니라 리덕스에 저장된 이름이 있는데
        // 만약 그 이름과 지금 이름이 다르면 어떻게 할 것인가?
      }
    }
  })
}

export default MQTT;