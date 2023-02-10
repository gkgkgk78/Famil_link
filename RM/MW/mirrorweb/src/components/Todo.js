import React, { useEffect, useState, } from 'react';
import {useSelector, } from 'react-redux';
import {MdCheckBox, MdCheckBoxOutlineBlank} from 'react-icons/md';
import axios from 'axios';
import "../pages/Main.css";

function Todo(){
    const [todos, setTodos] = useState(null);

    const {familyAccessToken,} =useSelector(state => ({
        familyAccessToken: state.valid.familyAccessToken,
    }));


    useEffect(()=>{
        axios({
        method: "get",
          url: `http://i8a208.p.ssafy.io:3000/todo`,
          headers: {
            "Authorization": `Bearer ${familyAccessToken}`
          }
        })
        .then ((res) => {

          if (res.data.todolist) {
              const objectList = res.data.todolist
              let emptyList = []
              for (let todo of objectList) emptyList.push(todo)
              setTodos(emptyList);
          }
          else console.log("todo가 없어")
        })  
        .catch ((err) => {
            console.log(err)
        })
    },[])
    
    return (
        <div className='Todo'>
            <h4 className='TodoTitle'>To-do</h4>
            <hr />
            <div className='TodoListItem'>
                <ul >
                    {todos&&todos.map(todo => (
                        <div key={todo.id} className='TodoText'>
                        { todo.status ? <div className='checkbox'><MdCheckBox /><p className='checktext'>{todo.content}</p></div> : <div className='blankbox'><MdCheckBoxOutlineBlank/><p className='blanktext'>{todo.content}</p></div> }    
                        </div>
                    ))} 
                </ul>
            </div>
        </div>
    );
}

export default Todo;