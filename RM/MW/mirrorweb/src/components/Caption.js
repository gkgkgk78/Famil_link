import React, { useEffect, useState, useRef } from "react"
import { useSelector } from 'react-redux';
import "./Caption.css"
import axios from "axios"
import secrets from "./secrets.json"

const Caption = () => {
    //멘트 처리, text에 담기
    const {caption00, name, weather, schedules} = useSelector((state) => ({
        caption00 : state.valid.caption,
        name : state.valid.myname,
        weather : state.valid.weather,
        schedules : state.valid.schedules
    }))

    const [text, setText] = useState({message : `${caption00[0]} ${name}님`});


    
    // API_KEY는 JSON 파일에
    const API_KEY = secrets.google_speech_api_key
    const ttsURL = `https://texttospeech.googleapis.com/v1/text:synthesize?key=${API_KEY}`
    
    const mounted = useRef(false);

    useEffect(() => {
      if (!mounted.current) {
        mounted.current = true;
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

            if (idx===textLength) clearInterval(changeText);
        },3000)
    },[weather, schedules, name])

    // useEffect(() => {

    //        const requestBody = {
    //           "audioConfig": {
    //               "audioEncoding": "LINEAR16",
    //               "effectsProfileId": [
    //                 "small-bluetooth-speaker-class-device"
    //               ],
    //               "pitch": 1.4,
    //               "speakingRate": 1.1
    //             },
    //             "input": {
    //               "text": text.message
    //             },
    //             "voice": {
    //               "languageCode": "ko-KR",
    //               "name": "ko-KR-Wavenet-A"
    //             }
    //         }
    //       axios.post(ttsURL, requestBody)
  
    //       .then((response) => {
    //         let audioSource = new Audio("data:audio/wav;base64," + response.data.audioContent)
    //         audioSource.play()
    //       })
  
    //       .catch((err) => {
    //           console.log(err)
    //       })
    //     },[text])

    return (
        <div style={{height:'40px'}}>{text.message}</div>
    )
};

export default Caption;
