import React from "react";
import { useNavigate } from "react-router-dom";

const Main = () => {
    const navigate = useNavigate();
    const isLoggedIn = false;

    return (
        <div>
            {isLoggedIn ? '메인 페이지' : navigate("/login")}
        </div>
    )

}
export default Main;