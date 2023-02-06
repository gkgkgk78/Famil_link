// 액션 타입
const SET_FAMILYACCESSTOKEN = "valid/SET_FAMILYACCESSTOKEN"
const SET_FAMILYREFRSCHTOKEN = "valid/SET_FAMILYREFRSCHTOKEN"
const SET_MEMBERACCESSTOKEN = "valid/SET_MEMBERACCESSTOKEN"
const SET_MEMBERREFRSCHTOKEN = "valid/SET_MEMBERREFRSCHTOKEN"

const CHANGE_VALID = "valid/CHANGE_VALID"
const SET_ME = "valid/SET_ME"
const SET_INFO = "valid/SET_INFO"
const START_RECORDING = "valid/START_RECORDING"
const STOP_RECORDING = "valid/STOP_RECORDING"
const SET_TOMEMBER = "valid/SET_TOMEMBER"
const SET_VIDEOS = "valid/SET_VIDEOS"


// 액션 생성 함수
export const setFamilyAccessToken = (familytoken) => ({type:SET_FAMILYACCESSTOKEN, familytoken})
export const setFamilyRefreshToken = (familyrefreshtoken) => ({type:SET_FAMILYREFRSCHTOKEN, familyrefreshtoken})
export const setMemberAccessToken = (membertoken) => ({type:SET_MEMBERACCESSTOKEN, membertoken})
export const setMemberRefreshToken = (memberrefreshtoken) => ({type:SET_FAMILYREFRSCHTOKEN, memberrefreshtoken})

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
    familyAccessToken: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0Iiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImxldmVsIjoiYWNjb3VudCIsImlhdCI6MTY3NTY2NDUwNywiZXhwIjoxMDAxNjc1NjY0NTA3fQ.at0tI3D_G1fqGhAckLTpFx9qfZmPZo9FC-VwH_7uhYI",
    familyRefreshToken: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0Iiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImxldmVsIjoiYWNjb3VudCIsImlhdCI6MTY3NTY2NDUwOCwiZXhwIjoxNjg1NjY0NTA4fQ.9lRH2t0orgsy3IaLUrOi08Ysz1Ke6-RRNMNWwcTvJaI",
    memberAccessToken: "",
    memberRefreshToken: "",
    validation: false,
    isRecording: false,
    memberInfo: {
        "최진우": 4
    },
    toMember: null,
    videos: []
}


// 리듀서
export default function valid(state = initialState, action) {
    switch (action.type) {
        case SET_FAMILYACCESSTOKEN:
            return {
                ...state,
                familyAccessToken: action.familytoken
            }
        case SET_FAMILYREFRSCHTOKEN:
            return {
                ...state,
                familyRefreshToken: action.familyrefreshtoken
            }
        case SET_MEMBERACCESSTOKEN:
            return {
                ...state,
                memberAccessToken: action.membertoken
            }
        case SET_MEMBERREFRSCHTOKEN:
            return {
                ...state,
                memberRefreshToken: action.memberrefreshtoken
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