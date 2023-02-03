import React, {useEffect, useRef, useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"
import { setInfo, setValid } from './valid';
import {useSelector, useDispatch } from "react-redux";


function MQTT() {
  const { validation, memberInfo } = useSelector(state => ({
    validation : state.valid.validation,
    memberInfo : state.valid.memberInfo
  }))
  const dispatch = useDispatch();
  const changeStoreValid = () => dispatch(setValid())
  const changeStoreMeberInfo = () => dispatch(setInfo())

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
          url: "http://localhost:9999/member/login",
          headers:{
            "Conctent-type": "application/json; charset=utf-8",
            "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImxldmVsIjoiYWNjb3VudCIsImlhdCI6MTY3NTMyNzg1NiwiZXhwIjoxNjg1MzI3ODU2fQ.JnUEZ7q7R2bsgfrXL-Rcls35jqHdNZQNw2-LgtkK_0E"
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