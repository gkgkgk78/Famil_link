import {useSelector,useDispatch} from "react-redux"
import { startRecording, stopRecording, setToCog } from "../modules/valid";

import { useNavigate } from "react-router-dom";
import React, {useRef, useEffect, useState} from 'react';


const Record = () => {
    // redux에서 불러오기
    const { to, recording, toCog } = useSelector(state => ({
        recording: state.valid.isRecording,
        to: state.valid.toMember,
    }))
    const dispatch = useDispatch();
    const startRecord = () => dispatch(startRecording())
    const stopRecord = () => dispatch(stopRecording())
    const changeToCog = () => dispatch(setToCog())

    const [timer,setTimer] = useState(0);
    const [publicMsg, setMsg] = useState("받으시는 분 이름을 말씀해주세요");

    const navMounted = useRef(false)
    const toMounted = useRef(false)
    
    const Navigate = useNavigate();

    // STT를 통해 받는 멤버가 바뀌었을 때(설정되었을 때)
    useEffect(() => {
        if (!toMounted.current) {
            toMounted.current = true
        } else {
            if (to) {
                startRecord();
                setInterval(() => {
                    setTimer( (timer) => {
                        if (0<=timer<=14) {
                            return timer+1
                        } else {
                            return 15
                        } 
                    })
                }, 1000)
            }
        }
    }, [to])

    // 현재 recording에 따라 
    useEffect(() => {
        if (!navMounted.current) {
            navMounted.current = true;
        } else {
            if (recording === false) {
                setMsg("녹화가 종료되었습니다. 메인 페이지로 돌아갑니다.")
                setTimeout(() => {
                    Navigate("/")
                }, 2000)
            } else if (recording === true) { 
                setTimeout(() => {
                    stopRecord()
                },15000)
            }
        }

    },[recording])

    useEffect(() => {
        if (to) {
            setMsg("녹화 중입니다.")
        } else {
            setTimeout(setMsg("다시 한 번 말씀해주세요"),
            3000)
        }
    },[to])


    return ( 
        <div>
            <div className="fb">
                <p>{publicMsg}</p>
            </div>
            <div className="timer">
                <p>00:{timer <= 9 ? `0${timer}` :`${timer}`}</p>
            </div>
        </div>
     );
}
 
export default Record;
