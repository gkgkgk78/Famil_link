import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import ReactPlayer from "react-player";
import axios from "axios";

import { useNavigate } from "react-router-dom";
import { Buffer } from "buffer";
import { formControlClasses } from "@mui/material";


const PlayVideo = () => {

    const navigate = useNavigate();
    const [videoURL,setURL] = useState("")

    const localAxios = () => {
        axios({
            url: "http://localhost:9999/movie/2",
            headers: {
                "Authorization" : "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImxldmVsIjoiYWNjb3VudCIsImlhdCI6MTY3NTA2MDg4OSwiZXhwIjoxNjg1MDYwODg5fQ.OhUbJt41_G4V93jsjDDNHz3BCd5ZOQBcFRBNfbwVV5I"
            }
        })
        .then((res) => {
            const base64Data = Buffer.from(JSON.stringify(res.data)).toString("base64")
            /* let blob = new Blob([new ArrayBuffer(base64Data)], {type:"video/mp4"});
            console.log(blob)
            const url = window.URL.createObjectURL(blob); */
            setURL(base64Data)
            
        })
        .catch((err) => {
            console.log(err)
        })
    }

    useEffect(() => {
        console.log("데이터가 바뀌었다.")
    }, [videoURL])

    const videoList = useState(2)
    const [nowplaying, setVideo] = useState(0)
    

    const nextVideo = (video, nowplaying) => {
        if (nowplaying === video.length -1) {
            navigate("/")
        } else {
            setVideo(nowplaying +1)
        }
    }

    return (
        <div> 
          <ReactPlayer
            url= ""
            muted={false}
            playing
            controls
            progressInterval={1000}
            onEnded={() => {nextVideo(videoList, nowplaying)}}
          />
          <button onClick={localAxios}>요청보내기</button>
        </div>
        
     );
}
 
export default PlayVideo;