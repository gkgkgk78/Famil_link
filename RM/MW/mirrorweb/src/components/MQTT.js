import React, {useEffect, useRef, useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"
import { setInfo, setMe, setMemberAccessToken, setMemberRefreshToken, setToMember, setValid, setVideos, startRecording, stopRecording, setFamilyAccessToken } from '../modules/valid';
import {useSelector, useDispatch } from "react-redux";
import { useNavigate } from 'react-router-dom';


function MQTT() {
  // redux에서 state 불러오기
  const { familyAccessToken, memberAccessToken, me, validation, toMember , recording, storedVideos, memInfo } = useSelector(state => ({
    familyAccessToken: state.valid.familyAccessToken,
    memberAccessToken: state.valid.memberAccessToken,
    me : state.valid.me,
    validation : state.valid.validation,
    toMember : state.valid.toMember,
    recording : state.valid.isRecording,
    storedVideos : state.valid.videos,
    memInfo : state.valid.memberInfo
  }))
  // redux에서 action 가져오기
  const dispatch = useDispatch();
  const changeStoreValid = () => dispatch(setValid())
  const changeStoreMeberInfo = (info) => dispatch(setInfo(info))
  const setVideoList = (videoList) => dispatch(setVideos(videoList))
  const onRecording = () => dispatch(startRecording())
  const notRecording = () => dispatch(stopRecording())
  const saveMe = (me) => dispatch(setMe(me)) 
  const saveMemberToken = (membertoken) => dispatch(setMemberAccessToken(membertoken))
  const saveMemberRefreshToken = (memreftoken) => dispatch(setMemberRefreshToken(memreftoken))
  const saveToMember = (tomember) => dispatch(setToMember(tomember))
  const saveFAToken = fatoken => dispatch(setFamilyAccessToken(fatoken))

  const Navigate = useNavigate()

  // mqtt 연결 준비
  const URL = "ws://localhost:9001";
  const client = mqtt.connect(URL);

  // 사용할 변수 정의
  const [userList, setList] = useState([])
  const [nameData, setName] = useState(null)
  const [imageData, setImage] = useState("")

  const nameValid = useRef(false);
  const validMounted = useRef(false);
  const recordMounted = useRef(false);
  const testMounted = useRef(false);
  const memberMounted = useRef(false);
  const familyMounted = useRef(false);
  const testNumber = useRef(0);


  // 브로커에 연결되면
  client.on('connect', () => {
    // 연결 되면 토픽을 구독
    client.subscribe(["/local/face/result/", "/local/qrtoken/"], function (err) {
      if (err) {
        console.log(err)
      }
    })
  })
  
  // 메세지가 오면
  client.on('message', async function (topic, message) {
    // 만약 내가 설정되지 않은 상태라면
    if (topic === "/local/face/result/") {
      if (memInfo) {
        if (!me) {
          let name = JSON.parse(message).name
          // 토픽이 안면 인식 토픽이라면
          if (topic === "/local/face/result/") {
            // 리스트에 이름을 계속 담다가
            if (name !== "NONE"){
              setList( function(preState) {
                return [...preState, name]
              })
              // 20개 이상 담기면
              if (userList.length >= 30) {
                // 10개로 자름
                setList(function(preState) {
                 return preState.slice(0,20)
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
                  if (!nameValid.current) {
                    nameValid.current=true
                    setName(() => {
                      return name
                    })
                  }
                }
              }
            }
          } 
        }
      }
      // 토픽이 로그인관련 이면
    } else if (topic === "/local/qrtoken/") {
      client.publish("/local/qr/","0")
      let msg = JSON.parse(message)

      axios({
        method:"get",
        url:"http://i8a208.p.ssafy.io:3000/account/auth",
        headers:{
          "Authorization": `Bearer ${msg}`
        }
      })
      .then((res) => {
        if (res.data["result"]===true) {
          saveFAToken(msg)
        }
      })
      .catch((err) => {
        client.publish("/local/qr/","1")
      })
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
/*         client.unsubscribe("/local/face/result/", function (err) {
          if (err) {
            console.log(err)
          }
        }) */
        // 이미지를 JSON으로 변환
        // axios 요청
        const jsonInfo = 
        {
          "json": imageData,
          "uid": memInfo[nameData]
        }
        axios({
          method: "post",
          url: "http://i8a208.p.ssafy.io:3000/member/login",
          headers:{
            "Content-type": "Application/json",
            "Authorization": `Bearer ${familyAccessToken}`
          },
          data: JSON.stringify(jsonInfo)
        })
        // 응답이 오면
        .then((res) => {
          if (res.data) {
            // 가족 정보를 redux 상태에 저장
            saveMe(res.data["uid"])
            saveMemberToken(res.data["access-token"])
            saveMemberRefreshToken(res.data["refresh-token"])
            
          } else {
            console.log("정확한 데이터를 받지 못했습니다.")
          }
          
        })
        .catch((err) => {
          console.log(err)
        })
      }
      },[nameData])
  

  // 멤버 토큰이 저장되었을 때 비디오 리스트 요청
  useEffect(() => {
    if (!memberMounted.current){
      memberMounted.current=true
    } else {
      console.log(`멤버 토큰은 ${memberAccessToken}`)
      console.log(`나는 ${me}`)
      axios({
        method: "get",
        url: `http://i8a208.p.ssafy.io:3000/movie/video-list`,
        headers: {
          "Authorization": `Bearer ${memberAccessToken}`
        }
      })
      .then ((res) => {
        console.log(res)
        const objectList = res.data["movie-list"]
        let emptyList = []
        for (let movie of objectList) {
           emptyList.push(movie["uid"])
          }
        setVideoList(emptyList)
        Navigate("/playvideo")


      })
      .catch ((err) => {
        console.log("동영상이 없어")
        console.log(err)
      })
    }
  }, [memberAccessToken])

  // 녹화 상태가 변경될 때 브로커로 메세지 보냄
  useEffect(() => {
    if (!recordMounted) {
      recordMounted.current = true
    } else {
      if (recording === false) {
        // 녹화가 끝났으면 녹화가 끝났다는 메세지를 보냄
        client.publish("/local/record/", "0")
      } else {
        // 녹화가 시작되었으면 데이터와 신호를 쏴줌
        const publishData = {
          "from" : me,
          "to" : toMember,
          "token" : memberAccessToken,
        }
        const jsonData = JSON.stringify(publishData)
        console.log(jsonData)
        if (publishData["to"]) {
          client.publish("/local/token/", jsonData)

          client.publish("/local/record/", "1")
          saveToMember(null)
        } else {
          console.log("받으실 분이 없어요")
        }
      }
    }
    },[recording])

  useEffect(() => {
    if (!familyMounted.current) {
      familyMounted.current=true
    } else {
      console.log(familyAccessToken)
      axios({
        method: "get",
        url: `http://i8a208.p.ssafy.io:3000/account/member-list`,
        headers: {
          "Authorization": `Bearer ${familyAccessToken}`
        }
      })
      .then ((res) => {
        console.log(res)
        /* if (res.result === true) {
          const emptyObject = {}
          for (let member of res.list) {
            emptyObject.memeber.name = member.uid
          } 
          changeStoreMeberInfo(emptyObject)
        */
        
        const emptyObject = {}
          for (let member of res.data) {
            emptyObject[member.name] = member.uid
          }
        changeStoreMeberInfo(emptyObject)
        
      })
      .catch ((err) => {
        console.log(err)
      })
    }
  },[familyAccessToken])

  useEffect(() => {
    console.log(memInfo)
  },[memInfo])
}





export default MQTT;