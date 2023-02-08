import axios from "axios"
import { useEffect, useRef, useState } from "react"
import secrets from "./secrets.json"

const TextToSpeech = () => {
    // API_KEY는 JSON 파일에
    const API_KEY = secrets.google_speech_api_key
    const ttsURL = `https://texttospeech.googleapis.com/v1/text:synthesize?key=${API_KEY}`

    const [text, setText] = useState({message:""});
    /* const [audioSource, setAudioSource] = useState(null) */

    const mounted = useRef(false);

    // 메세지를 불러오는 로직: 적용 시 수정 필요
    const setTextAndRead =  e => {
        setText(text => ({
            ...text,
            message:e.target.innerText
        }))
    }
    useEffect(() => {
      if (!mounted.current) {
        mounted.current = true;
      } else {
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
      }
    },[text])


    return ( 
        <div>
            <button
            onClick={setTextAndRead}
            >
               안녕하세요
            </button>
        </div>
     );
}
 
export default TextToSpeech;