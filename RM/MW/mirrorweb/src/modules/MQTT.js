import React, {useEffect, useRef, useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"
import { setInfo, setValid } from './valid';
import {useSelector, useDispatch } from "react-redux";


function MQTT() {
  // redux에서 state 불러오기
  const { validation, memberInfo, recording } = useSelector(state => ({
    validation : state.valid.validation,
    memberInfo : state.valid.memberInfo,
    recording : state.valid.isRecording
  }))
  // redux에서 action 가져오기
  const dispatch = useDispatch();
  const changeStoreValid = () => dispatch(setValid())
  const changeStoreMeberInfo = (info) => dispatch(setInfo(info))

  // mqtt 연결 준비
  const URL = "ws://localhost:9001";
  const client = mqtt.connect(URL);

  // 사용할 변수 정의
  const [userList, setList] = useState([]);
  const [imageData, setImage] = useState({
    "image":""
  })
  const validMounted = useRef(false);
  const recordMounted = useRef(false);


  // 브로커에 연결되면
  client.on('connect', () => {
    // 연결 되면 토픽을 구독
    client.subscribe("/local/face/result/", function (err) {
      if (err) {
        console.log(err)
      }
    })
  })
  
  // 메세지가 오면
  client.on('message', function (topic, message) {
    let name = JSON.parse(message).name
    // 리스트에 이름을 계속 담다가
    if (name !== "NONE"){
      setList( function(preState) {
        return [...preState, name]
      })
      // 10개 이상 담기면
      if (userList.length >= 10) {
        // 10개로 자름
        setList(function(preState) {
         return preState.slice(0,10)
        })   
      }
      // 만약 모든 원소가 같으면
      if ((userList.filter(user => user !== userList[0])).length ===0) {
        // state의 인식 상태를 전환한다.
        changeStoreValid()
        // 가져온 이미지를 변수에 담는다.
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
      if (!validMounted.current) {
        validMounted.current = true;
      } else {
        // 이미지를 JSON으로 변환
        let image = JSON.stringify(imageData)
        // axios 요청
        axios({
          method: "post",
          url: "http://i8a208.p.ssafy.io:3000/member/login",
          headers:{
            "Content-type": "application/json;",
            "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImxldmVsIjoiYWNjb3VudCIsImlhdCI6MTY3NTMyNzg1NiwiZXhwIjoxNjg1MzI3ODU2fQ.JnUEZ7q7R2bsgfrXL-Rcls35jqHdNZQNw2-LgtkK_0E"
          },
          data: image
        })
        // 응답이 오면
        .then((res) => {
          if (res.data) {
            // 가족 정보를 redux 상태에 저장
            changeStoreMeberInfo(res.data)
            // 인식 여부를 redux 상태에 저장
            changeStoreValid()
          } else {
            console.log("오류가 발생했습니다.")
          }
          
        })
        .catch((err) => {
          console.log(err)
        })
      }
      },[validation])
    
  useEffect(() => {
    if (!recordMounted.current) {
      recordMounted.current = true;
    } else {
      // 토큰 보내기
      if (recording === false) {
        const publishData = {
          "from" : "",
          "to" : "",
          "accessToken" : "",
        }
        const jsonData = JSON.stringify(publishData)
        client.publish("/record/", jsonData)
      }
    }
  },[recording])
}


export default MQTT;