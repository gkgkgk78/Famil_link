import React, { useState } from 'react';
import "./Caption.css"

const Caption = props => {
    
    // const [caption, setCaption] = useState('123');
    // const hi = () => setCaption('안녕하세요 ' + {caption} + '님');
    // hi();
    return <div className='Caption'>안녕하세요, {props.name}님 </div>
    
};

export default Caption;
