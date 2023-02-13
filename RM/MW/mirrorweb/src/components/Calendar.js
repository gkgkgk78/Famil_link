import React, { useEffect, useState, } from 'react';
import {useSelector, useDispatch } from "react-redux";
import axios from 'axios';
import "../pages/Main.css";
import { setSchedule } from '../modules/valid';

function Calendar(){

    const dispatch = useDispatch();
    const onsetSchedule = (e) => dispatch(setSchedule(e))

    const [schedule, setSchedule00] = useState(null);

    const {memberAccessToken,} = useSelector(state => ({
        memberAccessToken: state.valid.memberAccessToken,
    }));

    useEffect(()=>{
        if(!memberAccessToken) return;
        axios({
        method: "get",
        url: `http://i8a208.p.ssafy.io:3000/schedule/list/five-list`,
        headers: {
            "Authorization": `Bearer ${memberAccessToken}`
          }
        })
        .then ((res) => {
            
            if (res.data.topList) {
                const objectList = res.data.topList
                let emptyList = []
                for (let sched of objectList){                    
                    var js_date = new Date(sched.date);
                    js_date.setHours(js_date.getHours() - 9);
                    // 연도, 월, 일 추출
                    var year = js_date.getFullYear();
                    var month = js_date.getMonth() + 1;
                    var day = js_date.getDate();
                
                    var hour = js_date.getHours();
                    var min = js_date.getMinutes();
                    var sec = js_date.getSeconds();
                
                    // 월, 일의 경우 한자리 수 값이 있기 때문에 공백에 0 처리
                    if (month < 10) {
                        month = '0' + month;
                    }

                    if (day < 10) {
                        day = '0' + day;
                    }

                    sched.parseDate=`${month}월 ${day}일`;
                    
                    emptyList.push(sched)
                }
                setSchedule00(emptyList);
                onsetSchedule(emptyList);
            } 
            else console.log("일정이 없어")
        })  
        .catch ((err) => {
            console.log(err)
        })
    },[memberAccessToken])
        

    return (
        <div className='Calendar'>
            <h3 className='CalendarTitle'>일정</h3>
            <hr />
            <ul className='CalendarUl'>
                {schedule&&schedule.map(schedules => (
                    <li key={schedules.id}>
                        {`${schedules.parseDate}  ${schedules.content}`}
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default Calendar;