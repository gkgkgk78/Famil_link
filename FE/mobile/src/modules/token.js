import { createAction } from "redux-actions";
import { handleActions } from "redux-actions";

const initialState = {
    familyaccesstoken: "",
    familyuid: "",
    familymemberprofile: "",
    familymembername: "",
}

const SET_FAMILY_ACCESS = 'token/SET_FAMILY_ACCESS'
const SET_FAMILY_UID = 'token/SET_FAMILY_UID'
const SET_FAMILY_MEMBER_PROFILE = 'token/SET_FAMILY_MEMBER_PROFILE'
const SET_FAMILY_MEMBER_NAME = 'token/SET_FAMILY_MEMBER_NAME'


export const familyToken = createAction(SET_FAMILY_ACCESS, fatkn => fatkn); 
export const familyUid = createAction(SET_FAMILY_UID, fauid => fauid);
export const familyMemberProfile = createAction(SET_FAMILY_MEMBER_PROFILE, fmpf => fmpf)
export const familyMemberName = createAction(SET_FAMILY_MEMBER_NAME, fmnm => fmnm)


export default handleActions(
    {
        [SET_FAMILY_ACCESS]: (state, {payload: fatkn}) => ({
            ...state,
            familyaccesstoken:fatkn,
        }),
        [SET_FAMILY_UID]: (state, {payload: fauid}) => ({
            ...state,
            familyuid: fauid,
        }),
        [SET_FAMILY_MEMBER_NAME]: (state, {payload: fmnm}) => ({
            ...state,
            familymembername: fmnm,
        }),
        [SET_FAMILY_MEMBER_PROFILE]: (state, {payload: fmpf}) => ({
            ...state,
            familymemberprofile: fmpf,
        })
    },
    initialState,
)