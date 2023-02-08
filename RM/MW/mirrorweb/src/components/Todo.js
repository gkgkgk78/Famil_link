import React, { useEffect, useState, useRef } from 'react';
import {MdCheckBox, MdCheckBoxOutlineBlank} from 'react-icons/md';
import axios from 'axios';
import "../pages/Main.css";
// import { setTodos, } from '../modules/valid';
import {useSelector, useDispatch } from "react-redux";



function Todo(){

    const {familyAccessToken,} =useSelector(state => ({
        familyAccessToken: state.valid.familyAccessToken,
        }));
    // const dispatch = useDispatch();
    // const saveTodos = (todos00) => dispatch(setTodos(todos00))
    const [todos, setTodos] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // useEffect(() => {
    //     const fetchTodo = async () => {
    //         try {
    //             setError(null);
    //             setTodos({message : null});
    //             setLoading(true);
                
    //             const response = await axios.get(
    //                 'https://jsonplaceholder.typicode.com/todos'
    //             );
    //             setTodos(response.data);
    //             setTodos(todos => ({
    //                 ...todos,
    //                 message: response.data,
    //               }));
    //         } catch (e) {
    //             setError(e);
    //         }
    //         setLoading(false);
    //     };
    //     fetchTodo();
    // }, []);

    useEffect(()=>{axios({
        method: "get",
          url: `http://i8a208.p.ssafy.io:3000/todo`,
          headers: {
            "Authorization": `Bearer ${familyAccessToken}`
          }
        })
        .then ((res) => {
            // console.log(res);
          if (res.data.todolist) {
              const objectList = res.data.todolist
              let emptyList = []
              for (let todo of objectList) emptyList.push(todo)
              // saveTodos(emptyList)
              setTodos(emptyList);
              console.log(todos)
            } else console.log("todo가 없어")
        })  
        .catch ((err) => {
            console.log(err)
        })},[])
        



    // if (loading) return <div>로딩중...</div>;
    // if (error) return <div>애러가 발생했습니다.</div>;
    // if (!todos) return null;
    
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