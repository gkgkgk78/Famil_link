import { createAction, handleActions } from "redux-actions";
import { takeLatest } from "redux-saga/effects";
import * as authAPI from '../lib/api/auth';
import createRequestSaga, { createRequestActionTypes, 
} from "../lib/createRequestSaga";


const TEMP_SET_USER = 'user/TEMP_SET_USER' // 새로고침 이후 임시 로그인 처리

const SET_FAMILY_ACCESS = 'user/SET_FAMILY_ACCESS'
const SET_FAMILY_REFRESH = 'user/SET_FAMILY_REFRESH'
const SET_MEMBER_ACCESS = 'user/SET_MEMBER_ACCESS'
const SET_MEMBER_REFRESH = 'user/SET_MEMBER_REFRESH'

// 회원 정보 확인
const [CHECK, CHECK_SUCCESS, CHECK_FAILURE] = createRequestActionTypes(
    'user/CHECK',
);

export const tempSetUser = createAction(TEMP_SET_USER, user => user);
export const check = createAction(CHECK);

export const setFamilyAccess = createAction(SET_FAMILY_ACCESS, fatoken => fatoken);
export const setFamilyRefresh = createAction(SET_FAMILY_REFRESH, frtoken => frtoken);
export const setMemberAccess = createAction(SET_MEMBER_ACCESS, matoken => matoken);
export const setMemberRefresh = createAction(SET_MEMBER_REFRESH, mrtoken => mrtoken);

const checkSaga = createRequestSaga(CHECK, authAPI.check);
export function* userSaga() {
    yield takeLatest(CHECK, checkSaga);
}

const initialState = {
    user: null,
    checkError: null,
    fatoken: null,
    frtoken: null,
    matoken: null,
    mrtoken: null,
};

export default handleActions(
    {
        [TEMP_SET_USER]: (state, {payload: user}) => ({
            ...state,
            user,
        }), 
        [CHECK_SUCCESS]: (state, {payload: user }) => ({
            ...state,
            user,
            checkError: null,
        }),
        [CHECK_FAILURE]: (state, {payload: error}) => ({
            ...state,
            user: null,
            checkError: error,
        }),
        [SET_FAMILY_ACCESS]: (state, {payload: fatoken}) => ({
            ...state,
            fatoken,
        }),
        [SET_FAMILY_REFRESH]: (state, {payload: frtoken}) => ({
            ...state,
            frtoken,
        }),
        [SET_MEMBER_ACCESS]: (state, {payload: matoken}) => ({
            ...state,
            matoken,
        }),
        [SET_MEMBER_REFRESH]: (state, {payload: mrtoken}) => ({
            ...state,
            mrtoken,
        })
    },
    initialState,
)