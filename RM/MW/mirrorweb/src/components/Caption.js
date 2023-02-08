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
    const [text, setText] = useState({message : `${caption00[0]} ${name}님`});

    // API_KEY는 JSON 파일에
    const API_KEY = secrets.google_speech_api_key
    const ttsURL = `https://texttospeech.googleapis.com/v1/text:synthesize?key=${API_KEY}`

    useEffect(() => {
        let idx =1;
        const requestBody = {
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
          console.log(requestBody.input)
        })

        .catch((err) => {
            console.log(err)
        })
        const changeText = setInterval(()=>{
            setText(text => ({
                ...text,
                message: caption00[idx++]
            }));

            if (idx===4) clearInterval(changeText);
        },3000)
    },[])

    useEffect(() => {

           const requestBody = {
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
        },[text])

    return (
        <div style={{height:'40px'}}>{text.message}</div>
    )
};

export default Caption;