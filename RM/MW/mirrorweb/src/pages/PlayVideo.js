import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import ReactPlayer from "react-player";
import axios from "axios";

import { useNavigate } from "react-router-dom";


const PlayVideo = () => {

    const navigate = useNavigate();
    const {videoList, token} = useSelector(state => ({
        videoList : state.valid.videos,
        memacctoken: state.valid.memberAccessToken
    }))
    
    useEffect(() => {
        console.log(videoList)
    },[])

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
    const urlList = videoList.map(function(el) {
        return `http://i8a208.p.ssafy.io:3000/movie/${el}`
    })

    const nextVideo = (video, nowplaying) => {
        if (nowplaying === video.length -1) {
            console.log("2초 뒤에 메인 페이지로 넘어갑니다.")
            setTimeout(() => {
              navigate("/")
            }, 2000)
        } else {
            setVideo(() => {
                return nowplaying +1
          })
        }
    }

    return (
        <div> 
          <ReactPlayer
            url = {urlList[nowplaying]}
            muted={false}
            playing={true}
            controls
            progressInterval={1000}
            onEnded = {(() => nextVideo(urlList, nowplaying))}
          />
        </div>
        
     );
}
 
export default PlayVideo;