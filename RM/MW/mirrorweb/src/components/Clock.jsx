import React, { useState, useEffect  } from 'react';

const Clock = () => {
    const [nowTime, setTime] = useState(new Date());

    useEffect(() => {
        const id =setInterval(() => {
            setTime(new Date());
        }, 1000);
        return (() => clearInterval(id))
    }, [])
    

    return(
        <div>
            <div>{nowTime.toLocaleDateString()}</div>
            <div>{nowTime.toLocaleTimeString()}</div>
        </div>
    )
}

export default Clock;