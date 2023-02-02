// (로그인이 된 뒤) 매번 처음 보이는 안면 인식 페이지

import React from 'react';

import { useNavigate } from 'react-router-dom';
import { Button } from '@mui/material';

import axios from 'axios';
import Caption from '../components/Caption';

const FaceValid = () => {
    const navigate = useNavigate();
    /* 
    라즈베리파이에서 사람A가 인증 되었다는 신호가 오면
    사람 A의 정보를 백엔드에 요청

    axios({
      method: "get",
      url:"백의 url",
      data:{
        구성원 식별자
      }
    })
    .then (res => {
      사용자 정보를 store에 저장(식별자, 새로온 영상 편지 URL, 가족 계정의 일정과 Todo)
      if (영상 있음) {
        navigate("/playvideo")
      } else ( 영상 없음 ) {
        navigate("/main")
      }
    )
    .catch(err => {
        에러 처리
    })
    */


    

    return (
        <div>
            <div style={{
              height:'401.5px'
            }}></div>
            <Caption></Caption>
            <Button
            onClick={() => {
              navigate("/playvideo")
            }}>
              동영상 재생페이지로
            </Button>
        </div>
    );
    
}
 
export default FaceValid;