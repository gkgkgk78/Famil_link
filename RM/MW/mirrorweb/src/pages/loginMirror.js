import { border, borderColor } from '@mui/system';
import React from 'react';

function loginMirror(props) {
    return (
        <div>
            <div style = {{
                marginTop : '100px',
                fontSize : '90px',
            }}>패밀링크</div>
            <div style ={{
                border : '5px solid',
                borderColor : 'white',
                width : '300px',
                height : '300px',
                marginTop : '250px',
                marginLeft : 'auto',
                marginRight : 'auto',
                
            }}></div>
        </div>
    );
}

export default loginMirror;