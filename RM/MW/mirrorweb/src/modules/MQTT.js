import React, {useEffect, useRef, useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"

function MQTT() {
  const URL = "ws://localhost:9001";
  const client = mqtt.connect(URL);
  const [userList, setList] = useState([]);
  const [isValid,setValid] = useState(null)
  const [imageData, setImage] = useState({
    "image":""
  })
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
    if (name !== "NONE"){
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
        let image = JSON.parse(message).image
        if (image.length > 0) {
          setImage(() => {
            return image
          })
      }
      }
    }
  })

  useEffect(() =>{
      // 마운트 됐을 때는 실행 안 함
      if (!mounted.current) {
        mounted.current = true;
      } else {
        console.log(JSON.stringify(imageData))
        axios({
          method: "post",
          url: "http://i8a208.p.ssafy.io:3000/member/login",
          headers:{
            "Conctent-type": "application/json; charset=utf-8",
            "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2Iiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImxldmVsIjoiYWNjb3VudCIsImlhdCI6MTY3NTMyNDc4OCwiZXhwIjoxNjg1MzI0Nzg4fQ.rB9k4f7WurY4qbPltJpqPOGc5jdt8k8g1AYNJSYjpjU"
          },
          data: imageData
        })
        .then((res) => {
          console.log(res)
        })
        .catch((err) => {
          console.log(err)
        })
      }
      },[isValid])
    
}


export default MQTT;