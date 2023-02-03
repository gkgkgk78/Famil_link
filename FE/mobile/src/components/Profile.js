import React, { Fragment } from 'react';
import familyImage from './images/1.PNG';
function Profile(props) {
    return (
        <>
        <section style = {{
            display : 'flex',
            flexDirection : 'column',
        }}>
            <img style = {{
                border : '1px solid orange',
                width : '100px',
                height : '100px',
                margin : '30px 47.5px 5px',
            }}src={familyImage}>
            </img>
            <div>이름</div>
       </section>
        </>
    )
        };
export default Profile;