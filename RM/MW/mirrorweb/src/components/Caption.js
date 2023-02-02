import React, { useState } from 'react';
import "./Caption.css"

const Caption = props => {
    
    const [caption, setCaption] = useState('123');
    
    const captionControl = () => setCaption('여기는 자막입니다');

    return <div className='Caption'> {caption} </div>
    
};

export default Caption;
