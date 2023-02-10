import client from './client'
import {apiInstance} from "./client.js";

const api_headers = apiInstance();

function getBoardKindList(param, success, fail) {
    api_headers.defaults.headers["Authorization"] = "Bearer " + localStorage.getItem("faccesstoken").replace(/\"/gi, '');

    console.log(localStorage.getItem("faccesstoken").replace(/\"/gi, ''))

    console.log(localStorage.getItem("faccesstoken"));
    api_headers.post(`/todo`, JSON.stringify(param)).then(success).catch(fail);
}

export {
    getBoardKindList,
};