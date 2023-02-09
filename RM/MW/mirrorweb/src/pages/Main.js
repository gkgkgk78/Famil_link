import React, {useEffect} from 'react';
import "./Main.css"
import Todo from '../components/Todo';
import Calendar from '../components/Calendar';
import Caption from "../components/Caption"
import {useNavigate} from "react-router-dom"
import {useSelector} from "react-redux"


const Main = () => {
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
