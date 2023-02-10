import React, {useEffect, useRef, useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"
import { setInfo, setMe, setMemberAccessToken, setMemberRefreshToken, setToMember, setValid, setVideos, startRecording, stopRecording, setFamilyAccessToken, setTodos, setMyname } from '../modules/valid';
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
  const saveMyname = data => dispatch(setMyname(data))


  const Navigate = useNavigate()

  // mqtt 연결 준비
  const URL = "ws://localhost:9001";
  const client = mqtt.connect(URL);

  // 사용할 변수 정의
  const [userList, setList] = useState([])
  const [noneList, setNoneList] = useState([]) 
  const [nameData, setName] = useState(null)
  const [imageData, setImage] = useState("")

  const nameValid = useRef(false);
  const validMounted = useRef(false);
  const recordMounted = useRef(false);
  const testMounted = useRef(false);
  const memberMounted = useRef(false);
  const familyMounted = useRef(false);
  const testNumber = useRef(0);

  const userAxiosActivated = useRef(0)


  // 마운트 될 때 한 번 구독한다.
  useEffect(() => {
    client.on('connect', () => {
      // 연결 되면 토픽을 구독
      client.subscribe(["/local/face/result/", "/local/qrtoken/"], function (err) {
        console.log("구독")      
        if (err) {
          console.log(err)
        }
      })
    })
  },[])
  
  
  useEffect(() => {
    // 브로커에 연결되면
    client.on('message', async function (topic, message) {
      // 토픽이 안면 인식 토픽이라면
      if (topic === "/local/face/result/") {
        let jsonMsg = JSON.parse(message)
        let name = jsonMsg.name
        let image = jsonMsg.image
        let userData = [name, image]
        // 리스트에 이름을 계속 담다가
          if (name !== "NONE"){
            setList(oldArray => [...oldArray,userData])
            setNoneList([])
          }  else {
            setNoneList(oldNoneArray => [...oldNoneArray,name])
          }
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
      } else if (topic === "/local/sound/") {
      }
    })
  },[me])
  
  // 메세지가 오면
  useEffect(() => {
    if (!me) {
      if (userAxiosActivated.current===0){
        if (userList.length>=5) {
          console.log("5개 넘었음")
          if (userList.filter(el => el[0]===userList[0][0]).length === userList.length) {
            userAxiosActivated.current = 1
            const cognizedUID = memInfo[userList[0][0]]
            axios({
              method:"post",
              url:"http://i8a208.p.ssafy.io:3000/member/login",
              headers:{
                "Authorization": `Bearer ${familyAccessToken}`
              },
              data: {
                "json":userList[0][1],
                "uid": cognizedUID
              }
            })
            .then ((res) => {
              saveMe(res.data["uid"])
              saveMyname(res.data["name"])
              saveMemberToken(res.data["access-token"])
              saveMemberRefreshToken(res.data["refresh-token"])
              console.log("로그인 됨")
              setList(() => [])
              setNoneList(() => [])
            })
            .catch((err) => {
              console.log(err)
            })
          } else {
            setList(() => [] )
          }
        }
      }
    } else {
      if (userList.length>=5) {
        setList(() => [])
      }
    }
    
  },[userList])
  

  // 멤버 토큰이 저장되었을 때 비디오 리스트 요청
  useEffect(() => {
    if (!memberMounted.current){
      memberMounted.current=true
    } else {
      if (me) {
        axios({
          method: "get",
          url: `http://i8a208.p.ssafy.io:3000/movie/video-list`,
          headers: {
            "Authorization": `Bearer ${memberAccessToken}`
          }
        })
        .then ((res) => {
          console.log(res)
          if (res.data["movie-list"]){
            const objectList = res.data["movie-list"]
            let emptyList = []
            for (let movie of objectList) {
               emptyList.push(movie["uid"])
              }
            setVideoList(emptyList)
            Navigate("/playvideo")
          }
  
        })
        .catch ((err) => {
          console.log("동영상이 없어")
          console.log(err)
        })
      }
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
      axios({
        method: "get",
        url: `http://i8a208.p.ssafy.io:3000/account/member-list`,
        headers: {
          "Authorization": `Bearer ${familyAccessToken}`
        }
      })
      .then ((res) => {
        if (res.data.result === true){
          const emptyObject = {}
            for (let member of res.data.list) {
              
              emptyObject[member.name] = member.uid
            }
          changeStoreMeberInfo(emptyObject)
        }
      })
      .catch ((err) => {
        console.log(err)
      })
    }
  },[familyAccessToken])

  useEffect(() => {
    if (me) {
      if (noneList.length >=12) {
        setVideoList(() => [])
        saveMe(null)
        saveMemberToken(null)
        saveMemberRefreshToken(null)
        changeStoreValid(false)
        setNoneList(() => [])
        console.log("로그아웃")
        userAxiosActivated.current = 0
      }
      }
    
  },[noneList])

}




export default MQTT;