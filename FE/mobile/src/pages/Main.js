import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { isLoggIn } from '../modules/auth';
import { useDispatch } from "react-redux";
import '../components/navbar/navbar.css'
import Home from '../components/navbar/home/Home'
import About from "../components/navbar/about/About";
import Services from "../components/navbar/services/Services";
import Resume from "../components/navbar/resume/Resume";
import Portfolio from "../components/navbar/portfolio/Portfolio";
import Testimonials from "../components/navbar/testmonials/Testimonials";
import Blog from "../components/navbar/blog/Blog";
import { familyToken } from "../modules/token";
import axios from "axios";

const Main = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [isMember, setIsMember] = useState();
    const fatkn = localStorage.getItem('faccesstoken')

    axios.get('http://i8a208.p.ssafy.io:3000/account/member-list', {
        headers : {
            "Authorization": `Bearer ${fatkn}`
        }
    }).then((res) => {
        console.log(res)
        setIsMember(res.result)
    }).catch((err) => {
        console.log(err)
    })

    
    return (
        <div>
            
        {/* {isMember ? navigate('/FamilyMember') : navigate('/FamilyMemberRegister')} */}
        <main className="main">
        <Home />
        <About />
        <Services />
        <Resume />
        <Portfolio />
        <Testimonials />
        <Blog />
      </main>
        </div>
    )
}
export default Main;