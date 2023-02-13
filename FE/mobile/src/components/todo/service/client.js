import { API_BASE_URL } from "../ApiBaseUrl";
import axios from "axios";


function apiInstance() {
    return axios.create({
        baseURL: API_BASE_URL,
        headers: {
            "Content-Type": "application/json;charset=utf-8",
        },
    });
}

export {apiInstance};

    
// export default client;