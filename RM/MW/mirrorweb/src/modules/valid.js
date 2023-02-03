// 액션 타입
const CHANGE_VALID = "valid/CHANGE_VALID"
const SET_INFO = "valid/SET_INFO"
const START_RECORDING = "valid/START_RECORDING"
const STOP_RECORDING = "valid/STOP_RECORDING"


// 액션 생성 함수
export const setValid = () => ({type:CHANGE_VALID});
export const setInfo = (info) => ({type: SET_INFO, info})
export const startRecording = () => ({type: START_RECORDING})
export const stopRecording = () => ({type: STOP_RECORDING})


// 초기 상태
const initialState = {
    validation: false,
    isRecording: false,
    memberInfo: {}
}


// 리듀서
export default function valid(state = initialState, action) {
    switch (action.type) {
        case CHANGE_VALID:
            return {
                ...state,
                valid: true
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
    }
}