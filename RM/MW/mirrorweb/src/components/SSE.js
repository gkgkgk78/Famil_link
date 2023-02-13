import {useState, useEffect} from "react";
import { EventSourcePolyfill } from 'event-source-polyfill';
import axios from "axios";
import {useSelector, useDispatch } from "react-redux";
import { setSSEcondition } from '../modules/valid';


const BASE_URL = "http://http://i8a208.p.ssafy.io:3000/sse"

function SSE() {
    const { memactoken, ssecondition} = useSelector(state => ({
      memactoken : state.valid.memberAccessToken,
      ssecondition : state.valid.ssecondition
    }))

    const dispatch = useDispatch()
    const changeSSE = (bool) => dispatch(setSSEcondition(bool))

    useEffect(() => {
      console.log("SSE MOUNTED")
      let eventSource = new EventSourcePolyfill(`${BASE_URL}/subscribe`,
          {
            headers: {
              "Authorization": `Bearer ${memactoken}`
            },
            withCredentials: true
          });
      if (memactoken) {
        const ssesubscribe = async () => {
          
          eventSource.onmessage = () => {
            if (ssecondition === false) {
              changeSSE(true)
            }
          }
        }

        ssesubscribe();
        } else {
          eventSource.close()
        }
    }, [memactoken])

        

        

}




export default SSE;