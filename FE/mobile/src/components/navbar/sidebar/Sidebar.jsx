import React from "react";
import "./sidebar.css"
import Logo from "../../images/다운로드.jpg"
import SimpleLineIcon from 'react-simple-line-icons';
import Button from '../../common/Button'
import styled from 'styled-components'
import { useNavigate } from "react-router-dom";

// const ButtonWithMarginTop = styled(Button)`
//   margin-top: 1rem;
// `;

const Sidebar = ({type}) => {
    const navigate=useNavigate()
    // const token = localStorage.getItem("faccesstoken")
    // const loginstatus = {
    //     { token? "로그아웃" : "로그인"}
    // }
    // const text = loginstatus[type];
    // const onLogin = () => {
    //     navigate('/login')
    // }
    const onLogout = () => {
        localStorage.removeItem('faccesstoken')
        navigate('/login')
    }

    // const onlog = {
    //     {token ? onLogout : onLogin }
    // }

    // const onlogg = onlog[type]

    return (
        <>
        <aside className="aside">
            <a href="/" className="nav__logo">
                <img src={Logo} alt="" />
            </a>
            <nav className="nav">
                <div className="nav__menu">
                    <ul className="nav__list">
                        <li className="nav__item">
                            <a href="/" className="nav__link">
                                <SimpleLineIcon name="home" />
                            </a>
                        </li>
                        <li className="nav__item">
                            <a href="#about" className="nav__link">
                                <SimpleLineIcon name="user-following" />
                            </a>
                        </li>
                        <li className="nav__item">
                            <a href="#services" className="nav__link">
                            <SimpleLineIcon name="briefcase" />
                            </a>
                        </li>
                        <li className="nav__item">
                            <a href="#resume" className="nav__link">
                            <SimpleLineIcon name="graduation" />
                            </a>
                        </li>
                        <li className="nav__item">
                            <a href="#portfolio" className="nav__link">
                            <SimpleLineIcon name="layers" />
                            </a>
                        </li>
                        <li className="nav__item">
                            <a href="#blog" className="nav__link">
                            <SimpleLineIcon name="note" />
                            </a>
                        </li>
                        <li className="nav__item">
                            <Button gray fullWidth onClick={onLogout}>
                                로그 아웃
                            </Button>
                        </li>
                    </ul>
                </div>
            </nav>

            <div className="nav__footer">
                <span className="copyright">&copy; 2022 - 2023.</span>
            </div>
        </aside>

        <div className="nav__togle">
            <SimpleLineIcon name="menu" />
        </div>
        </>
    )
}

export default Sidebar;