import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import ReactPlayer from "react-player";
import axios from "axios";

import { useNavigate } from "react-router-dom";
import { Buffer } from "buffer";


const PlayVideo = () => {

    const navigate = useNavigate();
    const {videoList, token} = useSelector(state => ({
        videoList : state.valid.videos,
        token: state.valid.accessToken
    }))

/*     const localAxios = () => {
        axios({
            url: "http://i8a208.p.ssafy.io:3000/movie/3",
            headers: {
                "Authorization" : `Bearer ${token}`
            }
        })
        .then((res) => {
            console.log(res)
        })
        .catch((err) => {
            console.log(err)
        })
    } */

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
            url = {`http://i8a208.p.ssafy.io:3000/movie/${nowplaying}`}
            muted={false}
            playing
            controls
            progressInterval={1000}
            onEnded={() => {nextVideo(videoList, nowplaying)}}
          />
        </div>
        
     );
}
 
export default PlayVideo;