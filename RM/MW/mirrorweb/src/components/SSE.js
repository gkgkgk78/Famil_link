import { useSelector } from "react-redux";
import {useState, useEffect} from "react";
import {EventSourcePolyfill} from 'event-source-polyfill';
import axios from "axios";

const BASE_URL = "http://i8a208.p.ssafy.io:3000/sse"

function SEE() {
  const {memactoken, me} = useSelector(state => ({
    me : state.valid.me,
    memactoken : state.valid.memberAccessToken
  }))    

  const handleConnect = () => {
    const sse = new EventSourcePolyfill(`${BASE_URL}/subscribe`,
        {
            headers: {
                "Authorization": `Bearer ${memactoken}`
            }
        });

    sse.addEventListener('send', () => {
        console.log("test!")

    });
  }
  useEffect(() => {
    handleConnect()
  },[])

}

export default SEE;