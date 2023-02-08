import { createAction } from "redux-actions";
import { handleActions } from "redux-actions";

const initialState = {
    familyaccesstoken: "",
}

const SET_FAMILY_ACCESS = 'token/SET_FAMILY_ACCESS'

export const familyToken = createAction(SET_FAMILY_ACCESS, fatkn => fatkn);


export default handleActions(
    {
        [SET_FAMILY_ACCESS]: (state, {payload: fatkn}) => ({
            ...state,
            familyaccesstoken:fatkn,
        })
    },
    initialState,
)