import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import ReactPlayer from "react-player";
import axios from "axios";


import { useNavigate } from "react-router-dom";


const PlayVideo = () => {

    const [nowplaying, setVideo] = useState(0)
    const navigate = useNavigate();
    const {videoList, memacctoken} = useSelector(state => ({
        videoList : state.valid.videos,
        memacctoken: state.valid.memberAccessToken
    }))


    useEffect(() => {
        axios({
            method:'GET',
            url:`http://i8a208.p.ssafy.io:3000/movie/${videoList[nowplaying]}`,
            responseType:'blob',
            headers: {
                'Authorization': `Bearer ${memacctoken}`
            },
        })
        .then((res) => {
            const url = window.URL.createObjectURL(new Blob([res.data], { type: res.headers['content-type'] } ));
            setVideoData(url)
            axios({
                method: "PUT",
                url: `http://i8a208.p.ssafy.io:3000/movie/${videoList[nowplaying]}`,
                headers: {
                    'Authorization': `Bearer ${memacctoken}`
                }
            })
            .then((res) => {
                console.log(res)
            })
            .catch((err) => {
                console.log(err)
            })
        })
        .catch(e => {
            console.log(`error === ${e}`)
        })

    },[nowplaying])

    const [videoData, setVideoData] = useState(null)
    
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
            url = {videoData}
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
