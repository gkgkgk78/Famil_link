import React, {useState, useEffect} from "react";
import "./home.css";
import Me from "../../images/다운로드.jpg";
import HeaderSocials from './HeaderSocials';
import ScrollDown from './ScrollDown';
import { useNavigate } from "react-router-dom";
// import { useSelector } from "react-redux";

const Home = () => {
    // const { fmpf, fmnm } = useSelector(({ token }) => ({
    //     fmpf: token.familymemberprofile,
    //     fmnm: token.familymembername,
    //   }))
    const [sidebarProfile, setSidebarProfile]  = useState();
    const [sidebarName, setSidebarName] = useState();
    const navigate = useNavigate();

    useEffect(() => {
        if(localStorage.getItem("fmurl")) {
      setSidebarProfile(localStorage.getItem("fmurl").replace(/"/gi, ""))
      setSidebarName(localStorage.getItem("fmname").replace(/"/gi, ""))
        }
        else {
            navigate('/login')
        }
    },[])
  
    return (
        <section className="home container" id="home">
            <div className="intro">
                <img src={sidebarProfile} alt="" className="home__img" />
                <h1 className="home__name">{sidebarName}</h1>
                <span className="home__education">I'm a Front-End developer</span>
            
            <HeaderSocials />
            
            <a href="#contact" className="btn">Hire Me</a>
            
            <ScrollDown />
            </div>
        </section>
    )
}

export default Home;