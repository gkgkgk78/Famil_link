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

    // 시간과 날짜 2자리 수 표기, 시간은 12시간으로 표시
    let nowHours = nowTime.getHours() > 12 ? String(nowTime.getHours()-12).padStart(2,0) : String(nowTime.getHours()).padStart(2,0)
    let nowMinutes = String(nowTime.getMinutes()).padStart(2,0)
    let nowMonth = String(nowTime.getMonth()+1).padStart(2,0)
    let nowDate = String(nowTime.getDate()).padStart(2,0)
    

    return(
        <div className='clock'>                
            <div className='time'>{ (nowTime.getHours()) >= 12 ? <div className='ampm'>P<br />M</div> : <div className='ampm'>A<br />M</div>}  {nowHours} : {nowMinutes}</div>
            <div className='date'>{nowTime.getFullYear()} {nowMonth} {nowDate} {nowTime.toLocaleDateString('ko-KR', {weekday: 'short'})}</div>
        </div>
    )
}

export default Clock;