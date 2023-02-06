import React, {useEffect, useRef, useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"
import { setInfo, setValid, setVideos, startRecording, stopRecording } from '../modules/valid';
import {useSelector, useDispatch } from "react-redux";


function MQTT() {
  // redux에서 state 불러오기
  const { accessToken, me, validation, toMember , recording, storedVideos } = useSelector(state => ({
    accessToken: state.valid.accessToken,
    me : state.valid.me,
    validation : state.valid.validation,
    toMember : state.valid.toMember,
    recording : state.valid.isRecording,
    storedVideos : state.valid.videos
  }))
  // redux에서 action 가져오기
  const dispatch = useDispatch();
  const changeStoreValid = () => dispatch(setValid())
  const changeStoreMeberInfo = (info) => dispatch(setInfo(info))
  const setVideoList = (videoList) => dispatch(setVideos(videoList))
  const onRecording = () => dispatch(startRecording())
  const notRecording = () => dispatch(stopRecording())


  // mqtt 연결 준비
  const URL = "ws://localhost:9001";
  const client = mqtt.connect(URL);

  // 사용할 변수 정의
  const [userList, setList] = useState([]);
  const [imageData, setImage] = useState("")

  const validMounted = useRef(false);
  const recordMounted = useRef(false);
  const testMounted = useRef(false);
  const testNumber = useRef(0);


  // 브로커에 연결되면
  client.on('connect', () => {
    // 연결 되면 토픽을 구독
    client.subscribe(["/local/face/result/","/record/"], function (err) {
      if (err) {
        console.log(err)
      }
    })
  })
  
  // 메세지가 오면
  client.on('message', async function (topic, message) {
    let name = JSON.parse(message).name
    // 토픽이 안면 인식 토픽이라면
    if (topic === "/local/face/result/") {
      // 리스트에 이름을 계속 담다가
      if (name !== "NONE"){
        setList( function(preState) {
          return [...preState, name]
        })
        // 15개 이상 담기면
        if (userList.length >= 15) {
          // 10개로 자름
          setList(function(preState) {
           return preState.slice(0,10)
          })   
        }
        // 만약 모든 원소가 같으면
        if ((userList.filter(user => user !== userList[0])).length ===0) {
          // 가져온 이미지를 변수에 담는다.
          let imageArray = JSON.parse(message).image
          if (imageArray.length > 0) {
            setImage(() => {
              return imageArray
            })
          }
        }
      }
      // 토픽이 녹화 토픽이라면
    } else if (topic === "/record/") {
      // 녹화가 종료되었다는 메세지를 받으면
      let res = JSON.parse(message).result
      if (res === 0) {
        notRecording()
      }
    }
  })

  // 이미지 데이터가 변경되었으면
  useEffect(() => {
    // 마운트될 때에는 실행하지 않고
    if (!testMounted.current) {
      testMounted.current = true;
    } else {
      // 인식되었다고 스토어에 저장
      if (testNumber.current === 0) {
        setImage(imageData)
        changeStoreValid()
        testNumber.current +=1
      }
    }
  }, [imageData])

  useEffect(() =>{
      // 마운트 됐을 때는 실행 안 함
      if (!validMounted.current) {
        validMounted.current = true;
      } else {
        // 구독 끊기
        client.unsubscribe("/local/face/result/", function (err) {
          if (err) {
            console.log(err)
          }
        })
        // 이미지를 JSON으로 변환
        const image = JSON.stringify(imageData)
        // axios 요청
        axios({
          method: "post",
          url: "http://i8a208.p.ssafy.io:3000/member/login",
          headers:{
            "Content-type": "Application/json",
            "Authorization": `Bearer ${accessToken}`
          },
          data: image
        })
        // 응답이 오면
        .then((res) => {
          if (res.data) {
            // 가족 정보를 redux 상태에 저장
            changeStoreMeberInfo(res.data)
            // 저장한 뒤 바로 "나"에 대한 정보를 요청
            axios({
              method: "get",
              url: "",
              headers: {
                "Authorization": `Bearer ${accessToken}`
              }
            })
            .then ((res) => {
                setVideoList((res) => {
                  return [...storedVideos,res.videos]
                })
              })
              .catch ((err) => {
                console.log(err)
              })  
          } else {
            console.log("정확한 데이터를 받지 못했습니다.")
          }
          
        })
        .catch((err) => {
          console.log(err)
        })
      }
      },[validation])
  
  // 만약 스토어의 toMember에 변화가 일어나면
  useEffect(() => {
    // 마운트될 당시엔 useEffect 동작 방지
    if (!recordMounted.current) {
      recordMounted.current = true;
    } else {
      if (recording === false) {
        // 나, 받는 사람, 토큰을 담아서 브로커로 보낸다.
        const publishData = {
          "from" : me,
          "to" : toMember,
          "accessToken" : accessToken,
        }
        const jsonData = JSON.stringify(publishData)
        onRecording()
        client.publish("/record/", jsonData)
      }
    }
  },[toMember])
}


export default MQTT;