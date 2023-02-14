import React, { useState } from "react";
import "./sidebar.css";
import SimpleLineIcon from "react-simple-line-icons";
// import button from "../../common/button";
import styled from "styled-components";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";

const Sidebar = ({ type }) => {
  const navigate = useNavigate();
  const [sidebarProfile, setSidebarProfile]  = useState();

  useEffect(() => {
    if(localStorage.getItem("fmurl")) {
    setSidebarProfile(localStorage.getItem("fmurl").replace(/"/gi, ""))
    }
  },[])

  const [showSidebar, setShowSidebar] = useState(false);

  const toggleSidebar = () => {
    setShowSidebar(!showSidebar);
  };

  const onLogout = () => {
    localStorage.removeItem("faccesstoken")
    localStorage.removeItem("fmccesstoken")
    localStorage.removeItem("fauid") 
    localStorage.removeItem("profile");
    localStorage.removeItem("fmname");
    localStorage.removeItem("fmurl");
    navigate("/login");
  };
  const onLogin = () => {
    navigate("/login");
  };

  const token = localStorage.getItem("faccesstoken")

  let loginCheck;
  if(token != null){
    loginCheck = (
      <button className="btn" onClick={onLogout}>
                로그아웃
      </button>
    )
  } else {
    loginCheck = (
      <button className="btn" onClick={onLogin}>
                로그인
      </button>
    )
  }

  return (
    <>
      <aside className={showSidebar ? "aside show-menu" : "aside"}>
        <a href="/" className="nav__logo">
          <img src={sidebarProfile} alt="" />
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
                <a href="/FamilyMember" className="nav__link">
                  <SimpleLineIcon name="user-following" />
                </a>
              </li>
              <li className="nav__item">
                <a href="#about" className="nav__link">
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
                {loginCheck}
              </li>
            </ul>
          </div>
        </nav>

        <div className="nav__footer">
          <span className="copyright">&copy; 2022 - 2023.</span>
        </div>
      </aside>

      <div
        className={showSidebar ? "nav__toggle nav__toggle-open" : "nav__toggle"}
        onClick={() => setShowSidebar(!showSidebar)}
      >
        <SimpleLineIcon name="menu" />
      </div>
    </>
  );
};

export default Sidebar;
