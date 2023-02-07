import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { isLoggIn } from '../modules/auth';
import { useDispatch } from "react-redux";

const Main = () => {
    // const navigate = useNavigate();
    // const dispatch = useDispatch();
    // const [logIn, setLogIn] = useState(false);
    
    // useEffect(() => {
    //     dispatch(isLoggIn({logIn}))
    //     console.log(setLogIn)
    // },[])
    
    return (
        <div>
            {/* {logIn ? '메인페이지' : navigate("/login")} */}
            <p> ㅎㅇㅎㅇ</p>
        </div>
    )

}
export default Main;