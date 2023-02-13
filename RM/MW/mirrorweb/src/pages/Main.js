import React, {useEffect} from 'react';
import "./Main.css"
import Todo from '../components/Todo';
import Calendar from '../components/Calendar';
import Caption from "../components/Caption"
import {useNavigate} from "react-router-dom"
import {useSelector} from "react-redux"


const Main = () => {
  const {me} = useSelector(state => ({
    me : state.valid.me
  }))
  
  const Navigate = useNavigate()
  const { fatoken } = useSelector(state => ({
    fatoken : state.valid.familyAccessToken
  }))
  useEffect(() => {
    if (!fatoken) {
      Navigate('/qr')
    }
  },[])

    return ( 
        <div>
          <div>{me ? <p>멤버가 로그인 되었습니다.</p> : <p>아직 멤버가 로그인 되지 않았어요</p>}</div>
          <div className='calendardiv'>
            <Calendar />  
          </div>
          <Caption></Caption>
          <div className='tododiv'>
            <Todo />
          </div>
        </div>
     );
}
 
export default React.memo(Main);
