import React, { useEffect, useState, } from 'react';
import axios from 'axios';
import "../pages/Main.css";
import {useSelector, useDispatch } from "react-redux";

function Calendar(){
    const [schedule, setSchedule] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const {memberAccessToken,} =useSelector(state => ({
        memberAccessToken: state.valid.memberAccessToken,
        }));

    // useEffect(() => {
    //     const fetchSchedule = async () => {
    //         try {
    //             setError(null);
    //             setLoading(true);
    //             setSchedule(null);

    //             const response = await axios.get(
    //                 'https://jsonplaceholder.typicode.com/posts'
    //             );
    //             setSchedule(response.data);
    //         } catch (e) {
    //             setError(e);
    //         }
    //         setLoading(false);
    //     };
    //     fetchSchedule();
    // },[]);

    // if (loading) return <div>로딩중...</div>
    // if (error) return <div>에러가 발생했습니다</div>;
    // if (!schedule) return null;

    useEffect(()=>{axios({
        method: "get",
          url: `http://i8a208.p.ssafy.io:3000/schedule/list/five-list`,
          headers: {
            "Authorization": `Bearer ${memberAccessToken}`
          }
        })
        .then ((res) => {
            // console.log(res);
            
          if (res.data.topList) {
              const objectList = res.data.topList
              let emptyList = []
              for (let sched of objectList){
                  
                                    // var js_date = new Date(parseInt(sched.date));
                    var js_date = new Date(sched.date);
                    js_date.setHours(js_date.getHours() - 9);
                    // 연도, 월, 일 추출
                    var year = js_date.getFullYear();
                    var month = js_date.getMonth() + 1;
                    var day = js_date.getDate();
                
                    var hour = js_date.getHours();
                    var min = js_date.getMinutes();
                    var sec = js_date.getSeconds();
                
                    var js_date1 = new Date();
                    var nyear = js_date1.getFullYear();
                    var nmonth = js_date1.getMonth() + 1;
                    var nday = js_date1.getDate();

                        // 월, 일의 경우 한자리 수 값이 있기 때문에 공백에 0 처리
                    if (month < 10) {
                        month = '0' + month;
                    }

                    if (day < 10) {
                        day = '0' + day;
                    }
                    if (nmonth < 10) {
                        nmonth = '0' + nmonth;
                    }

                    if (nday < 10) {
                        nday = '0' + nday;
                    }
                    sched.parseDate=`${month}월 ${day}일`;
                    
                    emptyList.push(sched)
                }
                // saveTodos(emptyList)
                setSchedule(emptyList);
                console.log(schedule)
            } 
             else console.log("일정이 없어")
        })  
        .catch ((err) => {
            console.log(err)
        })},[])
        

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