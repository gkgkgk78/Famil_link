import React, {useEffect} from "react";
import { useNavigate, useLocation, Outlet } from "react-router-dom";
import Cookies from "js-cookie";

const AuthLayout = () => {
    const navigate = useNavigate();
    const { pathname } = useLocation();

    useEffect(() => {
        if (!Cookies.get("id")) {
            navigate('/', {state: pathname});
        }
    }, []);

    return (
        <div>
            <Outlet />
        </div>
    );
};

export default AuthLayout;