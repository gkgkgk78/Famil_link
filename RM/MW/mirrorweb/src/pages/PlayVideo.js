import { useEffect, useState, useRef } from "react";
import { useSelector, useDispatch } from "react-redux";
import ReactPlayer from "react-player";
import axios from "axios";


import { useNavigate } from "react-router-dom";
import { setSSEcondition, setVideos } from "../modules/valid";


const PlayVideo = () => {
    
    const navigate = useNavigate();
    const {videoList, memacctoken, me, ssecondition} = useSelector(state => ({
        videoList : state.valid.videos,
        memacctoken: state.valid.memberAccessToken,
        me : state.valid.me,
        ssecondition : state.valid.ssecondition
    }))

    const [nowplaying, setVideo] = useState(0)
    const [nowVideoList, setNowVideoList] = useState(videoList)

    const dispatch = useDispatch();
    const changeSSE = (bool) => dispatch(setSSEcondition(bool))
    const updateVideoList = (newList) => dispatch(setVideos(newList))

    const sseMounted = useRef(false)

    useEffect(() => {
        axios({
            method:'GET',
            url:`http://i8a208.p.ssafy.io:3000/movie/${nowVideoList[nowplaying]}`,
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

    useEffect(() => {
        if (!sseMounted.current) {
            sseMounted.current = true
        } else {
            if (ssecondition === true) {
                axios({
                    method: "get",
                    url: `http://i8a208.p.ssafy.io:3000/movie/video-list`,
                    headers: {
                      "Authorization": `Bearer ${memacctoken}`
                    }
                  })
                  .then ((res) => {
                    if (res.data["movie-list"]){
                      const objectList = res.data["movie-list"]
                      let emptyList = []
                      for (let movie of objectList) {
                         emptyList.push(movie["uid"])
                        }
                      updateVideoList(emptyList)
                      setNowVideoList(emptyList)
                    }
                    changeSSE(false)
                  })
                  .catch ((err) => {
                    console.log("동영상이 없어")
                    console.log(err)
                  })
            }
        }
        
    },[ssecondition])

    const [videoData, setVideoData] = useState(null)


    const nextVideo = (nowVideoList, nowplaying) => {
        if (nowplaying === nowVideoList.length -1) {
            console.log("재생이 끝나서 2초 뒤에 메인 페이지로 넘어갑니다.")
            setTimeout(() => {
              navigate("/")
            }, 2000)
        } else {
            if (me) {
                setVideo(() => {
                    return nowplaying +1
              })
            } else {
                console.log("멤버 로그아웃. 2초 뒤 메인 페이지로 넘어갑니다.")
                setTimeout(() => {
                  navigate("/")
                }, 2000)
            }

        }
    }

    return (
        <div>
          <div className="playercontainer">
          <ReactPlayer
            url = {videoData}
            muted={false}
            playing={true}
            controls
            width="100%"
            height="auto"
            progressInterval={1000}
            onEnded = {(() => nextVideo(nowVideoList, nowplaying))}
          />
          </div>
        </div>
        
     );
}
 
export default PlayVideo;
