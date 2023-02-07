import React, { useEffect, useRef, useState } from "react"
import { useSelector } from 'react-redux';
import "./Caption.css"
import axios from "axios"
import secrets from "./secrets.json"

const Caption = () => {

    //멘트 처리, text에 담기
    const {caption00, name} = useSelector((state) => ({
        caption00 : state.valid.caption,
        name : state.valid.me,
    }))

    const idx = useRef(1);

    console.log(caption00)
    const [text, setText] = useState({message : `${caption00[0]} ${name}님`});

    // API_KEY는 JSON 파일에
    const API_KEY = secrets.google_speech_api_key
    const ttsURL = `https://texttospeech.googleapis.com/v1/text:synthesize?key=${API_KEY}`

    const mounted = useRef(true);
    // /* const [audioSource, setAudioSource] = useState(null) */

    // // 메세지를 불러오는 로직: 적용 시 수정 필요
    const setTextAndRead =  () => {
        setTimeout(()=>{
            console.log(111);
            setText(text => ({
                ...text,
                message: caption00[idx] 
            }))
        },1000);
    }

    useEffect(() => {
      if (!mounted.current) {
        mounted.current = true;
      } else {
        let requestBody = {
            "audioConfig": {
                "audioEncoding": "LINEAR16",
                "effectsProfileId": [
                  "small-bluetooth-speaker-class-device"
                ],
                "pitch": 1.4,
                "speakingRate": 1.1
              },
              "input": {
                "text": text.message
              },
              "voice": {
                "languageCode": "ko-KR",
                "name": "ko-KR-Wavenet-A"
              }
          }
        axios.post(ttsURL, requestBody)

        .then((response) => {
          let audioSource = new Audio("data:audio/wav;base64," + response.data.audioContent)
          audioSource.play()
        })

        .catch((err) => {
            console.log(err)

        })
      }
    },[text])

    // const [caption, setCaption] = useState(caption00[0]);s
    return (
        <div onChange={setTextAndRead}>
            {text.message}
        </div>
    )
    // <div className='Caption' onChange={setTextAndRead}>{text}</div>
    // return <div className='Caption' onChange={setTextAndRead}>{text}</div>
    // {/* {caption00.map((cap, index)=>(<div key={index} onLoad={setTextAndRead}>{cap}</div>))} */}
};

export default Caption;

// setInterval(()=>)