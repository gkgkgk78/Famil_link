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
const SET_SSECONDITION = "valid/SET_SSECONDITION"
const SET_TOCOG = "valid/SET_TOCOG"

const SET_VIDEOS = "valid/SET_VIDEOS"

const SET_TODOS = "valid/SET_TODOS"
const SET_CAPTIONS = "valid/SET_CAPTIONS"
const SET_WEATHER = "valid/SET_WEATHER"
const SET_SCHEDULE = "valid/SET_SCHEDULE"
const SET_MYNAME = "valid/SET_MYNAME"


// 액션 생성 함수
export const setFamilyAccessToken = (familytoken) => ({type:SET_FAMILYACCESSTOKEN, familytoken})
export const setFamilyRefreshToken = (familyrefreshtoken) => ({type:SET_FAMILYREFRSCHTOKEN, familyrefreshtoken})
export const setMemberAccessToken = (membertoken) => ({type:SET_MEMBERACCESSTOKEN, membertoken})
export const setMemberRefreshToken = (memberrefreshtoken) => ({type:SET_FAMILYREFRSCHTOKEN, memberrefreshtoken})

export const setValid = (bool) => ({type:CHANGE_VALID, bool})
export const setMe = data => ({type:SET_ME, data})
export const setMyname = data => ({type:SET_MYNAME, data})
export const setInfo = info => ({type: SET_INFO, info})

export const startRecording = () => ({type: START_RECORDING})
export const stopRecording = () => ({type: STOP_RECORDING})
export const setToMember = memberID => ({type: SET_TOMEMBER, memberID}) 
export const setToCog = bool => ({type:SET_TOCOG, bool})

export const setVideos = videoList => ({type: SET_VIDEOS, videoList})
export const setSSEcondition = bool => ({type: SET_SSECONDITION, bool})

export const setTodos = todoList => ({type: SET_TODOS, todoList})
export const setSchedule = schedule => ({type: SET_SCHEDULE, schedule})

export const setCaptions = captions => ({type: SET_CAPTIONS, captions})

export const setWeather = weather => ({type: SET_WEATHER, weather})

// 초기 상태
const initialState = {
    // 
    me: null,
    myname: null,
    familyAccessToken: "",
    familyRefreshToken: "",
    memberAccessToken: "",
    memberRefreshToken: "",

    validation: false,
    isRecording: false,

    memberInfo: null,
    toMember: null,
    toCog: true,
    
    videos: [],
    ssecondition: false,

    todos: [],
    schedules: null,
    weather: false,
    caption:['안녕하세요'],
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
                validation: action.bool
            }
        case SET_ME:
            return {
                ...state,
                me: action.data
            }
        case SET_MYNAME:
            return {
                ...state,
                myname: action.data
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
        case SET_SSECONDITION:
            return {
                ...state,
                ssecondition: action.bool
            }
        case SET_TOMEMBER:
            return {
                ...state,
                toMember: action.memberID
            }
        case SET_TOCOG:
            return {
                ...state,
                toCog: action.bool
            }
        case SET_TODOS:
            return {
                ...state,
                todos: action.todoList
            }
        case SET_CAPTIONS:
            return {
                ...state,
                caption: action.caption
            }
        case SET_WEATHER:
            return {
                ...state,
                weather: action.weather
            }
        case SET_SCHEDULE:
            return {
                ...state,
                schedules: action.schedule
            }
        default :
            return state
    }
}