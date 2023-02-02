import { border, borderColor } from '@mui/system';
import React from 'react';

function loginMirror(props) {
    return (
        <div>
            <div>패밀링크</div>
            <div style ={{
                border : '5px solid',
                borderColor : 'white',
                width : '300px',
                height : '300px',
                marginTop : '150px',
                marginLeft : 'auto',
                marginRight : 'auto',
            }}></div>
        </div>
    );
}

export default loginMirror;