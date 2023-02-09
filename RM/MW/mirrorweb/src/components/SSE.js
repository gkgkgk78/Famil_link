import './App.css';
import {useState} from "react";
import { EventSourcePolyfill } from 'event-source-polyfill';
import axios from "axios";


const BASE_URL = "http://http://i8a208.p.ssafy.io:3000/sse"

function SSE() {
    const [count, setCount] = useState(null);

    const handleConnect = () =>{
        const sse = new EventSourcePolyfill(`${BASE_URL}/send`);

        sse.addEventListener('send', (e) => {
            const { data: receivedConnectData } = e;

            console.log('connect event data: ',receivedConnectData);
        });

    }


  return (
    <div className="App">
        <button onClick={handleConnect}>connect 요청</button>
        <div>{count}</div>
    </div>
  );
}

export default SSE;