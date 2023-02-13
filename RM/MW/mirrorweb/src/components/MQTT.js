import React, {useEffect, useRef, useState} from 'react';
import mqtt from "precompiled-mqtt";
import axios from "axios"
import { setInfo, setMe, setMemberAccessToken, setMemberRefreshToken, setToMember, setValid, setVideos, startRecording, stopRecording, setFamilyAccessToken, setTodos } from '../modules/valid';
import {useSelector, useDispatch } from "react-redux";
import { useLocation, useNavigate } from 'react-router-dom';


function MQTT() {
  // redux에서 state 불러오기
  const { familyAccessToken, memberAccessToken, me, caption00, name, weather, schedules,validation, toMember , recording, storedVideos, memInfo } = useSelector(state => ({
    familyAccessToken: state.valid.familyAccessToken,
    memberAccessToken: state.valid.memberAccessToken,
    me : state.valid.me,
    validation : state.valid.validation,
    toMember : state.valid.toMember,
    recording : state.valid.isRecording,
    storedVideos : state.valid.videos,
    memInfo : state.valid.memberInfo,
    caption00 : state.valid.caption,
    name : state.valid.myname,
    weather : state.valid.weather,
    schedules : state.valid.schedules
  }))
  // redux에서 action 가져오기
  const dispatch = useDispatch();
  const changeStoreValid = () => dispatch(setValid())
  const changeStoreMeberInfo = (info) => dispatch(setInfo(info))
  const setVideoList = (videoList) => dispatch(setVideos(videoList))
  const saveMe = (me) => dispatch(setMe(me)) 
  const saveMemberToken = (membertoken) => dispatch(setMemberAccessToken(membertoken))
  const saveMemberRefreshToken = (memreftoken) => dispatch(setMemberRefreshToken(memreftoken))
  const saveToMember = (tomember) => dispatch(setToMember(tomember))
  const saveFAToken = fatoken => dispatch(setFamilyAccessToken(fatoken))


  const Navigate = useNavigate()
  const location = useLocation();

  // mqtt 연결 준비
  const URL = "ws://localhost:9001";
  const client = mqtt.connect(URL);

  // 사용할 변수 정의
  const [userList, setList] = useState([])
  const [noneList, setNoneList] = useState([]) 
  const [soundData, setSoundData] = useState("")
  const [imageData, setImage] = useState("")

  const recordMounted = useRef(false);
  const memberMounted = useRef(false);
  const familyMounted = useRef(false);

  const userAxiosActivated = useRef(0)


  // 마운트 될 때 한 번 구독한다.
  useEffect(() => {
    client.on('connect', () => {
      // 연결 되면 토픽을 구독
      client.subscribe(["/local/face/result/", "/local/qrtoken/", "/local/sound/"], function (err) {
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
        // 녹화중이 아닐 때에는 계속 사람을 인식한다.
        if (location.pathname !== "/record") {
          let jsonMsg = JSON.parse(message)
          let name = jsonMsg.name
          let image = jsonMsg.image
          let userData = [name, image]
          // 리스트에 이름을 계속 담다가
            if (name !== "NONE"){
              setList(oldArray => [...oldArray,userData])
              setNoneList(() => [])
            }  else {
              setNoneList(oldNoneArray => [...oldNoneArray,name])
            }
          // 녹화 중이면 인식 중단 
          } else {
            setList(() => [])
            setNoneList(() => [])
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
        .catch(() => {
          client.publish("/local/qr/","1")
        })
      } else if (topic === "/local/sound/") {
        setSoundData(JSON.parse(message))
      }
    })
  },[me])
  
  useEffect(() => {
    if (!me) {
      if (userAxiosActivated.current===5){
        if (userList.length>=5) {
          // 길이 5의 배열에서 4개 이상이 0번과 같으면
          if (userList.filter(el => el[0]===userList[0][0]).length>=4) {
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
              saveMemberToken(res.data["access-token"])
              saveMemberRefreshToken(res.data["refresh-token"])
              setList(() => [])
              setNoneList(() => [])
            })
            .catch((err) => {
              console.log(err)
            })
          // 길이 4의 배열에서 0번만 다를 때 = 4개는 똑같음
          } else if (userList.filter(el => el[0]===userList[0][0]).length === 1)  {
            userAxiosActivated.current = 1
            const cognizedUID = memInfo[userList[1][0]]
            axios({
              method:"post",
              url:"http://i8a208.p.ssafy.io:3000/member/login",
              headers:{
                "Authorization": `Bearer ${familyAccessToken}`
              },
              data: {
                "json":userList[1][1],
                "uid": cognizedUID
              }
            })
            .then ((res) => {
              saveMe(res.data["uid"])
              saveMemberToken(res.data["access-token"])
              saveMemberRefreshToken(res.data["refresh-token"])
              setList(() => [])
              setNoneList(() => [])
            })
            .catch((err) => {
              console.log(err)
            })
          // 길이 4의 배열에서 비율이 3:2면 
          } else {
            // 리스트를 비우기
            setList(() => [])
          }
        }
      }
    // 이미 로그인 상태면
    } else {
      if (userList.length === 5) {
        // 리스트 비우기
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
        // client.publish("/local/tts/","")
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

  // 가족 계정이 로그인 되었을 때 멤버 리스트 요청 보내기
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

  // 로그아웃 처리
  useEffect(() => {
    if (me) {
      if (noneList.length >=7) {
        setVideoList(() => [])
        saveMe(null)
        saveMemberToken(null)
        saveMemberRefreshToken(null)
        changeStoreValid(false)
        setNoneList(() => [])
        console.log("로그아웃")
        Navigate("/")
        userAxiosActivated.current = 0
        }
      }
    
  },[noneList])

  const mounted00 = useRef(false);
  const [text, setText] = useState({message : `${caption00[0]} ${name}님`});

    useEffect(() => {
      if (!mounted00.current) {
        mounted00.current = true;
        return;
      } 
        if(!schedules) return;
        if(!weather) return;
        if(!name) return;
        let idx =0;
        const textScript = [];
        textScript.push(`오늘의 날씨는 ${weather}입니다`)
        let today = new Date();
        let month = today.getMonth() +1;
        let date = today.getDate();
        if(month<10) {
          today = `0${month}월 ${date}일`
        }
        else{
          today = `${month}월 ${date}일`
        };
        let flag = 0;
        schedules.map(schedule => {

          console.log(schedule.parseDate);
          console.log(today);

          if(schedule.parseDate === today) {
            flag=1;
            textScript.push(`오늘은 ${schedule.content.slice(0,2)}님의 ${schedule.content.slice(3,)}입니다`)
          }
        })
        if(!flag) textScript.push(`오늘도 좋은 하루 보내세요`)
        else textScript.push('메시지를 보내시겠습니까?')
        
        textScript.push('')

        let textLength = textScript.length-1;

        const changeText = setInterval(()=>{

            setText(text => ({
                ...text,
                message: textScript[idx++]
            }));
            
            const jsonData0 = JSON.stringify(text)
            if (text["message"]) client.publish("/local/tts/", jsonData0)

            if (idx===textLength) clearInterval(changeText);
        },3000)
    },[weather, schedules, name])

}



export default MQTT;