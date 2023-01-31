import React, {useEffect, useState} from 'react';
import axios from 'axios';
import "../pages/Main.css";


function Calendar(){
    const [schedule, setSchedule] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchSchedule = async () => {
            try {
                setError(null);
                setLoading(true);
                setSchedule(null);

                const response = await axios.get(
                    'https://jsonplaceholder.typicode.com/posts'
                );
                setSchedule(response.data);
            } catch (e) {
                setError(e);
            }
            setLoading(false);
        };
        fetchSchedule();
    },[]);

    if (loading) return <div>로딩중...</div>
    if (error) return <div>에러가 발생했습니다</div>;
    if (!schedule) return null;


    return (
        <div className='Calendar'>
            <h3 className='CalendarTitle'>일정</h3>
            <hr />
            <ul className='CalendarUl'>
                {schedule.slice(0,5).map(schedules => (
                    <li key={schedules.id}>
                        { schedules.userId === 1 && schedules.title}
                    </li>
                ))}
            </ul>
        </div>
    )

}



export default Calendar;