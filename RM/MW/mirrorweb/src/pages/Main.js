import React from 'react';
import "./Main.css"
import Todo from '../components/Todo';
import Calendar from '../components/Calendar';
import Caption from '../components/Caption';

const Main = () => {
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
