import React, {useEffect, useRef, useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"

function MQTT() {
  const URL = "ws://localhost:9001";
  const client = mqtt.connect(URL);
  const [userList, setList] = useState([]);
  const [isValid,setValid] = useState(false)
  const mounted = useRef(false);


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
    setList( function(preState) {
      return [...preState, name]
    })
    if (userList.length >= 10) {
      setList(function(preState) {
       return preState.slice(0,10)
      })   
    }
    if ((userList.filter(user => user !== userList[0])).length ===0) {
      setValid(() => {
        return true
      })
    }
  })

  useEffect(() =>{
    if (!mounted.current) {
      mounted.current = true;
      console.log("마운트 됐구나")
    } else {
      console.log("인식됐구나")
      const name =userList[0]
      console.log("axios 보낸다.")
      axios({
        method: "get",
        url: "이름을 포함한 url",
        header: "",
      })
      .then((res) => {
        console.log(res)
        // 정보 저장
      })
      .catch((err) => {
        console.log(err)
      })
    }
  },[isValid])
}


export default MQTT;