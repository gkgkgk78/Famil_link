// 재생할 영상이 더 이상 없을 때 띄워지는 페이지
import React from 'react';
import "./Main.css"
import Todo from '../components/Todo';
import Calendar from '../components/Calendar';
import {useSelector} from "react-redux";
import { useNavigate } from 'react-router-dom';

const Main = () => {
    const {familyaccestoken} = useSelector(state => ({
      familyaccestoken: state.valid.familyAccessToken
    }))

    const Navigate = useNavigate()
    useEffect(()=> {
      if (!familyaccestoken) {
        Navigate("/qr")
      }
    })

    return ( 
        <div>
          <div className='calendardiv'>
            <Calendar />
          </div>
          <div className='tododiv'>
            <Todo />
          </div>
        </div>
     );
}
 
export default React.memo(Main);
