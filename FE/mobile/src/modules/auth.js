// 회원 인증 Form  Redux
import { createAction, handleActions } from "redux-actions";
// immer
import produce from "immer";
import { takeLatest } from "redux-saga/effects";
// redux-saga 관련
import createRequestSaga, {
  createRequestActionTypes,
} from "../lib/createRequestSaga";
// 모든 rest api 가져오기
import * as authAPI from "../lib/api/auth";
import axios from 'axios'

const API_URL = "http://localhost:3000";

// 회원인증 onChange, onSubmit 관련
const CHANGE_FIELD = "auth/CHANGE_FIELD";
const INITIALIZE_FORM = "auth/INITIALIZE_FORM";
const LOG_OUT = 'token/LOG_OUT'


// 회원가입 관련 saga
// 비구조화 할당 통해서 createRequestActionTyle에서의
// [type, SUCCESS, FAILURE]을 [REGISTER, REGISTER_SUCCESS, REGISTER_FAILURE]로
const [SIGNUP, SIGNUP_SUCCESS, SIGNUP_FAILURE] =
  createRequestActionTypes("auth/SIGNUP");

  // 로그인 관련 saga
const [LOGIN, LOGIN_SUCCESS, LOGIN_FAILURE] =
  createRequestActionTypes("auth/LOGIN_FAILURE");

  
  
  // onChange
export const changeField = createAction(
  CHANGE_FIELD,
  ({ form, key, value }) => ({
    form, //signup, login
    key, // username, password, passwordconfirm
    value, //실제 바꾸려는 값
  })
  );
  
  // Form 초기 상태
  export const initializeForm = createAction(INITIALIZE_FORM, (form) => form); //register / login
  
  //로그인
  export const login = createAction(LOGIN, ({ email, pw }) => ({
    email,
    pw,
  }));
  
  export const getCurrentUser = () => {
    return JSON.parse(localStorage.getItem("faccesstoken"));
  };
  
  export const familyLogout = createAction(LOG_OUT, lgot => lgot);
  // 회원가입
export const signup = createAction(
  SIGNUP,
  ({ email, pw, address, phone, nickname }) => ({
    email,
    pw,
    address,
    phone,
    nickname,
  })
);


//사가 생성
// [회원가입 사가]
// signup === family.post('/api/auth/signup', { username, password, ..., });
const signupSaga = createRequestSaga(SIGNUP, authAPI.signup);

// [로그인 사가]
// login === family.post('/api/auth/login', {username, password});
const loginSaga = createRequestSaga(LOGIN, authAPI.login);

// redux-saga는 "제너레이터 함수"를 사용해 비동기 작업을 관리
export function* authSaga() {
  // 기존에 진행중이던 작업이 있다면 취소,
  // 가장 마지막으로 실행된 작업만 수행
  yield takeLatest(SIGNUP, signupSaga);
  yield takeLatest(LOGIN, loginSaga);
}


// 초기화
const initialState = {
  signup: {
    email: "",
    pw: "",
    pwConfirm: "",
    address: "",
    nickname: "",
    phone: "",
  },
  login: {
    email: "",
    pw: "",
  },
  auth: null,
  authError: null,
  logIn: false,
  logout: false,
};

const auth = handleActions(
  {
    [CHANGE_FIELD]: (state, { payload: { form, key, value } }) =>
      produce(state, (draft) => {
        draft[form][key] = value; //예: state.signup.usrename을 바꾼다
      }),
    [INITIALIZE_FORM]: (state, { payload: form }) => ({
      ...state,
      [form]: initialState[form],
      authError: null, //폼 전환시 회원 인증 에러 초기화
    }),
    // 회원 가입 성공
    [SIGNUP_SUCCESS]: (state) => ({
      ...state,
      authError: null,
    }),
    // 회원 가입 실패
    [SIGNUP_FAILURE]: (state, { payload: error }) => ({
      ...state,
      authError: error,
    }),
    //로그인 성공
    [LOGIN_SUCCESS]: (state, { payload: auth }) => ({
      ...state,
      authError: null,
      auth,
      logIn: true,
    }),
    //로그인 실패
    [LOGIN_FAILURE]: (state, { payload: error }) => ({
      ...state,
      authError: error,
    }),
    [LOG_OUT]: (state, {payload: lgot}) => ({
      ...state,
      logout: true,
  }),
  },
  initialState
);

export default auth;
