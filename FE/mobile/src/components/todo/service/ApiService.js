import client from './client'
import {apiInstance} from "./client.js";

const api_headers = apiInstance();

function addTodoList(param, success, fail) {
    api_headers.defaults.headers["Authorization"] = "Bearer " + localStorage.getItem("faccesstoken").replace(/\"/gi, '');

    console.log(localStorage.getItem("faccesstoken").replace(/\"/gi, ''))

    console.log(localStorage.getItem("faccesstoken"));
    api_headers.post(`/todo`, JSON.stringify(param)).then(success).catch(fail);
}

function getTodoList(params, success, fail){
    api_headers.defaults.headers["Authorization"] = "Bearer " + localStorage.getItem("faccesstoken").replace(/\"/gi, '');
    api_headers.get(`/todo`).then(success).catch(fail);
}

function deleteTodo(param, success, fail){
    api_headers.defaults.headers["Authorization"] = "Bearer " + localStorage.getItem("faccesstoken").replace(/\"/gi, '');
    api_headers.delete(`/todo`, JSON.stringify(param)).then(success).catch(fail);
}

export {
    addTodoList,
    getTodoList,
    deleteTodo
};