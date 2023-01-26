// 재생할 영상이 더 이상 없을 때 띄워지는 페이지
import React from 'react';
import "./Main.css"
import Caption from '../components/Caption';
const Main = () => {
    return ( 
        <div>
          <div className="calendar">
            <h2>일정</h2>
            <hr/>
            <ul>
                <li>임시</li>
            </ul>
          </div>
          <div className="todo">
            <h2>To-do</h2>
            <ul>
                <hr/>
                <div>
                <li>
                   세탁소에서 패딩 찾아오기 
                </li>
                <li>
                    강아지 미용 맡기기
                </li>
                </div>
            </ul>        
          </div>
          <Caption name="현재사용자"></Caption>
        </div>
     );
}
 
export default Main;