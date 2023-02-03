import {useSelector,useDispatch} from "react-redux"
import { startRecording, stopRecording } from "../modules/valid";
import React, {useRef} from 'react';


const Record = () => {
    // redux에서 불러오기
    const { memberInfo, recording } = useSelector(state => ({
        recording: state.valid.isRecording
    }))
    // action dispatch
    const dispatch = useDispatch();
    const onRecording = () => dispatch(startRecording())
    const notRecording = () => dispatch(stopRecording())

    const timer = useRef(0);

    // 들어오고 1초뒤에 녹화 시작
    setTimeout( () => {
        onRecording()
        setInterval(() => {
           timer.current +=1 
        }, 1000);
    }, 1000)

    // 녹화 시작하고 30초 뒤에 자동으로 녹화 종료
    setTimeout( () => {
        notRecording()
    }, 31000)

    return ( 
        <div>
            {timer.current}
        </div>
     );
}
 
export default Record;