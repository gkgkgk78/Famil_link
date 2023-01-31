import { now, weekday } from 'moment';
import React, { useState, useEffect  } from 'react';
import './Clock.css';

const Clock = () => {
    const [nowTime, setTime] = useState(new Date());

    useEffect(() => {
        const id =setInterval(() => {
            setTime(new Date());
        }, 1000);
        return (() => clearInterval(id))
    }, [])
    

    return(
        <div className='clock'>                
            <div className='time'>{ (nowTime.getHours()) >= 12 ? <div className='ampm'>P<br />M</div> : <div className='ampm'>A<br />M</div>}  {nowTime.getHours() >= 12 ? <div>{nowTime.getHours()-12}</div> : <div>({nowTime.getHours()})</div>} : {nowTime.getMinutes()}</div>
            <div className='date'>{nowTime.getFullYear()} {nowTime.getMonth() + 1} {nowTime.getDate()} {nowTime.toLocaleDateString('ko-KR', {weekday: 'short'})}</div>
        </div>
    )
}

export default Clock;