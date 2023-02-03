import React, { Component } from 'react';
import Logo from '../components/Logo';
import Profile from '../components/Profile';
import { useNavigate } from 'react-router-dom';
// import { Button } from '@mui/material';

function ProfilePage(props) {

    const familyName = '찬호';
    const navigate = useNavigate();

    return (
        <div>
            <header style ={{
                background :'orange',
                color :'white',
            }}>
                <Logo></Logo>
                <span>아이콘</span>
                <span>아이콘</span>
            </header>
            <main>
                <div style ={{
                    marginTop : '30px',
                    color : 'orange',
                }}>{familyName}가족</div>
                <section style ={{

                    display : 'flex',
                    flexWrap : 'wrap',
                    alignItems : 'center',
                    color : 'orange',
                }}>
                    <Profile></Profile>
                    <Profile></Profile>
                    <Profile></Profile>
                    <Profile></Profile>
                    <Profile></Profile>
                    <section style = {{
                                display : 'flex',
                                flexDirection : 'column',
                    }}>
                        <button style = {{
                            border : '1px solid orange',
                            color : 'white',
                            background : 'orange',
                            width : '100px',
                            height : '100px',
                            margin : '30px 47.5px 5px',
                        }} 
                        onClick={() => {
                            navigate("/createmember")
                          }}
                          >+</button>
                        <span>가족 추가</span>
                    </section>
                </section>
            </main>
        </div>
    );
}

export default ProfilePage;