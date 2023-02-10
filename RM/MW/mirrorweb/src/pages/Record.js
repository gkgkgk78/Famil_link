import {useSelector,useDispatch} from "react-redux"
import { startRecording, stopRecording } from "../modules/valid";

import { useNavigate } from "react-router-dom";
import React, {useRef, useEffect, useState} from 'react';


const Record = () => {
    // redux에서 불러오기
    const { to, recording } = useSelector(state => ({
        recording: state.valid.isRecording,
        to: state.valid.toMember
    }))
    const dispatch = useDispatch();
    const startRecord = () => dispatch(startRecording())
    const stopRecord = () => dispatch(stopRecording())

    const [timer,setTimer] = useState(0);

    const navMounted = useRef(false)
    const toMounted = useRef(false)
    
    const Navigate = useNavigate();

    // 들어오고 1초뒤에 받는 사람 설정(STT로)
    useEffect(() => {
        setTimeout( () => {
            console.log("받으시는 분 이름을 말씀해주세요")
        }, 1000)
    },[])

    // STT를 통해 받는 멤버가 바뀌었을 때(설정되었을 때)
    useEffect(() => {
        if (!toMounted.current) {
            toMounted.current = true
        } else {
            if (to) {
                startRecord();
                console.log(` to useEffect 안에서 ${recording}`)
                setInterval(() => {
                    setTimer( (timer) => {
                            return timer+1
                    })
                }, 1000)
            }
            // 녹화 시작
        }
    }, [to])

    // 현재 recording에 따라 
    useEffect(() => {
        if (!navMounted.current) {
            navMounted.current = true;
        } else {
            if (recording === false) {
                console.log("녹화가 종료되었습니다. 메인 페이지로 돌아갑니다.")
                setTimeout(() => {
                    Navigate("/")
                }, 3000)
            } else if (recording === true) { 
                console.log("녹화가 시작됩니다.")
                console.log(`recording useEffect 안에서 ${recording}`)
                setTimeout(() => {
                    stopRecord()
                },30000)
            }
        }

    },[recording])


    return ( 
        <div>
            <p>00:{timer <= 9 ? `0${timer}` : 10<=timer<=29 ? `${timer}` : "30"}</p>
        </div>
     );
}
 
export default Record;