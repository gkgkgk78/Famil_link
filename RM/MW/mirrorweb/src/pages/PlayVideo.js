import { useState } from "react";
import { useSelector } from "react-redux";
import ReactPlayer from "react-player";

import { useNavigate } from "react-router-dom";

const PlayVideo = () => {
    // const videolist= useSelector(state => state.videos)
    // state에 저장한 새로온 영상 편지 식별자로 DB에 요청을 보내 플레이

    
    // 영상이 플레이 되었을 때

    const navigate = useNavigate();
    
    const videoList = [
        "https://youtu.be/Eq7TEvaMT1k",
        "https://youtu.be/Dug_whWpaA8",
        "https://youtu.be/kgPNTt0_vTA"
    ]
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
            url= {videoList[nowplaying]}
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