import { useEffect, useRef, useState } from "react";
import secrets from "../secrets.json"

import useSpeechToText from "react-hook-speech-to-text"
import { useNavigate, useLocation } from "react-router-dom";

const STT = () => {
    const API_KEY = secrets.google_speech_api_key
    const mounted = useRef(false);

    const {
        error,
        isRecording,
        results,
        startSpeechToText,
        stopSpeechToText,
      } = useSpeechToText({
        continuous: true,
        useLegacyResults: false,
        crossBrowser: true,
        useOnlyGoogleCloud: true,
        googleApiKey: API_KEY,
        googleCloudRecognitionConfig: {
            languageCode: 'ko-KR'
          }
      });

    const Navigate = useNavigate();
    const location = useLocation();
    
    
    setTimeout(() => {
        startSpeechToText()
    }, 50)

    useEffect(() => {
        if (!mounted.current) {
            mounted.current = true;
        } else{
            if (results.length>1){
                if (location.pathname !== "/record") {
                    if (text.includes("녹화") || text.includes("노콰"))   { 
                          Navigate("/record")
                      }
                  } else if (location.pathname === "/record") {
                    console.log("")
                  }

            }
        }
      },[results])

      if (error) return <p>Web Speech API is not available in this browser 🤷‍</p>;

    }



export default STT;
