// 액션 타입
const SET_ACCESSTOKEN = "valid/SET_ACCESSTOKEN"
const CHANGE_VALID = "valid/CHANGE_VALID"
const SET_ME = "valid/SET_ME"
const SET_INFO = "valid/SET_INFO"
const START_RECORDING = "valid/START_RECORDING"
const STOP_RECORDING = "valid/STOP_RECORDING"
const SET_TOMEMBER = "valid/SET_TOMEMBER"
const SET_VIDEOS = "valid/SET_VIDEOS"


// 액션 생성 함수
export const setAccessToken = (token) => ({type:SET_ACCESSTOKEN, token})
export const setValid = () => ({type:CHANGE_VALID})
export const setMe = data => ({type:SET_ME, data})
export const setInfo = info => ({type: SET_INFO, info})
export const startRecording = () => ({type: START_RECORDING})
export const stopRecording = () => ({type: STOP_RECORDING})
export const setToMember = memberID => ({type: SET_TOMEMBER, memberID}) 
export const setVideos = videoList => ({type: SET_VIDEOS, videoList})


// 초기 상태
const initialState = {
    me: null,
    accessToken: null,
    validation: false,
    isRecording: false,
    memberInfo: {
        "최진우": 1,
        "정우진": 2
    },
    toMember: null,
    videos: []
}


// 리듀서
export default function valid(state = initialState, action) {
    switch (action.type) {
        case SET_ACCESSTOKEN:
            return {
                ...state,
                accessToken: action.token
            }
        case CHANGE_VALID:
            return {
                ...state,
                validation: true
            }
        case SET_ME:
            return {
                ...state,
                me: action.data
            }
        case SET_INFO:
            return {
                ...state,
                memberInfo: action.info
                     
            }
        case START_RECORDING:
            return {
                ...state,
                isRecording: true
            }
        case STOP_RECORDING:
            return {
                ...state,
                isRecording: false
            }
        case SET_VIDEOS:
            return {
                ...state,
                videos:action.videoList
            }
        case SET_TOMEMBER:
            return {
                ...state,
                toMember: action.memberID
            }
        default :
          return state
    }
}