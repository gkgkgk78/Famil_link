import './App.css';
import {useState} from "react";
import {EventSourcePolyfill} from 'event-source-polyfill';
import axios from "axios";


const BASE_URL = "http://i8a208.p.ssafy.io:3000/sse"

function App() {
    const [count, setCount] = useState(null);

    const handleConnect = () => {
        const sse = new EventSourcePolyfill(`${BASE_URL}/subscribe`,
            {
                headers: {
                    "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwicm9sZXMiOlsiUk9MRV9NRU1CRVIiXSwibGV2ZWwiOiJtZW1iZXIiLCJpYXQiOjE2NzYyNjUzNjksImV4cCI6MTAwMTY3NjI2NTM2OX0.TcX3rwIs04Yj6bWYBhTJNd772WkI-S7kzrBCYvi4VEU"
                }
            });

        sse.addEventListener('send', (e) => {
            const {data: receivedConnectData} = e;

            console.log('connect event data: ', receivedConnectData);
        });

        sse.addEventListener('count', e => {

            const {data: receivedCount} = e;

            // setData(JSON.parse(receivedSections));
            console.log("count event data", receivedCount);
            setCount(receivedCount);
        })
    }


    return (
        <div className="App">
            <button onClick={handleConnect}>connect 요청</button>
            <div>{count}</div>
        </div>
    );
}

export default App;
