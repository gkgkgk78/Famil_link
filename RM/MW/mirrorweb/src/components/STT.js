import React, { useEffect, useRef, useState } from "react";
import secrets from "./secrets.json"

import useSpeechToText from "react-hook-speech-to-text"
import { useNavigate, useLocation } from "react-router-dom";
import {useSelector, useDispatch } from "react-redux";
import { setToMember, startRecording } from "../modules/valid";

const STT = () => {
    const API_KEY = secrets.google_speech_api_key
    const mounted = useRef(false);
    const {memberInf, to, from} = useSelector(state => ({
      memberInf: state.valid.memberInfo,
      to: state.valid.toMember,
      from: state.valid.me
    }))

    const dispatch = useDispatch();
    const changeToMember = (member) => dispatch(setToMember(member));

    const {
        error,
        results,
        startSpeechToText,
      } = useSpeechToText({
        continuous: true,
        useLegacyResults: false,
        crossBrowser: true,
        useOnlyGoogleCloud: true,
        googleApiKey: API_KEY,
        googleCloudRecognitionConfig: {
            languageCode: 'ko-KR',
            model:"latest_short"
          }
      });

    const Navigate = useNavigate();
    const location = useLocation();
    
    useEffect(() => {
      setTimeout(() => {
          startSpeechToText()
      }, 50)
    },[])

    useEffect(() => {
        if (!mounted.current) {
            mounted.current = true;
        } else{
            if (results.length>=1){
              let text = results[results.length-1].transcript
              console.log(text)
                // 현재 녹화 페이지가 아니면
                if (location.pathname !== "/record") {
                    // 녹화라는 음성이 인식되었을 때
                    if (text.includes("녹화") || text.includes("노콰"))   {
                         // 녹화 페이지로 이동 
                          Navigate("/record")
                      }
                // 현재 녹화 페이지이면
                  } else if (location.pathname === "/record") {
                    if (to===null) {
                      if (memberInf) {
                        // 음성 인식한 텍스트가 멤버 중에 있으면
                        if (Object.keys(memberInf).includes(text)) {
                          // 받는 멤버를 저장한다.
                          changeToMember(memberInf[text])
                        } else {
                          // 음성 인식이 잘 안되었으면 다시 한 번 말해라
                          console.log("다시 한 번 말씀해주세요")
                        }
                      } else {
                        console.log("임시 에러")
                      }
                    } else {
                      console.log("받는 사람이 이미 설정되어 있습니다.")
                    }
                  }
            }
        }
      },[results])

      if (error) return <p>Web Speech API is not available in this browser 🤷‍</p>;

    }



export default React.memo(STT);
